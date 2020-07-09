package com.baton.exchange.controller;


import com.baton.exchange.model.Trade;
import com.baton.exchange.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("trades")
public class TradeController {

    @Autowired
    private TradeService tradeService;

    @GetMapping
    Page<Trade> getOrders(@RequestParam Optional<Long> sellTraderId,
                          @RequestParam Optional<Long> buyTraderId,
                          @RequestParam Optional<String> stockSymbol,
                          @RequestParam @DateTimeFormat(pattern="dd/MM/yyyy") Optional<Date> tradeDate,
                          Pageable pageable){
        return tradeService.getTrades(sellTraderId,buyTraderId
                ,stockSymbol,tradeDate, pageable);
    }
}
