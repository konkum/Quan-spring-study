package com.example.spring_study.service;

import com.example.spring_study.model.Employee;
import com.example.spring_study.model.payload.EmployeeRequest;
import com.example.spring_study.repository.DeviceRepository;
import com.example.spring_study.repository.EmployeeRepository;
import com.example.spring_study.service.impl.DeviceServiceImpl;
import com.example.spring_study.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTests {
    @Mock
    private EmployeeRepository employeeRepository;
    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;
    private EmployeeRequest employeeRequest;

    @BeforeEach
    void setup(){
        employee = new Employee();
        employee.setId(1);
        employee.setAddress("Address 1");
        employee.setPhoneNumber("0123456789");
        employee.setFullName("Name 1");
        employee.setAccountBalance(10000.0);

        employeeRequest = new EmployeeRequest();
        employeeRequest.setAddress("Address 1");
        employeeRequest.setPhoneNumber("0123456789");
        employeeRequest.setFullName("Name 1");
        employeeRequest.setAccountBalance(10000.0);
    }

    @Test
    void testCreateEmployee(){
        when(employeeRepository.save(any())).thenReturn(employee);

        Employee result =  employeeService.createEmployee(employeeRequest);

        assertEquals(1,result.getId());
        assertEquals("0123456789",result.getPhoneNumber());
        verify(employeeRepository,times(1)).save(any());
    }

    @Test
    void testGetEmployeeById(){
        when(employeeRepository.findById(anyInt())).thenReturn(Optional.of(employee));

        Employee result =  employeeService.getEmployeeById(1);

        assertEquals(1,result.getId());
        assertEquals("0123456789",result.getPhoneNumber());
        verify(employeeRepository,times(1)).findById(anyInt());
    }

    @Test
    void testGetEmployees(){
        when(employeeRepository.findAll()).thenReturn(List.of(employee));

        List<Employee> result =  employeeService.getAllEmployees();

        assertEquals(1,result.size());
        assertEquals(1,result.get(0).getId());
        assertEquals("0123456789",result.get(0).getPhoneNumber());
        verify(employeeRepository,times(1)).findAll();
    }

    @Test
    void testDeleteEmployee(){
        doNothing().when(employeeRepository).deleteById(anyInt());

        employeeService.deleteEmployee(1);

        verify(employeeRepository,times(1)).deleteById(anyInt());
    }

    @Test
    void testUpdateEmployee(){
        Employee updatedEmployee = new Employee();
        updatedEmployee.setId(1);
        updatedEmployee.setAddress("Address 1");
        updatedEmployee.setPhoneNumber("0123456789");
        updatedEmployee.setFullName("Name 2");
        updatedEmployee.setAccountBalance(10000.0);
        when(employeeRepository.findById(anyInt())).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any())).thenReturn(updatedEmployee);

        Employee result =  employeeService.updateEmployee(1,employeeRequest);

        assertEquals(1,updatedEmployee.getId());
        assertEquals("0123456789",updatedEmployee.getPhoneNumber());
        assertEquals("Name 2", updatedEmployee.getFullName());
        verify(employeeRepository,times(1)).save(any());
    }

    @Test
    void testCreateEmployee_NotCreated(){
        when(employeeRepository.save(any())).thenReturn(null);

        Employee result =  employeeService.createEmployee(employeeRequest);

        assertNull(result);
        verify(employeeRepository,times(1)).save(any());
    }

    @Test
    void testGetEmployeeById_NotFound(){
        when(employeeRepository.findById(anyInt())).thenReturn(Optional.empty());

        Employee result =  employeeService.getEmployeeById(1);

        assertNull(result);
        verify(employeeRepository,times(1)).findById(anyInt());
    }

    @Test
    void testGetEmployees_NotFound(){
        when(employeeRepository.findAll()).thenReturn(List.of());

        List<Employee> result =  employeeService.getAllEmployees();

        assertEquals(0,result.size());
        verify(employeeRepository,times(1)).findAll();
    }

    @Test
    void testUpdateEmployee_NotFound(){
        Employee updatedEmployee = new Employee();
        updatedEmployee.setId(1);
        updatedEmployee.setAddress("Address 1");
        updatedEmployee.setPhoneNumber("0123456789");
        updatedEmployee.setFullName("Name 2");
        updatedEmployee.setAccountBalance(10000.0);
        when(employeeRepository.findById(anyInt())).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any())).thenReturn(null);

        Employee result =  employeeService.updateEmployee(1,employeeRequest);

        assertNull(result);
        verify(employeeRepository,times(1)).save(any());
    }
}
