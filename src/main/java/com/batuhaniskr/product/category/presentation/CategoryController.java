package com.batuhaniskr.product.category.presentation;

import com.batuhaniskr.product.category.domain.CategoryDTO;
import com.batuhaniskr.product.category.application.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/api")
    @ResponseBody
    public List<CategoryDTO> getAllCategories() {
        return categoryService.getAllCategory();
    }
}
