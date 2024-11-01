package com.example.spring_study.repository;

import com.example.spring_study.constant.Type;
import com.example.spring_study.model.Borrowing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface BorrowingRepository extends JpaRepository<Borrowing, Integer> {
    //@Query("SELECT b FROM Borrowing b JOIN b.devices d WHERE d.itemName = :itemName")
    @Query(value = "SELECT b.* FROM borrowing b " +
            "JOIN borrowing_devices bd ON b.id = bd.borrowing_id " +
            "JOIN device d ON bd.devices_id = d.id " +
            "WHERE d.item_name = :itemName", nativeQuery = true)
    Page<Borrowing> findByDeviceName(@Param("itemName") String itemName, Pageable pageable);

    Page<Borrowing> findByDateAudit_HandOverDateBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    @Query(value = "SELECT b.* FROM borrowing b " +
            "JOIN borrowing_devices bd ON b.id = bd.borrowing_id " +
            "JOIN device d ON bd.devices_id = d.id " +
            "WHERE d.type = :type", nativeQuery = true)
    Page<Borrowing> findByDeviceType(@Param("type") Type type, Pageable pageable);

    Page<Borrowing> findByTotalPrice(double totalPrice, Pageable pageable);
}
