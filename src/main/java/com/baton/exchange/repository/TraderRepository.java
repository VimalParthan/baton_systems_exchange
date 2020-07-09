package com.baton.exchange.repository;

import com.baton.exchange.model.Trader;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TraderRepository extends JpaRepository<Trader, Long> {
}
