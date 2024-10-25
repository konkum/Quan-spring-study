package com.example.spring_study.controller;

import com.example.spring_study.constant.RateType;
import com.example.spring_study.constant.Type;
import com.example.spring_study.model.DateAudit;
import com.example.spring_study.model.Device;
import com.example.spring_study.model.payload.BaseSearchRequest;
import com.example.spring_study.model.payload.BaseSortRequest;
import com.example.spring_study.model.payload.DeviceRequest;
import com.example.spring_study.service.DeviceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DeviceController.class)
@AutoConfigureMockMvc(addFilters = false)
public class DeviceControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeviceService deviceService;

    @Autowired
    private ObjectMapper objectMapper;


    private Device device1;
    private Device device2;
    private Clock fixedClock;
    private DeviceRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        fixedClock = Clock.fixed(Instant.parse("2024-10-23T17:04:11.983812500Z"), ZoneId.systemDefault());

        DateAudit dateAudit;
        dateAudit = new DateAudit();
        dateAudit.setCreatedAt(LocalDateTime.now(fixedClock).minusDays(1).truncatedTo(ChronoUnit.MICROS));

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

        DateAudit dateAudit1;
        dateAudit1 = new DateAudit();
        dateAudit1.setCreatedAt(LocalDateTime.now(fixedClock).truncatedTo(ChronoUnit.MICROS));

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
    void testCreateDevice() throws Exception {
        when(deviceService.createDevice(any(DeviceRequest.class))).thenReturn(device1);

        mockMvc.perform(post("/api/v1/device/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.type").value("MOUSE"))
                .andExpect(jsonPath("$.unitPrice").value(110.0))
                .andExpect(jsonPath("$.rateType").value("LIKENEW"));

        verify(deviceService,times(1)).createDevice(any(DeviceRequest.class));
    }

    @Test
    void testGetDeviceById() throws Exception {
        when(deviceService.getDeviceById(anyInt())).thenReturn(device1);

        mockMvc.perform(get("/api/v1/device/get")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("id","1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.type").value("MOUSE"))
                .andExpect(jsonPath("$.unitPrice").value(110.0))
                .andExpect(jsonPath("$.rateType").value("LIKENEW"));

        verify(deviceService,times(1)).getDeviceById(anyInt());
    }

    @Test
    void testGetDevices() throws Exception {
        when(deviceService.getAllDevices(any())).thenReturn(new PageImpl<>(List.of(device1,device2)));

        mockMvc.perform(get("/api/v1/device/getAll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("pageNumber","0")
                        .param("pageSize","10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content",hasSize(2)))
                .andExpect(jsonPath("$.content[0].id").value(1));

        verify(deviceService,times(1)).getAllDevices(any());
    }

    @Test
    void testDelete() throws Exception {
        when(deviceService.deleteDevice(anyInt())).thenReturn(true);

        mockMvc.perform(delete("/api/v1/device/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("id","1"))
                .andExpect(status().isOk());

        verify(deviceService,times(1)).deleteDevice(anyInt());
    }

    @Test
    void testGetDevicesSortByOriginalPrice() throws Exception {
        when(deviceService.getDevicesSortedBy(any())).thenReturn(new PageImpl<>(List.of(device1,device2)));

        mockMvc.perform(get("/api/v1/device/getDevicesSortBy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("pageNumber","0")
                        .param("pageSize","10")
                        .param("sortString","originalPrice")
                        .param("sortDirection","ASC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content",hasSize(2)))
                .andExpect(jsonPath("$.content[0].id").value(1));

        verify(deviceService,times(1)).getDevicesSortedBy(any());
    }

    @Test
    void testGetDevicesSortByCreatedDate() throws Exception {
        when(deviceService.getDevicesSortedBy(any())).thenReturn(new PageImpl<>(List.of(device1,device2)));

        mockMvc.perform(get("/api/v1/device/getDevicesSortBy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("pageNumber","0")
                        .param("pageSize","10")
                        .param("sortString","dateAudit.createdAt")
                        .param("sortDirection","ASC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content",hasSize(2)))
                .andExpect(jsonPath("$.content[0].dateAudit.createdAt").value(device1.getDateAudit().getCreatedAt().toString()))
                .andExpect(jsonPath("$.content[1].dateAudit.createdAt").value(device2.getDateAudit().getCreatedAt().toString()));

        verify(deviceService,times(1)).getDevicesSortedBy(any());
    }

    @Test
    void testSearchDeviceByCreatedAtDate() throws Exception {
        when(deviceService.findDeviceByDate(any(),any(),any())).thenReturn(new PageImpl<>(List.of(device1)));

        mockMvc.perform(get("/api/v1/device/findByCreatedDate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("startDate",LocalDateTime.now(fixedClock).minusDays(1).toString())
                        .param("endDate",LocalDateTime.now(fixedClock).plusDays(1).toString())
                        .param("pageNumber","0")
                        .param("pageSize","10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content",hasSize(1)))
                .andExpect(jsonPath("$.content[0].dateAudit.createdAt").value(device1.getDateAudit().getCreatedAt().toString()));

        verify(deviceService,times(1)).findDeviceByDate(any(),any(),any());
    }

    @Test
    void testSearchDeviceByItemName() throws Exception {
        when(deviceService.findDeviceByItemName(anyString(),any())).thenReturn(new PageImpl<>(List.of(device1)));

        mockMvc.perform(get("/api/v1/device/findByItemName")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("itemName","Item 1")
                        .param("pageNumber","0")
                        .param("pageSize","10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content",hasSize(1)))
                .andExpect(jsonPath("$.content[0].itemName").value("Item 1"));

        verify(deviceService,times(1)).findDeviceByItemName(anyString(),any());
    }

    @Test
    void testSearchDeviceByType() throws Exception {
        when(deviceService.findDeviceByType(any(),any())).thenReturn(new PageImpl<>(List.of(device1)));

        mockMvc.perform(get("/api/v1/device/findByType")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("type","MOUSE")
                        .param("pageNumber","0")
                        .param("pageSize","10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content",hasSize(1)))
                .andExpect(jsonPath("$.content[0].type").value("MOUSE"));

        verify(deviceService,times(1)).findDeviceByType(any(),any());
    }

    @Test
    void testSearchDeviceByRateType() throws Exception {
        when(deviceService.findDeviceByRateType(any(),any())).thenReturn(new PageImpl<>(List.of(device1)));

        mockMvc.perform(get("/api/v1/device/findByRateType")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("rateType","LIKENEW")
                        .param("pageNumber","0")
                        .param("pageSize","10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content",hasSize(1)))
                .andExpect(jsonPath("$.content[0].rateType").value("LIKENEW"));

        verify(deviceService,times(1)).findDeviceByRateType(any(),any());
    }

    @Test
    void testCreateDevice_TypeError() throws Exception {
        request.setType("TEST");
        when(deviceService.createDevice(any(DeviceRequest.class))).thenReturn(null);

        mockMvc.perform(post("/api/v1/device/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotAcceptable());

        verify(deviceService,times(1)).createDevice(any(DeviceRequest.class));
    }

    @Test
    void testGetDeviceById_NotFound() throws Exception {
        when(deviceService.getDeviceById(999)).thenReturn(null);

        mockMvc.perform(get("/api/v1/device/get")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("id","999"))
                .andExpect(status().isNotFound());

        verify(deviceService,times(1)).getDeviceById(anyInt());
    }

    @Test
    void testGetDevices_NotFound() throws Exception {
        when(deviceService.getAllDevices(any(BaseSearchRequest.class))).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/device/getAll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("pageNumber","0")
                        .param("pageSize","0"))
                .andExpect(status().isNotFound());

        verify(deviceService,times(1)).getAllDevices(any());
    }

    @Test
    void testDelete_CannotDelete() throws Exception {
        when(deviceService.deleteDevice(999)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/device/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("id","999"))
                .andExpect(status().isNotFound());

        verify(deviceService,times(1)).deleteDevice(anyInt());
    }

    @Test
    void testGetDevicesSortByOriginalPrice_NotFound() throws Exception {
        when(deviceService.getDevicesSortedBy(any(BaseSortRequest.class))).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/device/getDevicesSortBy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("pageNumber","0")
                        .param("pageSize","10")
                        .param("sortString","originalPrice")
                        .param("sortDirection","ASC"))
                .andExpect(status().isNotFound());

        verify(deviceService,times(1)).getDevicesSortedBy(any());
    }

    @Test
    void testSearchDeviceByCreatedAtDate_NotFound() throws Exception {
        when(deviceService.findDeviceByDate(any(),any(),any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/device/findByCreatedDate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("startDate",LocalDateTime.now(fixedClock).plusHours(1).toString())
                        .param("endDate",LocalDateTime.now(fixedClock).plusDays(1).toString())
                        .param("pageNumber","0")
                        .param("pageSize","10"))
                .andExpect(status().isNotFound());

        verify(deviceService,times(1)).findDeviceByDate(any(),any(),any());
    }

    @Test
    void testSearchDeviceByItemName_NotFound() throws Exception {
        when(deviceService.findDeviceByItemName(anyString(),any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/device/findByItemName")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("itemName","Item 3")
                        .param("pageNumber","0")
                        .param("pageSize","10"))
                .andExpect(status().isNotFound());

        verify(deviceService,times(1)).findDeviceByItemName(anyString(),any());
    }

    @Test
    void testSearchDeviceByType_BadRequest() throws Exception {

        mockMvc.perform(get("/api/v1/device/findByType")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("type","TEST")
                        .param("pageNumber","0")
                        .param("pageSize","10"))
                .andExpect(status().isBadRequest());

        verify(deviceService,never()).findDeviceByType(any(),any());
    }

    @Test
    void testSearchDeviceByType_NotFound() throws Exception {
        when(deviceService.findDeviceByType(any(),any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/device/findByType")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("type","MOUSE")
                        .param("pageNumber","0")
                        .param("pageSize","10"))
                .andExpect(status().isNotFound());

        verify(deviceService,times(1)).findDeviceByType(any(),any());
    }

    @Test
    void testSearchDeviceByRateType_BadRequest() throws Exception {

        mockMvc.perform(get("/api/v1/device/findByRateType")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("rateType","TEST")
                        .param("pageNumber","0")
                        .param("pageSize","10"))
                .andExpect(status().isBadRequest());

        verify(deviceService,never()).findDeviceByRateType(any(),any());
    }

    @Test
    void testSearchDeviceByRateType_NotFound() throws Exception {
        when(deviceService.findDeviceByRateType(any(),any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/device/findByRateType")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("rateType","NEW")
                        .param("pageNumber","0")
                        .param("pageSize","10"))
                .andExpect(status().isNotFound());

        verify(deviceService,times(1)).findDeviceByRateType(any(),any());
    }

}
