package com.smarthome.server.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.smarthome.server.entity.Device;
import com.smarthome.server.entity.requests.RenameDeviceRequest;
import com.smarthome.server.repository.DeviceRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DeviceControllerTestsIT {


    private final static int DEVICE_SERIAL_1 = 11;
    private final static int DEVICE_SERIAL_2 = 22;
    private final static int DEVICE_SERIAL_3 = 33;
    private final static int DEVICE_SERIAL_4 = 44;
    private final static int DEVICE_SERIAL_5 = 44;
    private final static int ROOM_ID_1 = 111;
    private final static int ROOM_ID_2 = 222;
    private final static int ROOM_ID_3 = 333;
    private final static Device DEVICE_1 = Device.builder().serial(DEVICE_SERIAL_1).brightness(100).deviceStatus("On").roomID(ROOM_ID_1).build();
    private final static Device DEVICE_2 = Device.builder().serial(DEVICE_SERIAL_2).brightness(100).deviceStatus("On").roomID(ROOM_ID_2).build();
    private final static Device DEVICE_3 = Device.builder().serial(DEVICE_SERIAL_3).brightness(100).deviceStatus("On").roomID(ROOM_ID_3).build();
    private final static Device DEVICE_CHANGE_STATUS = Device.builder().serial(DEVICE_SERIAL_5).brightness(100).deviceStatus("On").roomID(ROOM_ID_3).build();
    private final static RenameDeviceRequest DEVICE_3_WITH_NAME = RenameDeviceRequest.builder().deviceSerial(DEVICE_SERIAL_3).newDeviceName("NAME").build();
    private final static Device DEVICE_TO_DELETE = Device.builder().serial(DEVICE_SERIAL_4).deviceName("NAME").build();

    @Autowired
    private MockMvc mvc;
    @Autowired
    private DeviceRepository repository;

    @Before
    public void setup() {
        repository.save(DEVICE_1);
        repository.save(DEVICE_2);
        repository.save(DEVICE_TO_DELETE);
        repository.save(DEVICE_CHANGE_STATUS);
    }

    @Test
    public void shouldFindAllDevices() throws Exception {
        mvc.perform(get("/findAllDevices")).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void shouldFindByDeviceId() throws Exception {
        mvc.perform(get("/getDeviceByRoomID?roomID=" + DEVICE_2.getRoomID())).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].serial", is(DEVICE_2.getSerial())));
    }

    @Test
    public void shouldCreateDevice() throws Exception {
        mvc.perform(post("/addDevice")
                        .content(asJsonString(DEVICE_3))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertTrue(repository.findBySerial(DEVICE_SERIAL_3).isPresent());
        assertEquals(repository.findBySerial(DEVICE_SERIAL_3), Optional.of(DEVICE_3));
    }

    @Test
    public void shouldChangeNameOfDevice() throws Exception {
        mvc.perform(post("/renameDevice")
                        .content(asJsonString(DEVICE_3_WITH_NAME))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertTrue(repository.findBySerial(DEVICE_SERIAL_3).isPresent());
        assertEquals(repository.findBySerial(DEVICE_SERIAL_3).get().getDeviceName(), DEVICE_3_WITH_NAME.getNewDeviceName());
    }

    @Test
    public void shouldDeleteDevice() throws Exception {
        mvc.perform(post("/deleteDevice")
                        .content(asJsonString(DEVICE_TO_DELETE))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertTrue(repository.findBySerial(DEVICE_TO_DELETE.getSerial()).isEmpty());
    }

    @Test
    public void shouldChangeDeviceStatus() throws Exception {
        mvc.perform(get("/changeDeviceStatus-http/" + DEVICE_CHANGE_STATUS.getSerial())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertTrue(repository.findBySerial(DEVICE_CHANGE_STATUS.getSerial()).isPresent());
        assertEquals(repository.findBySerial(DEVICE_CHANGE_STATUS.getSerial()).get().getDeviceStatus(), "Off");
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
