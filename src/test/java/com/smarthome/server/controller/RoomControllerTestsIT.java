package com.smarthome.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smarthome.server.entity.Room;
import com.smarthome.server.repository.RoomRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class RoomControllerTestsIT {

    private static final String ROOM_NAME = "room";
    private static final String ROOM_CHANGE_NAME = "xxx";
    private static final int ROOM_ID = 1;
    private static final int ROOM_ID_2 = 2;
    private static final int ROOM_ID_3 = 3;
    private static final Room ROOM_1 = new Room(ROOM_ID, ROOM_NAME, "yes");
    private static final Room ROOM_2 = new Room(ROOM_ID_2, ROOM_NAME, "yes");
    private static final Room ROOM_TO_DELETE = new Room(ROOM_ID_3, ROOM_NAME, "yes");
    private static final Room ROOM_TO_CHANGE = new Room(ROOM_ID, ROOM_CHANGE_NAME, "yes");


    @Autowired
    private MockMvc mvc;
    @Autowired
    private RoomRepository roomRepository;


    @Before
    public void setup() {
        roomRepository.save(ROOM_1);
    }


    @Test
    public void shouldFindAllRooms() throws Exception {
        mvc.perform(get("/rooms")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].roomName", is(ROOM_NAME)));
    }

    @Test
    public void shouldCreateRoom() throws Exception {
        mvc.perform(post("/addRoom")
                        .content(asJsonString(ROOM_2))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertTrue(roomRepository.findById(ROOM_ID_2).isPresent());
        assertEquals(roomRepository.findById(ROOM_ID_2), Optional.of(ROOM_2));
    }

    @Test
    public void shouldChangeRoomName() throws Exception {
        mvc.perform(post("/renameRoom")
                        .content(asJsonString(ROOM_TO_CHANGE))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertTrue(roomRepository.findById(ROOM_ID).isPresent());
        assertEquals(roomRepository.findById(ROOM_ID), Optional.of(ROOM_TO_CHANGE));
    }

    @Test
    public void shouldDelete() throws Exception {
        mvc.perform(delete("/deleteRoom/" + ROOM_TO_DELETE.getId()))
                .andExpect(status().isOk());

        assertTrue(roomRepository.findById(ROOM_TO_DELETE.getId()).isEmpty());
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
