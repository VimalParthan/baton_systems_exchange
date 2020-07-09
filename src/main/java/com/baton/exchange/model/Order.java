package com.baton.exchange.model;

import com.baton.exchange.enums.OrderType;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name="orders")
public class Order extends BaseModel{

    @ManyToOne(fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name="trader_id")
    private Trader trader;

    @Column(name="order_type")
    @Enumerated(EnumType.STRING)
    @NotNull
    private OrderType orderType;

    @Column(name="stock_symbol")
    @NotBlank(message = "stockSymbol cannot be empty")
    private String stockSymbol;

    @Column(name="price")
    @NotNull(message = "price should be a positive number")
    @Min(value = 1, message= "price should be grater than or equal to 1")
    private Double price;

    @Column(name="is_matched")
    private Boolean isMatched = false;

}
