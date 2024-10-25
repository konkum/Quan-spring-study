package com.example.spring_study.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Entity
@NoArgsConstructor
@Table(name = "employee")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String fullName;
    private String address;
    private String phoneNumber;
    private Double accountBalance;

    public Employee(String fullName, String address, String phoneNumber, Double accountBalance) {
        this.fullName = fullName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.accountBalance = accountBalance;
    }
}
