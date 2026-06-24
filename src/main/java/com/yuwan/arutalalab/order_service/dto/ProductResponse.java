package com.yuwan.arutalalab.order_service.dto;


import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ProductResponse {

    private UUID id;
    private String name;
    private String sku;
    private BigDecimal price;
    private int stock;
    private String status;

}
