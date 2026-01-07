# Personal Finance Manager

A production-grade personal finance management REST API built with Spring Boot 3.x. This application enables users to track income, expenses, and savings goals through a secure, session-based authentication system.

## 🚀 Features

- **User Authentication**: Secure registration, login, and logout with session-based authentication
- **Transaction Management**: Full CRUD operations for income and expense transactions
- **Category Management**: Default and custom categories for organizing transactions
- **Savings Goals**: Track progress towards financial goals with automatic calculations
- **Reports**: Monthly and yearly financial reports with category breakdowns
- **Data Isolation**: Complete separation of user data

## 🛠️ Technology Stack

| Component | Technology |
|-----------|------------|
| Language | Java 17 |
| Framework | Spring Boot 3.2.0 |
| Security | Spring Security |
| Database | H2 (In-memory/File) |
| Build Tool | Maven |
| Testing | JUnit 5, Mockito |

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+

## 🏃 Quick Start

### Local Development

1. **Clone the repository**
```bash
git clone https://github.com/rhythmraheja/personal-finance-manager.git
cd personal-finance-manager
```

2. **Build the project**
```bash
mvn clean install
```

3. **Run the application**
```bash
mvn spring-boot:run
```

4. **Access the API**
```
Base URL: http://localhost:8080/api
H2 Console: http://localhost:8080/h2-console
```

### Running Tests
```bash
mvn test
```

### Generate Test Coverage Report
```bash
mvn jacoco:report
```
Coverage report available at: `target/site/jacoco/index.html`

## 📚 API Documentation

### Authentication Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login` | Login and create session |
| POST | `/api/auth/logout` | Logout and invalidate session |

### Transaction Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/transactions` | Create a transaction |
| GET | `/api/transactions` | Get all transactions (with filters) |
| GET | `/api/transactions/{id}` | Get a specific transaction |
| PUT | `/api/transactions/{id}` | Update a transaction |
| DELETE | `/api/transactions/{id}` | Delete a transaction |

### Category Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/categories` | Get all categories |
| POST | `/api/categories` | Create a custom category |
| DELETE | `/api/categories/{name}` | Delete a custom category |

### Goal Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/goals` | Create a savings goal |
| GET | `/api/goals` | Get all goals |
| GET | `/api/goals/{id}` | Get a specific goal |
| PUT | `/api/goals/{id}` | Update a goal |
| DELETE | `/api/goals/{id}` | Delete a goal |

### Report Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/reports/monthly/{year}/{month}` | Get monthly report |
| GET | `/api/reports/yearly/{year}` | Get yearly report |

## 📝 API Examples

### Register User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user@example.com",
    "password": "password123",
    "fullName": "John Doe",
    "phoneNumber": "+1234567890"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{
    "username": "user@example.com",
    "password": "password123"
  }'
```

### Create Transaction
```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{
    "amount": 5000.00,
    "date": "2024-01-15",
    "category": "Salary",
    "description": "January Salary"
  }'
```

### Create Goal
```bash
curl -X POST http://localhost:8080/api/goals \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{
    "goalName": "Emergency Fund",
    "targetAmount": 10000.00,
    "targetDate": "2026-01-01"
  }'
```

### Get Monthly Report
```bash
curl -X GET "http://localhost:8080/api/reports/monthly/2024/1" \
  -b cookies.txt
```

## 🏗️ Project Structure

```
src/main/java/com/finance/manager/
├── FinanceManagerApplication.java
├── config/
│   ├── SecurityConfig.java
│   └── DataInitializer.java
├── controller/
│   ├── AuthController.java
│   ├── TransactionController.java
│   ├── CategoryController.java
│   ├── GoalController.java
│   └── ReportController.java
├── service/
│   ├── UserService.java
│   ├── TransactionService.java
│   ├── CategoryService.java
│   ├── GoalService.java
│   └── ReportService.java
├── repository/
│   ├── UserRepository.java
│   ├── TransactionRepository.java
│   ├── CategoryRepository.java
│   └── GoalRepository.java
├── entity/
│   ├── User.java
│   ├── Transaction.java
│   ├── Category.java
│   └── Goal.java
├── dto/
│   ├── request/
│   └── response/
├── exception/
│   └── GlobalExceptionHandler.java
└── enums/
    └── TransactionType.java
```

## 🎯 Default Categories

| Category | Type |
|----------|------|
| Salary | INCOME |
| Food | EXPENSE |
| Rent | EXPENSE |
| Transportation | EXPENSE |
| Entertainment | EXPENSE |
| Healthcare | EXPENSE |
| Utilities | EXPENSE |

## 🔒 Security Features

- BCrypt password hashing
- Session-based authentication
- CORS configuration
- Request validation
- Data isolation between users

## 🚀 Deployment to Render

1. Push your code to GitHub

2. Create a new Web Service on [Render](https://render.com)

3. Connect your GitHub repository

4. Configure the service:
   - **Environment**: Docker
   - **Build Command**: (handled by Dockerfile)
   - **Start Command**: (handled by Dockerfile)

5. Add environment variables:
   - `SPRING_PROFILES_ACTIVE`: prod

6. Deploy!

## 📊 Running E2E Tests

```bash
chmod +x financial_manager_tests.sh
./financial_manager_tests.sh http://localhost:8080/api
```

For deployed API:
```bash
./financial_manager_tests.sh https://personal-finance-manager-w76m.onrender.com/api
```

## 🌐 Live Demo

- **API Base URL**: https://personal-finance-manager-w76m.onrender.com/api
- **Health Check**: https://personal-finance-manager-w76m.onrender.com/

## 🧪 Test Coverage

The project maintains 80%+ code coverage with:
- Unit tests for all service classes
- Controller tests with MockMvc
- Edge case coverage

## 📄 License

This project is created for educational purposes.

## 👤 Author

Created as part of a coding assignment.

