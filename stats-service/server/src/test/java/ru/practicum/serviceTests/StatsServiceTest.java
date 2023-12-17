package ru.practicum.serviceTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.HitDto;
import ru.practicum.StatsDto;
import ru.practicum.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@SpringBootTest
@Transactional
public class StatsServiceTest {
    private StatsService statsService;

    @Autowired
    public StatsServiceTest(StatsService statsService) {
        this.statsService = statsService;
    }

    private HitDto hitDtoCreate() {
        int r = ThreadLocalRandom.current().nextInt(0, 254);
        return HitDto.builder().app("SomeApp")
                .uri("/someUrl")
                .ip("192.168.0." + r)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Test
    public void saveHitTest() {
        HitDto hitDto = hitDtoCreate();
        HitDto savedHitDto = statsService.save(hitDto);
        Assertions.assertEquals(hitDto.getApp(), savedHitDto.getApp(), "hitDto app is wrong");
        Assertions.assertEquals(hitDto.getUri(), savedHitDto.getUri(), "hitDto uri is wrong");
        Assertions.assertEquals(hitDto.getIp(), savedHitDto.getIp(), "hitDto IP is wrong");
        Assertions.assertEquals(hitDto.getTimestamp(), savedHitDto.getTimestamp(), "hitDto timestamp is wrong");
    }

    @Test
    public void getStatsTest() throws InterruptedException {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusSeconds(3);
        List<String> uris = List.of("/someUrl");
        for (int i = 0; i < 5; i++) {
            statsService.save(hitDtoCreate());
            Thread.sleep(1_000L);
        }
        List<StatsDto> statsDto = statsService.get(startTime, endTime, uris, false);
        Assertions.assertEquals(1, statsDto.size());
        Assertions.assertEquals("SomeApp", statsDto.get(0).getApp());
        Assertions.assertEquals("/someUrl", statsDto.get(0).getUri());
        Assertions.assertEquals(3, statsDto.get(0).getHits());
        System.out.println(statsService.get(startTime, endTime, uris, false).toString());
    }
}
