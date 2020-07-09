package com.baton.exchange;

import com.baton.exchange.dto.TradeControllerResponse;
import com.baton.exchange.enums.OrderType;
import com.baton.exchange.model.Trade;
import com.baton.exchange.model.Trader;
import com.baton.exchange.service.TradeService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TradeControllerTest extends AbstractTest{

    @Autowired
    private TradeService tradeService;

    @Override
    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    @Transactional
    public void testTradeControllerPagination() throws Exception {
        Trader trader_1 = createTrader();
        Trader trader_2 = createTrader();

        for(int i=0; i<=21;i++){
            createOrder(trader_1, OrderType.BUY, "test_stock", 110.0);
        }
        for(int i=0; i<=21;i++){
            createOrder(trader_2, OrderType.SELL, "test_stock", 110.0);
        }

        tradeService.tryCreatingTradeByMatchingOrders("test_stock");
        String uri = "/trades?stockSymbol=test_stock";
        TradeControllerResponse firstPageResponse = hitGetApi(uri, TradeControllerResponse.class);
        TradeControllerResponse secondPageResponse = hitGetApi(uri+"&page=1", TradeControllerResponse.class);

        List<Trade> firstPageTrades = firstPageResponse.getContent();
        List<Trade> secondPageTrades = secondPageResponse.getContent();
        assertEquals(20, firstPageTrades.size());
        assertEquals(2, secondPageTrades.size());
    }

    @Test
    @Transactional
    public void testTradeControllerWithMultipleParams() throws Exception {
        Trader trader_1 = createTrader();
        Trader trader_2 = createTrader();

        createOrder(trader_1, OrderType.BUY, "test_stock", 110.0);
        createOrder(trader_1, OrderType.BUY, "test_stock", 110.0);
        createOrder(trader_2, OrderType.SELL, "test_stock", 110.0);
        createOrder(trader_2, OrderType.SELL, "test_stock", 110.0);
        createOrder(trader_1, OrderType.BUY, "test_stock_2", 111.0);
        createOrder(trader_2, OrderType.SELL, "test_stock_2", 111.0);

        tradeService.tryCreatingTradeByMatchingOrders("test_stock");
        tradeService.tryCreatingTradeByMatchingOrders("test_stock_2");
        String uri = "/trades?stockSymbol=test_stock_2&sellTraderId="+trader_2.getId();
        TradeControllerResponse firstPageResponse = hitGetApi(uri, TradeControllerResponse.class);

        List<Trade> trades = firstPageResponse.getContent();
        assertEquals(1, trades.size());
        assertEquals("TEST_STOCK_2", trades.get(0).getBuyOrder().getStockSymbol());
        assertEquals(trader_2.getId(), trades.get(0).getSellOrder().getTrader().getId());
    }
}
