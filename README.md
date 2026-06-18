# Library Management System

## Overview

The Library Management System is a Java-based desktop application developed to streamline library operations, including user management, book cataloguing, borrowing and returning books, transaction tracking, and fine management.

The system implements role-based access control for Administrators, Librarians, and Students, ensuring that each user has access only to relevant functionalities.

## Features

### Authentication & User Management

* User registration and login
* Role-based access control
* Administrator account management

### Book Management

* Add new books
* Update book information
* Delete books
* Search books by title, author, ISBN, or category

### Borrowing & Returning

* Borrow available books
* Return borrowed books
* Automatic transaction tracking
* Due date management

### Reporting & Tracking

* View borrowing history
* Transaction management
* Fine tracking for overdue books

## User Roles

### Administrator

* Manage users
* Manage books
* View reports
* Access all system features

### Librarian

* Add, edit, and delete books
* Process borrowing and returning transactions
* View reports

### Student/User

* Register an account
* Search available books
* Borrow books
* Return books
* View personal transaction history

## Technologies Used

* Java (JDK 8+)
* Java Swing (GUI)
* JDBC
* MySQL Database
* XAMPP (MySQL Server)
* Maven

## System Architecture

The application follows the Model-View-Controller (MVC) architecture:

### Model

Handles business logic and database interaction.

Examples:

* UserDAO
* BookDAO

### View

Graphical user interface built with Java Swing.

Examples:

* Login Page
* Registration Form
* Dashboard

### Controller

Processes user actions and coordinates communication between views and models.

## Class Diagram

The project uses an object-oriented design consisting of:

* User
* Student
* Librarian
* Admin
* Book
* Transaction
* BorrowingTransaction
* ReturningTransaction

The class diagram is available in the repository documentation.

## Database Setup

### Requirements

* MySQL Server
* XAMPP (optional)
* MySQL JDBC Driver

### Create Database

Create a database named:

project_db

### Import Database

Import the provided SQL file:

database/library_management.sql

### Update Database Connection

Configure your database credentials in the DBConnection class:

jdbc:mysql://localhost:3306/project_db

## Running the Application

### Option 1: Run from IDE

1. Open the project in IntelliJ IDEA, Eclipse, or NetBeans.
2. Configure the MySQL database.
3. Add the MySQL JDBC Driver.
4. Run the main application file.

### Option 2: Run Using JAR

java -jar project-1.0-SNAPSHOT.jar

or

java -jar project-1.0-SNAPSHOT-jar-with-dependencies.jar

## Screenshots

### Login Screen

(Add screenshot here)

### Registration Screen

(Add screenshot here)

### Admin Dashboard

(Add screenshot here)

### Librarian Dashboard

(Add screenshot here)

### Book Management

(Add screenshot here)

### Transaction Management

(Add screenshot here)

## Future Enhancements

* Password hashing and encryption
* Password reset functionality
* Email notifications
* PDF and CSV report export
* Improved UI/UX
* Dark mode support
* Enhanced analytics and reporting

## Learning Outcomes

This project demonstrates practical experience in:

* Object-Oriented Programming (OOP)
* Java Desktop Application Development
* Database Design
* JDBC Integration
* MVC Architecture
* Role-Based Access Control
* Software Documentation

## Author

Jonathan Festus

Aspiring Software Engineer | Java Developer | Web Developer

Feel free to connect and provide feedback on the project.
