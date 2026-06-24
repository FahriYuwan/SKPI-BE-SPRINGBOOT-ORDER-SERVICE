package com.yuwan.arutalalab.order_service.repository;

import com.yuwan.arutalalab.order_service.entity.Order;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface OrderRepository extends CrudRepository<Order, UUID> {
}
