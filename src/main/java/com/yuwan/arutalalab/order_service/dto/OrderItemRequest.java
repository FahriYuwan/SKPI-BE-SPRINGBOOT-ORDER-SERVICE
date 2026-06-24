package com.yuwan.arutalalab.order_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class OrderItemRequest {

    @NotNull(message = "Product ID tidak boleh kosong")
    private UUID productId;

    @NotNull
    @Min(value = 1, message = "Kuantitas minimal 1")
    private Integer quantity;
}
