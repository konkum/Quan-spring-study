package com.example.spring_study.logging;

import com.example.spring_study.repository.LogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.logging.LogLevel;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingInterceptor.class);
    private final LogRepository logRepository;
    private final ObjectMapper objectMapper;
    private final ThreadLocal<String> requestIdHolder = new ThreadLocal<>();

    public RequestLoggingInterceptor(LogRepository logRepository, ObjectMapper objectMapper) {
        this.logRepository = logRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestId = UUID.randomUUID().toString();
        requestIdHolder.set(requestId);

        // Wrap request to allow reading the body multiple times
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);

        // Set MDC context for logging
        MDC.put("requestId", requestId);
        MDC.put("clientIp", getClientIp(request));
        MDC.put("method", request.getMethod());
        MDC.put("endpoint", request.getRequestURI());

        // Log initial request info
        logger.info("Request received: {} {} from IP: {}",
                request.getMethod(),
                request.getRequestURI(),
                getClientIp(request));

        request.setAttribute("startTime", System.currentTimeMillis());

        // Store request parameters
        request.setAttribute("parameters", captureRequestParameters(wrappedRequest));

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        try {
            long startTime = (Long) request.getAttribute("startTime");
            long duration = System.currentTimeMillis() - startTime;

            // Get stored request parameters
            String parameters = (String) request.getAttribute("parameters");

            // Create and save log entry
            saveLogEntry(request, response, duration, parameters, null);

            MDC.put("responseStatus", String.valueOf(response.getStatus()));
            MDC.put("duration", duration + "ms");

            logger.info("Request completed in {}ms with status {}", duration, response.getStatus());
        } catch (Exception e) {
            logger.error("Error in postHandle logging", e);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        try {
            if (ex != null) {
                long startTime = (Long) request.getAttribute("startTime");
                long duration = System.currentTimeMillis() - startTime;
                String parameters = (String) request.getAttribute("parameters");

                // Save error log entry
                saveLogEntry(request, response, duration, parameters, ex);
            }
        } catch (Exception e) {
            logger.error("Error in afterCompletion logging", e);
        } finally {
            MDC.clear();
            requestIdHolder.remove();
        }
    }

    private void saveLogEntry(HttpServletRequest request, HttpServletResponse response,
                              long duration, String parameters, Exception ex) {
        try {
            // Get authenticated user
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = (auth != null) ? auth.getName() : "Anonymous";

            LogEntry logEntry = LogEntry.builder()
                    .timestamp(LocalDateTime.now())
                    .level(ex != null ? LogLevel.ERROR : LogLevel.INFO)
                    .method(request.getMethod() + " " + request.getRequestURI())
                    .message(createMessage(request, response))
                    .action(determineAction(request.getMethod()))
                    .executionTimeMs(duration)
                    .userName(username)
                    .ipAddress(getClientIp(request))
                    .parameters(parameters)
                    .result(captureResponse(response))
                    .stackTrace(ex != null ? getStackTrace(ex) : null)
                    .build();

            // Async save to avoid blocking
            CompletableFuture.runAsync(() -> {
                try {
                    logRepository.save(logEntry);
                } catch (Exception e) {
                    logger.error("Failed to save log entry", e);
                }
            });
        } catch (Exception e) {
            logger.error("Error creating log entry", e);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

    private String captureRequestParameters(HttpServletRequest request) {
        try {
            Map<String, Object> params = new HashMap<>();

            // Add query parameters
            request.getParameterMap().forEach(params::put);

            // Add headers
            Map<String, String> headers = new HashMap<>();
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                headers.put(headerName, request.getHeader(headerName));
            }
            params.put("headers", headers);

            return objectMapper.writeValueAsString(params);
        } catch (Exception e) {
            logger.warn("Failed to capture request parameters", e);
            return "Failed to capture parameters";
        }
    }

    private String captureResponse(HttpServletResponse response) {
        try {
            if (response instanceof ContentCachingResponseWrapper wrapper) {
                byte[] buf = wrapper.getContentAsByteArray();
                if (buf.length > 0) {
                    return new String(buf, wrapper.getCharacterEncoding());
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to capture response", e);
        }
        return null;
    }

    private String determineAction(String method) {
        return switch (method.toUpperCase()) {
            case "POST" -> "CREATE";
            case "PUT", "PATCH" -> "UPDATE";
            case "DELETE" -> "DELETE";
            default -> "GET";
        };
    }

    private String createMessage(HttpServletRequest request, HttpServletResponse response) {
        return String.format("%s %s completed with status %d",
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus());
    }

    private String getStackTrace(Exception ex) {
        if (ex == null) return null;

        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : ex.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }
}
