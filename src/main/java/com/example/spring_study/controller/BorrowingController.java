package com.example.spring_study.controller;

import com.example.spring_study.constant.ProjectRole;
import com.example.spring_study.constant.Type;
import com.example.spring_study.model.Borrowing;
import com.example.spring_study.model.payload.BaseSearchRequest;
import com.example.spring_study.model.payload.BaseSortRequest;
import com.example.spring_study.model.payload.BorrowingRequest;
import com.example.spring_study.service.BorrowingService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.security.authorization.AuthorityAuthorizationManager.hasRole;

@AllArgsConstructor
@RestController
@RequestMapping(path = "api/v1/borrowing")
public class BorrowingController {
    private BorrowingService borrowingService;

    @PreAuthorize(ProjectRole.USER)
    @GetMapping(path = "/get")
    private ResponseEntity<Borrowing> getBorrowingById(@Param("id") int id) {
        Borrowing borrowing = borrowingService.getBorrowingById(id);
        if (borrowing == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(borrowing);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping(path = "/getAll")
    private ResponseEntity<Page<Borrowing>> getAllBorrowings(@Valid @ModelAttribute BaseSearchRequest request) {
        Page<Borrowing> borrowings = borrowingService.getAllBorrowing(request);
        if (borrowings == null || borrowings.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(borrowings);
    }

    @PreAuthorize(ProjectRole.USER)
    @GetMapping(path = "/getBorrowingsSortedBy")
    private ResponseEntity<Page<Borrowing>> getBorrowingsSortedBy(@Valid @ModelAttribute BaseSortRequest request) {
        Page<Borrowing> borrowings = borrowingService.getBorrowingsSortedBy(request);
        if (borrowings == null || borrowings.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(borrowings);
    }

    @PreAuthorize(ProjectRole.ADMIN)
    @DeleteMapping(path = "/delete")
    private ResponseEntity deleteBorrowing(@Param("id") int id) {
        boolean canDelete = borrowingService.deleteBorrowing(id);
        if (!canDelete) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }

    @PreAuthorize(ProjectRole.ADMIN)
    @PostMapping(path = "/create")
    private ResponseEntity<Borrowing> createBorrowing(@Valid @RequestBody BorrowingRequest request) {
        Borrowing borrowing = borrowingService.createBorrowing(request);
        if (borrowing == null) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        return ResponseEntity.ok().body(borrowing);
    }

    @PreAuthorize(ProjectRole.ADMIN)
    @PutMapping(path = "/update")
    private ResponseEntity<Borrowing> updateBorrowing(@Param("id") int id, @Valid @RequestBody BorrowingRequest request) {
        Borrowing borrowing = borrowingService.updateBorrowing(id, request);
        if (borrowing == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(borrowing);
    }

    @PreAuthorize(ProjectRole.USER)
    @GetMapping(path = "/findByItemName")
    private ResponseEntity<Page<Borrowing>> findByItemName(@Param("itemName") String itemName, @Valid @ModelAttribute BaseSearchRequest pageable) {
        Page<Borrowing> borrowings = borrowingService.findByDeviceName(itemName, pageable);
        if (borrowings.isEmpty() || borrowings == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(borrowings);
    }

    @PreAuthorize(ProjectRole.USER)
    @GetMapping(path = "/findByHandOverDate")
    private ResponseEntity<Page<Borrowing>> findByHandOverDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime startDate,
                                                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime endDate,
                                                               @Valid @ModelAttribute BaseSearchRequest pageable) {
        Page<Borrowing> borrowings = borrowingService.findByHandOverDate(startDate, endDate, pageable);
        if (borrowings.isEmpty() || borrowings == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(borrowings);
    }

    @PreAuthorize(ProjectRole.USER)
    @GetMapping(path = "/findByItemType")
    private ResponseEntity<Page<Borrowing>> findByItemType(@Param("type") Type type, @Valid @ModelAttribute BaseSearchRequest pageable) {
        Page<Borrowing> borrowings = borrowingService.findByDeviceType(type, pageable);
        if (borrowings.isEmpty() || borrowings == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(borrowings);
    }

    @PreAuthorize(ProjectRole.USER)
    @GetMapping(path = "/findByTotalPrice")
    private ResponseEntity<Page<Borrowing>> findByTotalPrice(@Param("totalPrice") double totalPrice, @Valid @ModelAttribute BaseSearchRequest pageable) {
        Page<Borrowing> borrowings = borrowingService.findByTotalPrice(totalPrice, pageable);
        if (borrowings.isEmpty() || borrowings == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(borrowings);
    }

    @PreAuthorize(ProjectRole.USER)
    @GetMapping(path = "/transferDevice")
    private ResponseEntity<List<Borrowing>> transferDevice(@Param("borrowingIdFrom") int borrowingIdFrom,
                                                           @Param("borrowingIdTo") int borrowingIdTo,
                                                           @Param("deviceId") int deviceId) {
        List<Borrowing> borrowings = borrowingService.transferDevice(borrowingIdFrom, borrowingIdTo, deviceId);
        if (borrowings.isEmpty() || borrowings == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(borrowings);
    }
}
