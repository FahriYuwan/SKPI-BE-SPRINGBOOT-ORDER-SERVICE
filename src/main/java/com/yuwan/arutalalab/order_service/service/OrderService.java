package com.yuwan.arutalalab.order_service.service;

import com.yuwan.arutalalab.order_service.client.CatalogClient;
import com.yuwan.arutalalab.order_service.dto.OrderItemRequest;
import com.yuwan.arutalalab.order_service.dto.OrderRequest;
import com.yuwan.arutalalab.order_service.dto.OrderResponse;
import com.yuwan.arutalalab.order_service.dto.ProductResponse;
import com.yuwan.arutalalab.order_service.entity.Order;
import com.yuwan.arutalalab.order_service.entity.OrderItem;
import com.yuwan.arutalalab.order_service.entity.OrderStatus;
import com.yuwan.arutalalab.order_service.repository.OrderRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import com.yuwan.arutalalab.order_service.dto.OrderItemResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final CatalogClient catalogClient;

    public OrderService(OrderRepository orderRepository, CatalogClient catalogClient) {
        this.orderRepository = orderRepository;
        this.catalogClient = catalogClient;
    }


    //Method untuk membuat order
    @Transactional
    public OrderResponse createOrder(@Valid OrderRequest request) {
        Order order = new Order();
        order.setCustomerName(request.getCustomerName());
        order.setCustomerEmail(request.getEmail());
        order.setOrderStatus(OrderStatus.PENDING);

        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequest itemReq : request.getOrderItems()) {
            ProductResponse product = catalogClient.getProduct(itemReq.getProductId());

            //Validasi Stock
            if (product.getStock() < itemReq.getQuantity()){
                throw new RuntimeException("Stok tidak cukup untuk produk " + product.getName());
            }

            // SNAPSHOT DATA
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(itemReq.getQuantity());
            orderItem.setOrder(order); // Relasikan kembali ke order utama

            orderItems.add(orderItem);

            // Kurangi Stok di catalog-service
            int remainingStock = product.getStock() - itemReq.getQuantity();
            catalogClient.updateStock(product.getId(), remainingStock);
        }

        order.setOrderItems(orderItems);

        // 3. Simpan Order ke Database (order_db)
        Order savedOrder = orderRepository.save(order);

        // 4. Response DTO

        return getOrderResponse(savedOrder);
    }

    //Method untuk membayar
    @Transactional
    public OrderResponse payOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException(
                        "Pesanan dengan " + orderId + " yang Anda maksud tidak tersedia"
                ));
        if(order.getOrderStatus().equals(OrderStatus.PENDING )){
            order.setOrderStatus(OrderStatus.PAID);
            orderRepository.save(order);
            return getOrderResponse(order);
        }else {
            throw new RuntimeException("Order Status tidak Valid, karena status order adalah" + order.getOrderStatus());
        }

    }

    @Transactional
    public OrderResponse cancelOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException(
                        "Pesanan dengan " + orderId + " yang Anda maksud tidak tersedia"
                ));
        if(order.getOrderStatus().equals(OrderStatus.PENDING)){
            order.setOrderStatus(OrderStatus.CANCELLED);

            for(OrderItem itemRequest : order.getOrderItems()){
                ProductResponse product = catalogClient.getProduct(itemRequest.getProductId());
                int newStock = product.getStock() + itemRequest.getQuantity();
                catalogClient.updateStock(itemRequest.getProductId(), newStock);
            }
            orderRepository.save(order);
            return getOrderResponse(order);
        }else{
            throw new RuntimeException("Order Status tidak Valid, karena status order adalah" + order.getOrderStatus());
        }
    }

    public List<OrderResponse> getAllOrders() {

        List<OrderResponse> orderResponseList = new ArrayList<>();
        Iterable<Order> orders = orderRepository.findAll();

        for (Order order : orders) {
            orderResponseList.add(getOrderResponse(order));
        }
        return orderResponseList;
    }

    public OrderResponse getOrders(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException(
                        "Pesanan dengan " + orderId + " yang Anda maksud tidak tersedia"
                ));
        return getOrderResponse(order);
    }

    @NonNull
    private static OrderResponse getOrderResponse(Order savedOrder) {
        OrderResponse response = new OrderResponse();
        response.setId(savedOrder.getId());
        response.setCustomerName(savedOrder.getCustomerName());
        response.setEmail(savedOrder.getCustomerEmail());
        response.setStatus(savedOrder.getOrderStatus().name());

        List<OrderItemResponse> responseItems = new ArrayList<>();
        for (OrderItem savedItem : savedOrder.getOrderItems()) {
            OrderItemResponse itemRes = new OrderItemResponse();
            itemRes.setProductId(savedItem.getProductId());
            itemRes.setProductName(savedItem.getProductName());
            itemRes.setProductPrice(savedItem.getPrice());
            itemRes.setQuantity(savedItem.getQuantity());
            responseItems.add(itemRes);
        }

        response.setOrderItems(responseItems);
        return response;
    }


}
