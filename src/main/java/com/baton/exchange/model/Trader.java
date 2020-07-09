package com.baton.exchange.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name="traders")
public class Trader extends BaseModel{

    @Column(name="name")
    private String name;

    @Column(name="phone_number")
    private Long phoneNumber;

    @Column(name="email")
    private String email;

}
