package com.example.spring_study.repository;

import com.example.spring_study.logging.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<LogEntry, Integer> {
}
