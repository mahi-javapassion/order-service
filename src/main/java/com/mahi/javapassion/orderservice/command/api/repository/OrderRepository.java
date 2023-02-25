package com.mahi.javapassion.orderservice.command.api.repository;

import com.mahi.javapassion.orderservice.command.api.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, String> {
}
