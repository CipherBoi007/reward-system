# 🏪 Retail Reward Points System

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.4-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue)
![Maven](https://img.shields.io/badge/Maven-3.9-red)
![License](https://img.shields.io/badge/License-MIT-yellow)

A production-grade **Retail Reward Points System** built with Spring Boot 3 that manages customer reward points based on purchase transactions. The system allows customers to earn points on purchases and redeem them for rewards, with complete transaction history and audit trails.

---

## 📋 Table of Contents
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Architecture](#-architecture)
- [Business Rules](#-business-rules)
- [API Documentation](#-api-documentation)
- [Database Schema](#-database-schema)
- [Setup Instructions](#-setup-instructions)
- [Installation](#-installation)
- [Running the Application](#-running-the-application)
- [API Testing with Postman](#-api-testing-with-postman)
- [Project Structure](#-project-structure)
- [License](#-license)

---

## ✨ Features

### Core Functionality
- ✅ **Earn Points** - Automatic points calculation on purchases
- ✅ **Redeem Points** - Convert points to rewards (1 point = ₹1)
- ✅ **Transaction History** - Complete audit trail with balance tracking
- ✅ **Filter by Date** - View transactions by year and month
- ✅ **Bonus Points** - Extra 5 points for purchases > ₹1000

### Technical Features
- ✅ **RESTful APIs** - Well-designed endpoints with proper HTTP methods
- ✅ **Validation** - Comprehensive input validation using Jakarta Validation
- ✅ **Exception Handling** - Global exception handler with custom exceptions
- ✅ **Transaction Management** - Spring `@Transactional` for data consistency
- ✅ **Layered Architecture** - Clean separation of concerns
- ✅ **JPA Repositories** - Efficient database operations
- ✅ **DTO Pattern** - Data transfer objects for API layer

---

## 🛠 Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 17 LTS | Core programming language |
| Spring Boot | 3.2.4 | Application framework |
| Spring Data JPA | 3.2.4 | Database ORM |
| PostgreSQL | 17 | Production database |
| H2 Database | 2.2.224 | Testing database |
| Maven | 3.9+ | Build tool |
| Lombok | 1.18.30 | Boilerplate code reduction |
| Jakarta Validation | 3.0.2 | Input validation |
| JUnit | 5.10 | Unit testing |

---

## 🏗 Architecture

The application follows **Clean Architecture** principles with clear separation of concerns

┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
│ Controller │────▶│ Service │────▶│ Repository │
│ (REST API) │ │ (Business Logic│ │ (Data Access) │
└─────────────────┘ └─────────────────┘ └─────────────────┘
│ │ │
▼ ▼ ▼
┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
│ DTO │ │ Validation │ │ Entity │
│ (Data Transfer)│ │ Rules │ │ (JPA Model) │
└─────────────────┘ └─────────────────┘ └─────────────────┘


### Layer Responsibilities

| Layer | Package | Responsibility |
|-------|---------|----------------|
| **Controller** | `com.reward.controller` | Handle HTTP requests/responses |
| **Service** | `com.reward.service` | Implement business logic |
| **Repository** | `com.reward.repository` | Database operations |
| **Entity** | `com.reward.entity` | JPA entity models |
| **DTO** | `com.reward.dto` | Data transfer objects |
| **Exception** | `com.reward.exception` | Global exception handling |
| **Config** | `com.reward.config` | Application configuration |

---

## 📊 Business Rules

### Earning Rules

┌─────────────────────────────────────────────────┐
│ EARNING RULES │
├─────────────────────────────────────────────────┤
│ • For every ₹100 spent → 1 point (floor division)│
│ • Example: ₹199 → 1 point, ₹250 → 2 points │
│ • If billAmount > ₹1000 → +5 bonus points │
│ • Points earned only after successful purchase │
│ • Zero or negative billAmount is invalid │
└─────────────────────────────────────────────────┘

### Redemption Rules

┌─────────────────────────────────────────────────┐
│ REDEMPTION RULES │
├─────────────────────────────────────────────────┤
│ • 1 point = ₹1 value │
│ • Cannot redeem more than available points │
│ • Redemption amount must be > 0 │
│ • Redeemed points deducted immediately │
│ • Redemption stored as transaction │
└─────────────────────────────────────────────────┘


### History Rules

┌─────────────────────────────────────────────────┐
│ HISTORY RULES │
├─────────────────────────────────────────────────┤
│ • Every earning and redemption must be logged │
│ • Filter by year and month │
│ • Sorted by transactionDate ascending │
│ • Shows: type, points, balanceAfter │
└─────────────────────────────────────────────────┘


---

## 📚 API Documentation

### Base URL : http://localhost:8080/api/rewards

### Endpoints Summary

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/purchase` | Process purchase and earn points | ❌ |
| POST | `/redeem` | Redeem points for rewards | ❌ |
| GET | `/history` | Get all transactions (filter by year/month) | ❌ |
| GET | `/customer/{customerId}/history` | Get customer transactions | ❌ |

---

### Database Schema

ER - DIAGRAM :
┌─────────────────┐       ┌─────────────────┐
│    customers    │       │    purchases    │
├─────────────────┤       ├─────────────────┤
│ customer_id (PK)│◄──────│ customer_id (FK)│
│ name            │       │ purchase_id (PK)│
│ total_points    │       │ bill_amount     │
│ is_active       │       │ earned_points   │
│ created_at      │       │ purchase_date   │
│ updated_at      │       └─────────────────┘
└─────────────────┘               │
         │                        │
         │                        │
         ▼                        ▼
┌─────────────────┐       ┌─────────────────┐
│reward_transactions│      │                 │
├─────────────────┤       │                 │
│ transaction_id(PK)│      │                 │
│ customer_id (FK) │◄──────┘                 │
│ type (EARN/REDEEM)│                        │
│ points           │                         │
│ points_balance_after│                      │
│ transaction_date │                         │
└─────────────────┘─────────────────────────┘

### Prerequisites

👉 Java 17 LTS or higher
👉 PostgreSQL 17 or higher
👉 Maven 3.9+
👉 Git (optional)
👉 Postman (for API testing)

### Installation 

Clone the Repository :
git clone https://github.com/CipherBoi007/Reward-System.git
cd Reward-System

Configure Database :
Powershell -
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="your_password"
Command Prompt -
set DB_USERNAME=postgres
set DB_PASSWORD=your_password
Linux/Mac -
export DB_USERNAME=postgres
export DB_PASSWORD=your_password

### Build the Application
mvn clean install

### Running the Application

Development Mode : mvn spring-boot:run
Production Mode : java -jar target/reward-system-0.0.1-SNAPSHOT.jar
With Profile : java -jar -Dspring.profiles.active=prod target/reward-system-0.0.1-SNAPSHOT.jar

Note :

On first run, the application automatically creates:

⭐ Customer 1: John Doe (500 points)
⭐ Customer 2: Jane Smith (750 points)

 ### Project Structure
 retail-reward-points-system/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── reward/
│   │   │           ├── RewardSystemApplication.java
│   │   │           ├── config/
│   │   │           │   └── DataInitializer.java
│   │   │           ├── controller/
│   │   │           │   └── RewardController.java
│   │   │           ├── service/
│   │   │           │   ├── RewardService.java
│   │   │           │   └── impl/
│   │   │           │       └── RewardServiceImpl.java
│   │   │           ├── repository/
│   │   │           │   ├── CustomerRepository.java
│   │   │           │   ├── PurchaseRepository.java
│   │   │           │   └── RewardTransactionRepository.java
│   │   │           ├── entity/
│   │   │           │   ├── Customer.java
│   │   │           │   ├── Purchase.java
│   │   │           │   ├── RewardTransaction.java
│   │   │           │   └── enums/
│   │   │           │       └── TransactionType.java
│   │   │           ├── dto/
│   │   │           │   ├── request/
│   │   │           │   │   ├── PurchaseRequest.java
│   │   │           │   │   └── RedeemRequest.java
│   │   │           │   └── response/
│   │   │           │       ├── RewardResponse.java
│   │   │           │       └── HistoryResponse.java
│   │   │           └── exception/
│   │   │               ├── ResourceNotFoundException.java
│   │   │               ├── BusinessException.java
│   │   │               ├── ErrorResponse.java
│   │   │               └── GlobalExceptionHandler.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── static/
│   └── test/
│       └── java/
│           └── com/
│               └── reward/
│                   └── RewardSystemApplicationTests.java
├── .gitignore
├── pom.xml
├── README.md
└── mvnw

### LICENSE 

MIT License

Copyright (c) 2026 CipherBoi007

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.



