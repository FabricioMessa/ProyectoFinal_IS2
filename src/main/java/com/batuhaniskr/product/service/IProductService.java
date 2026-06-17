package com.batuhaniskr.product.service;

import com.batuhaniskr.product.dto.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IProductService {
    Page<ProductDTO> getAllProduct(Pageable pageable, String username);
    void saveProduct(ProductDTO productDTO, String email);
    void deleteProduct(Integer id);
    ProductDTO getProductById(Integer id);
}