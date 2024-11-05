package com.example.spring_study.services;

import com.example.spring_study.model.payload.EmployeeRequest;
import com.example.spring_study.model.payload.EmployeeResponse;
import com.example.spring_study.model.payload.EmployeeResponseUpdate;

import java.util.List;

public interface EmployeeService {
    public EmployeeResponse createEmployee(EmployeeRequest request);

    public EmployeeResponseUpdate updateEmployee(int id, EmployeeRequest request);

    public EmployeeResponse getEmployeeById(int id);

    public List<EmployeeResponse> getAllEmployees();

    public void deleteEmployee(int id);
}
