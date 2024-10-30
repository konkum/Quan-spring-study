package com.example.spring_study.model.payload;

import com.example.spring_study.model.Role;
import com.example.spring_study.validator.ValidPhoneNumber;
import com.example.spring_study.validator.ValidateRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmployeeResponse {
    @NonNull
    private int id;
    @NonNull
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String userName;
    @NonNull
    private String fullName;
    @NonNull
    private String address;
    @NonNull
    @ValidPhoneNumber
    private String phoneNumber;
    @NonNull
    @Min(value = 0, message = "Account ballance cannot be negative")
    private Double accountBalance;
    @ValidateRole
    private Set<Role> roles;
}
