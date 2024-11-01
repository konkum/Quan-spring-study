package com.example.spring_study.model.payload;

import com.example.spring_study.model.Borrowing;
import com.example.spring_study.validator.ValidateSortField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowingSortRequest extends BaseSortRequest {
    @NonNull
    @ValidateSortField(entityClass = BorrowingResponse.class)
    private String sortString;
}
