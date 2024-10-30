package com.example.spring_study.model.payload;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@Data
@NoArgsConstructor
public class BorrowingRequest {
    @NonNull
    @Min(value = 0, message = "There is no employee id that is negative")
    private Integer employeeId;
    @NonNull
    private List<Integer> devicesId;
}
