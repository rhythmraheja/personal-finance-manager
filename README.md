# Personal Finance Manager

A production-grade personal finance management REST API built with Spring Boot 3.2. This application enables users to track income, expenses, and savings goals through a secure, session-based authentication system.

## 🌐 Live Demo

| Environment | URL |
|-------------|-----|
| **Production API** | https://personal-finance-manager-w76m.onrender.com/api |
| **Health Check** | https://personal-finance-manager-w76m.onrender.com/ |
| **Local Development** | http://localhost:8080/api |

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
| Coverage | JaCoCo (83%+) |
| Deployment | Docker, Render |

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- Git

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
mvn verify
```
Coverage report available at: `target/site/jacoco/index.html`

---

## 📚 API Documentation

### Base URLs

| Environment | Base URL |
|-------------|----------|
| **Local** | `http://localhost:8080/api` |
| **Production** | `https://personal-finance-manager-w76m.onrender.com/api` |

### HTTP Status Codes

| Code | Description |
|------|-------------|
| 200 | Success |
| 201 | Created |
| 400 | Bad Request (validation errors) |
| 401 | Unauthorized (invalid credentials/session) |
| 403 | Forbidden (accessing other user's data) |
| 404 | Resource Not Found |
| 409 | Conflict (duplicate resource) |

---

## 🔐 Authentication API

### Register User

**Endpoint:** `POST /api/auth/register`

<details>
<summary><b>📍 Local Development</b></summary>

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
</details>

<details>
<summary><b>🌐 Production API</b></summary>

```bash
curl -X POST https://personal-finance-manager-w76m.onrender.com/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user@example.com",
    "password": "password123",
    "fullName": "John Doe",
    "phoneNumber": "+1234567890"
  }'
```
</details>

**Response (201 Created):**
```json
{
  "message": "User registered successfully",
  "userId": 1
}
```

---

### Login

**Endpoint:** `POST /api/auth/login`

<details>
<summary><b>📍 Local Development</b></summary>

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{
    "username": "user@example.com",
    "password": "password123"
  }'
```
</details>

<details>
<summary><b>🌐 Production API</b></summary>

```bash
curl -X POST https://personal-finance-manager-w76m.onrender.com/api/auth/login \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{
    "username": "user@example.com",
    "password": "password123"
  }'
```
</details>

**Response (200 OK):**
```json
{
  "message": "Login successful"
}
```

> **Note:** Save the session cookie using `-c cookies.txt` for subsequent authenticated requests.

---

### Logout

**Endpoint:** `POST /api/auth/logout`

<details>
<summary><b>📍 Local Development</b></summary>

```bash
curl -X POST http://localhost:8080/api/auth/logout \
  -b cookies.txt
```
</details>

<details>
<summary><b>🌐 Production API</b></summary>

```bash
curl -X POST https://personal-finance-manager-w76m.onrender.com/api/auth/logout \
  -b cookies.txt
```
</details>

**Response (200 OK):**
```json
{
  "message": "Logout successful"
}
```

---

## 💰 Transaction API

### Create Transaction

**Endpoint:** `POST /api/transactions`

<details>
<summary><b>📍 Local Development</b></summary>

```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{
    "amount": 50000.00,
    "date": "2024-01-15",
    "category": "Salary",
    "description": "January Salary"
  }'
```
</details>

<details>
<summary><b>🌐 Production API</b></summary>

```bash
curl -X POST https://personal-finance-manager-w76m.onrender.com/api/transactions \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{
    "amount": 50000.00,
    "date": "2024-01-15",
    "category": "Salary",
    "description": "January Salary"
  }'
```
</details>

**Response (201 Created):**
```json
{
  "id": 1,
  "amount": 50000.00,
  "date": "2024-01-15",
  "category": "Salary",
  "description": "January Salary",
  "type": "INCOME"
}
```

---

### Get All Transactions

**Endpoint:** `GET /api/transactions`

**Query Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| startDate | String | Filter by start date (YYYY-MM-DD) |
| endDate | String | Filter by end date (YYYY-MM-DD) |
| category | String | Filter by category name |

<details>
<summary><b>📍 Local Development</b></summary>

```bash
# Get all transactions
curl -X GET http://localhost:8080/api/transactions \
  -b cookies.txt

# With filters
curl -X GET "http://localhost:8080/api/transactions?startDate=2024-01-01&endDate=2024-12-31&category=Salary" \
  -b cookies.txt
```
</details>

<details>
<summary><b>🌐 Production API</b></summary>

```bash
# Get all transactions
curl -X GET https://personal-finance-manager-w76m.onrender.com/api/transactions \
  -b cookies.txt

# With filters
curl -X GET "https://personal-finance-manager-w76m.onrender.com/api/transactions?startDate=2024-01-01&endDate=2024-12-31" \
  -b cookies.txt
```
</details>

**Response (200 OK):**
```json
{
  "transactions": [
    {
      "id": 1,
      "amount": 50000.00,
      "date": "2024-01-15",
      "category": "Salary",
      "description": "January Salary",
      "type": "INCOME"
    }
  ]
}
```

---

### Update Transaction

**Endpoint:** `PUT /api/transactions/{id}`

<details>
<summary><b>📍 Local Development</b></summary>

```bash
curl -X PUT http://localhost:8080/api/transactions/1 \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{
    "amount": 55000.00,
    "description": "Updated January Salary"
  }'
```
</details>

<details>
<summary><b>🌐 Production API</b></summary>

```bash
curl -X PUT https://personal-finance-manager-w76m.onrender.com/api/transactions/1 \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{
    "amount": 55000.00,
    "description": "Updated January Salary"
  }'
```
</details>

**Response (200 OK):**
```json
{
  "id": 1,
  "amount": 55000.00,
  "date": "2024-01-15",
  "category": "Salary",
  "description": "Updated January Salary",
  "type": "INCOME"
}
```

---

### Delete Transaction

**Endpoint:** `DELETE /api/transactions/{id}`

<details>
<summary><b>📍 Local Development</b></summary>

```bash
curl -X DELETE http://localhost:8080/api/transactions/1 \
  -b cookies.txt
```
</details>

<details>
<summary><b>🌐 Production API</b></summary>

```bash
curl -X DELETE https://personal-finance-manager-w76m.onrender.com/api/transactions/1 \
  -b cookies.txt
```
</details>

**Response (200 OK):**
```json
{
  "message": "Transaction deleted successfully"
}
```

---

## 📁 Category API

### Get All Categories

**Endpoint:** `GET /api/categories`

<details>
<summary><b>📍 Local Development</b></summary>

```bash
curl -X GET http://localhost:8080/api/categories \
  -b cookies.txt
```
</details>

<details>
<summary><b>🌐 Production API</b></summary>

```bash
curl -X GET https://personal-finance-manager-w76m.onrender.com/api/categories \
  -b cookies.txt
```
</details>

**Response (200 OK):**
```json
{
  "categories": [
    { "name": "Salary", "type": "INCOME", "custom": false },
    { "name": "Food", "type": "EXPENSE", "custom": false },
    { "name": "Rent", "type": "EXPENSE", "custom": false },
    { "name": "Transportation", "type": "EXPENSE", "custom": false },
    { "name": "Entertainment", "type": "EXPENSE", "custom": false },
    { "name": "Healthcare", "type": "EXPENSE", "custom": false },
    { "name": "Utilities", "type": "EXPENSE", "custom": false }
  ]
}
```

---

### Create Custom Category

**Endpoint:** `POST /api/categories`

<details>
<summary><b>📍 Local Development</b></summary>

```bash
curl -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{
    "name": "Freelance",
    "type": "INCOME"
  }'
```
</details>

<details>
<summary><b>🌐 Production API</b></summary>

```bash
curl -X POST https://personal-finance-manager-w76m.onrender.com/api/categories \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{
    "name": "Freelance",
    "type": "INCOME"
  }'
```
</details>

**Response (201 Created):**
```json
{
  "name": "Freelance",
  "type": "INCOME",
  "custom": true
}
```

---

### Delete Custom Category

**Endpoint:** `DELETE /api/categories/{name}`

<details>
<summary><b>📍 Local Development</b></summary>

```bash
curl -X DELETE http://localhost:8080/api/categories/Freelance \
  -b cookies.txt
```
</details>

<details>
<summary><b>🌐 Production API</b></summary>

```bash
curl -X DELETE https://personal-finance-manager-w76m.onrender.com/api/categories/Freelance \
  -b cookies.txt
```
</details>

**Response (200 OK):**
```json
{
  "message": "Category deleted successfully"
}
```

> **Note:** Default categories cannot be deleted. Only custom categories can be removed.

---

## 🎯 Savings Goals API

### Create Goal

**Endpoint:** `POST /api/goals`

<details>
<summary><b>📍 Local Development</b></summary>

```bash
curl -X POST http://localhost:8080/api/goals \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{
    "goalName": "Emergency Fund",
    "targetAmount": 100000.00,
    "targetDate": "2026-12-31",
    "startDate": "2025-01-01"
  }'
```
</details>

<details>
<summary><b>🌐 Production API</b></summary>

```bash
curl -X POST https://personal-finance-manager-w76m.onrender.com/api/goals \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{
    "goalName": "Emergency Fund",
    "targetAmount": 100000.00,
    "targetDate": "2026-12-31",
    "startDate": "2025-01-01"
  }'
```
</details>

**Response (201 Created):**
```json
{
  "id": 1,
  "goalName": "Emergency Fund",
  "targetAmount": 100000.00,
  "targetDate": "2026-12-31",
  "startDate": "2025-01-01",
  "currentProgress": 0,
  "progressPercentage": 0.0,
  "remainingAmount": 100000.00
}
```

---

### Get All Goals

**Endpoint:** `GET /api/goals`

<details>
<summary><b>📍 Local Development</b></summary>

```bash
curl -X GET http://localhost:8080/api/goals \
  -b cookies.txt
```
</details>

<details>
<summary><b>🌐 Production API</b></summary>

```bash
curl -X GET https://personal-finance-manager-w76m.onrender.com/api/goals \
  -b cookies.txt
```
</details>

**Response (200 OK):**
```json
{
  "goals": [
    {
      "id": 1,
      "goalName": "Emergency Fund",
      "targetAmount": 100000.00,
      "targetDate": "2026-12-31",
      "startDate": "2025-01-01",
      "currentProgress": 25000.00,
      "progressPercentage": 25.0,
      "remainingAmount": 75000.00
    }
  ]
}
```

---

### Update Goal

**Endpoint:** `PUT /api/goals/{id}`

<details>
<summary><b>📍 Local Development</b></summary>

```bash
curl -X PUT http://localhost:8080/api/goals/1 \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{
    "targetAmount": 150000.00,
    "targetDate": "2027-06-30"
  }'
```
</details>

<details>
<summary><b>🌐 Production API</b></summary>

```bash
curl -X PUT https://personal-finance-manager-w76m.onrender.com/api/goals/1 \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{
    "targetAmount": 150000.00,
    "targetDate": "2027-06-30"
  }'
```
</details>

**Response (200 OK):**
```json
{
  "id": 1,
  "goalName": "Emergency Fund",
  "targetAmount": 150000.00,
  "targetDate": "2027-06-30",
  "startDate": "2025-01-01",
  "currentProgress": 25000.00,
  "progressPercentage": 16.67,
  "remainingAmount": 125000.00
}
```

---

### Delete Goal

**Endpoint:** `DELETE /api/goals/{id}`

<details>
<summary><b>📍 Local Development</b></summary>

```bash
curl -X DELETE http://localhost:8080/api/goals/1 \
  -b cookies.txt
```
</details>

<details>
<summary><b>🌐 Production API</b></summary>

```bash
curl -X DELETE https://personal-finance-manager-w76m.onrender.com/api/goals/1 \
  -b cookies.txt
```
</details>

**Response (200 OK):**
```json
{
  "message": "Goal deleted successfully"
}
```

---

## 📊 Reports API

### Monthly Report

**Endpoint:** `GET /api/reports/monthly/{year}/{month}`

<details>
<summary><b>📍 Local Development</b></summary>

```bash
curl -X GET http://localhost:8080/api/reports/monthly/2024/1 \
  -b cookies.txt
```
</details>

<details>
<summary><b>🌐 Production API</b></summary>

```bash
curl -X GET https://personal-finance-manager-w76m.onrender.com/api/reports/monthly/2024/1 \
  -b cookies.txt
```
</details>

**Response (200 OK):**
```json
{
  "month": 1,
  "year": 2024,
  "totalIncome": {
    "Salary": 50000.00,
    "Freelance": 10000.00
  },
  "totalExpenses": {
    "Food": 5000.00,
    "Rent": 15000.00,
    "Transportation": 3000.00
  },
  "netSavings": 37000.00
}
```

---

### Yearly Report

**Endpoint:** `GET /api/reports/yearly/{year}`

<details>
<summary><b>📍 Local Development</b></summary>

```bash
curl -X GET http://localhost:8080/api/reports/yearly/2024 \
  -b cookies.txt
```
</details>

<details>
<summary><b>🌐 Production API</b></summary>

```bash
curl -X GET https://personal-finance-manager-w76m.onrender.com/api/reports/yearly/2024 \
  -b cookies.txt
```
</details>

**Response (200 OK):**
```json
{
  "year": 2024,
  "totalIncome": {
    "Salary": 600000.00,
    "Freelance": 120000.00
  },
  "totalExpenses": {
    "Food": 60000.00,
    "Rent": 180000.00,
    "Transportation": 36000.00
  },
  "netSavings": 444000.00
}
```

---

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
│   ├── ReportController.java
│   └── HomeController.java
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
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   ├── DuplicateResourceException.java
│   ├── InvalidRequestException.java
│   ├── ForbiddenException.java
│   └── UnauthorizedException.java
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
- Session-based authentication with secure cookies
- CORS configuration
- Input validation with detailed error messages
- Complete data isolation between users
- Protection against unauthorized access

## 🚀 Deployment

### Deploy to Render

1. Push your code to GitHub

2. Create a new Web Service on [Render](https://render.com)

3. Connect your GitHub repository

4. Configure the service:
   - **Environment**: Docker
   - **Build Command**: (handled by Dockerfile)
   - **Start Command**: (handled by Dockerfile)

5. Add environment variables:
   ```
   SPRING_PROFILES_ACTIVE=prod
   ```

6. Deploy!

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | `default` |
| `SERVER_PORT` | Application port | `8080` |

## 📊 Running E2E Tests

### Local Testing
```bash
chmod +x financial_manager_tests.sh
./financial_manager_tests.sh http://localhost:8080/api
```

### Production Testing
```bash
./financial_manager_tests.sh https://personal-finance-manager-w76m.onrender.com/api
```

### Expected Output
```
TEST EXECUTION SUMMARY
================================================
Total Tests Executed: 86
Tests Passed: 86
Tests Failed: 0
Success Rate: 100%
🎉 ALL TESTS PASSED! 🎉
```

## 🧪 Test Coverage

| Metric | Coverage |
|--------|----------|
| **Instructions** | 83% |
| **Branches** | 82% |
| **Controller** | 100% |
| **Service** | 97% |

The project maintains 80%+ code coverage with:
- Unit tests for all service classes
- Controller tests with MockMvc
- DTO and Entity tests
- Exception handling tests


## 👤 Author

**Rhythm Raheja**
- GitHub: [@rhythmraheja](https://github.com/rhythmraheja)
