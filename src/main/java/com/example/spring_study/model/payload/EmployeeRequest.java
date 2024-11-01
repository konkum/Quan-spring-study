package com.example.spring_study.model.payload;

import com.example.spring_study.validator.ValidPhoneNumber;
import com.example.spring_study.validator.ValidateRole;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Set;

@Data
@NoArgsConstructor
public class EmployeeRequest {
    @NonNull
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String userName;
    @NonNull
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    @NonNull
    private String fullName;
    @NonNull
    private String address;
    @NonNull
    @ValidPhoneNumber
    private String phoneNumber;
    @NonNull
    @Min(value = 0, message = "Account ballance cannot be null")
    private Double accountBalance;
    @ValidateRole
    private Set<String> roles;
}
