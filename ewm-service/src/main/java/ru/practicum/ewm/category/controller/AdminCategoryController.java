package ru.practicum.ewm.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.IncomingCategoryDto;
import ru.practicum.ewm.category.service.CategoryService;

import javax.validation.Valid;

import static ru.practicum.ewm.Constants.API_LOGSTRING;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
public class AdminCategoryController {
    private final CategoryService categoryService;

    @PostMapping(value = "admin/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@Valid @RequestBody IncomingCategoryDto incomingCategoryDto) {
        log.info("{} POST admin/categories. IncomingCategoryDto={}", API_LOGSTRING, incomingCategoryDto.toString());
        return categoryService.addCategory(incomingCategoryDto);
    }

    @DeleteMapping(value = "admin/categories/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCategory(@PathVariable Long categoryId) {
        log.info("{} DELETE admin/categories/{catId}. CategoryId={}", API_LOGSTRING, categoryId);
        categoryService.removeCategory(categoryId);
    }

    @PatchMapping(value = "admin/categories/{categoryId}")
    public CategoryDto updateCategory(@PathVariable Long categoryId,
                                      @Valid @RequestBody CategoryDto categoryDto) {
        log.info("{} PATCH admin/categories/{catId}. CategoryId={}, CategoryDto={}", API_LOGSTRING, categoryId, categoryDto.toString());
        return categoryService.updateCategory(categoryId, categoryDto);
    }
}
