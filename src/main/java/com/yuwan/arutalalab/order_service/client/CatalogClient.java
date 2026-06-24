package com.yuwan.arutalalab.order_service.client;

import com.yuwan.arutalalab.order_service.dto.ProductResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
public class CatalogClient {

    private final RestClient restClient;
    public CatalogClient(RestClient.Builder builder) {
        this.restClient = builder.baseUrl("http://localhost:8081").build();
    }

    public ProductResponse getProduct(UUID productID){
        return restClient.get()
                .uri("api/products/{id}", productID)
                .retrieve()
                .body(ProductResponse.class);
    }

    public void updateStock(UUID productId, Integer newStock) {
        restClient.patch()
                .uri("/api/products/{id}/stock", productId)
                .body(new UpdateStockRequest(newStock))
                .retrieve()
                .toBodilessEntity();
    }
}

class UpdateStockRequest {
    private Integer stock;
    public UpdateStockRequest(Integer stock) { this.stock = stock; }
    public Integer getStock() { return stock; }
}

