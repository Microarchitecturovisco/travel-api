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
- [Services description](#services-description)

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

## Services description

- **offer provide service** - This service provides offers for users. It communicates through queues with transports and hotels to get available hotels/rooms and transport at certain period and connect it to present avvailable offers (transport + rooms) to user. It also provides detailed view of specific offer with data collected from hotel module.
- **reservation service** - This service orchestrates the process of reservation. 
- **hotel service** - This service stores hotel data such as hotel details, room details and room reservation. It provides information about avaivability of a room/hotel to offer provider and reservation service and shares an option with reservation service to reserve certain rooms.
- **transport service** - 

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

