# Payment Service Microservice

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Architecture](#architecture)
- [Setup and Installation](#setup-and-installation)
  - [Prerequisites](#prerequisites)
  - [Clone the Repository](#clone-the-repository)
  - [Build the Project](#build-the-project)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
  - [Locally](#locally)
  - [Using Docker](#using-docker)
- [API Documentation](#api-documentation)
- [Endpoints Overview](#endpoints-overview)
  - [Payment Endpoints](#payment-endpoints)
- [Error Handling](#error-handling)

## Overview
The **Payment Service** is a Spring Boot-based microservice for managing payments using PayPal. It supports functionalities like creating PayPal orders, capturing payments, and retrieving payment details.

## Features
- Create, capture, and cancel PayPal orders.
- Retrieve order details directly from PayPal.
- View payment history by user.
- Integration with RabbitMQ for asynchronous payment confirmation.
- Configurable success and cancel URLs.

## Technologies Used
- **Java 21** with **Spring Boot**
- **PayPal SDK** for payment integration
- **Spring Data JPA** for ORM
- **PostgreSQL** as the database
- **OpenAPI/Swagger** for API documentation
- **RabbitMQ** for message handling
- **Maven** for build management
- **Docker** for containerization

## Architecture
The service is structured into:
- **Controller Layer**: Handles HTTP requests and responses.
- **Service Layer**: Contains business logic.
- **Repository Layer**: Interfaces with the database.
- **Client Layer**: Manages communication with PayPal.

---

## Setup and Installation

### Prerequisites
- Java Development Kit (JDK) 21
- Maven 3.9+
- PostgreSQL
- Docker (optional)

### Clone the Repository
``bash
git clone https://github.com/PRPO-ekipa03/payment-service.git
cd paymentservice
``

### Build the Project
``bash
mvn clean package -DskipTests
``

---

## Configuration
Set the following configuration parameters in your environment or `application.properties` file:

``properties
# Application
spring.application.name=payment-service
server.port=8082

# PayPal configuration
paypal.client.id=${PAYPAL_CLIENT_ID}
paypal.client.secret=${PAYPAL_CLIENT_SECRET}
paypal.mode=${PAYPAL_MODE}
paypal.success.url=http://${API-GATEWAY-HOST:localhost}:${API-GATEWAY-PORT:8080}/api/payments/success
paypal.cancel.url=http://${API-GATEWAY-HOST:localhost}:${API-GATEWAY-PORT:8080}/api/payments/cancel

# Database configuration
spring.datasource.url=jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA settings
spring.jpa.hibernate.ddl-auto=create
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.show-sql=true

# RabbitMQ
spring.rabbitmq.host=${MQ_HOST:localhost}
spring.rabbitmq.port=5672
spring.rabbitmq.username=${MQ_USERNAME}
spring.rabbitmq.password=${MQ_PASSWORD}
``

---

## Running the Application

### Locally
Run the application with:
``bash
java -jar target/payment-service.jar
``  

The service will start on port 8082 or the port specified in your configuration.

### Using Docker
To containerize the application:

1. **Build the Docker Image**:  
``bash
docker build -t payment-service .
``  

2. **Run the Docker Container**:  
``bash
docker run -p 8082:8082 \
  -e DB_HOST=your_db_host \
  -e DB_PORT=your_db_port \
  -e DB_NAME=your_db_name \
  -e DB_USERNAME=your_db_username \
  -e DB_PASSWORD=your_db_password \
  -e PAYPAL_CLIENT_ID=your_paypal_client_id \
  -e PAYPAL_CLIENT_SECRET=your_paypal_client_secret \
  -e PAYPAL_MODE=sandbox \
  payment-service
``  

---

## API Documentation
The Swagger UI is available at:  
``  
http://localhost:8082/payments/swagger-ui  
``  

---

## Endpoints Overview

### Payment Endpoints
- **Create PayPal Order**: `POST /api/payments/create`
- **Capture PayPal Order**: `GET /api/payments/success`
- **Cancel PayPal Order**: `GET /api/payments/cancel`
- **Get Order Details**: `GET /api/payments/details/{orderId}`
- **Get Payments by User**: `GET /api/payments/user`

---

## Error Handling
The service provides detailed error responses for common issues:
- **404 Not Found**: Payment or order not found.
- **400 Bad Request**: Invalid payment status or input.
- **500 Internal Server Error**: Unexpected server errors.
- **503 Service Unavailable**: PayPal service issues.
