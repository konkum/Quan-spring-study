package com.example.spring_study.model;

import com.example.spring_study.constant.RateType;
import com.example.spring_study.constant.Type;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

import java.time.LocalDate;

@Data
@Entity
@ToString
@Table(name = "device")
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Type type;
    private Double unitPrice;
    private RateType rateType;
    private DateAudit dateAudit;
    private String branchName;
    private String itemName;
    private String version;
    private Double originalPrice;

    public Device(Type type, Double unitPrice, RateType rateType, String branchName, String itemName, String version, Double originalPrice) {
        this.type = type;
        this.unitPrice = unitPrice;
        this.rateType = rateType;
        this.branchName = branchName;
        this.dateAudit = new DateAudit();
        this.itemName = itemName;
        this.version = version;
        this.originalPrice = originalPrice;
    }

    public Device(){
        dateAudit = new DateAudit();
    }

    public double calculateTotalPrice() {
        return this.originalPrice * this.rateType.getValue();
    }
}
