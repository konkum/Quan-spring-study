package com.example.spring_study.model.payload;

import com.example.spring_study.model.Device;
import com.example.spring_study.validator.ValidateSortField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceSortRequest extends BaseSortRequest {
    @NonNull
    @ValidateSortField(entityClass = Device.class)
    private String sortString;
}
