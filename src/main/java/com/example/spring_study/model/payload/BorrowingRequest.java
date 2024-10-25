package com.example.spring_study.model.payload;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class BorrowingRequest {
    @NotNull
    private Integer employeeId;
    @NotNull
    private List<Integer> devicesId;
}
