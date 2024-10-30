package com.example.spring_study.mapping;

import com.example.spring_study.model.Employee;
import com.example.spring_study.model.payload.EmployeeRequest;
import com.example.spring_study.model.payload.EmployeeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {
    @Mapping(target = "id", source = "employee.id")
    @Mapping(target = "userName", source = "employee.userName")
    @Mapping(target = "fullName", source = "employee.fullName")
    @Mapping(target = "address", source = "employee.address")
    @Mapping(target = "phoneNumber", source = "employee.phoneNumber")
    @Mapping(target = "accountBalance", source = "employee.accountBalance")
    @Mapping(target = "roles", source = "employee.roles")
    EmployeeResponse toResponse(Employee employee);

    List<EmployeeResponse> toResponseList(List<Employee> employees);
}
