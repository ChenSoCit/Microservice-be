package com.example.product_service.dtos.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class ProductRequest {
    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String nameProduct;

    private String description;

    @NotNull(message = "Giá không được null")
    @Positive(message = "Giá phải > 0")
    private BigDecimal price;

    @NotNull(message = "Số lượng tồn kho không được null")
    @Min(value = 0, message = "Số lượng tồn kho phải >= 0")
    private Integer stockQuantity;

    @NotNull(message = "CategoryId không được null")
    private Integer categoryId;


}
