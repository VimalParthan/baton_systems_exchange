package com.baton.exchange.service;

import com.baton.exchange.dto.TradeCreationRequestMessage;
import com.baton.exchange.enums.OrderType;
import com.baton.exchange.model.Order;
import com.baton.exchange.model.Trade;
import com.baton.exchange.model.Trader;
import com.baton.exchange.repository.OrderRepository;
import com.baton.exchange.repository.TradeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;

import java.util.Date;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
public class TradeService {

    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TradeRepository tradeRepository;

    @Value("${rabbitmq.exchangeName}")
    private String exchange;

    @Value("${rabbitmq.routingkey}")
    private String routingkey;

    public void sendTradeCreationRequestMessage(TradeCreationRequestMessage tradeCreationRequestMessage) {
        rabbitTemplate.convertAndSend(exchange, routingkey, tradeCreationRequestMessage);
        log.info("Send msg = " + tradeCreationRequestMessage);
    }

    @Transactional
    public List<Trade> tryCreatingTradeByMatchingOrders(String stockSymbol){
        List<Order> unmatchedOrders = orderRepository.findByIsMatchedAndStockSymbol(
                false, stockSymbol.toUpperCase());

        if(unmatchedOrders.isEmpty()){
            log.info("No unmatched orders found for stock symbol {}", stockSymbol);
            return null;
        }

        return createTrades(unmatchedOrders);
    }

    private List<Trade> createTrades(List<Order> unmatchedOrders){
        List<Trade> tradeList = new ArrayList<>();

        for(int i=0; i<unmatchedOrders.size()-1;i++){
            Order orderToMatch = unmatchedOrders.get(i);
            if(orderToMatch.getIsMatched()){
                continue;
            }

            for(int j= i+1;j<unmatchedOrders.size();j++){
                Order possibleMatch = unmatchedOrders.get(j);
                if(possibleMatch.getIsMatched()){
                    continue;
                }
                if(orderToMatch.getOrderType().equals(possibleMatch.getOrderType())){
                    continue;
                }

                if(orderToMatch.getTrader().getId().equals(possibleMatch.getTrader().getId())){
                    continue;
                }
                if(orderToMatch.getPrice().equals(possibleMatch.getPrice())){
                    tradeList.add(createTradeForOrders(orderToMatch, possibleMatch));
                    orderToMatch.setIsMatched(true);
                    possibleMatch.setIsMatched(true);
                    orderRepository.save(orderToMatch);
                    orderRepository.save(possibleMatch);
                    break;
                }
            }

        }
        tradeRepository.flush();
        orderRepository.flush();
        return tradeList;
    }

    private Trade createTradeForOrders(Order order1, Order order2){
        Trade trade = new Trade();

        if(order1.getOrderType().equals(OrderType.BUY)){
            trade.setBuyOrder(order1);
            trade.setSellOrder(order2);
        }else {
            trade.setSellOrder(order1);
            trade.setBuyOrder(order2);
        }
        tradeRepository.save(trade);
        return trade;
    }

    public Page<Trade> getTrades(Optional<Long> sellTraderId,
                                 Optional<Long> buyTraderId,
                                 Optional<String> stockSymbol,
                                 Optional<Date> tradeDate,
                                 Pageable pageable){
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Trade trade = new Trade();
        Order buyOrder = getOrderForTrade(buyTraderId,stockSymbol);
        Order sellOrder = getOrderForTrade(sellTraderId, stockSymbol);
        trade.setBuyOrder(buyOrder);
        trade.setSellOrder(sellOrder);
        tradeDate.ifPresent(trade::setTradeDate);
        return tradeRepository.findAll(Example.of(trade), pageable);
    }

    private Order getOrderForTrade(Optional<Long> traderId, Optional<String> stockSymbol){
        Order order = new Order();
        Trader trader = new Trader();
        traderId.ifPresent(trader::setId);
        stockSymbol.ifPresent(sym->order.setStockSymbol(sym.toUpperCase()));
        order.setIsMatched(true);
        order.setTrader(trader);
        return order;
    }

}
