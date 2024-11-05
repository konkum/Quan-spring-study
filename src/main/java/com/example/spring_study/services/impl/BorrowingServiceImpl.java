package com.example.spring_study.services.impl;

import com.example.spring_study.constant.SortParam;
import com.example.spring_study.constant.Type;
import com.example.spring_study.exception.BorrowingNotFoundException;
import com.example.spring_study.exception.DeviceNotFoundException;
import com.example.spring_study.exception.EmployeeNotFoundException;
import com.example.spring_study.mapping.BorrowingMapper;
import com.example.spring_study.model.Borrowing;
import com.example.spring_study.model.Device;
import com.example.spring_study.model.Employee;
import com.example.spring_study.model.payload.*;
import com.example.spring_study.repository.BorrowingRepository;
import com.example.spring_study.repository.DeviceRepository;
import com.example.spring_study.repository.EmployeeRepository;
import com.example.spring_study.services.BorrowingService;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class BorrowingServiceImpl implements BorrowingService {
    @Autowired
    private BorrowingRepository borrowingRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private BorrowingMapper mapper;

    @Override
    public BorrowingResponse createBorrowing(BorrowingRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> {
                    log.error("Employee not found with id: {}", request.getEmployeeId());
                    return new EmployeeNotFoundException(request.getEmployeeId());
                });
        if (employee == null) {
            return null;
        }

        List<Device> devices = new ArrayList<>();

        for (int deviceId : request.getDevicesId()) {
            Device device = deviceRepository.findById(deviceId)
                    .orElseThrow(() -> {
                        log.error("Device not found with id: {}", deviceId);
                        return new DeviceNotFoundException(deviceId);
                    });
            if (device == null) {
                continue;
            }

            devices.add(device);
        }

        if (devices.isEmpty()) {
            return null;
        }

        Borrowing borrowing = new Borrowing(employee, devices);
        BorrowingResponse response = mapper.borrowingToBorrowingResponse(borrowingRepository.save(borrowing));
        return response;
    }

    @Override
    public BorrowingResponse updateBorrowing(int id, BorrowingRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> {
                    log.error("Employee not found with id: {}", request.getEmployeeId());
                    return new EmployeeNotFoundException(request.getEmployeeId());
                });
        if (employee == null) {
            return null;
        }

        List<Device> devices = new ArrayList<>();

        for (int deviceId : request.getDevicesId()) {
            Device device = deviceRepository.findById(deviceId)
                    .orElseThrow(() -> {
                        log.error("Device not found with id: {}", deviceId);
                        return new DeviceNotFoundException(deviceId);
                    });
            if (device == null) {
                continue;
            }

            devices.add(device);
        }

        if (devices.isEmpty()) {
            return null;
        }

        Borrowing borrowing = borrowingRepository.findById(id).orElseThrow(() -> {
            log.error("Borrowing not found with id: {}", id);
            return new BorrowingNotFoundException(id);
        });
        if (borrowing == null) {
            return null;
        }

        borrowing.setEmployee(employee);
        borrowing.setDevices(devices);
        borrowing.getDateAudit().updateHandOverDate();
        borrowing.updateTotalPrice();

        return mapper.borrowingToBorrowingResponse(borrowingRepository.save(borrowing));
    }

    @Override
    public boolean deleteBorrowing(int id) {
        if (!borrowingRepository.existsById(id)) {
            log.error("Borrowing not found with id: {}", id);
            return false;
        }
        borrowingRepository.deleteById(id);
        return true;
    }

    @Override
    public BorrowingResponse getBorrowingById(int id) {
        Borrowing borrowing = borrowingRepository.findById(id).orElseThrow(() -> {
            log.error("Borrowing not found with id: {}", id);
            return new BorrowingNotFoundException(id);
        });
        return mapper.borrowingToBorrowingResponse(borrowing);
    }

    @Override
    public Page<BorrowingResponse> getAllBorrowing(BaseSearchRequest request) {
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize());
        return mapper.borrowingPageToResponsePage(borrowingRepository.findAll(pageable));
    }

    @Override
    public Page<BorrowingResponse> getBorrowingsSortedBy(BorrowingSortRequest request) {
        Sort sort = Sort.by(Sort.Order.asc(request.getSortString()));
        SortParam sortParam = SortParam.valueOf(request.getSortDirection());
        if (sortParam != null) {
            switch (sortParam) {
                case ASC -> sort = Sort.by(Sort.Order.asc(request.getSortString()));
                case DESC -> sort = Sort.by(Sort.Order.desc(request.getSortString()));
            }
        }
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize());
        return mapper.borrowingPageToResponsePage(borrowingRepository.findAll(pageable));
    }

    @Override
    public Page<BorrowingResponse> findByDeviceName(String name, BaseSearchRequest request) {
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize());
        Page<Borrowing> borrowings = borrowingRepository.findByDeviceName(name, pageable);
        if (borrowings.isEmpty()) {
            log.error("Cannot find borrowing with the name or the borrowing you looking for does not exist");
            return null;
        }

        return mapper.borrowingPageToResponsePage(borrowings);
    }

    @Override
    public Page<BorrowingResponse> findByHandOverDate(LocalDateTime startDate, LocalDateTime endDate, BaseSearchRequest request) {
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize());
        Page<Borrowing> borrowings = borrowingRepository.findByDateAudit_HandOverDateBetween(startDate, endDate, pageable);
        if (borrowings.isEmpty()) {
            log.error("Cannot find borrowing with the startDate and endDate or the borrowing you looking for does not exist");
            return null;
        }

        return mapper.borrowingPageToResponsePage(borrowings);
    }

    @Override
    public Page<BorrowingResponse> findByDeviceType(Type type, BaseSearchRequest request) {
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize());
        Page<Borrowing> borrowings = borrowingRepository.findByDeviceType(type, pageable);
        if (borrowings.isEmpty()) {
            log.error("Cannot find borrowing with the type given or the borrowing you looking for does not exist");
            return null;
        }

        return mapper.borrowingPageToResponsePage(borrowings);
    }

    @Override
    public Page<BorrowingResponse> findByTotalPrice(double totalPrice, BaseSearchRequest request) {
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize());
        Page<Borrowing> borrowings = borrowingRepository.findByTotalPrice(totalPrice, pageable);
        if (borrowings.isEmpty()) {
            log.error("Cannot find borrowing with the type given or the borrowing you looking for does not exist");
            return null;
        }

        return mapper.borrowingPageToResponsePage(borrowings);
    }

    @Override
    public List<BorrowingResponse> transferDevice(int borrowingIdFrom, int borrowingIdTo, int deviceId) {
        Borrowing existingBorrowing = borrowingRepository.findById(borrowingIdFrom)
                .orElseThrow(() -> new BorrowingNotFoundException(borrowingIdFrom));
        if (existingBorrowing == null) {
            log.error("There is no borrowing with id: {}", borrowingIdFrom);
            return Collections.emptyList();
        }
        Device existingDevice = existingBorrowing.getDevices().stream().filter(device -> device.getId() == deviceId)
                .findFirst().orElse(null);

        if (existingDevice == null) {
            log.error("There is no device with id: {}", deviceId);
            return Collections.emptyList();
        }

        existingBorrowing.removeDevice(existingDevice);

        Borrowing newBorrowing = borrowingRepository.findById(borrowingIdTo)
                .orElseThrow(() -> new BorrowingNotFoundException(borrowingIdTo));
        if (newBorrowing == null) {
            log.error("There is no borrowing with id: {}", borrowingIdTo);
            return Collections.emptyList();
        }

        newBorrowing.addDevice(existingDevice);

        existingBorrowing.updateTotalPrice();

        newBorrowing.updateTotalPrice();

        borrowingRepository.save(existingBorrowing);
        borrowingRepository.save(newBorrowing);
        List<Borrowing> borrowings = List.of(existingBorrowing, newBorrowing);
        return mapper.borrowingsToResponses(borrowings);
    }
}
