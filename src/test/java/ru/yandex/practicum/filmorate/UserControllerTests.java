package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.MvcResult;
import ru.yandex.practicum.filmorate.controllers.UserController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.yandex.practicum.filmorate.TestUsers.*;


@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserController controller;

    @Order(1)
    @Test
    public void shouldPostTheCommonUser() throws Exception {
        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(user))
                )
                .andExpect(status().isOk());
    }

    @Order(2)
    @Test
    public void shouldPostTheCommonUserTwice() throws Exception {
        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(user))
                )
                .andExpect(status().isConflict());
    }

    @Order(3)
    @Test
    public void shouldPostTheUserWithEmptyEmail() throws Exception {
        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userWithEmptyEmail))
                )
                .andExpect(status().isBadRequest());
    }

    @Order(4)
    @Test
    public void shouldPostTheUserWithEmptyLogin() throws Exception {
        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userWithEmptyLogin))
                )
                .andExpect(status().isBadRequest());
    }

    @Order(5)
    @Test
    public void shouldPostTheUserWithLoginWithSpaces() throws Exception {
        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userWithLoginWithSpacesJSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Order(6)
    @Test
    public void shouldEditNameWithEmptyName() throws Exception {
        userWithEmptyName.setId(1);
        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(userWithEmptyName))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @Order(7)
    @Test
    public void shouldPostUserFromFuture() throws Exception {
        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userFromFutureJSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Order(8)
    @Test
    public void shouldPutTheCommonUserTwice() throws Exception {
        mockMvc.perform(
                        put("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(user))
                )
                .andExpect(status().isConflict());
    }

    @Order(9)
    @Test
    public void shouldUpdateTheCommonUser() throws Exception {
        updatedUser.setId(1);
        MvcResult result = mockMvc.perform(
                        put("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedUser))
                )
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        Assertions.assertEquals("User id: 1 updated", content);
    }
}