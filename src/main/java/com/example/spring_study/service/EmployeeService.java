package com.example.spring_study.service;

import com.example.spring_study.model.Device;
import com.example.spring_study.model.Employee;
import com.example.spring_study.model.payload.DeviceRequest;
import com.example.spring_study.model.payload.EmployeeRequest;

import java.util.List;

public interface EmployeeService {
    public Employee createEmployee(EmployeeRequest request);
    public Employee updateEmployee(int id,EmployeeRequest request);
    public Employee getEmployeeById(int id);
    public List<Employee> getAllEmployees();
    public void deleteEmployee(int id);
}
