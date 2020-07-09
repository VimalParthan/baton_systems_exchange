package com.baton.exchange.repository;

import com.baton.exchange.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByIsMatchedAndStockSymbol(Boolean isMatched, String StockSymbol);
}
