package com.example.spring_study.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Embeddable
public class DateAudit {
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime handOverDate;
    private LocalDateTime evictionDate;

    public DateAudit(){
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    public void updateUpdatedAt(){
        updatedAt = LocalDateTime.now();
    }

    public void updateHandOverDate(){
        handOverDate = LocalDateTime.now();
    }
}
