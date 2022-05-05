package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import ru.yandex.practicum.filmorate.controllers.FilmController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.yandex.practicum.filmorate.TestFilms.*;


@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FilmControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FilmController controller;

    @Order(1)
    @Test
    public void shouldPostTheCommonFilm() throws Exception {
        mockMvc.perform(
                        post("/films")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(film))
                )
                .andExpect(status().isOk());
    }

    @Order(2)
    @Test
    public void shouldPostTheCommonFilmTwice() throws Exception {
        mockMvc.perform(
                        post("/films")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(film))
                )
                .andExpect(status().isConflict());
    }

    @Order(3)
    @Test
    public void shouldPostTheOldFilm() throws Exception {
        mockMvc.perform(
                        post("/films")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(oldMovieJSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Order(4)
    @Test
    public void shouldPostTheFilmWithEmptyTitle() throws Exception {
        mockMvc.perform(
                        post("/films")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(filmWithEmptyTitle))
                )
                .andExpect(status().isBadRequest());
    }

    @Order(5)
    @Test
    public void shouldPostTheFilmWith200CharDescription() throws Exception {
        mockMvc.perform(
                        post("/films")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(filmWith200CharDescription))
                )
                .andExpect(status().isOk());
    }

    @Order(6)
    @Test
    public void shouldPostTheFilmWith201CharDescription() throws Exception {
        mockMvc.perform(
                        post("/films")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(filmWith201CharDescription))
                )
                .andExpect(status().isBadRequest());
    }

    @Order(7)
    @Test
    public void shouldPostTheFilmWitNegativeDuration() throws Exception {
        mockMvc.perform(
                        post("/films")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(filmWithNegativeDuration))
                )
                .andExpect(status().isBadRequest());
    }

    @Order(8)
    @Test
    public void shouldPutTheCommonFilmTwice() throws Exception {
        mockMvc.perform(
                        put("/films")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(film))
                )
                .andExpect(status().isConflict());
    }

    @Order(9)
    @Test
    public void shouldUpdateTheCommonFilm() throws Exception {
        mockMvc.perform(
                        put("/films")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedFilm))
                )
                .andExpect(status().isOk());
    }
}
