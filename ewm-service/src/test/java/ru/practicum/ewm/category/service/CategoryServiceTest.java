package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.IncomingCategoryDto;
import ru.practicum.ewm.exceptions.CategoryNotFoundException;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CategoryServiceTest {
    private final CategoryService service;

    @Test
    public void addAndFindCategoryTest() {
        CategoryDto result = service.addCategory(IncomingCategoryDto.builder()
                .name("Test category")
                .build());
        Assertions.assertEquals(1, service.findCategoryDtosList(0, 10).size());
        Assertions.assertEquals(result.getName(), service.getCategoryDtoById(result.getId()).getName());
    }

    @Test
    public void updateCategoryTest() {
        CategoryDto newCategory = service.addCategory(IncomingCategoryDto.builder()
                .name("Test category")
                .build());
        CategoryDto updatedCategory = newCategory;
        updatedCategory.setName("Updated category");
        CategoryDto result = service.updateCategory(newCategory.getId(), updatedCategory);
        Assertions.assertEquals(newCategory.getId(), result.getId());
        Assertions.assertEquals(updatedCategory.getName(), result.getName());
    }

    @Test
    public void getWrongCategoryIdTest() {
        Assertions.assertThrows(CategoryNotFoundException.class, () -> service.getCategoryById(100L));
    }

    @Test
    public void removeCategoryTest() {
        CategoryDto newCategory = service.addCategory(IncomingCategoryDto.builder()
                .name("Test category")
                .build());
        service.removeCategory(newCategory.getId());
        Assertions.assertTrue(service.findCategoryDtosList(0, 10).isEmpty());
    }
}
