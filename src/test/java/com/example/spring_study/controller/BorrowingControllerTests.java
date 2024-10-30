package com.example.spring_study.controller;

import com.example.spring_study.constant.RateType;
import com.example.spring_study.constant.Type;
import com.example.spring_study.model.*;
import com.example.spring_study.model.payload.*;
import com.example.spring_study.service.BorrowingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

@WebMvcTest(BorrowingController.class)
@AutoConfigureMockMvc(addFilters = false)
public class BorrowingControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BorrowingService borrowingService;

    @Autowired
    private ObjectMapper objectMapper;

    private BorrowingResponse borrowing;
    private BorrowingResponse borrowing2;
    private BorrowingRequest borrowingRequest;
    private DateAudit dateAudit;
    private Device device1;
    private Device device2;
    private Clock fixedClock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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

        EmployeeResponse employee = new EmployeeResponse(1, "Test UserName", "John Doe", "123 Street", "123456789", 1000.0, Set.of(new Role(1, "ROLE_ADMIN")));

        borrowingRequest = new BorrowingRequest();
        borrowingRequest.setEmployeeId(employee.getId());
        borrowingRequest.setDevicesId(List.of(1, 2));

        borrowing = new BorrowingResponse(1, dateAudit, employee, List.of(device1, device2));
        borrowing.getDateAudit().setHandOverDate(LocalDateTime.now(fixedClock).truncatedTo(ChronoUnit.MICROS));


        EmployeeResponse employee2 = new EmployeeResponse(2, "Test UserName 2", "Test Doe", "123 Street", "123456789", 1000.0, Set.of(new Role(1, "ROLE_USER")));
        DateAudit dateAudit1;
        dateAudit1 = new DateAudit();
        dateAudit1.setCreatedAt(LocalDateTime.now(fixedClock));

        borrowing2 = new BorrowingResponse(2, dateAudit1, employee2, List.of(device1));
        borrowing2.setId(2);
        borrowing2.setDateAudit(dateAudit1);
        borrowing2.getDateAudit().setHandOverDate(LocalDateTime.now(fixedClock).minusDays(3).truncatedTo(ChronoUnit.MICROS));
    }

    @Test
    void testCreateBorrowing() throws Exception {
        when(borrowingService.createBorrowing(any(BorrowingRequest.class))).thenReturn(borrowing);

        mockMvc.perform(post("/api/v1/borrowing/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(borrowingRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(borrowing.getId()))
                .andExpect(jsonPath("$.totalPrice").value(borrowing.getTotalPrice()))
                .andExpect(jsonPath("$.employee").value(borrowing.getEmployee()))
                .andExpect(jsonPath("$.devices", hasSize(2)));

        verify(borrowingService, times(1)).createBorrowing(any(BorrowingRequest.class));
    }

    @Test
    void testGetBorrowingById() throws Exception {
        when(borrowingService.getBorrowingById(1)).thenReturn(borrowing);

        mockMvc.perform(get("/api/v1/borrowing/get")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(borrowing.getId()))
                .andExpect(jsonPath("$.totalPrice").value(borrowing.getTotalPrice()))
                .andExpect(jsonPath("$.employee").value(borrowing.getEmployee()))
                .andExpect(jsonPath("$.devices", hasSize(2)));

        verify(borrowingService, times(1)).getBorrowingById(anyInt());
    }

    @Test
    void testGetAllBorrowings() throws Exception {
        List<BorrowingResponse> borrowings = List.of(borrowing);
        when(borrowingService.getAllBorrowing(any(BaseSearchRequest.class))).thenReturn(new PageImpl<>(borrowings));

        mockMvc.perform(get("/api/v1/borrowing/getAll")
                        .param("pageNumber", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));

        verify(borrowingService, times(1)).getAllBorrowing(any(BaseSearchRequest.class));
    }

    @Test
    void testDeleteBorrowing() throws Exception {
        when(borrowingService.deleteBorrowing(1)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/borrowing/delete")
                        .param("id", "1"))
                .andExpect(status().isOk());

        verify(borrowingService, times(1)).deleteBorrowing(1);
    }

    @Test
    void testGetAllBorrowingsSortedByTotalPrice() throws Exception {
        List<BorrowingResponse> borrowings = List.of(borrowing2, borrowing);
        when(borrowingService.getBorrowingsSortedBy(any(BaseSortRequest.class))).thenReturn(new PageImpl<>(borrowings));

        mockMvc.perform(get("/api/v1/borrowing/getBorrowingsSortedBy")
                        .param("pageNumber", "0")
                        .param("pageSize", "10")
                        .param("sortString", "totalPrice")
                        .param("sortDirection", "ASC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].totalPrice").value(borrowing2.getTotalPrice()))
                .andExpect(jsonPath("$.content[1].totalPrice").value(borrowing.getTotalPrice()));

        verify(borrowingService, times(1)).getBorrowingsSortedBy(any(BaseSortRequest.class));
    }

    @Test
    void testGetAllBorrowingsSortedByHandOverDate() throws Exception {
        List<BorrowingResponse> borrowings = List.of(borrowing2, borrowing);
        when(borrowingService.getBorrowingsSortedBy(any(BaseSortRequest.class))).thenReturn(new PageImpl<>(borrowings));

        mockMvc.perform(get("/api/v1/borrowing/getBorrowingsSortedBy")
                        .param("pageNumber", "0")
                        .param("pageSize", "10")
                        .param("sortString", "dateAudit.handOverDate")
                        .param("sortDirection", "ASC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].dateAudit.handOverDate").value(borrowing2.getDateAudit().getHandOverDate().toString()))
                .andExpect(jsonPath("$.content[1].dateAudit.handOverDate").value(borrowing.getDateAudit().getHandOverDate().toString()));

        verify(borrowingService, times(1)).getBorrowingsSortedBy(any(BaseSortRequest.class));
    }

    @Test
    void testSearchBorrowingsByHandOverDate() throws Exception {
        List<BorrowingResponse> borrowings = List.of(borrowing);
        when(borrowingService.findByHandOverDate(any(LocalDateTime.class), any(LocalDateTime.class), any(BaseSearchRequest.class))).thenReturn(new PageImpl<>(borrowings));

        mockMvc.perform(get("/api/v1/borrowing/findByHandOverDate")
                        .param("startDate", LocalDateTime.now(fixedClock).minusDays(1).toString())
                        .param("endDate", LocalDateTime.now(fixedClock).plusDays(1).toString())
                        .param("pageNumber", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].dateAudit.handOverDate").value(borrowing.getDateAudit().getHandOverDate().toString()));

        verify(borrowingService, times(1)).findByHandOverDate(any(LocalDateTime.class), any(LocalDateTime.class), any(BaseSearchRequest.class));
    }

    @Test
    void testSearchBorrowingsByDeviceName() throws Exception {
        List<BorrowingResponse> borrowings = List.of(borrowing);
        when(borrowingService.findByDeviceName(anyString(), any(BaseSearchRequest.class))).thenReturn(new PageImpl<>(borrowings));

        mockMvc.perform(get("/api/v1/borrowing/findByItemName")
                        .param("itemName", "Item 1")
                        .param("pageNumber", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].devices[0].itemName").value("Item 1"));

        verify(borrowingService, times(1)).findByDeviceName(anyString(), any(BaseSearchRequest.class));
    }

    @Test
    void testSearchBorrowingsByDeviceType() throws Exception {
        List<BorrowingResponse> borrowings = List.of(borrowing);
        when(borrowingService.findByDeviceType(any(Type.class), any(BaseSearchRequest.class))).thenReturn(new PageImpl<>(borrowings));

        mockMvc.perform(get("/api/v1/borrowing/findByItemType")
                        .param("type", "MOUSE")
                        .param("pageNumber", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].devices[0].type").value("MOUSE"));

        verify(borrowingService, times(1)).findByDeviceType(any(Type.class), any(BaseSearchRequest.class));
    }

    @Test
    void testSearchBorrowingsByTotalPrice() throws Exception {
        List<BorrowingResponse> borrowings = List.of(borrowing);
        when(borrowingService.findByTotalPrice(anyDouble(), any(BaseSearchRequest.class))).thenReturn(new PageImpl<>(borrowings));

        mockMvc.perform(get("/api/v1/borrowing/findByTotalPrice")
                        .param("totalPrice", "290")
                        .param("pageNumber", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].totalPrice").value(290));

        verify(borrowingService, times(1)).findByTotalPrice(anyDouble(), any(BaseSearchRequest.class));
    }

    @Test
    void testTransferDevice() throws Exception {
        List<BorrowingResponse> borrowings = List.of(borrowing, borrowing2);
        when(borrowingService.transferDevice(anyInt(), anyInt(), anyInt())).thenReturn(borrowings);

        mockMvc.perform(get("/api/v1/borrowing/transferDevice")
                        .param("borrowingIdFrom", "1")
                        .param("borrowingIdTo", "2")
                        .param("deviceId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].devices[0].id").value(1));

        verify(borrowingService, times(1)).transferDevice(anyInt(), anyInt(), anyInt());
    }

    @Test
    void testCreateBorrowing_NotCreated() throws Exception {
        when(borrowingService.createBorrowing(any(BorrowingRequest.class))).thenReturn(null);

        mockMvc.perform(post("/api/v1/borrowing/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(borrowingRequest)))
                .andExpect(status().isNotAcceptable());

        verify(borrowingService, times(1)).createBorrowing(any(BorrowingRequest.class));
    }

    @Test
    void testGetBorrowingById_NotFound() throws Exception {
        when(borrowingService.getBorrowingById(999)).thenReturn(null);

        mockMvc.perform(get("/api/v1/borrowing/get")
                        .param("id", "999"))
                .andExpect(status().isNotFound());

        verify(borrowingService, times(1)).getBorrowingById(anyInt());
    }

    @Test
    void testGetAllBorrowings_NotFound() throws Exception {
        List<BorrowingResponse> borrowings = List.of(borrowing);
        when(borrowingService.getAllBorrowing(any(BaseSearchRequest.class))).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/borrowing/getAll")
                        .param("pageNumber", "0")
                        .param("pageSize", "0"))
                .andExpect(status().isNotFound());

        verify(borrowingService, times(1)).getAllBorrowing(any(BaseSearchRequest.class));
    }

    @Test
    void testDeleteBorrowing_NotFound() throws Exception {
        when(borrowingService.deleteBorrowing(999)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/borrowing/delete")
                        .param("id", "999"))
                .andExpect(status().isNotFound());

        verify(borrowingService, times(1)).deleteBorrowing(999);
    }

    @Test
    void testGetAllBorrowingsSortedByTotalPrice_NotFound() throws Exception {
        List<BorrowingResponse> borrowings = List.of(borrowing2, borrowing);
        when(borrowingService.getBorrowingsSortedBy(any(BaseSortRequest.class))).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/borrowing/getBorrowingsSortedBy")
                        .param("pageNumber", "0")
                        .param("pageSize", "0")
                        .param("sortString", "totalPrice")
                        .param("sortDirection", "ASC"))
                .andExpect(status().isNotFound());

        verify(borrowingService, times(1)).getBorrowingsSortedBy(any(BaseSortRequest.class));
    }

    @Test
    void testSearchBorrowingsByHandOverDate_NotFound() throws Exception {
        List<BorrowingResponse> borrowings = List.of(borrowing);
        when(borrowingService.findByHandOverDate(any(LocalDateTime.class), any(LocalDateTime.class), any(BaseSearchRequest.class))).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/borrowing/findByHandOverDate")
                        .param("startDate", LocalDateTime.now(fixedClock).plusDays(1).toString())
                        .param("endDate", LocalDateTime.now(fixedClock).plusDays(2).toString())
                        .param("pageNumber", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isNotFound());

        verify(borrowingService, times(1)).findByHandOverDate(any(LocalDateTime.class), any(LocalDateTime.class), any(BaseSearchRequest.class));
    }

    @Test
    void testSearchBorrowingsByDeviceName_NotFound() throws Exception {
        List<BorrowingResponse> borrowings = List.of(borrowing);
        when(borrowingService.findByDeviceName(anyString(), any(BaseSearchRequest.class))).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/borrowing/findByItemName")
                        .param("itemName", "Item 3")
                        .param("pageNumber", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isNotFound());

        verify(borrowingService, times(1)).findByDeviceName(anyString(), any(BaseSearchRequest.class));
    }

    @Test
    void testSearchBorrowingsByDeviceType_NotFound() throws Exception {
        List<BorrowingResponse> borrowings = List.of(borrowing);
        when(borrowingService.findByDeviceType(any(Type.class), any(BaseSearchRequest.class))).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/borrowing/findByItemType")
                        .param("type", "MOUSE")
                        .param("pageNumber", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isNotFound());

        verify(borrowingService, times(1)).findByDeviceType(any(Type.class), any(BaseSearchRequest.class));
    }

    @Test
    void testSearchBorrowingsByDeviceType_BadRequest() throws Exception {

        mockMvc.perform(get("/api/v1/borrowing/findByItemType")
                        .param("type", "TEST")
                        .param("pageNumber", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isBadRequest());

        verify(borrowingService, never()).findByDeviceType(any(Type.class), any(BaseSearchRequest.class));
    }

    @Test
    void testSearchBorrowingsByTotalPrice_NotFound() throws Exception {
        List<BorrowingResponse> borrowings = List.of(borrowing);
        when(borrowingService.findByTotalPrice(anyDouble(), any(BaseSearchRequest.class))).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/borrowing/findByTotalPrice")
                        .param("totalPrice", "290")
                        .param("pageNumber", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isNotFound());

        verify(borrowingService, times(1)).findByTotalPrice(anyDouble(), any(BaseSearchRequest.class));
    }

    @Test
    void testSearchBorrowingsByTotalPrice_BadRequest() throws Exception {

        mockMvc.perform(get("/api/v1/borrowing/findByTotalPrice")
                        .param("totalPrice", "TEST")
                        .param("pageNumber", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isBadRequest());

        verify(borrowingService, never()).findByTotalPrice(anyDouble(), any(BaseSearchRequest.class));
    }

    @Test
    void testTransferDevice_NotFoundBorrowingOrDevice() throws Exception {
        List<BorrowingResponse> borrowings = List.of(borrowing, borrowing2);
        when(borrowingService.transferDevice(anyInt(), anyInt(), anyInt())).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/borrowing/transferDevice")
                        .param("borrowingIdFrom", "1")
                        .param("borrowingIdTo", "2")
                        .param("deviceId", "1"))
                .andExpect(status().isNotFound());

        verify(borrowingService, times(1)).transferDevice(anyInt(), anyInt(), anyInt());
    }
}
