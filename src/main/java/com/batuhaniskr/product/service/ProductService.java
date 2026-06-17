package com.batuhaniskr.product.service;

import com.batuhaniskr.product.dto.ProductDTO;
import com.batuhaniskr.product.model.Category;
import com.batuhaniskr.product.model.Product;
import com.batuhaniskr.product.model.Role;
import com.batuhaniskr.product.model.User;
import com.batuhaniskr.product.repository.CategoryRepository;
import com.batuhaniskr.product.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductService implements IProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository,
                          UserService userService,
                          ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @Override
    public Page<ProductDTO> getAllProduct(Pageable pageable, String username) {
        User user = userService.findByEmail(username);
        
        if (isAdmin(user)) {
            return productRepository.findAll(pageable)
                    .map(x -> modelMapper.map(x, ProductDTO.class));
        }

        return productRepository.findByUser(pageable, user)
                .map(x -> modelMapper.map(x, ProductDTO.class));
    }

    // Método extraído (Responsabilidad Única)
    private boolean isAdmin(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));
    }

    @Override
    public void saveProduct(ProductDTO productDTO, String email) {
        User user = userService.findByEmail(email);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
        Product product = modelMapper.map(productDTO, Product.class);
        Category category = categoryRepository.findByCategoryName(product.getCategory().getCategoryName());
        product.setCategory(category);
        product.setUser(user);

        productRepository.save(product);
    }

    @Override
    public void deleteProduct(Integer id) {
        productRepository.delete(id);
    }

    @Override
    public ProductDTO getProductById(Integer id) {
        Product product = productRepository.findOne(id);
        return modelMapper.map(product, ProductDTO.class);
    }
}