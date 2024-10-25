package com.example.spring_study.repository;


import com.example.spring_study.constant.RateType;
import com.example.spring_study.constant.Type;
import com.example.spring_study.model.Device;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface DeviceRepository extends JpaRepository<Device,Integer> {
    Page<Device> findByItemName(String itemName, Pageable pageable);
    Page<Device> findByDateAudit_CreatedAtBetween(LocalDateTime startDateTime,LocalDateTime endDateTime,Pageable pageable);
    Page<Device> findByType(Type type,Pageable pageable);
    Page<Device> findByRateType(RateType rateType,Pageable pageable);
}
