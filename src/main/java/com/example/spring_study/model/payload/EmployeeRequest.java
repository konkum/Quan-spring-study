package com.example.spring_study.model.payload;

import com.example.spring_study.validator.ValidPhoneNumber;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;

@Data
public class EmployeeRequest {
    @NotNull
    private String fullName;
    @NotNull
    private String address;
    @NotNull
    @ValidPhoneNumber
    private String phoneNumber;
    @NonNull
    @Min(value = 0, message = "Account ballance cannot be null")
    private Double accountBalance;
}
