package com.yuwan.arutalalab.order_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    @NotBlank(message = "Nama customer harus diisi")
    private String customerName;

    @NotBlank
    @Email(message = "Format email Anda harus valid")
    private String email;

    @NotEmpty(message = "Pesanan Anda tidak boleh kosong")
    @Valid
    private List<OrderItemRequest> orderItems;
}
