package com.batuhaniskr.product.service;

import com.batuhaniskr.product.dto.CategoryDTO;
import com.batuhaniskr.product.model.Category;
import com.batuhaniskr.product.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Autowired // Todo inyectado por constructor (Buenas prácticas)
    public CategoryService(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    public List<CategoryDTO> getAllCategory() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(this::toDTO) // Refactoring Issue #9: Referencia actualizada
                .collect(Collectors.toList());
    }

    // Refactoring Issue #9: Rename Method aplicado
    private CategoryDTO toDTO(Category category) {
        return modelMapper.map(category, CategoryDTO.class);
    }
}