package com.baton.exchange.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name="trades")
public class Trade extends BaseModel {

    @ManyToOne
    @PrimaryKeyJoinColumn(name="sell_order_id")
    private Order sellOrder;

    @ManyToOne
    @PrimaryKeyJoinColumn(name="buy_order_id")
    private Order buyOrder;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "trade_date")
    private Date tradeDate;

}
