package com.yuwan.arutalalab.order_service.service;

import com.yuwan.arutalalab.order_service.client.CatalogClient;
import com.yuwan.arutalalab.order_service.dto.OrderRequest;
import com.yuwan.arutalalab.order_service.dto.OrderResponse;
import com.yuwan.arutalalab.order_service.dto.ProductResponse;
import com.yuwan.arutalalab.order_service.entity.Order;
import com.yuwan.arutalalab.order_service.entity.OrderItem;
import com.yuwan.arutalalab.order_service.repository.OrderRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final CatalogClient catalogClient;

    public OrderService(OrderRepository orderRepository, CatalogClient catalogClient) {
        this.orderRepository = orderRepository;
        this.catalogClient = catalogClient;
    }

    @Transactional
    public OrderResponse createOrder(@Valid OrderRequest request) {
        Order order = new Order();
        order.setCustomerName(order.getCustomerName());
        order.setCustomerEmail(order.getCustomerEmail());
        order.setOrderStatus(order.getOrderStatus());

        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItem orderItem : request.getOrderItems()) {
            ProductResponse product = catalogClient.getProduct(itemReq.getProductId());

            //Validasi Stock
            if (product.getStock() < itemReq.getQuantity()){
                throw new RuntimeException("Stok tidak cukup untuk produk " + product.getName());
            }

            // SNAPSHOT DATA
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductPrice(product.getPrice());
            orderItem.setQuantity(itemReq.getQuantity());
            orderItem.setOrder(order); // Relasikan kembali ke order utama

            items.add(orderItem);

            // Kurangi Stok di catalog-service
            int remainingStock = product.getStock() - itemReq.getQuantity();
            catalogClient.updateStock(product.getId(), remainingStock);
        }

        order.setItems(items);

        // 3. Simpan Order ke Database (order_db)
        Order savedOrder = orderRepository.save(order);

        // 4. MAPPING Balik ke Response DTO
        OrderResponse response = new OrderResponse();
        response.setId(savedOrder.getId());
        response.setCustomerName(savedOrder.getCustomerName());
        response.setCustomerEmail(savedOrder.getCustomerEmail());
        response.setStatus(savedOrder.getStatus().name());

        List<OrderItemResponse> responseItems = new ArrayList<>();
        for (OrderItem savedItem : savedOrder.getItems()) {
            OrderItemResponse itemRes = new OrderItemResponse();
            itemRes.setProductId(savedItem.getProductId());
            itemRes.setProductName(savedItem.getProductName());
            itemRes.setProductPrice(savedItem.getProductPrice());
            itemRes.setQuantity(savedItem.getQuantity());
            responseItems.add(itemRes);
        }
        response.setItems(responseItems);

        return response;
    }
}
