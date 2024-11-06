package com.example.spring_study.controllers;

import com.example.spring_study.constant.RateType;
import com.example.spring_study.constant.Type;
import com.example.spring_study.model.Device;
import com.example.spring_study.model.payload.BaseSearchRequest;
import com.example.spring_study.model.payload.DeviceRequest;
import com.example.spring_study.model.payload.DeviceSortRequest;
import com.example.spring_study.services.DeviceService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping(path = "/api/v1/device")
public class DeviceController {
    private DeviceService deviceService;

    @Autowired
    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @PostMapping(path = "/create")
    public ResponseEntity<Device> createDevice(@Valid @RequestBody DeviceRequest request) {
        Device device = deviceService.createDevice(request);
        if (device == null) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        return ResponseEntity.ok(device);
    }

    @GetMapping(path = "/get")
    public ResponseEntity<Device> getDeviceById(@Param("id") int id) {
        Device device = deviceService.getDeviceById(id);
        if (device == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(device);
    }

    @GetMapping(path = "/getAll")
    public ResponseEntity<Page<Device>> getDevices(@Valid @ModelAttribute BaseSearchRequest request) {
        Page<Device> devices = deviceService.getAllDevices(request);
        if (devices == null || devices.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(devices);
    }

    @GetMapping(path = "/getDevicesSortBy")
    public ResponseEntity<Page<Device>> getDevicesSortBy(@Valid @ModelAttribute DeviceSortRequest request) {
        Page<Device> devices = deviceService.getDevicesSortedBy(request);
        if (devices == null || devices.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(devices);
    }

    @GetMapping(path = "/findByItemName")
    public ResponseEntity<Page<Device>> findByItemName(@Param("item name") String itemName, @Valid @ModelAttribute BaseSearchRequest pageable) {
        Page<Device> devices = deviceService.findDeviceByItemName(itemName, pageable);
        if (devices.isEmpty() || devices == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(devices);
    }

    @GetMapping(path = "/findByCreatedDate")
    public ResponseEntity<Page<Device>> findByCreatedDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime startDate,
                                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime endDate,
                                                          @Valid @ModelAttribute BaseSearchRequest pageable) {
        Page<Device> devices = deviceService.findDeviceByDate(startDate, endDate, pageable);
        if (devices.isEmpty() || devices == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(devices);
    }

    @GetMapping(path = "/findByType")
    public ResponseEntity<Page<Device>> findByType(@Param("type") Type type, @Valid @ModelAttribute BaseSearchRequest pageable) {
        Page<Device> devices = deviceService.findDeviceByType(type, pageable);
        if (devices.isEmpty() || devices == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(devices);
    }

    @GetMapping(path = "/findByRateType")
    public ResponseEntity<Page<Device>> findByRateType(@Param("rate type") RateType rateType, @Valid @ModelAttribute BaseSearchRequest pageable) {
        Page<Device> devices = deviceService.findDeviceByRateType(rateType, pageable);
        if (devices.isEmpty() || devices == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(devices);
    }

    @PutMapping(path = "/update")
    public ResponseEntity<Device> updateDevice(@Param("id") int id, @Valid @RequestBody DeviceRequest request) {
        Device device = deviceService.updateDevice(id, request);
        if (device == null) {
            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        }

        return ResponseEntity.ok(device);
    }

    @DeleteMapping(path = "/delete")
    public ResponseEntity deleteDevice(@Param("id") int id) {
        boolean canDelete = deviceService.deleteDevice(id);
        if (!canDelete) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }
}
