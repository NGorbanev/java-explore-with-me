package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.IncomingCategoryDto;
import ru.practicum.ewm.category.model.Category;

import java.util.List;

public interface CategoryService {

    CategoryDto addCategory(IncomingCategoryDto newCategoryDto);

    void removeCategory(Long categoryId);

    CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto);

    List<CategoryDto> findCategoryDtosList(Integer from, Integer size);

    CategoryDto getCategoryDtoById(Long categoryId);

    Category getCategoryById(Long categoryId);
}
