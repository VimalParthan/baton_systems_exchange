package com.baton.exchange.repository;

import com.baton.exchange.model.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradeRepository extends JpaRepository<Trade, Long> {
    List<Trade> findByBuyOrderId(Long buyOrderId);
}
