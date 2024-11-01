package com.example.spring_study.service;

import com.example.spring_study.exception.EmployeeNotFoundException;
import com.example.spring_study.mapping.EmployeeMapper;
import com.example.spring_study.model.Employee;
import com.example.spring_study.model.Role;
import com.example.spring_study.model.payload.EmployeeRequest;
import com.example.spring_study.model.payload.EmployeeResponse;
import com.example.spring_study.model.payload.EmployeeResponseUpdate;
import com.example.spring_study.repository.DeviceRepository;
import com.example.spring_study.repository.EmployeeRepository;
import com.example.spring_study.repository.RoleRepository;
import com.example.spring_study.service.impl.DeviceServiceImpl;
import com.example.spring_study.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTests {
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private RoleRepository roleRepository;
    @InjectMocks
    private EmployeeServiceImpl employeeService;
    @MockBean
    private EmployeeMapper mapper;
    @MockBean
    private PasswordEncoder passwordEncoder;

    private String password;
    private Set<Role> roles;
    private Employee employee;
    private EmployeeRequest employeeRequest;
    private EmployeeResponse employeeResponse;

    @BeforeEach
    void setup() {
        password = "$2a$12$FKrhFM/410sEi9k0fQqbgeZ/GotNsnRKzkCl/rVVdkn8kxAFdNH/2";

        roles = Set.of(new Role(1, "ROlE_USER"), new Role(2, "ROLE_ADMIN"));

        employee = new Employee();
        employee.setId(1);
        employee.setPassword(password);
        employee.setUserName("John");
        employee.setAddress("Address 1");
        employee.setPhoneNumber("0123456789");
        employee.setFullName("Name 1");
        employee.setAccountBalance(10000.0);
        employee.setRoles(roles);

        employeeRequest = new EmployeeRequest();
        employeeRequest.setUserName("John");
        employeeRequest.setPassword("test");
        employeeRequest.setAddress("Address 1");
        employeeRequest.setPhoneNumber("0123456789");
        employeeRequest.setFullName("Name 1");
        employeeRequest.setAccountBalance(10000.0);

        employeeResponse = new EmployeeResponse();
        employeeResponse.setId(1);
        employeeResponse.setUserName("John");
        employeeResponse.setAddress("Address 1");
        employeeResponse.setPhoneNumber("0123456789");
        employeeResponse.setFullName("Name 1");
        employeeResponse.setAccountBalance(10000.0);
        employeeResponse.setRoles(roles);
    }

    @Test
    void testCreateEmployee() {
        when(employeeRepository.save(any())).thenReturn(employee);
        when(roleRepository.findByNameIn(any())).thenReturn(roles);
        when(passwordEncoder.encode(anyString())).thenReturn(password);
        when(mapper.toResponse(any())).thenReturn(employeeResponse);

        EmployeeResponse result = employeeService.createEmployee(employeeRequest);

        assertEquals(1, result.getId());
        assertEquals("0123456789", result.getPhoneNumber());
        verify(employeeRepository, times(1)).save(any());
        verify(roleRepository, times(1)).findByNameIn(any());
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(mapper, times(1)).toResponse(any());
    }

    @Test
    void testGetEmployeeById() {
        when(employeeRepository.findById(anyInt())).thenReturn(Optional.of(employee));
        when(mapper.toResponse(any())).thenReturn(employeeResponse);

        EmployeeResponse result = employeeService.getEmployeeById(1);

        assertEquals(1, result.getId());
        assertEquals("0123456789", result.getPhoneNumber());
        verify(employeeRepository, times(1)).findById(anyInt());
        verify(mapper, times(1)).toResponse(any());
    }

    @Test
    void testGetEmployees() {
        when(employeeRepository.findAll()).thenReturn(List.of(employee));
        when(mapper.toResponseList(any())).thenReturn(List.of(employeeResponse));

        List<EmployeeResponse> result = employeeService.getAllEmployees();

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals("0123456789", result.get(0).getPhoneNumber());
        verify(employeeRepository, times(1)).findAll();
        verify(mapper, times(1)).toResponseList(any());
    }

    @Test
    void testDeleteEmployee() {
        doNothing().when(employeeRepository).deleteById(anyInt());

        employeeService.deleteEmployee(1);

        verify(employeeRepository, times(1)).deleteById(anyInt());
    }

    @Test
    void testUpdateEmployee() {
        Employee updatedEmployee = new Employee();
        updatedEmployee.setId(1);
        updatedEmployee.setUserName("John");
        updatedEmployee.setAddress("Address 1");
        updatedEmployee.setPhoneNumber("0123456789");
        updatedEmployee.setFullName("Name 2");
        updatedEmployee.setAccountBalance(10000.0);

        EmployeeResponse updateResponse = new EmployeeResponse();
        updateResponse.setId(1);
        updateResponse.setUserName("John");
        updateResponse.setAddress("Address 1");
        updateResponse.setPhoneNumber("0123456789");
        updateResponse.setFullName("Name 2");
        updateResponse.setAccountBalance(10000.0);
        updateResponse.setRoles(roles);

        when(employeeRepository.findById(anyInt())).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any())).thenReturn(updatedEmployee);
        when(roleRepository.findByNameIn(any())).thenReturn(roles);
        when(passwordEncoder.encode(anyString())).thenReturn(password);
        when(mapper.toResponse(any())).thenReturn(updateResponse);


        EmployeeResponseUpdate result = employeeService.updateEmployee(1, employeeRequest);

        assertEquals(1, updatedEmployee.getId());
        assertEquals("0123456789", updatedEmployee.getPhoneNumber());
        assertEquals("Name 2", updatedEmployee.getFullName());
        verify(employeeRepository, times(1)).save(any());
        verify(roleRepository, times(1)).findByNameIn(any());
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(mapper, times(1)).toResponse(any());
    }

    @Test
    void testCreateEmployee_NotCreated() {
        when(employeeRepository.save(any())).thenReturn(null);

        EmployeeResponse result = employeeService.createEmployee(employeeRequest);

        assertNull(result);
        verify(employeeRepository, times(1)).save(any());
    }

    @Test
    void testGetEmployeeById_NotFound() {
        when(employeeRepository.findById(anyInt())).thenReturn(Optional.empty());

        EmployeeNotFoundException result = assertThrows(EmployeeNotFoundException.class, () -> employeeService.getEmployeeById(1));

        verify(employeeRepository, times(1)).findById(anyInt());
    }

    @Test
    void testGetEmployees_NotFound() {
        when(employeeRepository.findAll()).thenReturn(List.of());

        List<EmployeeResponse> result = employeeService.getAllEmployees();

        assertEquals(0, result.size());
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    void testUpdateEmployee_NotFound() {
        Employee updatedEmployee = new Employee();
        updatedEmployee.setId(1);
        updatedEmployee.setUserName("John");
        updatedEmployee.setPassword(password);
        updatedEmployee.setAddress("Address 1");
        updatedEmployee.setPhoneNumber("0123456789");
        updatedEmployee.setFullName("Name 2");
        updatedEmployee.setAccountBalance(10000.0);
        when(employeeRepository.findById(anyInt())).thenReturn(Optional.empty());

        EmployeeNotFoundException result = assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.updateEmployee(1, employeeRequest);
        });

        verify(employeeRepository, times(1)).findById(anyInt());
    }
}
