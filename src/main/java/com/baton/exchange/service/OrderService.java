package com.baton.exchange.service;

import com.baton.exchange.dto.TradeCreationRequestMessage;

import com.baton.exchange.enums.OrderType;
import com.baton.exchange.model.Order;
import com.baton.exchange.model.Trader;
import com.baton.exchange.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TradeService traderService;

    public Order createOrder(Order order) {
        order.setStockSymbol(order.getStockSymbol().toUpperCase());
        Order orderCreated = orderRepository.save(order);
        orderRepository.flush();
        TradeCreationRequestMessage tradeCreationRequestMessage = new TradeCreationRequestMessage();
        tradeCreationRequestMessage.setStockSymbol(orderCreated.getStockSymbol());
        traderService.sendTradeCreationRequestMessage(tradeCreationRequestMessage);
        return orderCreated;
    }

    public List<Order> getUnmatchedOrder(String stockSymbol){
        return orderRepository.findByIsMatchedAndStockSymbol(false, stockSymbol);
    }

    public Page<Order> getOrders(Optional<OrderType> orderType,
                                 Optional<String> stockSymbol,
                                 Optional<Double> price,
                                 Optional<Boolean> isMatched,
                                 Pageable pageable){

        Order order = new Order();
        orderType.ifPresent(order::setOrderType);
        isMatched.ifPresent(order::setIsMatched);
        stockSymbol.ifPresent(s-> order.setStockSymbol(s.toUpperCase()));
        price.ifPresent(order::setPrice);
        return orderRepository.findAll(Example.of(order),pageable);
    }

}
