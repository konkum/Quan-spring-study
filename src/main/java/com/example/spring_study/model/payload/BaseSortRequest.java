package com.example.spring_study.model.payload;

import com.example.spring_study.constant.SortParam;
import lombok.Data;

@Data
public class BaseSortRequest extends BaseSearchRequest{
    private String sortString;
    private SortParam sortDirection;
}
