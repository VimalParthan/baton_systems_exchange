package com.baton.exchange.controller;

import com.baton.exchange.enums.OrderType;
import com.baton.exchange.model.Order;
import com.baton.exchange.repository.OrderRepository;
import com.baton.exchange.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Object> createOrder(@Valid @RequestBody Order order){
        Order orderCreated = orderService.createOrder(order);
        return new ResponseEntity<Object>(order, HttpStatus.CREATED);
    }

    @GetMapping
    Page<Order> getOrders(@RequestParam Optional<OrderType> orderType,
                           @RequestParam Optional<String> stockSymbol,
                           @RequestParam Optional<Double> price,
                           @RequestParam Optional<Boolean> isMatched,
                           Pageable pageable){
        return orderService.getOrders(orderType,stockSymbol,price,isMatched, pageable);
    }


}
