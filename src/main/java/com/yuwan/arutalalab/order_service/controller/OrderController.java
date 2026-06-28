package com.yuwan.arutalalab.order_service.controller;

import com.yuwan.arutalalab.order_service.dto.OrderRequest;
import com.yuwan.arutalalab.order_service.dto.OrderResponse;
import com.yuwan.arutalalab.order_service.repository.OrderRepository;
import com.yuwan.arutalalab.order_service.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final OrderRepository orderRepository;

    //Constructor Injection
    public OrderController(OrderService orderService, OrderRepository orderRepository) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
    }

    //Membuat order
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest orderReq) {
        OrderResponse orderResponse = orderService.createOrder(orderReq);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponse);
    }

    //Membayar order
    @PatchMapping("/{id}/pay")
    public ResponseEntity<OrderResponse> payOrder(@PathVariable UUID id) {
        OrderResponse orderResponse = orderService.payOrder(id);
        return ResponseEntity.status(HttpStatus.OK).body(orderResponse);
    }

    //Membatalkan order
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable UUID id) {
        OrderResponse orderResponse = orderService.cancelOrder(id);
        return ResponseEntity.status(HttpStatus.OK).body(orderResponse);
    }

    //Menampilkan semua order
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List orderResponse = orderService.getAllOrders();
        return ResponseEntity.status(HttpStatus.OK).body(orderResponse);
    }

    //Menampilkan spesifik order
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable UUID id) {
        OrderResponse orderResponse = orderService.getOrders(id);
        return ResponseEntity.status(HttpStatus.OK).body(orderResponse);
    }

}
