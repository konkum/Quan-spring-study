package com.example.spring_study.model.payload;

import com.example.spring_study.constant.SortParam;
import lombok.Data;


@Data
public class BaseSearchRequest {
    private int pageNumber;
    private int pageSize = 10;
}
