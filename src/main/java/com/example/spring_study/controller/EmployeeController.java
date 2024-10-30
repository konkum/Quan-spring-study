package com.example.spring_study.controller;

import com.example.spring_study.model.Employee;
import com.example.spring_study.model.payload.EmployeeRequest;
import com.example.spring_study.model.payload.EmployeeResponse;
import com.example.spring_study.model.payload.EmployeeResponseUpdate;
import com.example.spring_study.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @GetMapping(path = "/get")
    private ResponseEntity<EmployeeResponse> getEmployeeById(@Param("id") int id) {
        EmployeeResponse employee = employeeService.getEmployeeById(id);
        if (employee == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(employee);
    }

    @GetMapping(path = "/getAll")
    private ResponseEntity<List<EmployeeResponse>> getAllEmployees() {
        List<EmployeeResponse> employee = employeeService.getAllEmployees();
        if (employee == null || employee.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(employee);
    }

    @DeleteMapping(path = "/delete")
    private ResponseEntity deleteEmployee(@Param("id") int id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/create")
    private ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeRequest request) {
        EmployeeResponse employee = employeeService.createEmployee(request);
        if (employee == null) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        return ResponseEntity.ok().body(employee);
    }

    @PutMapping(path = "/update")
    private ResponseEntity<EmployeeResponseUpdate> updateEmployee(@Param("id") int id, @Valid @RequestBody EmployeeRequest request) {
        EmployeeResponseUpdate employee = employeeService.updateEmployee(id, request);
        employee.setUpdatePassword(true);
        if (employee == null) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        return ResponseEntity.ok().body(employee);
    }

}
