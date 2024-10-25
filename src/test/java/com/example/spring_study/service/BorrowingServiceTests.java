package com.example.spring_study.service;

import com.example.spring_study.constant.RateType;
import com.example.spring_study.constant.SortParam;
import com.example.spring_study.constant.Type;
import com.example.spring_study.model.Borrowing;
import com.example.spring_study.model.DateAudit;
import com.example.spring_study.model.Device;
import com.example.spring_study.model.Employee;
import com.example.spring_study.model.payload.BaseSearchRequest;
import com.example.spring_study.model.payload.BaseSortRequest;
import com.example.spring_study.model.payload.BorrowingRequest;
import com.example.spring_study.repository.BorrowingRepository;
import com.example.spring_study.repository.DeviceRepository;
import com.example.spring_study.repository.EmployeeRepository;
import com.example.spring_study.service.impl.BorrowingServiceImpl;
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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BorrowingServiceTests {
    @Mock
    private BorrowingRepository borrowingRepository;

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private BorrowingServiceImpl borrowingService;

    private Borrowing borrowing;
    private Borrowing borrowing2;
    private BorrowingRequest borrowingRequest;
    private Device device1;
    private Device device2;
    private Employee employee;
    private Clock fixedClock;


    @BeforeEach
    void setUp(){
        fixedClock = Clock.fixed(Instant.parse("2024-10-23T17:04:11.983812500Z"), ZoneId.systemDefault());
        DateAudit dateAudit;
        dateAudit = new DateAudit();
        dateAudit.setCreatedAt(LocalDateTime.now(fixedClock));

        device1 = new Device();
        device1.setId(1);
        device1.setType(Type.MOUSE);
        device1.setUnitPrice(110.0);
        device1.setRateType(RateType.LIKENEW);
        device1.setBranchName("Branch 1");
        device1.setItemName("Item 1");
        device1.setVersion("1.0");
        device1.setOriginalPrice(150.0);
        device1.setDateAudit(dateAudit);

        device2 = new Device();
        device2.setId(2);
        device2.setType(Type.CASE);
        device2.setUnitPrice(130.0);
        device2.setRateType(RateType.NEW);
        device2.setBranchName("Branch 2");
        device2.setItemName("Item 2");
        device2.setVersion("1.1");
        device2.setOriginalPrice(170.0);
        device2.setDateAudit(dateAudit);

        employee = new Employee();
        employee.setId(1);
        employee.setFullName("Name 1");
        employee.setPhoneNumber("PhoneNumber");
        employee.setAddress("Address 1");
        employee.setAccountBalance(1000.0);

        borrowing = new Borrowing();
        borrowing.setId(1);
        borrowing.setDateAudit(dateAudit);
        borrowing.getDateAudit().setHandOverDate(LocalDateTime.now(fixedClock));
        borrowing.setEmployee(employee);
        borrowing.setDevices(new ArrayList<>(Arrays.asList(device1,device2)));
        borrowing.updateTotalPrice();

        DateAudit dateAudit1;
        dateAudit1 = new DateAudit();
        dateAudit1.setCreatedAt(LocalDateTime.now(fixedClock));
        borrowing2 = new Borrowing();
        borrowing2.setId(2);
        borrowing2.setDateAudit(dateAudit1);
        borrowing2.getDateAudit().setHandOverDate(LocalDateTime.now(fixedClock).minusDays(1));
        borrowing2.setEmployee(employee);
        borrowing2.setDevices(new ArrayList<>(Arrays.asList(device1)));
        borrowing2.updateTotalPrice();

        borrowingRequest = new BorrowingRequest();
        borrowingRequest.setDevicesId(List.of(1));
        borrowingRequest.setEmployeeId(1);
    }

    @Test
    void testCreateBorrowing() {
        // Arrange
        when(employeeRepository.findById(borrowing.getEmployee().getId())).thenReturn(Optional.of(employee));
        when(deviceRepository.findById(1)).thenReturn(Optional.of(device1));
        lenient().when(deviceRepository.findById(2)).thenReturn(Optional.of(device2));
        when(borrowingRepository.save(any(Borrowing.class))).thenReturn(borrowing);

        // Act
        Borrowing createdBorrowing = borrowingService.createBorrowing(borrowingRequest);
        DateAudit dateAudit;
        dateAudit = new DateAudit();
        dateAudit.setCreatedAt(LocalDateTime.now(fixedClock));
        createdBorrowing.setDateAudit(dateAudit);

        // Assert
        assertNotNull(createdBorrowing);
        assertEquals(1, createdBorrowing.getEmployee().getId());
        assertEquals(LocalDateTime.now(fixedClock), createdBorrowing.getDateAudit().getCreatedAt());
        verify(employeeRepository,times(1)).findById(borrowing.getEmployee().getId());
        verify(deviceRepository,times(1)).findById(anyInt());
        verify(borrowingRepository, times(1)).save(any(Borrowing.class));
    }

    @Test
    void testGetBorrowingById() {
        // Arrange
        when(borrowingRepository.findById(anyInt())).thenReturn(Optional.of(borrowing));

        // Act
        Borrowing foundBorrowing = borrowingService.getBorrowingById(1);

        // Assert
        assertNotNull(foundBorrowing);
        assertEquals(1, foundBorrowing.getId());
        verify(borrowingRepository,times(1)).findById(anyInt());
    }

    @Test
    void testGetAllBorrowings(){
        BaseSearchRequest baseSearchRequest = new BaseSearchRequest();
        baseSearchRequest.setPageNumber(0);
        baseSearchRequest.setPageSize(10);
        // Arrange
        Page<Borrowing> page = new PageImpl<>(List.of(borrowing, borrowing2));
        when(borrowingRepository.findAll(any(Pageable.class))).thenReturn(page);

        // Act
        Page<Borrowing> foundBorrowings = borrowingService.getAllBorrowing(baseSearchRequest);

        // Assert
        assertNotNull(foundBorrowings);
        assertEquals(1, foundBorrowings.getContent().get(0).getId());
        verify(borrowingRepository,times(1)).findAll(any(Pageable.class));
    }

    @Test
    void testDeleteBorrowing() {
        // Arrange
        when(borrowingRepository.existsById(anyInt())).thenReturn(true);
        doNothing().when(borrowingRepository).deleteById(borrowing.getId());

        // Act
        borrowingService.deleteBorrowing(1);

        // Assert
        verify(borrowingRepository,times(1)).existsById(anyInt());
        verify(borrowingRepository,times(1)).deleteById(anyInt());
    }

    @Test
    void testGetAllBorrowingsSortedByTotalPrice() {
        // Arrange
        BaseSortRequest baseSearchRequest = new BaseSortRequest();
        baseSearchRequest.setPageNumber(0);
        baseSearchRequest.setPageSize(10);
        baseSearchRequest.setSortString("totalPrice");
        baseSearchRequest.setSortDirection(SortParam.ASC);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("totalPrice").ascending());
        Page<Borrowing> page = new PageImpl<>(List.of(borrowing2, borrowing));
        when(borrowingRepository.findAll(any(Pageable.class))).thenReturn(page);

        // Act
        Page<Borrowing> result = borrowingService.getBorrowingsSortedBy(baseSearchRequest);

        // Assert
        assertEquals(2, result.getTotalElements());
        assertEquals(120.0, result.getContent().get(0).getTotalPrice());
        assertEquals(290.0, result.getContent().get(1).getTotalPrice());
        verify(borrowingRepository,times(1)).findAll(any(Pageable.class));
    }

    @Test
    void testGetAllBorrowingsSortedByHandoverDate() {
        // Arrange
        BaseSortRequest baseSearchRequest = new BaseSortRequest();
        baseSearchRequest.setPageNumber(0);
        baseSearchRequest.setPageSize(10);
        baseSearchRequest.setSortString("dateAudit.handOverDate");
        baseSearchRequest.setSortDirection(SortParam.ASC);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("dateAudit.handOverDate").ascending());
        Page<Borrowing> page = new PageImpl<>(List.of(borrowing2, borrowing));
        when(borrowingRepository.findAll(any(Pageable.class))).thenReturn(page);

        // Act
        Page<Borrowing> result = borrowingService.getBorrowingsSortedBy(baseSearchRequest);

        // Assert
        assertEquals(2, result.getTotalElements());
        assertEquals(LocalDateTime.now(fixedClock).minusDays(1), result.getContent().get(0).getDateAudit().getHandOverDate());
        assertEquals(LocalDateTime.now(fixedClock), result.getContent().get(1).getDateAudit().getHandOverDate());
        verify(borrowingRepository,times(1)).findAll(any(Pageable.class));
    }

    @Test
    void testSearchBorrowingByDeviceName() {
        // Arrange
        BaseSearchRequest baseSearchRequest = new BaseSortRequest();
        baseSearchRequest.setPageNumber(0);
        baseSearchRequest.setPageSize(10);
        Page<Borrowing> page = new PageImpl<>(List.of(borrowing));

        when(borrowingRepository.findByDeviceName(anyString(), any(Pageable.class))).thenReturn(page);

        // Act
        Page<Borrowing> result = borrowingService.findByDeviceName("Item 1", baseSearchRequest);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals("Item 1", result.getContent().get(0).getDevices().get(0).getItemName());
        verify(borrowingRepository,times(1)).findByDeviceName(anyString(), any(Pageable.class));
    }

    @Test
    void testSearchBorrowingByHandOverDate() {
        // Arrange
        LocalDateTime startDate = LocalDateTime.now(fixedClock).minusMinutes(10);
        LocalDateTime endDate = LocalDateTime.now(fixedClock).plusMinutes(10);
        LocalDateTime handOverDate = LocalDateTime.now(fixedClock);
        BaseSearchRequest baseSearchRequest = new BaseSortRequest();
        baseSearchRequest.setPageNumber(0);
        baseSearchRequest.setPageSize(10);
        Page<Borrowing> page = new PageImpl<>(List.of(borrowing));

        when(borrowingRepository.findByDateAudit_HandOverDateBetween(any(LocalDateTime.class),any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);

        // Act
        Page<Borrowing> result = borrowingService.findByHandOverDate(startDate,endDate, baseSearchRequest);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(handOverDate, result.getContent().get(0).getDateAudit().getHandOverDate());
        verify(borrowingRepository,times(1)).findByDateAudit_HandOverDateBetween(any(LocalDateTime.class),any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void testSearchBorrowingByDeviceType() {
        // Arrange
        BaseSearchRequest baseSearchRequest = new BaseSortRequest();
        baseSearchRequest.setPageNumber(0);
        baseSearchRequest.setPageSize(10);
        Page<Borrowing> page = new PageImpl<>(List.of(borrowing));

        when(borrowingRepository.findByDeviceType(any(Type.class), any(Pageable.class))).thenReturn(page);

        // Act
        Page<Borrowing> result = borrowingService.findByDeviceType(Type.MOUSE, baseSearchRequest);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(Type.MOUSE, result.getContent().get(0).getDevices().get(0).getType());
        verify(borrowingRepository,times(1)).findByDeviceType(any(Type.class), any(Pageable.class));
    }

    @Test
    void testSearchBorrowingByTotalPrice() {
        // Arrange
        BaseSearchRequest baseSearchRequest = new BaseSortRequest();
        baseSearchRequest.setPageNumber(0);
        baseSearchRequest.setPageSize(10);
        Page<Borrowing> page = new PageImpl<>(List.of(borrowing));

        when(borrowingRepository.findByTotalPrice(anyDouble(), any(Pageable.class))).thenReturn(page);

        // Act
        Page<Borrowing> result = borrowingService.findByTotalPrice(290.0, baseSearchRequest);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(290.0, result.getContent().get(0).getTotalPrice());
        verify(borrowingRepository,times(1)).findByTotalPrice(anyDouble(), any(Pageable.class));
    }

    @Test
    void testTransferDevice() {
        // Arrange
        when(borrowingRepository.findById(1)).thenReturn(Optional.of(borrowing));
        when(borrowingRepository.findById(2)).thenReturn(Optional.of(borrowing2));
        when(borrowingRepository.save(any(Borrowing.class))).thenAnswer(invocation -> invocation.getArgument(0));
        // Act
        List<Borrowing> result = borrowingService.transferDevice(1,2,1);

        // Assert
        assertEquals(2, result.size());
        assertEquals(1,result.get(0).getId());
        assertEquals(2,result.get(1).getId());
        assertNotEquals(1,result.get(0).getDevices().get(0).getId());
        assertEquals(1,result.get(1).getDevices().get(0).getId());

        verify(borrowingRepository).findById(1);
        verify(borrowingRepository).findById(2);
        verify(borrowingRepository, times(2)).save(any(Borrowing.class));
    }

    @Test
    void testCreateBorrowing_NotFoundEmployee() {
        // Arrange
        when(employeeRepository.findById(borrowing.getEmployee().getId())).thenReturn(Optional.empty());

        // Act
        Borrowing createdBorrowing = borrowingService.createBorrowing(borrowingRequest);

        // Assert
        assertNull(createdBorrowing);
        verify(employeeRepository,times(1)).findById(borrowing.getEmployee().getId());
    }

    @Test
    void testCreateBorrowing_NotFoundDevice() {
        // Arrange
        when(employeeRepository.findById(borrowing.getEmployee().getId())).thenReturn(Optional.of(employee));
        when(deviceRepository.findById(1)).thenReturn(Optional.empty());

        // Act
        Borrowing createdBorrowing = borrowingService.createBorrowing(borrowingRequest);

        // Assert
        assertNull(createdBorrowing);
        verify(employeeRepository,times(1)).findById(borrowing.getEmployee().getId());
        verify(deviceRepository,times(1)).findById(anyInt());
    }

    @Test
    void testGetBorrowingById_NotFound() {
        // Arrange
        when(borrowingRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Act
        Borrowing foundBorrowing = borrowingService.getBorrowingById(1);

        // Assert
        assertNull(foundBorrowing);
        verify(borrowingRepository,times(1)).findById(anyInt());
    }

    @Test
    void testGetAllBorrowings_NotFound(){
        BaseSearchRequest baseSearchRequest = new BaseSearchRequest();
        baseSearchRequest.setPageNumber(0);
        baseSearchRequest.setPageSize(10);
        // Arrange
        Page<Borrowing> page = new PageImpl<>(List.of(borrowing, borrowing2));
        when(borrowingRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList()));

        // Act
        Page<Borrowing> foundBorrowings = borrowingService.getAllBorrowing(baseSearchRequest);

        // Assert
        assertNotNull(foundBorrowings);
        assertEquals(0, foundBorrowings.getTotalElements());
        verify(borrowingRepository,times(1)).findAll(any(Pageable.class));
    }

    @Test
    void testDeleteBorrowing_NotFound() {
        // Arrange
        when(borrowingRepository.existsById(anyInt())).thenReturn(false);

        // Act
        borrowingService.deleteBorrowing(1);

        // Assert
        verify(borrowingRepository,times(1)).existsById(anyInt());
        verify(borrowingRepository,never()).deleteById(anyInt());
    }

    @Test
    void testGetAllBorrowingsSortedByTotalPrice_NotFound() {
        // Arrange
        BaseSortRequest baseSearchRequest = new BaseSortRequest();
        baseSearchRequest.setPageNumber(0);
        baseSearchRequest.setPageSize(10);
        baseSearchRequest.setSortString("totalPrice");
        baseSearchRequest.setSortDirection(SortParam.ASC);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("totalPrice").ascending());
        Page<Borrowing> page = new PageImpl<>(List.of(borrowing2, borrowing));
        when(borrowingRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        // Act
        Page<Borrowing> result = borrowingService.getBorrowingsSortedBy(baseSearchRequest);

        // Assert
        assertEquals(0,result.getTotalElements());
        verify(borrowingRepository,times(1)).findAll(any(Pageable.class));
    }

    @Test
    void testSearchBorrowingByDeviceName_NotFound() {
        // Arrange
        BaseSearchRequest baseSearchRequest = new BaseSortRequest();
        baseSearchRequest.setPageNumber(0);
        baseSearchRequest.setPageSize(10);
        Page<Borrowing> page = new PageImpl<>(List.of(borrowing));

        when(borrowingRepository.findByDeviceName(anyString(), any(Pageable.class))).thenReturn(Page.empty());

        // Act
        Page<Borrowing> result = borrowingService.findByDeviceName("Item 1", baseSearchRequest);

        // Assert
        assertNull(result);
        verify(borrowingRepository,times(1)).findByDeviceName(anyString(), any(Pageable.class));
    }

    @Test
    void testSearchBorrowingByHandOverDate_NotFound() {
        // Arrange
        LocalDateTime startDate = LocalDateTime.now(fixedClock).minusMinutes(10);
        LocalDateTime endDate = LocalDateTime.now(fixedClock).plusMinutes(10);
        LocalDateTime handOverDate = LocalDateTime.now(fixedClock);
        BaseSearchRequest baseSearchRequest = new BaseSortRequest();
        baseSearchRequest.setPageNumber(0);
        baseSearchRequest.setPageSize(10);
        Page<Borrowing> page = new PageImpl<>(List.of(borrowing));

        when(borrowingRepository.findByDateAudit_HandOverDateBetween(any(LocalDateTime.class),any(LocalDateTime.class), any(Pageable.class))).thenReturn(Page.empty());

        // Act
        Page<Borrowing> result = borrowingService.findByHandOverDate(startDate,endDate, baseSearchRequest);

        // Assert
        assertNull(result);
        verify(borrowingRepository,times(1)).findByDateAudit_HandOverDateBetween(any(LocalDateTime.class),any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void testSearchBorrowingByDeviceType_NotFound() {
        // Arrange
        BaseSearchRequest baseSearchRequest = new BaseSortRequest();
        baseSearchRequest.setPageNumber(0);
        baseSearchRequest.setPageSize(10);
        Page<Borrowing> page = new PageImpl<>(List.of(borrowing));

        when(borrowingRepository.findByDeviceType(any(Type.class), any(Pageable.class))).thenReturn(Page.empty());

        // Act
        Page<Borrowing> result = borrowingService.findByDeviceType(Type.MOUSE, baseSearchRequest);

        // Assert
        assertNull(null);
        verify(borrowingRepository,times(1)).findByDeviceType(any(Type.class), any(Pageable.class));
    }

    @Test
    void testSearchBorrowingByTotalPrice_NotFound() {
        // Arrange
        BaseSearchRequest baseSearchRequest = new BaseSortRequest();
        baseSearchRequest.setPageNumber(0);
        baseSearchRequest.setPageSize(10);
        Page<Borrowing> page = new PageImpl<>(List.of(borrowing));

        when(borrowingRepository.findByTotalPrice(anyDouble(), any(Pageable.class))).thenReturn(Page.empty());

        // Act
        Page<Borrowing> result = borrowingService.findByTotalPrice(240.0, baseSearchRequest);

        // Assert
        assertNull(result);
        verify(borrowingRepository,times(1)).findByTotalPrice(anyDouble(), any(Pageable.class));
    }

    @Test
    void testTransferDevice_NotFoundBorrowing1() {
        // Arrange
        when(borrowingRepository.findById(1)).thenReturn(Optional.empty());
        // Act
        List<Borrowing> result = borrowingService.transferDevice(1,2,1);

        // Assert
        assertEquals(0,result.size());

        verify(borrowingRepository).findById(1);
    }

    @Test
    void testTransferDevice_NotFoundBorrowing2() {
        // Arrange
        when(borrowingRepository.findById(1)).thenReturn(Optional.of(borrowing));
        when(borrowingRepository.findById(2)).thenReturn(Optional.empty());
        // Act
        List<Borrowing> result = borrowingService.transferDevice(1,2,1);

        // Assert
        assertEquals(0,result.size());

        verify(borrowingRepository).findById(1);
        verify(borrowingRepository).findById(2);
    }

    @Test
    void testTransferDevice_NoDeviceInBorrowing() {
        // Arrange
        borrowing.setDevices(new ArrayList<>());
        when(borrowingRepository.findById(1)).thenReturn(Optional.of(borrowing));
        // Act
        List<Borrowing> result = borrowingService.transferDevice(1,2,1);

        // Assert
        assertEquals(0,result.size());

        verify(borrowingRepository).findById(1);
    }
}
