package ru.practicum.ewm.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.CompilationUpdateRequest;
import ru.practicum.ewm.compilation.dto.IncomingCompilationDto;
import ru.practicum.ewm.compilation.service.CompilationService;

import javax.validation.Valid;

import static ru.practicum.ewm.Constants.API_LOGSTRING;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AdminCompilationController {
    private final CompilationService compilationService;


    @PostMapping(value = "/admin/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addCompilation(@Valid @RequestBody IncomingCompilationDto incomingCompilationDto) {
        log.info("{} POST /admin/compilations. IncomingCompilationDto={}", API_LOGSTRING, incomingCompilationDto.toString());
        return compilationService.addCompilation(incomingCompilationDto);
    }

    @PatchMapping(value = "/admin/compilations/{compilationId}")
    public CompilationDto updateCompilation(@PathVariable Long compilationId,
                                            @Valid @RequestBody CompilationUpdateRequest updateRequest) {
        log.info("{} PATCH /admin/compilations/{compId}. CompilationId={}, UpdateCompilationRequest={}",
                API_LOGSTRING,
                compilationId,
                updateRequest.toString());
        return compilationService.updateCompilation(compilationId, updateRequest);
    }

    @DeleteMapping(value = "/admin/compilations/{compilationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compilationId) {
        log.info("{} DELETE /admin/compilations/{compilationId}. CompilationId={}", API_LOGSTRING, compilationId);
        compilationService.deleteCompilation(compilationId);
    }

}
