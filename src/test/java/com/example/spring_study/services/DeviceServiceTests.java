package com.example.spring_study.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.spring_study.constant.RateType;
import com.example.spring_study.constant.Type;
import com.example.spring_study.exception.DeviceNotFoundException;
import com.example.spring_study.model.DateAudit;
import com.example.spring_study.model.Device;
import com.example.spring_study.model.payload.BaseSearchRequest;
import com.example.spring_study.model.payload.DeviceRequest;
import com.example.spring_study.model.payload.DeviceSortRequest;
import com.example.spring_study.repository.DeviceRepository;
import com.example.spring_study.services.impl.DeviceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class DeviceServiceTests {
    @Mock
    private DeviceRepository deviceRepository;
    @InjectMocks
    private DeviceServiceImpl deviceService;
    private Device device;
    private Device device2;
    private Clock fixedClock;
    private DeviceRequest request;
    private DateAudit dateAudit;

    @BeforeEach
    void setUp() {
        // Set a fixed clock time
        fixedClock = Clock.fixed(Instant.parse("2024-10-23T17:04:11.983812500Z"), ZoneId.systemDefault());

        // Setup the Device with fixed DateAudit
        dateAudit = new DateAudit();
        dateAudit.setCreatedAt(LocalDateTime.now(fixedClock).minusDays(1));

        device = new Device();
        device.setId(1);
        device.setType(Type.MOUSE);
        device.setUnitPrice(110.0);
        device.setRateType(RateType.LIKENEW);
        device.setBranchName("Branch 1");
        device.setItemName("Item 1");
        device.setVersion("1.0");
        device.setOriginalPrice(150.0);
        device.setDateAudit(dateAudit);

        DateAudit dateAudit1;
        dateAudit1 = new DateAudit();
        dateAudit1.setCreatedAt(LocalDateTime.now(fixedClock));

        device2 = new Device();
        device2.setId(2);
        device2.setType(Type.CASE);
        device2.setUnitPrice(130.0);
        device2.setRateType(RateType.NEW);
        device2.setBranchName("Branch 2");
        device2.setItemName("Item 2");
        device2.setVersion("1.1");
        device2.setOriginalPrice(170.0);
        device2.setDateAudit(dateAudit1);

        request = new DeviceRequest();
        request.setType("MOUSE");
        request.setUnitPrice(110.0);
        request.setRateType("LIKENEW");
        request.setBranchName("Branch 1");
        request.setItemName("Item 1");
        request.setVersion("1.0");
        request.setOriginalPrice(150.0);
    }

    @Test
    void testCreateDevice() {
        // Arrange
        when(deviceRepository.save(any(Device.class))).thenReturn(device);

        // Act
        Device createdDevice = deviceService.createDevice(request);
        createdDevice.setDateAudit(dateAudit);

        // Assert
        assertNotNull(createdDevice);
        assertEquals(Type.MOUSE, createdDevice.getType());
        assertEquals(LocalDateTime.now(fixedClock).minusDays(1), createdDevice.getDateAudit().getCreatedAt());
        verify(deviceRepository, times(1)).save(any(Device.class));
    }

    @Test
    void testGetDeviceById() {
        // Arrange
        when(deviceRepository.findById(1)).thenReturn(Optional.of(device));

        // Act
        Device foundDevice = deviceService.getDeviceById(1);
        foundDevice.setDateAudit(dateAudit);

        // Assert
        assertNotNull(foundDevice);
        assertEquals(1, foundDevice.getId());
        assertEquals("Item 1", foundDevice.getItemName());
        assertEquals(LocalDateTime.now(fixedClock).minusDays(1), foundDevice.getDateAudit().getCreatedAt());
        verify(deviceRepository, times(1)).findById(anyInt());
    }

    @Test
    void testUpdateDevice() {
        // Arrange
        Device updatedDevice = new Device();
        updatedDevice.setId(1);
        updatedDevice.setType(Type.MOUSE);
        updatedDevice.setUnitPrice(110.0);
        updatedDevice.setRateType(RateType.LIKENEW);
        updatedDevice.setBranchName("Branch 1");
        updatedDevice.setItemName("Updated Item");
        updatedDevice.setVersion("1.0");
        updatedDevice.setOriginalPrice(150.0);
        updatedDevice.setDateAudit(dateAudit);

        request.setItemName(updatedDevice.getItemName());

        when(deviceRepository.findById(1)).thenReturn(Optional.of(device));
        when(deviceRepository.save(any(Device.class))).thenReturn(updatedDevice);

        // Act
        Device result = deviceService.updateDevice(1, request);

        // Assert
        assertEquals("Updated Item", result.getItemName());
        verify(deviceRepository, times(1)).findById(anyInt());
        verify(deviceRepository, times(1)).save(any(Device.class));
        request.setItemName("Item 1");
    }

    @Test
    void testDeleteDevice() {
        // Arrange
        when(deviceRepository.existsById(device.getId())).thenReturn(true);
        doNothing().when(deviceRepository).deleteById(device.getId());

        // Act
        deviceService.deleteDevice(1);

        // Assert
        verify(deviceRepository, times(1)).existsById(anyInt());
        verify(deviceRepository, times(1)).deleteById(anyInt());
    }

    @Test
    void testViewDevicesSortedByPrice() {
        DeviceSortRequest baseSearchRequest = new DeviceSortRequest();
        baseSearchRequest.setPageNumber(0);
        baseSearchRequest.setPageSize(10);
        baseSearchRequest.setSortString("originalPrice");
        baseSearchRequest.setSortDirection("ASC");
        Pageable pageable = PageRequest.of(baseSearchRequest.getPageNumber(), baseSearchRequest.getPageSize(), Sort.by(baseSearchRequest.getSortString()));
        Page<Device> page = new PageImpl<>(List.of(device, device2));

        when(deviceRepository.findAll(pageable)).thenReturn(page);

        // Act
        Page<Device> devices = deviceService.getDevicesSortedBy(baseSearchRequest);

        // Assert
        assertNotNull(devices);
        assertEquals(2, devices.getTotalElements());
        assertEquals(150.0, devices.getContent().get(0).getOriginalPrice());
        assertEquals(170.0, devices.getContent().get(1).getOriginalPrice());
        verify(deviceRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void testViewDevicesSortedByCreatedAt() {
        DeviceSortRequest baseSearchRequest = new DeviceSortRequest();
        baseSearchRequest.setPageNumber(0);
        baseSearchRequest.setPageSize(10);
        baseSearchRequest.setSortString("dateAudit.createdAt");
        baseSearchRequest.setSortDirection("ASC");
        Pageable pageable = PageRequest.of(baseSearchRequest.getPageNumber(), baseSearchRequest.getPageSize(), Sort.by(baseSearchRequest.getSortString()));
        Page<Device> page = new PageImpl<>(List.of(device, device2));

        when(deviceRepository.findAll(pageable)).thenReturn(page);

        // Act
        Page<Device> devices = deviceService.getDevicesSortedBy(baseSearchRequest);

        // Assert
        assertNotNull(devices);
        assertEquals(2, devices.getTotalElements());
        assertEquals(LocalDateTime.now(fixedClock).minusDays(1), devices.getContent().get(0).getDateAudit().getCreatedAt());
        assertEquals(LocalDateTime.now(fixedClock), devices.getContent().get(1).getDateAudit().getCreatedAt());
        verify(deviceRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void testSearchDevicesByName() {
        // Arrange
        String itemName = "Item 1";
        BaseSearchRequest baseSearchRequest = new BaseSearchRequest();
        baseSearchRequest.setPageNumber(0);
        baseSearchRequest.setPageSize(10);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Device> page = new PageImpl<>(List.of(device));

        when(deviceRepository.findByItemName(itemName, pageable)).thenReturn(page);

        // Act
        Page<Device> devices = deviceService.findDeviceByItemName(itemName, baseSearchRequest);

        // Assert
        assertNotNull(devices);
        assertEquals(1, devices.getTotalElements());
        assertEquals(itemName, devices.getContent().get(0).getItemName());
        verify(deviceRepository, times(1)).findByItemName(anyString(), any(Pageable.class));
    }

    @Test
    void testSearchDevicesByCreatedAtDate() {
        // Arrange
        LocalDateTime startDate = dateAudit.getCreatedAt().minusMinutes(10);
        LocalDateTime endDate = dateAudit.getCreatedAt().plusMinutes(10);
        LocalDateTime createdAt = LocalDateTime.now(fixedClock).minusDays(1);
        BaseSearchRequest baseSearchRequest = new BaseSearchRequest();
        baseSearchRequest.setPageNumber(0);
        baseSearchRequest.setPageSize(10);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Device> page = new PageImpl<>(List.of(device));

        when(deviceRepository.findByDateAudit_CreatedAtBetween(startDate, endDate, pageable)).thenReturn(page);

        // Act
        Page<Device> devices = deviceService.findDeviceByDate(startDate, endDate, baseSearchRequest);

        // Assert
        assertNotNull(devices);
        assertEquals(1, devices.getTotalElements());
        assertEquals(createdAt, devices.getContent().get(0).getDateAudit().getCreatedAt());
        verify(deviceRepository, times(1)).findByDateAudit_CreatedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void testSearchDevicesByType() {
        // Arrange
        Type type = Type.MOUSE;
        BaseSearchRequest baseSearchRequest = new BaseSearchRequest();
        baseSearchRequest.setPageNumber(0);
        baseSearchRequest.setPageSize(10);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Device> page = new PageImpl<>(List.of(device));

        when(deviceRepository.findByType(type, pageable)).thenReturn(page);

        // Act
        Page<Device> devices = deviceService.findDeviceByType(type, baseSearchRequest);

        // Assert
        assertNotNull(devices);
        assertEquals(1, devices.getTotalElements());
        assertEquals(Type.MOUSE, devices.getContent().get(0).getType());
        verify(deviceRepository, times(1)).findByType(any(Type.class), any(Pageable.class));
    }

    @Test
    void testSearchDevicesByRateType() {
        // Arrange
        RateType rateType = RateType.LIKENEW;
        BaseSearchRequest baseSearchRequest = new BaseSearchRequest();
        baseSearchRequest.setPageNumber(0);
        baseSearchRequest.setPageSize(10);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Device> page = new PageImpl<>(List.of(device));

        when(deviceRepository.findByRateType(rateType, pageable)).thenReturn(page);

        // Act
        Page<Device> devices = deviceService.findDeviceByRateType(rateType, baseSearchRequest);

        // Assert
        assertNotNull(devices);
        assertEquals(1, devices.getTotalElements());
        assertEquals(RateType.LIKENEW, devices.getContent().get(0).getRateType());
        verify(deviceRepository, times(1)).findByRateType(any(RateType.class), any(Pageable.class));
    }

    @Test
    void testCreateDevice_CannotSave() {
        // Arrange
        request.setType("TEST");

        // Act
        Device createdDevice = deviceService.createDevice(request);

        // Assert
        assertNull(createdDevice);
        verify(deviceRepository, never()).save(any(Device.class));
    }

    @Test
    void testGetDeviceById_NotFound() {
        // Arrange
        when(deviceRepository.findById(999)).thenReturn(Optional.empty());

        // Act
        DeviceNotFoundException foundDevice = assertThrows(DeviceNotFoundException.class, () -> deviceService.getDeviceById(999));

        // Assert
        verify(deviceRepository, times(1)).findById(anyInt());
    }

    @Test
    void testUpdateDevice_NotFound() {

        when(deviceRepository.findById(999)).thenReturn(Optional.empty());

        // Act
        Device result = deviceService.updateDevice(999, request);

        // Assert
        assertNull(result);
        verify(deviceRepository, times(1)).findById(anyInt());
        verify(deviceRepository, never()).save(any(Device.class));
    }

    @Test
    void testUpdateDevice_CannotUpdate() {
        // Arrange
        Device updatedDevice = new Device();
        updatedDevice.setId(1);
        updatedDevice.setType(Type.MOUSE);
        updatedDevice.setUnitPrice(110.0);
        updatedDevice.setRateType(RateType.LIKENEW);
        updatedDevice.setBranchName("Branch 1");
        updatedDevice.setItemName("Updated Item");
        updatedDevice.setVersion("1.0");
        updatedDevice.setOriginalPrice(150.0);
        updatedDevice.setDateAudit(dateAudit);

        request.setItemName(updatedDevice.getItemName());
        request.setType("TEST");

        when(deviceRepository.findById(1)).thenReturn(Optional.of(device));

        // Act
        Device result = deviceService.updateDevice(1, request);

        // Assert
        assertNull(result);
        verify(deviceRepository, times(1)).findById(anyInt());
        verify(deviceRepository, never()).save(any(Device.class));
        request.setItemName("Item 1");
    }

    @Test
    void testDeleteDevice_NotFound() {
        // Arrange
        when(deviceRepository.existsById(device.getId())).thenReturn(false);

        // Act
        deviceService.deleteDevice(1);

        // Assert
        verify(deviceRepository, times(1)).existsById(anyInt());
        verify(deviceRepository, never()).deleteById(anyInt());
    }

    @Test
    void testViewDevicesSortedByPrice_NotFound() {
        DeviceSortRequest baseSearchRequest = new DeviceSortRequest();
        baseSearchRequest.setPageNumber(0);
        baseSearchRequest.setPageSize(10);
        baseSearchRequest.setSortString("originalPrice");
        baseSearchRequest.setSortDirection("ASC");
        Pageable pageable = PageRequest.of(baseSearchRequest.getPageNumber(), baseSearchRequest.getPageSize(), Sort.by(baseSearchRequest.getSortString()));
        Page<Device> page = new PageImpl<>(List.of(device, device2));

        when(deviceRepository.findAll(pageable)).thenReturn(Page.empty());

        // Act
        Page<Device> devices = deviceService.getDevicesSortedBy(baseSearchRequest);

        // Assert
        assertEquals(0, devices.getTotalElements());
        verify(deviceRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void testSearchDevicesByName_NotFound() {
        // Arrange
        String itemName = "Item 1";
        BaseSearchRequest baseSearchRequest = new BaseSearchRequest();
        baseSearchRequest.setPageNumber(0);
        baseSearchRequest.setPageSize(10);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Device> page = new PageImpl<>(List.of(device));

        when(deviceRepository.findByItemName(itemName, pageable)).thenReturn(Page.empty());

        // Act
        Page<Device> devices = deviceService.findDeviceByItemName(itemName, baseSearchRequest);

        // Assert
        assertNull(devices);
        verify(deviceRepository, times(1)).findByItemName(anyString(), any(Pageable.class));
    }

    @Test
    void testSearchDevicesByCreatedAtDate_NotFound() {
        // Arrange
        LocalDateTime startDate = dateAudit.getCreatedAt().minusMinutes(10);
        LocalDateTime endDate = dateAudit.getCreatedAt().plusMinutes(10);
        LocalDateTime createdAt = LocalDateTime.now(fixedClock).minusDays(1);
        BaseSearchRequest baseSearchRequest = new BaseSearchRequest();
        baseSearchRequest.setPageNumber(0);
        baseSearchRequest.setPageSize(10);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Device> page = new PageImpl<>(List.of(device));

        when(deviceRepository.findByDateAudit_CreatedAtBetween(startDate, endDate, pageable)).thenReturn(Page.empty());

        // Act
        Page<Device> devices = deviceService.findDeviceByDate(startDate, endDate, baseSearchRequest);

        // Assert
        assertNull(devices);
        verify(deviceRepository, times(1)).findByDateAudit_CreatedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void testSearchDevicesByType_NotFound() {
        // Arrange
        Type type = Type.MOUSE;
        BaseSearchRequest baseSearchRequest = new BaseSearchRequest();
        baseSearchRequest.setPageNumber(0);
        baseSearchRequest.setPageSize(10);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Device> page = new PageImpl<>(List.of(device));

        when(deviceRepository.findByType(type, pageable)).thenReturn(Page.empty());

        // Act
        Page<Device> devices = deviceService.findDeviceByType(type, baseSearchRequest);

        // Assert
        assertNull(devices);
        verify(deviceRepository, times(1)).findByType(any(Type.class), any(Pageable.class));
    }

    @Test
    void testSearchDevicesByRateType_NotFound() {
        // Arrange
        RateType rateType = RateType.LIKENEW;
        BaseSearchRequest baseSearchRequest = new BaseSearchRequest();
        baseSearchRequest.setPageNumber(0);
        baseSearchRequest.setPageSize(10);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Device> page = new PageImpl<>(List.of(device));

        when(deviceRepository.findByRateType(rateType, pageable)).thenReturn(Page.empty());

        // Act
        Page<Device> devices = deviceService.findDeviceByRateType(rateType, baseSearchRequest);

        // Assert
        assertNull(devices);
        verify(deviceRepository, times(1)).findByRateType(any(RateType.class), any(Pageable.class));
    }
}
