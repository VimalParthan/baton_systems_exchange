## Requirements

1. This project using PostgresSql 12.2 as its db. Please follow the steps in the [link](https://github.com/user/repo/blob/branch/other_file.md) for installing postgres. 

2. Once PostgresSql is installed, run the following commands:
  * Enter psql console by 
```
sudo -u postgres psql
```
  * Execute the following commands in the psql console:
```
CREATE DATABASE baton_systems;
CREATE USER baton_admin WITH ENCRYPTED PASSWORD 'password_123';
GRANT ALL PRIVILEGES ON DATABASE baton_systems TO baton_admin;
CREATE SCHEMA exchange;
```
3. Install RabbitMQ, it is used as the messaging broker for this application. This [link](https://www.digitalocean.com/community/tutorials/how-to-install-and-manage-rabbitmq) will help in installing RabbitMQ.

4. Please make sure you have Java jdk version 11 and maven installed in your system.

5. Make sure Postgres is running on port 5432 and RabbitMQ on 5672. Also make sure the RabbitMQ username is 'guest' and password is 'guest' 

## Running the application

1. Compile the application by executing the follwoing command
```
mvn clean install
```
2. Run the application by executing the follwoing command, which will also run the test cases.
```
mvn spring-boot:run
```
## Key design decisions and libraries used

1. The matching of orders for creation of trades was decided to be separated from the order creation controller. This was done by making the orders api to create an order and send a mesagge to the rabbitmq queue. The message will just be the stock_symbol, based upon which the queue listner will try and match orders for the stock symbol and therby creating a trade.

2. The GET api's for orders and trades have made use of the ExampleMatcher library to accomadate optional paramater querying with minimal code.

3. The GET api's for orders and trades have also made use of SpringBoot's pagination support.

4. Spring's RestControllerAdvisor has helped in further reducing code, when it comes to error handling.

5. Flyway has been used to handle db migration scripts.

## API signatures
   * There are two traders created by the migration script by default which can be used for testing.

1. POST api for Orders:
 
  * creates order and sends a message that triggers trade creation
  * URL `localhost:8080/orders` with body as follows
  ```
  {
    "orderType": "BUY",
    "stockSymbol": "AAPL",
    "price": 110,
    "trader":{
      "id":1
    }
  }
  ```
  
  
2. GET api for Orders:

  * Fecthes orders with support for multiple optionals params as follows
    1. `orderType` which could be either `BUY` or `SELL`.
    2. `stockSymbol` can be any string that reperesent a stock.
    3. `price` which is quoted for an order.
    4. `isMatched` a boolean field, representing if an order is matched inorder for a trade.
  * The api also supports pagination and sorting based on fields.
  * Sample URL `localhost:8080/orders?orderType=BUY&price=100&isMatched=false` with response as follows:
  ```  
{
    "content": [
        {
            "id": 3,
            "creationTimestamp": "2020-07-07T04:48:23.986+00:00",
            "updateTimestamp": "2020-07-07T04:48:23.986+00:00",
            "trader": {
                "id": 1,
                "creationTimestamp": "2020-07-07T10:13:19.808+00:00",
                "updateTimestamp": "2020-07-07T10:13:19.808+00:00",
                "name": "party_A",
                "phoneNumber": 7259971304,
                "email": "party_A@gmail.com"
            },
            "orderType": "BUY",
            "stockSymbol": "AAPL",
            "price": 100,
            "isMatched": false
        }
    ],
    "pageable": {
        "sort": {
            "sorted": false,
            "unsorted": true,
            "empty": true
        },
        "offset": 0,
        "pageNumber": 0,
        "pageSize": 20,
        "paged": true,
        "unpaged": false
    },
    "totalPages": 1,
    "totalElements": 1,
    "last": true,
    "size": 20,
    "number": 0,
    "sort": {
        "sorted": false,
        "unsorted": true,
        "empty": true
    },
    "first": true,
    "numberOfElements": 1,
    "empty": false
}
```

2. GET api for Trades:

  * Fecthes Trades with support for multiple optionals params as follows
    1. `sellTraderId` which represents the trader party's id who place the sell order for the trade.
    2. `buyTraderId` which represents the trader party's id who place the buy order for the trade.
    3. `stockSymbol` can be any string that reperesent bot buy and sell order stock.
    4. `tradeDate` date of the format dd/MM/YYYY which represent the day of the trade.
  * Sample URL `localhost:8080/trades?buyTraderId=1&stockSymbol=AAPL&tradeDate=07/07/2020` with response as follows:
  
  ```
  {
    "content": [
        {
            "id": 1,
            "creationTimestamp": "2020-07-07T04:44:14.251+00:00",
            "updateTimestamp": "2020-07-07T04:44:14.251+00:00",
            "sellOrder": {
                "id": 1,
                "creationTimestamp": "2020-07-07T04:43:54.540+00:00",
                "updateTimestamp": "2020-07-07T04:44:14.283+00:00",
                "trader": {
                    "id": 2,
                    "creationTimestamp": "2020-07-07T10:13:19.808+00:00",
                    "updateTimestamp": "2020-07-07T10:13:19.808+00:00",
                    "name": "party_B",
                    "phoneNumber": 7259971302,
                    "email": "party_B@gmail.com"
                },
                "orderType": "SELL",
                "stockSymbol": "AAPL",
                "price": 110,
                "isMatched": true
            },
            "buyOrder": {
                "id": 2,
                "creationTimestamp": "2020-07-07T04:44:14.190+00:00",
                "updateTimestamp": "2020-07-07T04:44:14.467+00:00",
                "trader": {
                    "id": 1,
                    "creationTimestamp": "2020-07-07T10:13:19.808+00:00",
                    "updateTimestamp": "2020-07-07T10:13:19.808+00:00",
                    "name": "party_A",
                    "phoneNumber": 7259971304,
                    "email": "party_A@gmail.com"
                },
                "orderType": "BUY",
                "stockSymbol": "AAPL",
                "price": 110,
                "isMatched": true
            },
            "tradeDate": "2020-07-07T00:00:00.000+00:00"
        }
    ],
    "pageable": {
        "sort": {
            "sorted": false,
            "unsorted": true,
            "empty": true
        },
        "offset": 0,
        "pageNumber": 0,
        "pageSize": 20,
        "paged": true,
        "unpaged": false
    },
    "totalPages": 1,
    "totalElements": 1,
    "last": true,
    "size": 20,
    "number": 0,
    "sort": {
        "sorted": false,
        "unsorted": true,
        "empty": true
    },
    "first": true,
    "numberOfElements": 1,
    "empty": false
}
```



