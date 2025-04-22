# Accommodation Management System

A Java console application to manage accommodations, restaurants, and payments backed by a MySQL database.

## Features

- **User Authentication**: Sign up, login, and reset password (stored securely in MySQL).
- **Role-based Access**: Admins, guests, and restaurant owners have different menus and permissions.
- **Accommodation Management**: Add, view, book, and delete properties.
- **Restaurant Management**: Add, view by cuisine, and delete restaurant entries.
- **Payment Processing**: Record and process payments for booked accommodations.

## Prerequisites

- **Java 17+** (JDK must be installed and `javac` / `java` available on your PATH).
- **MySQL 8+** (ensure the server is running).
- **MySQL Connector/J**: Place the `mysql-connector-j-9.3.0.jar` in the `lib/` directory.

## Database Setup

1. Log in to MySQL and run the following SQL script to create the schema:

   ```sql
   CREATE DATABASE IF NOT EXISTS accommodation_system;
   USE accommodation_system;

   CREATE TABLE users (
     username VARCHAR(50) PRIMARY KEY,
     password VARCHAR(255) NOT NULL,
     email VARCHAR(100) NOT NULL,
     role ENUM('admin','guest','owner') NOT NULL
   );

   CREATE TABLE accommodations (
     id INT AUTO_INCREMENT PRIMARY KEY,
     location VARCHAR(255) NOT NULL,
     price DOUBLE NOT NULL,
     available BOOLEAN NOT NULL,
     admin_username VARCHAR(50) NOT NULL,
     admin_phone VARCHAR(20),
     booked_by VARCHAR(50),
     FOREIGN KEY (admin_username) REFERENCES users(username),
     FOREIGN KEY (booked_by)    REFERENCES users(username)
   );

   CREATE TABLE restaurants (
     name VARCHAR(100) PRIMARY KEY,
     location VARCHAR(255),
     cuisine VARCHAR(50)
   );

   CREATE TABLE payments (
     payment_id VARCHAR(50) PRIMARY KEY,
     username VARCHAR(50) NOT NULL,
     accommodation_id INT NOT NULL,
     amount DOUBLE NOT NULL,
     payment_date DATETIME NOT NULL,
     payment_method VARCHAR(50),
     is_paid BOOLEAN NOT NULL,
     FOREIGN KEY (username) REFERENCES users(username),
     FOREIGN KEY (accommodation_id) REFERENCES accommodations(id)
   );
   ```

2. Update the connection settings in `DbUtil.java`:

   ```java
   private static final String URL =
     "jdbc:mysql://localhost:3306/accommodation_system?useSSL=false&serverTimezone=UTC";
   private static final String USER = "your_mysql_user";
   private static final String PASSWORD = "your_mysql_password";
   ```

## Project Structure

```
AMS/                           # Root folder
├── lib/                       # Third-party libraries
│    └── mysql-connector-j-9.3.0.jar
├── AccommodationSystem.java   # Main launcher
├── DbUtil.java                # JDBC connection utility
├── AccommodationManager.java  # JDBC-based manager
├── RestaurantManager.java     # JDBC-based manager
├── PaymentManager.java        # JDBC-based manager
├── UserManager.java           # JDBC-based manager
├── PasswordUtils.java         # Utility class
├── Accommodation.java         # Domain model
├── Restaurant.java            # Domain model
├── Payment.java               # Domain model
└── User.java                  # Domain model
```

## Build & Run

1. Open a terminal in the project root (`AMS/`).
2. Compile all Java files:
```bash
   javac -cp ".;lib\mysql-connector-j-9.3.0.jar" *.java
```

3. Run the application:
```bash
   java  -cp ".;lib\mysql-connector-j-9.3.0.jar" AccommodationSystem
```

## Usage

1. **Sign Up**: Create an account specifying role (`admin`, `guest`, or `owner`).
2. **Login**: Use your credentials to access the system.
3. **Admin Dashboard**: Add/delete accommodations, view bookings.
4. **Guest View**: View and book available accommodations.
5. **Restaurant Options**: View restaurants by cuisine or manage if you are an owner.
6. **Payments**: Process payment for bookings and view payment history.

## License

This project is licensed under the MIT License. Feel free to modify and distribute.

