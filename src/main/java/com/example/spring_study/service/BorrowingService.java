package com.example.spring_study.service;

import com.example.spring_study.constant.Type;
import com.example.spring_study.model.Borrowing;
import com.example.spring_study.model.payload.BaseSearchRequest;
import com.example.spring_study.model.payload.BaseSortRequest;
import com.example.spring_study.model.payload.BorrowingRequest;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface BorrowingService {
    public Borrowing createBorrowing(BorrowingRequest request);
    public Borrowing updateBorrowing(int id,BorrowingRequest request);
    public boolean deleteBorrowing(int id);
    public Borrowing getBorrowingById(int id);
    public Page<Borrowing> getAllBorrowing(BaseSearchRequest request);
    public Page<Borrowing> getBorrowingsSortedBy(BaseSortRequest request);
    public Page<Borrowing> findByDeviceName(String name,BaseSearchRequest request);
    public Page<Borrowing> findByHandOverDate(LocalDateTime startDate, LocalDateTime endDate, BaseSearchRequest request);
    public Page<Borrowing> findByDeviceType(Type type,BaseSearchRequest request);
    public Page<Borrowing> findByTotalPrice(double totalPrice, BaseSearchRequest request);
    public List<Borrowing> transferDevice(int borrowingIdFrom, int borrowingIdTo,int deviceId);
}
