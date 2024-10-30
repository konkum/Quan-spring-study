package com.example.spring_study.model.payload;

import com.example.spring_study.constant.SortParam;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseSearchRequest {
    @NonNull
    @Min(value = 0, message = "page number have to be above 0")
    private int pageNumber;
    @NonNull
    @Min(value = 0, message = "page size have to be above 0")
    private int pageSize = 10;
}
