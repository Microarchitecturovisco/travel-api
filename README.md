# Travel agency tour operator microservices project

The main topic of the project implementation is the
system for servicing customers interested in purchasing
tourist offers. This application is developed in
microservice architecture that uses brokered messages
for communication between components. Used CQRS, Event Sourcing and Saga patterns.

Frontend side of this project can be found [here](https://github.com/Microarchitecturovisco/travel-ui).

## Table of contents
- [Microservices architecture](#microservices-architecture)
- [Using docker compose](#using-docker-compose)


## Microservices architecture

This project is devided into 9 modules:
- **api gateway** - entrypoint for every request; by default available at port `8082`
- **discovery service** - eureka-based discovery service
- **hotel service** - module responsilbe for managing hotels-oriented data
- **transport service** - module responsilbe for managing transports-oriented data
- **offer provider service** - module responsible for returning offers based on search query
- **reservation service** - module responsible for making reservations of selected offers
- **payment service** - payment logic (mocked)
- **user service** - user-oriented data
- **data generator** - additional module for seeking live changes of the hotel and transport data

**RabbitMQ** is used as the message broker.

## Running with docker

To start all services use this command in the project root directory:
```bash
docker compose up -d
```


This command will also optionally build the .jars if they aren't already packaged.

To stop all associated containers, run:
```bash
docker compose down
```

## Available endpoints

- **GET** `/transports/available` - returns a list of available destinations for trips by bus and plane. Return body:
  ```json
  {
  "departure": {
    "bus": [
      {
        "idLocation": int,
        "country": string,
        "region": string,
      },
      {...}
    ],
    "plane": [
      {
        "idLocation": int,
        "country": string,
        "region": string
      },
      {...}
    ],
  }
  "arrival": [
    {
      "idLocation": int,
      "country": string,
      "region": string
    },
    {...}
  ]
  }
  ```
- **GET** `/offers` - returns trip offer according to search parameters.
    Request params:
    ```json
    "departuresBus": int or an array of ints
    "departuresPlane": int or an array of ints
    "arrivals": int or an array of ints
    "date_from": date in format yyyy-mm-dd
    "date_to": date in format yyyy-mm-dd
    "adults": int
    "children_under_3": int
    "children_under_10": int
    "children_under_18": int
    ```
    Return body:
    ```json
    [
        {
            "idHotel": int,
            "hotelName": string,
            "price": float,
            "destination": string,
            "rating": float,
            "imageUrl": string
        },
        {...}
    ]
    ```
- **GET** `/offers/{ID_hotel}` - returns the offer details with search parameters.
Request params:
   ```json
    "departuresBus": int or an array of ints
    "departuresPlane": int or an array of ints
    "arrivals": int or an array of ints
    "date_from": date in format yyyy-mm-dd
    "date_to": date in format yyyy-mm-dd
    "adults": int
    "children_under_3": int
    "children_under_10": int
    "children_under_18": int
    ```
    Return body:
    ```json
    {
        "idHotel": int,
        "hotelName": string,
        "description": string,
        "price": float,
        "destination": string,
        "rating": float,
        "imageUrls": [
        {
        "imageUrl": string,
        },
        {...}
        ],
        "catering": [
        {
            "type": CateringType lub int,
            "price": float,
            "rating": float
        },
        {...}
        ],
        "rooms": [
        {
            "name": string,
            "guestCapacity": int,
            "pricePerAdult": float,
            "description": [string]
        },
        {...}
        ],
        "departure": {
        {
            "price": float,
            "idTransport": 15,
            "country": string,
            "region": string,
            },
        },
        "possibleDepartures": {
        "bus": [
            {
            "idTransport": int,
            "country": string,
            "region": string,
            },
            {...}
        ],
        "plane": [
            {
            "idTransport": int,
            "country": string,
            "region": string
            },
            {...}
        ],
        }
    }
    ```
- **POST** 
