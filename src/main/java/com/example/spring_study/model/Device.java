package com.example.spring_study.model;

import com.example.spring_study.constant.RateType;
import com.example.spring_study.constant.Type;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@Entity
@ToString
@Table(name = "device")
@EntityListeners(AuditingEntityListener.class)
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NonNull
    private Type type;
    @NonNull
    @Min(value = 0, message = "Unit price must be non-negative")
    private Double unitPrice;
    @NonNull
    private RateType rateType;
    @Embedded
    private DateAudit dateAudit;
    @NonNull
    private String branchName;
    @NonNull
    private String itemName;
    private String version;
    @NonNull
    @Min(value = 0, message = "Original price must be non-negative")
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

    public Device() {
        dateAudit = new DateAudit();
    }

    public double calculateTotalPrice() {
        return this.originalPrice * this.rateType.getValue();
    }
}
