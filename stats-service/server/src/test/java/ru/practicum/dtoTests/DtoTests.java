package ru.practicum.dtoTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.HitDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class DtoTests {

    private final JacksonTester<HitDto> hitJson;
    private HitDto hitDto;

    public DtoTests(@Autowired JacksonTester<HitDto> hitJson) {
        this.hitJson = hitJson;
    }

    @BeforeEach
    public void beforeEach() {
        hitDto = HitDto.builder()
                .id(1L)
                .app("testApp")
                .uri("/testUri")
                .ip("127.0.0.1")
                .timestamp(LocalDateTime.of(2023, 1, 2, 3, 4))
                .build();
    }

    @Test
    public void testHitDto() throws Exception {
        JsonContent<HitDto> result = hitJson.write(hitDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.app").isEqualTo("testApp");
        assertThat(result).extractingJsonPathStringValue("$.uri").isEqualTo("/testUri");
        assertThat(result).extractingJsonPathStringValue("$.ip").isEqualTo("127.0.0.1");
        assertThat(result).extractingJsonPathStringValue("$.timestamp").isEqualTo("2023-01-02 03:04:00");
    }
}
