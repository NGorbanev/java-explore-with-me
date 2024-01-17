package ru.practicum.ewm.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.service.CompilationService;

import java.util.List;

import static ru.practicum.ewm.Constants.API_LOGSTRING;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class PublicCompilationController {
    private final CompilationService compilationService;

    @GetMapping("/compilations")
    public List<CompilationDto> findCompilations(@RequestParam(defaultValue = "false") String pinned,
                                                 @RequestParam(defaultValue = "0") Integer from,
                                                 @RequestParam(defaultValue = "10") Integer size) {
        log.info("{} GET /compilations. Pinned={}, from={}, size={}", API_LOGSTRING, pinned, from, size);
        return compilationService.findCompilations(Boolean.valueOf(pinned), from, size);
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto findCompilationById(@PathVariable Long compId) {
        log.info("{} GET /compilations/{compId}. CompilationId={}", API_LOGSTRING, compId);
        return compilationService.findCompilationById(compId);
    }
}
