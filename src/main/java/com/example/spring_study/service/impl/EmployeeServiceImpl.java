package com.example.spring_study.service.impl;

import com.example.spring_study.model.Employee;
import com.example.spring_study.model.payload.EmployeeRequest;
import com.example.spring_study.repository.EmployeeRepository;
import com.example.spring_study.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;
    @Override
    public Employee createEmployee(EmployeeRequest request) {
        try {
            Employee employee = new Employee(request.getFullName(),request.getAddress(),request.getPhoneNumber(),request.getAccountBalance());
            return employeeRepository.save(employee);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Employee updateEmployee(int id, EmployeeRequest request) {
        try {
            Employee employee = getEmployeeById(id);
            if (employee == null){
                return null;
            }

            employee.setFullName(request.getFullName());
            employee.setAddress(request.getAddress());
            employee.setPhoneNumber(request.getPhoneNumber());
            employee.setAccountBalance(request.getAccountBalance());

            return employeeRepository.save(employee);
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Employee getEmployeeById(int id) {
        return employeeRepository.findById(id).orElse(null);
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public void deleteEmployee(int id) {
        employeeRepository.deleteById(id);
    }
}
