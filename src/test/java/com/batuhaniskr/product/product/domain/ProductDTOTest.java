package com.batuhaniskr.product.product.domain;

import org.junit.Before;
import org.junit.Test;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductDTOTest {

    private Validator validator;

    @Before
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void price_Negative_ShouldFailValidation() {
        ProductDTO dto = new ProductDTO();
        dto.setPrice(new BigDecimal("-100.00"));
        dto.setQuantity(1);

        assertThat(validator.validate(dto)).isNotEmpty();
    }

    @Test
    public void price_Zero_ShouldPassValidation() {
        ProductDTO dto = new ProductDTO();
        dto.setPrice(BigDecimal.ZERO);
        dto.setQuantity(1);

        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    public void price_Positive_ShouldPassValidation() {
        ProductDTO dto = new ProductDTO();
        dto.setPrice(new BigDecimal("100.00"));
        dto.setQuantity(1);

        assertThat(validator.validate(dto)).isEmpty();
    }
}
