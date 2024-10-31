package com.example.spring_study.service.impl;

import com.example.spring_study.exception.EmployeeNotFoundException;
import com.example.spring_study.mapping.EmployeeMapper;
import com.example.spring_study.model.Employee;
import com.example.spring_study.model.Role;
import com.example.spring_study.model.payload.EmployeeRequest;
import com.example.spring_study.model.payload.EmployeeResponse;
import com.example.spring_study.model.payload.EmployeeResponseUpdate;
import com.example.spring_study.repository.EmployeeRepository;
import com.example.spring_study.repository.RoleRepository;
import com.example.spring_study.service.EmployeeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmployeeMapper mapper;

    @Override
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        try {
            String encodePassword = passwordEncoder.encode(request.getPassword());
            Set<Role> roles = roleRepository.findByNameIn(request.getRoles());
            Employee employee = new Employee(request.getUserName(), encodePassword, request.getFullName(), request.getAddress(), request.getPhoneNumber(), request.getAccountBalance(), roles);
            return mapper.toResponse(employeeRepository.save(employee));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public EmployeeResponseUpdate updateEmployee(int id, EmployeeRequest request) {
        try {
            Employee employee = employeeRepository.findById(id).orElseThrow(() -> {
                log.error("Employee not found with id: {}", id);
                return new EmployeeNotFoundException(id);
            });

            Set<Role> roles = roleRepository.findByNameIn(request.getRoles());
            employee.setUserName(request.getUserName());
            employee.setPassword(passwordEncoder.encode(request.getPassword()));
            employee.setFullName(request.getFullName());
            employee.setAddress(request.getAddress());
            employee.setPhoneNumber(request.getPhoneNumber());
            employee.setAccountBalance(request.getAccountBalance());
            employee.setRoles(roles);

            EmployeeResponse response = mapper.toResponse(employeeRepository.save(employee));

            return (EmployeeResponseUpdate) response;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public EmployeeResponse getEmployeeById(int id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Employee not found with id: {}", id);
                    return new EmployeeNotFoundException(id);
                });
        return mapper.toResponse(employee);
    }

    @Override
    public List<EmployeeResponse> getAllEmployees() {
        return mapper.toResponseList(employeeRepository.findAll());
    }

    @Override
    public void deleteEmployee(int id) {
        employeeRepository.deleteById(id);
    }
}
