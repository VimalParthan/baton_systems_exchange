package com.baton.exchange;

import com.baton.exchange.dto.OrderControllerResponse;
import com.baton.exchange.enums.OrderType;
import com.baton.exchange.model.Order;
import com.baton.exchange.model.Trader;
import com.baton.exchange.repository.OrderRepository;
import com.baton.exchange.repository.TraderRepository;
import org.junit.Before;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class OrderControllerTest extends AbstractTest{

    @Override
    @Before
    public void setUp() {
        super.setUp();
    }

    @Autowired
    TraderRepository traderRepository;

    @Autowired
    OrderRepository orderRepository;

    @Test
    @Transactional
    public void testOrderControllerPagination() throws Exception {
        Trader trader = createTrader();
        for(int i=0; i<=21;i++){
            createOrder(trader,OrderType.BUY, "test_stock", 110.0);
        }

        String uri = "/orders";
        OrderControllerResponse response = hitGetApi(uri, OrderControllerResponse.class);

        List<Order> orders = response.getContent();
        assertEquals(20, orders.size());
    }

    @Test
    @Transactional
    public void testGetOrderWithStockSymbolAndPriceParams() throws Exception {

        Trader trader = createTrader();
        createOrder(trader,OrderType.BUY, "TEST_STOCK",110.0);
        createOrder(trader,OrderType.SELL, "TEST_STOCK",110.0);
        createOrder(trader,OrderType.SELL, "TEST_STOCK_2",111.0);

        String uri = "/orders?stockSymbol=TEST_STOCK_2&price=111";
        OrderControllerResponse response = hitGetApi(uri, OrderControllerResponse.class);

        List<Order> orders = response.getContent();
        assertEquals(1, orders.size());
        assertEquals("TEST_STOCK_2", orders.get(0).getStockSymbol());
        assertEquals(111.0, orders.get(0).getPrice(), 0.0);

    }

    @Test
    @Transactional
    public void testGetOrderWithOrderTypeAndIsMatchedParams() throws Exception {

        Trader trader = createTrader();
        createOrder(trader,OrderType.BUY, "TEST_STOCK",110.0);
        createOrder(trader,OrderType.SELL, "TEST_STOCK",110.0);
        createOrder(trader,OrderType.SELL, "TEST_STOCK_2",111.0);

        String uri= "/orders?orderType=SELL&isMatched=false";
        OrderControllerResponse response = hitGetApi(uri, OrderControllerResponse.class);

        List<Order> orders= response.getContent();
        assertEquals(2, orders.size());
        assertTrue(!orders.get(0).getIsMatched()&&!orders.get(1).getIsMatched());
        assertTrue(orders.get(0).getOrderType().equals(OrderType.SELL)&&
                orders.get(0).getOrderType().equals(OrderType.SELL));

    }

    @Test
    @Transactional
    public void createOrder() throws Exception {
        String uri = "/orders";
        Trader trader = createTrader();
        String payload = getOrderJson(trader,OrderType.BUY,"test_stock",110.0);
        Order order = hitPostApi(uri,Order.class,payload);
        assertTrue(order.getStockSymbol().equals("TEST_STOCK"));
    }



}
