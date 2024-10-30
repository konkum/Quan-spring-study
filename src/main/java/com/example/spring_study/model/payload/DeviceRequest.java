package com.example.spring_study.model.payload;

import com.example.spring_study.constant.RateType;
import com.example.spring_study.constant.Type;
import com.example.spring_study.validator.ValueOfEnum;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class DeviceRequest {
    @NonNull
    @ValueOfEnum(enumClass = Type.class)
    private String type;
    @NonNull
    @Min(value = 0, message = "Unit price must be non-negative")
    private Double unitPrice;
    @NonNull
    @ValueOfEnum(enumClass = RateType.class)
    private String rateType;
    @NonNull
    private String branchName;
    @NonNull
    private String itemName;
    @NonNull
    private String version;
    @NonNull
    @Min(value = 0, message = "Original price must be non-negative")
    private Double originalPrice;
}
