package com.yuwan.arutalalab.order_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class OrderItemResponse {

    private UUID productId;
    private String productName;
    private BigDecimal productPrice;
    private Integer quantity;
}
