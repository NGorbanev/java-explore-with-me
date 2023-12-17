package ru.practicum.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.HitDto;
import ru.practicum.StatsDto;
import ru.practicum.controller.StatsController;
import ru.practicum.service.StatsService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = StatsController.class)
public class StatsControllerTest {
    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    StatsService service;

    private HitDto testHitDto = HitDto.builder()
            .id(1L)
            .app("testApp")
            .uri("/testUri")
            .ip("127.0.0.1")
            .timestamp(LocalDateTime.of(2023, 1, 2, 3, 4))
            .build();

    private StatsDto statsDto = StatsDto.builder()
            .app("SomeApp")
            .uri("/someUri")
            .hits(5L)
            .build();

    private List<StatsDto> statsDtoList = List.of(statsDto);

    @Test
    public void saveTest() throws Exception {
        when(service.save(any(HitDto.class))).thenReturn(testHitDto);
        mvc.perform(post("/hit")
                        .content(mapper.writeValueAsString(testHitDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void getTest() throws Exception {
        when(service.get(any(LocalDateTime.class), any(LocalDateTime.class), any(ArrayList.class), any(Boolean.class)))
                .thenReturn(statsDtoList);
        mvc.perform(get("/stats?start=2023-12-17 23:00:00&end=2023-12-17 23:30:00&uris=someUri1&unique=false")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].app", is(statsDto.getApp())))
                .andExpect(jsonPath("$.[0].uri", is(statsDto.getUri())))
                .andExpect(jsonPath("$.[0].hits", is(statsDto.getHits()), Long.class));
    }
}
