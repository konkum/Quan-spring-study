package com.example.spring_study.controllers;

import com.example.spring_study.constant.Type;
import com.example.spring_study.model.payload.*;
import com.example.spring_study.services.BorrowingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "api/v1/borrowing")
public class BorrowingController {
    @Autowired
    private BorrowingService borrowingService;

    @GetMapping(path = "/get")
    private ResponseEntity<BorrowingResponse> getBorrowingById(@Param("id") int id) {
        BorrowingResponse borrowing = borrowingService.getBorrowingById(id);
        if (borrowing == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().body(borrowing);
    }

    @GetMapping(path = "/getAll")
    private ResponseEntity<Page<BorrowingResponse>> getAllBorrowings(@Valid @ModelAttribute BaseSearchRequest request) {
        Page<BorrowingResponse> borrowings = borrowingService.getAllBorrowing(request);
        if (borrowings == null || borrowings.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(borrowings);
    }

    @GetMapping(path = "/getBorrowingsSortedBy")
    private ResponseEntity<Page<BorrowingResponse>> getBorrowingsSortedBy(@Valid @ModelAttribute BorrowingSortRequest request) {
        Page<BorrowingResponse> borrowings = borrowingService.getBorrowingsSortedBy(request);
        if (borrowings == null || borrowings.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(borrowings);
    }

    @DeleteMapping(path = "/delete")
    private ResponseEntity deleteBorrowing(@Param("id") int id) {
        boolean canDelete = borrowingService.deleteBorrowing(id);
        if (!canDelete) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/create")
    private ResponseEntity<BorrowingResponse> createBorrowing(@Valid @RequestBody BorrowingRequest request) {
        BorrowingResponse borrowing = borrowingService.createBorrowing(request);
        if (borrowing == null) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        return ResponseEntity.ok().body(borrowing);
    }

    @PutMapping(path = "/update")
    private ResponseEntity<BorrowingResponse> updateBorrowing(@Param("id") int id, @Valid @RequestBody BorrowingRequest request) {
        BorrowingResponse borrowing = borrowingService.updateBorrowing(id, request);
        if (borrowing == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(borrowing);
    }

    @GetMapping(path = "/findByItemName")
    private ResponseEntity<Page<BorrowingResponse>> findByItemName(@Param("itemName") String itemName, @Valid @ModelAttribute BaseSearchRequest pageable) {
        Page<BorrowingResponse> borrowings = borrowingService.findByDeviceName(itemName, pageable);
        if (borrowings.isEmpty() || borrowings == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(borrowings);
    }

    @GetMapping(path = "/findByHandOverDate")
    private ResponseEntity<Page<BorrowingResponse>> findByHandOverDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime startDate,
                                                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime endDate,
                                                                       @Valid @ModelAttribute BaseSearchRequest pageable) {
        Page<BorrowingResponse> borrowings = borrowingService.findByHandOverDate(startDate, endDate, pageable);
        if (borrowings.isEmpty() || borrowings == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(borrowings);
    }

    @GetMapping(path = "/findByItemType")
    private ResponseEntity<Page<BorrowingResponse>> findByItemType(@Param("type") Type type, @Valid @ModelAttribute BaseSearchRequest pageable) {
        Page<BorrowingResponse> borrowings = borrowingService.findByDeviceType(type, pageable);
        if (borrowings.isEmpty() || borrowings == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(borrowings);
    }

    @GetMapping(path = "/findByTotalPrice")
    private ResponseEntity<Page<BorrowingResponse>> findByTotalPrice(@Param("totalPrice") double totalPrice, @Valid @ModelAttribute BaseSearchRequest pageable) {
        Page<BorrowingResponse> borrowings = borrowingService.findByTotalPrice(totalPrice, pageable);
        if (borrowings.isEmpty() || borrowings == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(borrowings);
    }

    @GetMapping(path = "/transferDevice")
    private ResponseEntity<List<BorrowingResponse>> transferDevice(@Param("borrowingIdFrom") int borrowingIdFrom,
                                                                   @Param("borrowingIdTo") int borrowingIdTo,
                                                                   @Param("deviceId") int deviceId) {
        List<BorrowingResponse> borrowings = borrowingService.transferDevice(borrowingIdFrom, borrowingIdTo, deviceId);
        if (borrowings.isEmpty() || borrowings == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(borrowings);
    }
}
