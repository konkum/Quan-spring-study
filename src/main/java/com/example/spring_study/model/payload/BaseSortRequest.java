package com.example.spring_study.model.payload;

import com.example.spring_study.constant.SortParam;
import com.example.spring_study.validator.ValidateSortField;
import com.example.spring_study.validator.ValueOfEnum;
import lombok.Data;
import lombok.NonNull;

@Data
public class BaseSortRequest extends BaseSearchRequest {
    @NonNull
    private String sortString;
    @ValueOfEnum(enumClass = SortParam.class)
    private String sortDirection;
}
