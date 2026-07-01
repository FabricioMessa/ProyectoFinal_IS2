package com.batuhaniskr.product.category.application;

import com.batuhaniskr.product.category.domain.Category;
import com.batuhaniskr.product.category.domain.CategoryDTO;
import com.batuhaniskr.product.category.infrastructure.CategoryRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    public void getAllCategory_ShouldReturnCategoryList() {
        // Arrange
        Category cat1 = new Category();
        cat1.setId(1);
        cat1.setCategoryName("Electronica");

        Category cat2 = new Category();
        cat2.setId(2);
        cat2.setCategoryName("Ropa");

        CategoryDTO dto1 = new CategoryDTO();
        dto1.setId(1);
        dto1.setCategoryName("Electronica");

        CategoryDTO dto2 = new CategoryDTO();
        dto2.setId(2);
        dto2.setCategoryName("Ropa");

        when(categoryRepository.findAll()).thenReturn(Arrays.asList(cat1, cat2));
        when(modelMapper.map(cat1, CategoryDTO.class)).thenReturn(dto1);
        when(modelMapper.map(cat2, CategoryDTO.class)).thenReturn(dto2);

        // Act
        List<CategoryDTO> result = categoryService.getAllCategory();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCategoryName()).isEqualTo("Electronica");
        assertThat(result.get(1).getCategoryName()).isEqualTo("Ropa");
    }

    @Test
    public void getAllCategory_WhenEmpty_ShouldReturnEmptyList() {
        // Arrange
        when(categoryRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<CategoryDTO> result = categoryService.getAllCategory();

        // Assert
        assertThat(result).isEmpty();
    }
}
