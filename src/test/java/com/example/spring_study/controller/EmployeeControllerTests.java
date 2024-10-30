package com.example.spring_study.controller;

import com.example.spring_study.model.Employee;
import com.example.spring_study.model.payload.EmployeeRequest;
import com.example.spring_study.model.payload.EmployeeResponse;
import com.example.spring_study.model.payload.EmployeeResponseUpdate;
import com.example.spring_study.service.EmployeeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EmployeeControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    private EmployeeResponse employee;
    private EmployeeRequest employeeRequest;

    @BeforeEach
    void setup() {
        employee = new EmployeeResponse();
        employee.setId(1);
        employee.setUserName("Test");
        employee.setAddress("Address 1");
        employee.setPhoneNumber("0123456789");
        employee.setFullName("Name 1");
        employee.setAccountBalance(10000.0);

        employeeRequest = new EmployeeRequest();
        employeeRequest.setUserName("Test");
        employeeRequest.setPassword("12345");
        employeeRequest.setAddress("Address 1");
        employeeRequest.setPhoneNumber("0123456789");
        employeeRequest.setFullName("Name 1");
        employeeRequest.setAccountBalance(10000.0);
    }

    @Test
    void testCreateEmployee() throws Exception {
        when(employeeService.createEmployee(any(EmployeeRequest.class))).thenReturn(employee);

        mockMvc.perform(post("/api/v1/employee/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.address").value("Address 1"))
                .andExpect(jsonPath("$.accountBalance").value(10000.0))
                .andExpect(jsonPath("$.phoneNumber").value("0123456789"));

        verify(employeeService, times(1)).createEmployee(any(EmployeeRequest.class));
    }

    @Test
    void testGetEmployeeById() throws Exception {
        when(employeeService.getEmployeeById(anyInt())).thenReturn(employee);

        mockMvc.perform(get("/api/v1/employee/get")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.address").value("Address 1"))
                .andExpect(jsonPath("$.accountBalance").value(10000.0))
                .andExpect(jsonPath("$.phoneNumber").value("0123456789"));

        verify(employeeService, times(1)).getEmployeeById(anyInt());
    }

    @Test
    void testGetEmployees() throws Exception {
        when(employeeService.getAllEmployees()).thenReturn(List.of(employee));

        mockMvc.perform(get("/api/v1/employee/getAll")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].address").value("Address 1"))
                .andExpect(jsonPath("$[0].accountBalance").value(10000.0))
                .andExpect(jsonPath("$[0].phoneNumber").value("0123456789"));

        verify(employeeService, times(1)).getAllEmployees();
    }

    @Test
    void testDeleteEmployee() throws Exception {
        doNothing().when(employeeService).deleteEmployee(anyInt());

        mockMvc.perform(delete("/api/v1/employee/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("id", "1"))
                .andExpect(status().isOk());

        verify(employeeService, times(1)).deleteEmployee(anyInt());
    }


    @Test
    void testUpdateEmployee() throws Exception {
        when(employeeService.updateEmployee(anyInt(), any(EmployeeRequest.class))).thenReturn((EmployeeResponseUpdate) employee);

        mockMvc.perform(put("/api/v1/employee/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("id", "1")
                        .content(objectMapper.writeValueAsString(employeeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.address").value("Address 1"))
                .andExpect(jsonPath("$.accountBalance").value(10000.0))
                .andExpect(jsonPath("$.phoneNumber").value("0123456789"));

        verify(employeeService, times(1)).updateEmployee(anyInt(), any(EmployeeRequest.class));
    }

    @Test
    void testCreateEmployee_NotCreated() throws Exception {
        when(employeeService.createEmployee(any(EmployeeRequest.class))).thenReturn(null);

        mockMvc.perform(post("/api/v1/employee/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeRequest)))
                .andExpect(status().isNotAcceptable());

        verify(employeeService, times(1)).createEmployee(any(EmployeeRequest.class));
    }

    @Test
    void testGetEmployeeById_NotFound() throws Exception {
        when(employeeService.getEmployeeById(anyInt())).thenReturn(null);

        mockMvc.perform(get("/api/v1/employee/get")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("id", "999"))
                .andExpect(status().isNotFound());

        verify(employeeService, times(1)).getEmployeeById(anyInt());
    }

    @Test
    void testGetEmployees_NotFound() throws Exception {
        when(employeeService.getAllEmployees()).thenReturn(null);

        mockMvc.perform(get("/api/v1/employee/getAll")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(employeeService, times(1)).getAllEmployees();
    }


    @Test
    void testUpdateEmployee_NotAcceptable() throws Exception {
        when(employeeService.updateEmployee(anyInt(), any(EmployeeRequest.class))).thenReturn(null);

        mockMvc.perform(put("/api/v1/employee/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("id", "1")
                        .content(objectMapper.writeValueAsString(employeeRequest)))
                .andExpect(status().isNotAcceptable());

        verify(employeeService, times(1)).updateEmployee(anyInt(), any(EmployeeRequest.class));
    }
}
