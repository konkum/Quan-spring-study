package com.example.spring_study.logging;

import com.example.spring_study.repository.LogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.LogLevel;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

@Aspect
@Component
public class OperationLoggingAspect {

    private final LogRepository logRepository;

    private static final Logger logger = LoggerFactory.getLogger(OperationLoggingAspect.class);
    private ObjectMapper objectMapper;

    private long startTime;
    private String requestBody;

    public OperationLoggingAspect(LogRepository logRepository, ObjectMapper objectMapper) {
        this.logRepository = logRepository;
        this.objectMapper = objectMapper;
    }

    @Pointcut("execution(* com.example.spring_study.controllers..*.*(..)) && !@annotation(com.example.spring_study.logging.NoLogging)")
    public void controllerMethods() {
    }

    @Before("controllerMethods()")
    public void beginOperation(JoinPoint joinPoint) {
        startTime = System.currentTimeMillis();
        requestBody = null;
        requestBody = getArgsAsString(joinPoint.getArgs());
    }

    @AfterReturning(pointcut = "controllerMethods()", returning = "result")
    public void logOperation(JoinPoint joinPoint, Object result) {
        createAndSaveLogEntry(joinPoint, LogLevel.INFO, null, result);
    }

    @AfterThrowing(pointcut = "controllerMethods()", throwing = "ex")
    public void throwingOperation(JoinPoint joinPoint, Throwable ex) {
        createAndSaveLogEntry(joinPoint, LogLevel.ERROR, ex, null);
    }

    private void createAndSaveLogEntry(JoinPoint joinPoint, LogLevel logLevel, Throwable ex, Object result) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (authentication != null) ? authentication.getName() : "Anonymous";
        String ipAddress = getClientIp(request);

        LogEntry logEntry = LogEntry.builder()
                .timestamp(LocalDateTime.now())
                .level(logLevel)
                .userName(username)
                .action(determineAction(joinPoint.getSignature().getName()))
                .method(joinPoint.getSignature().toString())
                .message(createLogMessage(joinPoint.getArgs(), result, ex))
                .stackTrace(ex != null ? getStackTraceAsString(ex) : null)
                .ipAddress(ipAddress)
                .parameters(requestBody)
                .executionTimeMs(System.currentTimeMillis() - startTime)
                .result(result != null ? getObjAsString(result) : null)
                .build();

        if (result != null) {
            logger.info("Request received: {} {} from IP: {}", logEntry.getAction(), logEntry.getMethod(), logEntry.getIpAddress());
        } else if (ex != null) {
            logger.error(logEntry.getStackTrace());
        }

        logRepository.save(logEntry);
    }

    private String getObjAsString(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return "Unable to serialize response data";
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

    private String determineAction(String methodName) {
        if (methodName.startsWith("create") || methodName.startsWith("save")) return "CREATE";
        if (methodName.startsWith("update")) return "UPDATE";
        if (methodName.startsWith("delete")) return "DELETE";
        return "GET";
    }

    private String createLogMessage(Object[] args, Object result, Throwable ex) {
        String message = "Args: " + getArgsAsString(args);
        if (result != null) {
            message += ", Result: " + result.toString();
        }
        if (ex != null) {
            message += ", Error: " + ex.getMessage();
        }
        return message;
    }

    private String getArgsAsString(Object[] args) {
        try {
            return objectMapper.writeValueAsString(args);
        } catch (JsonProcessingException e) {
            return "Unable to serialize request data";
        }
    }

    private String getStackTraceAsString(Throwable ex) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
