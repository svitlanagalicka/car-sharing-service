# 🚗 Car Sharing Service

This is a web application for online car rental service, built with **Java**, **Spring Boot**, **Spring Security**,
**JWT**, **Stripe**, **Telegram Bots**, **Docker** and other modern technologies.
---

## 🚀 Features

- **Cars**: add, view, update, delete cars
- **Rentals**: users can rent cars, return them, and view active/past rentals
- **Payments**: integration with payment system (e.g., Stripe) for rental payments
- **Authentication / Registration**: users can register and log in securely using Spring Security and JWT tokens
- **Validation**: input data validation (e.g., rental dates, car availability)
- **Swagger UI**: interactive API documentation
- **Database**: database changes are managed with Liquibase
- **Docker**: run the app with Docker Compose
- **Telegram bot**: notifications via Telegram bot

---

## 🛠️ Technologies

- *Java 17*
- *Spring Boot 3.4.1*
- *Spring Data JPA*
- *Spring Security*
- *Spring Web (MVC)*
- *Lombok 1.18.36*
- *MapStruct 1.6.3*
- *JJWT 0.12.6 (JWT tokens)*
- *Jakarta Validation*
- *MySQL*
- *Liquibase 4.27.0*
- *Springdoc OpenAPI + Swagger UI 2.8.0*
- *Maven*
- *Docker Compose 2.5+*
- *Stripe Java SDK 29.1.0*
- *Telegram Bots Spring Boot Starter 6.9.0*

---

## 🏛️ How is it organized?

### 🔑 AuthController (for login and registration)

- **POST** `/auth/registration` — register a new user
- **POST** `/auth/login` — login and receive JWT token

### 👤 UserController

- **GET** `/users/me` - current user info
- **PUT/PATCH** `/users/me` - update profile / password
- **PUT** `/users/{id}/role` - update role
- **GET** `/users/all` - all users list

### 🚗 CarController

- **POST** `/cars` - add car (MANAGER only)
- **GET** `/cars` - list cars (public)
- **GET** `/cars/{id}` - get car by ID
- **PUT/PATCH** `/cars/{id}` - update
- **DELETE** `/cars/{id}` - delete

### 📄 RentalController

- **POST** `/rentals` - create rental (inventory -1)
- **GET** `/rentals/{id}` - get rental by ID
- **GET** `/rentals/active` - active rentals
- **POST** `/rentals/{id}/return` - return rental (inventory +1)

### 💳 PaymentController

- **POST** `/payments/create` - create Stripe session
- **GET** `/payments/{id}` - get payment
- **GET** `/payments/success/{sessionId}` - Stripe callback (success)
- **GET** `/payments/cancel/{sessionId}` - Stripe callback (cancel)
- **GET** `/payments` - list all (MANAGER) / own (CUSTOMER)

---

## 📋 Requirements

- *Java JDK 17+*
- *Maven*
- *Docker Desktop (version 20+)*
- *Docker Compose (version 2.5+)*

---

## 📝 How to run? (Instructions)
### Step 1: Clone the repository

```bash
git clone https://github.com/YOUR_GITHUB_USERNAME/car-sharing-service.git
cd car-sharing-service
```

### Step 2: Configure the .env file
```env
MYSQLDB_USER=<your_mysql_username>
MYSQLDB_ROOT_PASSWORD=<your_mysql_root_password>
MYSQLDB_DATABASE=car_sharing_service
MYSQL_LOCAL_PORT=3308
MYSQL_DOCKER_PORT=3306

JWT_EXPIRATION=3600000
JWT_SECRET=<your_jwt_secret_key>

SPRING_LOCAL_PORT=8080
SPRING_DOCKER_PORT=8080
DEBUG_PORT=5005
```
⚠️ **Important**: Never commit the .env file to GitHub!

### 🐳 Step 3: Run with Docker
```bash
docker compose up --build
```

### 🌐 Step 4: Access the app

Start the application using command:
```
mvn spring-boot:run
```

If you run the app locally (**not inside Docker**), open:
```
http://localhost:8081/api
```

**API documentation (Swagger UI)** is available at:
```
http://localhost:8081/api/swagger-ui/index.html
```

---

### 📊 Database structure (schema)
The diagram below illustrates the main relationships between tables/entities in the **Car Sharing Service application**:

- `Users` ↔ `Roles`: *Many-to-many*
- `Users` ↔ `Rentals`: *One-to-many* — a user can have multiple rentals
- `Rental` ↔ `Car`: *Many-to-one* — each rental is linked to a specific car
- `Car` ↔ `Category`: *Many-to-one* — each car belongs to one category
- `Rental` ↔ `Payment`: *One-to-one* — each rental has a payment

### 📬 Postman Collection
To test the API endpoints, use the included Postman collection:
postman/car-sharing-service.postman_collection.json

### 🧪 How to use:
```text
1. Open Postman
2. Click "Import" -> select the .json file from the repository
3. Authenticate: POST /auth/login with your credentials
4. Copy the JWT token from the login response
5. For subsequent requests, go to the Authorization tab -> select Bearer Token -> paste your JWT token
6. Now you can send authenticated requests with proper permissions
```