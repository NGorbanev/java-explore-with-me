package ru.practicum.ewm.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.service.CategoryService;

import java.util.List;

import static ru.practicum.ewm.Constants.API_LOGSTRING;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PublicCategoryController {
    private final CategoryService categoryService;

    @GetMapping("/categories")
    public List<CategoryDto> findCategories(@RequestParam(required = false, defaultValue = "0") Integer from,
                                            @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("{} GET /categories. From={}, Size={}", API_LOGSTRING, from, size);
        return categoryService.findCategoryDtosList(from, size);
    }

    @GetMapping("categories/{categoryId}")
    public CategoryDto findCategoryById(@PathVariable Long categoryId) {
        log.info("{} GET categories/{catId}. CategoryId={}", API_LOGSTRING, categoryId);
        return categoryService.getCategoryDtoById(categoryId);
    }
}
