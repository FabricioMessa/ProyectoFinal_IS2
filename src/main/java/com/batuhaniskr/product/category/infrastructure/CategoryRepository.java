package com.batuhaniskr.product.category.infrastructure;

import com.batuhaniskr.product.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    Category findByCategoryName(String name);
}
