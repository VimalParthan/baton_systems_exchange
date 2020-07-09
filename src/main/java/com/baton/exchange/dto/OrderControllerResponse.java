package com.baton.exchange.dto;

import com.baton.exchange.model.Order;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderControllerResponse {
    private List<Order> content;
}
