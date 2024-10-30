package com.example.spring_study.model.payload;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class LoginRequest {
    @NonNull
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String userName;
    @NonNull
    @Min(value = 0, message = "Account ballance cannot be null")
    private String password;
}
