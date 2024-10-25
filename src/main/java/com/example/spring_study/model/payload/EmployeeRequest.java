package com.example.spring_study.model.payload;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EmployeeRequest {
    @NotNull
    private String fullName;
    @NotNull
    private String address;
    @NotNull
    private String phoneNumber;
    @NotNull
    private Double accountBalance;
}
