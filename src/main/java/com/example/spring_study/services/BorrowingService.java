package com.example.spring_study.services;

import com.example.spring_study.constant.Type;
import com.example.spring_study.model.payload.*;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface BorrowingService {
    public BorrowingResponse createBorrowing(BorrowingRequest request);

    public BorrowingResponse updateBorrowing(int id, BorrowingRequest request);

    public boolean deleteBorrowing(int id);

    public BorrowingResponse getBorrowingById(int id);

    public Page<BorrowingResponse> getAllBorrowing(BaseSearchRequest request);

    public Page<BorrowingResponse> getBorrowingsSortedBy(BorrowingSortRequest request);

    public Page<BorrowingResponse> findByDeviceName(String name, BaseSearchRequest request);

    public Page<BorrowingResponse> findByHandOverDate(LocalDateTime startDate, LocalDateTime endDate, BaseSearchRequest request);

    public Page<BorrowingResponse> findByDeviceType(Type type, BaseSearchRequest request);

    public Page<BorrowingResponse> findByTotalPrice(double totalPrice, BaseSearchRequest request);

    public List<BorrowingResponse> transferDevice(int borrowingIdFrom, int borrowingIdTo, int deviceId);
}
