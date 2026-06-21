# Library Management System

A Java-based desktop application designed to simplify library operations such as book management, user management, borrowing and returning books, transaction tracking, and fine management.

## Overview

The Library Management System provides a centralized platform for managing library resources and users. The application follows the Model-View-Controller (MVC) architecture and uses Java Swing for the graphical user interface and MySQL for data storage.

The system supports multiple user roles including Administrators, Librarians, and Students, each with different permissions and functionalities.

---

## Features

### User Authentication

* User registration
* Secure login system
* Role-based access control

### Book Management

* Add books
* Update book information
* Delete books
* Search books by title, author, ISBN, or category

### Borrowing & Returning

* Borrow available books
* Return borrowed books
* Automatic due date tracking
* Fine calculation for overdue books

### Transaction Management

* View borrowing history
* Track active loans
* Monitor returned books

### Administrative Features

* User management
* Library inventory management
* Report viewing

---

## User Roles

### Administrator

* Full access to the system
* Manage users
* Manage books
* View reports
* Monitor transactions

### Librarian

* Manage books
* Process borrow and return requests
* View reports
* Monitor transactions

### Student/User

* Register an account
* Search books
* Borrow books
* Return books
* View personal borrowing history

---

## Technologies Used

* Java (JDK 8+)
* Java Swing
* JDBC
* MySQL
* XAMPP
* Maven
* MVC Architecture

---

## Project Structure

```text
src/
├── controller/
├── db/
├── model/
├── view/
└── main/
```

### Main Packages

| Package    | Description                            |
| ---------- | -------------------------------------- |
| main       | Application entry point                |
| model      | Business logic and database operations |
| view       | User interface components              |
| controller | Event handling and application control |
| db         | Database connection management         |

---

## Class Diagram

The system consists of several object-oriented components including:

* User
* Student
* Librarian
* Admin
* Book
* Transaction
* BorrowingTransaction
* ReturningTransaction

Refer to the documentation folder for the complete UML/Class Diagram.

---

## Screenshots

### Login Screen

Add screenshot here.

### Registration Screen

Add screenshot here.

### Dashboard

Add screenshot here.

### Book Management

Add screenshot here.

### Borrowing & Returning

Add screenshot here.

---

## System Requirements

* Java Runtime Environment (JRE) 8 or later
* MySQL Server
* XAMPP (optional)
* Windows, Linux, or macOS

---

## Database Setup

### Step 1: Create Database

Create a database named:

```sql
CREATE DATABASE projectdb;
```

### Step 2: Import Database

Import the SQL file included in:

```text
database/library_management.sql
```

### Step 3: Verify Database Connection

The application uses:

```java
jdbc:mysql://localhost:3306/projectdb
```

Default credentials:

```text
Host: localhost
Port: 3306
Database: projectdb
Username: root
Password: (empty)
```

Update credentials if your local MySQL setup differs.

---

## Running the Application

### Option 1: Run from IDE

1. Open the project in NetBeans, IntelliJ IDEA, or Eclipse.
2. Ensure MySQL is running.
3. Import the database.
4. Run the main application class.

### Option 2: Run Using Executable JAR

Use the bundled JAR file:

locate the folder ' target ' , open terminal and run the line below

```bash
java -jar project-1.0-SNAPSHOT-jar-with-dependencies.jar
```

**Important:** Use the `jar-with-dependencies` version because it contains all required libraries, including the MySQL JDBC driver.

---

## Documentation

Project documentation and user manuals are available in:

```text
docs/
```

Included documents:

* Technical Documentation
* User Manual
* UML/Class Diagram

---

## Future Improvements

* Password reset functionality
* Email notifications
* PDF and CSV report generation
* Improved UI/UX
* Dark mode support
* Enhanced reporting and analytics

---

## Learning Outcomes

This project demonstrates knowledge and practical experience in:

* Object-Oriented Programming (OOP)
* Java Desktop Application Development
* MVC Architecture
* JDBC Database Connectivity
* MySQL Database Design
* Software Documentation
* User Authentication and Authorization
* CRUD Operations

---

## Author

Jonathan Elochukwu Festus

Aspiring Software Engineer | Java Developer | Web Developer

GitHub: https://github.com/Jona710than/Library-Management-System.git

LinkedIn: https://linkedin.com/in/jonathan-festus-960873257

---

## License

This project was developed for educational and portfolio purposes.
