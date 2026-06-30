package com.batuhaniskr.product.category.presentation;

import com.batuhaniskr.product.category.domain.CategoryDTO;
import com.batuhaniskr.product.category.application.CategoryService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Api(value = "Categorías", description = "API de categorías de productos")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    @ApiOperation(value = "Listar todas las categorías", notes = "Obtiene el catálogo completo de categorías para clasificar los productos")
    public List<CategoryDTO> getAllCategories() {
        return categoryService.getAllCategory();
    }
}