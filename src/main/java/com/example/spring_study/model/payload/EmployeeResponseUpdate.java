package com.example.spring_study.model.payload;

import lombok.Data;

@Data
public class EmployeeResponseUpdate extends EmployeeResponse {
    private boolean updatePassword;
}
