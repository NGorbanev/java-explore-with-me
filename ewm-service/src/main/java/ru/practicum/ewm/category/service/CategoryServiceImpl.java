package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.IncomingCategoryDto;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.category.utils.CategoryMapper;
import ru.practicum.ewm.exceptions.CategoryNotFoundException;
import ru.practicum.ewm.exceptions.DataValidationException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto addCategory(IncomingCategoryDto newCategoryDto) {
        log.info("Add new category is servicing..");
        Category category = CategoryMapper.toCategory(newCategoryDto);
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public void removeCategory(Long categoryId) {
        log.info("Remove category id={} is servicing", categoryId);
        if (!categoryRepository.existsById(categoryId)) {
            throw new DataValidationException(String.format("Category id=%s not found", categoryId));
        }
        categoryRepository.deleteById(categoryId);
        log.info("Category id {} was deleted", categoryId);
    }

    @Override
    public CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto) {
        log.info("Category update request is servicng. Category id={}, New category={}", categoryId, categoryDto.toString());
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(String.format("Category id=%s was not found", categoryId)));
        category.setName(categoryDto.getName());
        log.info("Updating category id={}", categoryId);
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> findCategoryDtosList(Integer from, Integer size) {
        log.info("Find Category request is servicing.. Pagination from={} to={}", from, size);
        Pageable pageable = PageRequest.of(from / size, size);
        List<Category> categories = categoryRepository.findAll(pageable).getContent();
        return CategoryMapper.toDtos(categories);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategoryDtoById(Long categoryId) {
        log.info("Find categoryDto id={} request received", categoryId);
        Category category = getCategoryById(categoryId);
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    @Transactional(readOnly = true)
    public Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(String.format("Category id=%s was not found", categoryId)));
    }
}
