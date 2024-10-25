package com.example.spring_study.controller;

import com.example.spring_study.model.Employee;
import com.example.spring_study.model.payload.EmployeeRequest;
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
    private ResponseEntity<Employee> getEmployeeById(@Param("id") int id){
        Employee employee = employeeService.getEmployeeById(id);
        if (employee==null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(employee);
    }

    @GetMapping(path = "/getAll")
    private ResponseEntity<List<Employee>> getAllEmployees(){
        List<Employee> employee = employeeService.getAllEmployees();
        if (employee==null || employee.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(employee);
    }

    @DeleteMapping(path = "/delete")
    private ResponseEntity deleteEmployee(@Param("id") int id){
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/create")
    private ResponseEntity<Employee> createEmployee(@Valid @RequestBody EmployeeRequest request){
        Employee employee = employeeService.createEmployee(request);
        if (employee==null){
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        return ResponseEntity.ok().body(employee);
    }

    @PutMapping(path = "/update")
    private ResponseEntity<Employee> updateEmployee(@Param("id")int id, @Valid @RequestBody EmployeeRequest request){
        Employee employee = employeeService.updateEmployee(id,request);
        if (employee==null){
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        return ResponseEntity.ok().body(employee);
    }

}
