package com.baton.exchange;

import com.baton.exchange.enums.OrderType;
import com.baton.exchange.model.Order;
import com.baton.exchange.model.Trader;
import com.baton.exchange.repository.OrderRepository;
import com.baton.exchange.repository.TraderRepository;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ExchangeApplication.class)
@WebAppConfiguration
public abstract class AbstractTest {

    protected MockMvc mvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    private TraderRepository traderRepository;

    @Autowired
    private OrderRepository orderRepository;

    protected void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    protected String mapToJson(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }
    protected <T> T mapFromJson(String json, Class<T> clazz)
            throws JsonParseException, JsonMappingException, IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, clazz);
    }

    protected <T> T hitGetApi(String uri,Class<T> responseClass)
            throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        return mapFromJson(content, responseClass);

    }

    protected <T> T hitPostApi(String uri,Class<T> responseClass, String payload)
            throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(payload)).andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(201, status);
        String content = mvcResult.getResponse().getContentAsString();
        return mapFromJson(content, responseClass);
    }

    protected Trader createTrader(){
        Trader trader = new Trader();
        trader.setEmail("xyz@gmail.com");
        trader.setName("test_trader");
        trader.setPhoneNumber(7259971309L);
        traderRepository.save(trader);
        traderRepository.flush();
        return trader;
    }

    protected Order createOrder(Trader trader, OrderType orderType, String stockSymbol, Double price){
        Order order = getOrderObject(trader, orderType, stockSymbol, price);
        orderRepository.save(order);
        orderRepository.flush();
        return order;
    }

    protected Order getOrderObject(Trader trader, OrderType orderType, String stockSymbol, Double price){
        Order order = new Order();
        order.setTrader(trader);
        order.setStockSymbol(stockSymbol.toUpperCase());
        order.setOrderType(orderType);
        order.setPrice(price);
        return order;
    }

    protected String getOrderJson(Trader trader, OrderType orderType, String stockSymbol, Double price)
            throws Exception{
        Order order = getOrderObject(trader, orderType, stockSymbol, price);
        return mapToJson(order);
    }

}