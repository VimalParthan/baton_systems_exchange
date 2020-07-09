package com.baton.exchange;

import com.baton.exchange.enums.OrderType;
import com.baton.exchange.model.Order;
import com.baton.exchange.model.Trade;
import com.baton.exchange.model.Trader;
import com.baton.exchange.repository.TradeRepository;
import com.baton.exchange.service.TradeService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TradeCreationTest extends AbstractTest{

    private static final String TEST_STOCK = "TEST_STOCK";
    private static final String TEST_STOCK_2 = "TEST_STOCK_2";

    @Autowired
    private TradeService tradeService;

    @Autowired
    private TradeRepository tradeRepository;

    @Override
    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    @Transactional
    public void testTradeCreation() throws Exception {
        String uri = "/orders";
        Trader buyTrader = createTrader();
        Trader sellTrader = createTrader();

        Order buyOrder = createOrder(buyTrader, OrderType.BUY,TEST_STOCK,110.0);
        tradeService.tryCreatingTradeByMatchingOrders(TEST_STOCK);
        Order sellOrder = createOrder(sellTrader,OrderType.SELL,TEST_STOCK, 110.0);
        tradeService.tryCreatingTradeByMatchingOrders(TEST_STOCK);

        List<Trade> tradeList = tradeRepository.findByBuyOrderId(buyOrder.getId());
        assertEquals(1,tradeList.size());
        assertEquals(TEST_STOCK, tradeList.get(0).getSellOrder().getStockSymbol());
    }

    @Test
    @Transactional
    public void testMultipleTradeCreation() throws Exception {

        String uri = "/orders";
        Trader traderA = createTrader();
        Trader traderB = createTrader();
        Trader traderC = createTrader();


        Order sellIBMOrder = createOrder(traderA, OrderType.SELL,"IBM",110.0);
        List<Trade> sellIBMTrades = tradeService.tryCreatingTradeByMatchingOrders("IBM");
        Order sellINFYOrder = createOrder(traderA,OrderType.SELL,"INFY", 600.0);
        List<Trade> sellINFYTrades = tradeService.tryCreatingTradeByMatchingOrders("INFY");
        Order sellGOOGOrder = createOrder(traderA,OrderType.SELL,"GOOG", 500.0);
        List<Trade> sellGOOGTrades = tradeService.tryCreatingTradeByMatchingOrders("GOOG");
        Order buyIBMOrder = createOrder(traderB, OrderType.BUY,"IBM",110.0);
        List<Trade> buyIBMTrades = tradeService.tryCreatingTradeByMatchingOrders("IBM");
        Order buyIBMOrder2 = createOrder(traderC, OrderType.BUY,"IBM",110.0);
        List<Trade> buyIBM2Trades = tradeService.tryCreatingTradeByMatchingOrders("IBM");
        Order buyINFYOrder = createOrder(traderC, OrderType.BUY,"INFY",600.0);
        List<Trade> buyINFYTrades = tradeService.tryCreatingTradeByMatchingOrders("INFY");

        assertEquals(0,sellIBMTrades.size());
        assertEquals(0,sellINFYTrades.size());
        assertEquals(0,sellGOOGTrades.size());
        assertEquals(1,buyIBMTrades.size());
        assertEquals(0,buyIBM2Trades.size());
        assertEquals(1,buyINFYTrades.size());

    }

}
