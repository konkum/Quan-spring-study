package com.example.spring_study.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "borrowing")
public class Borrowing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private DateAudit dateAudit;
    private Double totalPrice;
    @OneToOne
    private Employee employee;
    @OneToMany
    private List<Device> devices;

    public Borrowing(Employee employee, List<Device> devices) {
        this.employee = employee;
        this.devices = devices;
        this.dateAudit = new DateAudit();
        this.dateAudit.updateHandOverDate();
        updateTotalPrice();
    }

    public void updateTotalPrice() {
        this.totalPrice= devices.stream().mapToDouble(Device::calculateTotalPrice).sum();
    }

    public void removeDevice(Device device){
        devices.remove(device);
    }

    public void addDevice(Device device){
        devices.add(device);
    }
}
