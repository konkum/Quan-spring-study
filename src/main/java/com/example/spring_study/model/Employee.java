package com.example.spring_study.model;

import com.example.spring_study.validator.ValidPhoneNumber;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@Table(name = "employee")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NonNull
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String userName;
    @NonNull
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    @NonNull
    private String fullName;
    @NonNull
    private String address;
    @NonNull
    @ValidPhoneNumber
    private String phoneNumber;
    @NonNull
    @Min(value = 0, message = "Account ballance cannot be null")
    private Double accountBalance;
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "employee_roles",
            joinColumns = @JoinColumn(name = "employee_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private Set<Role> roles;

    public Employee(String fullName, String address, String phoneNumber, Double accountBalance) {
        this.fullName = fullName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.accountBalance = accountBalance;
    }
}
