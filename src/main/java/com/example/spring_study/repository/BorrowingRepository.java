package com.example.spring_study.repository;

import com.example.spring_study.constant.Type;
import com.example.spring_study.model.Borrowing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface BorrowingRepository extends JpaRepository<Borrowing,Integer> {
    @Query("SELECT b FROM Borrowing b JOIN b.devices d WHERE d.itemName = :itemName")
    Page<Borrowing> findByDeviceName(@Param("itemName")String itemName, Pageable pageable);
    Page<Borrowing> findByDateAudit_HandOverDateBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    @Query("SELECT b FROM Borrowing b JOIN b.devices d WHERE d.type = :type")
    Page<Borrowing> findByDeviceType(@Param("type")Type type,Pageable pageable);
    Page<Borrowing> findByTotalPrice(double totalPrice,Pageable pageable);
}
