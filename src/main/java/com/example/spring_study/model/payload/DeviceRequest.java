package com.example.spring_study.model.payload;

import com.example.spring_study.constant.RateType;
import com.example.spring_study.constant.Type;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DeviceRequest {
    @NotNull
    private String type;
    @NotNull
    private Double unitPrice;
    @NotNull
    private String rateType;
    @NotNull
    private String branchName;
    @NotNull
    private String itemName;
    @NotNull
    private String version;
    @NotNull
    private Double originalPrice;
}
