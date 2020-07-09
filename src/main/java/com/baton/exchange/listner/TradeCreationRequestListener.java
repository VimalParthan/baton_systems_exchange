package com.baton.exchange.listner;

import com.baton.exchange.dto.TradeCreationRequestMessage;
import com.baton.exchange.model.Trade;
import com.baton.exchange.service.TradeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class TradeCreationRequestListener {
    @Autowired
    private TradeService tradeService;

    @RabbitListener(queues = "${rabbitmq.queueName}")
    public void processOrder(TradeCreationRequestMessage tradeCreationRequestMessage) {
        log.info("Trade creation request message Received: "+tradeCreationRequestMessage);
        List<Trade> tradeList = tradeService.tryCreatingTradeByMatchingOrders(
                tradeCreationRequestMessage.getStockSymbol());
        log.info("List of trades created: {}", tradeList);

    }

}
