package com.yuwan.arutalalab.order_service.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class OrderResponse {

    private UUID id;
    private String customerName;
    private String email;
    private String status;
    private List<OrderItemResponse> orderItems;
}
