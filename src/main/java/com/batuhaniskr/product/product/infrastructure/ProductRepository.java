package com.batuhaniskr.product.product.infrastructure;

import com.batuhaniskr.product.product.domain.Product;
import com.batuhaniskr.product.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    Page<Product> findByUser(Pageable pageable, User user);
}
