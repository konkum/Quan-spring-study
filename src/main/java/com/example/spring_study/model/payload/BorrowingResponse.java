package com.example.spring_study.model.payload;


import com.example.spring_study.model.DateAudit;
import com.example.spring_study.model.Device;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BorrowingResponse {
    private Integer id;
    private DateAudit dateAudit;
    @NonNull
    @Min(value = 0, message = "Total Price have to be a non negative number")
    private Double totalPrice;
    @NonNull
    private EmployeeResponse employee;
    @NonNull
    private List<Device> devices;

    public BorrowingResponse(Integer id, DateAudit dateAudit, EmployeeResponse employee, List<Device> devices) {
        this.id = id;
        this.dateAudit = dateAudit;
        this.employee = employee;
        this.devices = devices;
        updateTotalPrice();
    }


    public void updateTotalPrice() {
        this.totalPrice = devices.stream().mapToDouble(Device::calculateTotalPrice).sum();
    }
}
