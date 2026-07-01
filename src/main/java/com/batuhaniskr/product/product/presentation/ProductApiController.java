package com.batuhaniskr.product.product.presentation;

import com.batuhaniskr.product.product.domain.ProductDTO;
import com.batuhaniskr.product.product.application.ProductService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/products")
@Api(value = "Productos", description = "API de gestion de productos e inventario")
public class ProductApiController {

    private final ProductService productService;

    @Autowired
    public ProductApiController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @ApiOperation(value = "Listar productos", notes = "Obtiene lista paginada de productos del usuario autenticado")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Lista de productos obtenida exitosamente"),
        @ApiResponse(code = 401, message = "No autorizado")
    })
    public ResponseEntity<Page<ProductDTO>> getAllProducts(
            @ApiParam(value = "Numero de pagina (inicia en 1)", example = "1")
            @RequestParam(defaultValue = "1") int page,
            @ApiParam(value = "Tamano de pagina", example = "5")
            @RequestParam(defaultValue = "5") int size,
            Principal principal) {

        Pageable pageable = new PageRequest(page - 1, size);
        Page<ProductDTO> products = productService.getAllProduct(pageable, principal.getName());
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Obtener producto por ID", notes = "Retorna un producto especifico por su ID")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Producto encontrado"),
        @ApiResponse(code = 404, message = "Producto no encontrado")
    })
    public ResponseEntity<ProductDTO> getProduct(
            @ApiParam(value = "ID del producto", example = "1")
            @PathVariable Integer id) {
        ProductDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @PostMapping
    @ApiOperation(value = "Crear nuevo producto", notes = "Crea un producto en el inventario del usuario autenticado")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Producto creado exitosamente"),
        @ApiResponse(code = 400, message = "Datos de producto invalidos")
    })
    public ResponseEntity<Void> createProduct(
            @ApiParam(value = "Datos del producto a crear")
            @RequestBody ProductDTO productDTO,
            Principal principal) {
        productService.saveProduct(productDTO, principal.getName());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Eliminar producto", notes = "Elimina un producto del inventario. Requiere rol ADMIN.")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Producto eliminado exitosamente"),
        @ApiResponse(code = 403, message = "Acceso denegado"),
        @ApiResponse(code = 404, message = "Producto no encontrado")
    })
    public ResponseEntity<Void> deleteProduct(
            @ApiParam(value = "ID del producto a eliminar", example = "1")
            @PathVariable Integer id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }
}
