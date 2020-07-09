package com.baton.exchange.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

@Data
public class TradeCreationRequestMessage implements Serializable {
    private String stockSymbol;
    private Date messageCreationTimestamp;
    public TradeCreationRequestMessage(){
        messageCreationTimestamp = Calendar.getInstance().getTime();
    }
}
