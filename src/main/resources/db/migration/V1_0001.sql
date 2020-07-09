
CREATE TABLE  traders(
    id SERIAL PRIMARY KEY,
	name VARCHAR NOT NULL,
    email VARCHAR NOT NULL,
    phone_number BIGINT NOT NULL,
    creation_timestamp TIMESTAMP NOT NULL,
    update_timestamp TIMESTAMP NOT NULL
);

INSERT INTO traders
(name, email, phone_number, creation_timestamp, update_timestamp)
VALUES
('party_A', 'party_A@gmail.com', '7259971304', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


INSERT INTO traders
(name, email, phone_number, creation_timestamp, update_timestamp)
VALUES
('party_B', 'party_B@gmail.com', '7259971302', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO traders
(name, email, phone_number, creation_timestamp, update_timestamp)
VALUES
('party_C', 'party_C@gmail.com', '7259971301', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

CREATE TABLE  orders(
    id SERIAL PRIMARY KEY,
    trader_id BIGINT NOT NULL,
	order_type VARCHAR CHECK (order_type IN ('BUY', 'SELL')) NOT NULL,
    stock_symbol VARCHAR NOT NULL,
    price DOUBLE PRECISION NOT NULL,
    is_matched BOOLEAN DEFAULT FALSE NOT NULL,
    creation_timestamp TIMESTAMP NOT NULL,
    update_timestamp TIMESTAMP NOT NULL,
    CONSTRAINT orders_trader_id_fk FOREIGN KEY (trader_id) REFERENCES traders (id)
);

CREATE TABLE  trades(
    id SERIAL PRIMARY KEY,
    sell_order_id BIGINT NOT NULL,
    buy_order_id BIGINT NOT NULL,
    trade_date DATE NOT NULL,
    creation_timestamp TIMESTAMP NOT NULL,
    update_timestamp TIMESTAMP NOT NULL,
    CONSTRAINT trades_sell_order_id_fk FOREIGN KEY (sell_order_id) REFERENCES orders (id),
    CONSTRAINT trades_buy_order_id_fk FOREIGN KEY (buy_order_id) REFERENCES orders (id)
);
