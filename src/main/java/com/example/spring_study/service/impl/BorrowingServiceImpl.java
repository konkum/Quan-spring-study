package com.example.spring_study.service.impl;

import com.example.spring_study.constant.Type;
import com.example.spring_study.model.Borrowing;
import com.example.spring_study.model.Device;
import com.example.spring_study.model.Employee;
import com.example.spring_study.model.payload.BaseSearchRequest;
import com.example.spring_study.model.payload.BaseSortRequest;
import com.example.spring_study.model.payload.BorrowingRequest;
import com.example.spring_study.repository.BorrowingRepository;
import com.example.spring_study.repository.DeviceRepository;
import com.example.spring_study.repository.EmployeeRepository;
import com.example.spring_study.service.BorrowingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class BorrowingServiceImpl implements BorrowingService {
    @Autowired
    private BorrowingRepository borrowingRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private DeviceRepository deviceRepository;

    @Override
    public Borrowing createBorrowing(BorrowingRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId()).orElse(null);
        if (employee==null){
            return null;
        }

        List<Device> devices = new ArrayList<>();

        for (int deviceId : request.getDevicesId()){
            Device device = deviceRepository.findById(deviceId).orElse(null);
            if (device == null){
                continue;
            }

            devices.add(device);
        }

        if (devices.isEmpty()){
            return null;
        }

        Borrowing borrowing = new Borrowing(employee,devices);
        return borrowingRepository.save(borrowing);
    }

    @Override
    public Borrowing updateBorrowing(int id, BorrowingRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId()).orElse(null);
        if (employee==null){
            return null;
        }

        List<Device> devices = new ArrayList<>();

        for (int deviceId : request.getDevicesId()){
            Device device = deviceRepository.findById(deviceId).orElse(null);
            if (device == null){
                continue;
            }

            devices.add(device);
        }

        if (devices.isEmpty()){
            return null;
        }

        Borrowing borrowing = getBorrowingById(id);
        if (borrowing == null){
            return null;
        }

        borrowing.setEmployee(employee);
        borrowing.setDevices(devices);
        borrowing.getDateAudit().updateUpdatedAt();
        borrowing.getDateAudit().updateHandOverDate();
        borrowing.updateTotalPrice();

        return borrowingRepository.save(borrowing);
    }

    @Override
    public boolean deleteBorrowing(int id) {
        if (!borrowingRepository.existsById(id)){
            return false;
        }
        borrowingRepository.deleteById(id);
        return true;
    }

    @Override
    public Borrowing getBorrowingById(int id) {
        return borrowingRepository.findById(id).orElse(null);
    }

    @Override
    public Page<Borrowing> getAllBorrowing(BaseSearchRequest request) {
        Pageable pageable = PageRequest.of(request.getPageNumber(),request.getPageSize());
        return borrowingRepository.findAll(pageable);
    }

    @Override
    public Page<Borrowing> getBorrowingsSortedBy(BaseSortRequest request) {
        Sort sort = Sort.by(Sort.Order.asc(request.getSortString()));
        if (request.getSortDirection()!=null){
            switch (request.getSortDirection()){
                case ASC -> sort = Sort.by(Sort.Order.asc(request.getSortString()));
                case DESC -> sort = Sort.by(Sort.Order.desc(request.getSortString()));
            }
        }
        Pageable pageable = PageRequest.of(request.getPageNumber(),request.getPageSize());
        return borrowingRepository.findAll(pageable);
    }

    @Override
    public Page<Borrowing> findByDeviceName(String name, BaseSearchRequest request) {
        Pageable pageable = PageRequest.of(request.getPageNumber(),request.getPageSize());
        Page<Borrowing> borrowings = borrowingRepository.findByDeviceName(name,pageable);
        if (borrowings.isEmpty()){
            return null;
        }

        return borrowings;
    }

    @Override
    public Page<Borrowing> findByHandOverDate(LocalDateTime startDate, LocalDateTime endDate, BaseSearchRequest request) {
        Pageable pageable = PageRequest.of(request.getPageNumber(),request.getPageSize());
        Page<Borrowing> borrowings = borrowingRepository.findByDateAudit_HandOverDateBetween(startDate,endDate,pageable);
        if (borrowings.isEmpty()){
            return null;
        }

        return borrowings;
    }

    @Override
    public Page<Borrowing> findByDeviceType(Type type, BaseSearchRequest request) {
        Pageable pageable = PageRequest.of(request.getPageNumber(),request.getPageSize());
        Page<Borrowing> borrowings = borrowingRepository.findByDeviceType(type,pageable);
        if (borrowings.isEmpty()){
            return null;
        }

        return borrowings;
    }

    @Override
    public Page<Borrowing> findByTotalPrice(double totalPrice, BaseSearchRequest request) {
        Pageable pageable = PageRequest.of(request.getPageNumber(),request.getPageSize());
        Page<Borrowing> borrowings = borrowingRepository.findByTotalPrice(totalPrice,pageable);
        if (borrowings.isEmpty()){
            return null;
        }

        return borrowings;
    }

    @Override
    public List<Borrowing> transferDevice(int borrowingIdFrom, int borrowingIdTo, int deviceId) {
        Borrowing existingBorrowing = getBorrowingById(borrowingIdFrom);
        if (existingBorrowing == null){
            return Collections.emptyList();
        }
        Device existingDevice = existingBorrowing.getDevices().stream().filter(device -> device.getId() == deviceId)
                .findFirst().orElse(null);

        if (existingDevice==null){
            return Collections.emptyList();
        }

        existingBorrowing.removeDevice(existingDevice);

        Borrowing newBorrowing = getBorrowingById(borrowingIdTo);
        if (newBorrowing == null) {
            return Collections.emptyList();
        }

        newBorrowing.addDevice(existingDevice);

        existingBorrowing.updateTotalPrice();
        existingBorrowing.getDateAudit().updateUpdatedAt();

        newBorrowing.updateTotalPrice();
        newBorrowing.getDateAudit().updateUpdatedAt();

        borrowingRepository.save(existingBorrowing);
        borrowingRepository.save(newBorrowing);
        List<Borrowing> borrowings = List.of(existingBorrowing,newBorrowing);
        return borrowings;
    }
}
