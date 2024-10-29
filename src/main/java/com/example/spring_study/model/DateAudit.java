package com.example.spring_study.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Embeddable
@NoArgsConstructor
public class DateAudit {
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    private LocalDateTime handOverDate;
    private LocalDateTime evictionDate;

    public void updateHandOverDate() {
        handOverDate = LocalDateTime.now();
    }
}
