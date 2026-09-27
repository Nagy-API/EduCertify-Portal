# EduCertify Portal

**Academic Java & Database Project**

EduCertify Portal is an academic desktop application for managing educational programs, students, enrollments, learning progress, and credentials. Its JavaFX interface uses JDBC to read and update a local Microsoft SQL Server database. The repository includes the database schema, example data, and a separate SQL query exercise script.

## Features

- Manage students, instructors, categories, programs, and units
- Enroll students in programs and update enrollment status and progress
- Track completion of individual units
- Issue credentials and look them up by verification code
- Filter programs and view database reports

## Tech Stack

Java 17, JavaFX 21, Maven, JDBC, Microsoft SQL Server, and T-SQL.

## Architecture / Project Structure

- `src/main/java/com/academicportal/Main.java` — JavaFX entry point and navigation
- `src/main/java/com/academicportal/ui/` — desktop views and form interactions
- `src/main/java/com/academicportal/dao/` — JDBC queries and updates
- `src/main/java/com/academicportal/model/` — application data models
- `src/main/java/com/academicportal/db/` — database connection configuration
- `Database/AcademicPortal_DDL.sql` — schema and fictional example records
- `Database/AcademicPortal_Queries.sql` — SQL exercises and reports; this script **changes data** and is not required to start the app

## Database

The application expects a Microsoft SQL Server database named `AcademicPortal`. In SQL Server Management Studio, run [`Database/AcademicPortal_DDL.sql`](Database/AcademicPortal_DDL.sql) on a local SQL Server instance. The script creates the database, tables, constraints, and fictional seed records; run it on a fresh instance because it is not an idempotent migration. Configure a SQL Server login with access to the database for the desktop application. SQL Server must accept TCP connections on the configured host and port.

## Getting Started

1. Install JDK 17, Maven 3.9 or newer, and Microsoft SQL Server. Enable SQL Server authentication and TCP/IP for the instance you will use.
2. Clone the repository:

   ```bash
   git clone https://github.com/Nagy-API/EduCertify-Portal.git
   cd EduCertify-Portal
   ```

3. Initialize the database with the DDL script above and create a SQL login for the application.
4. Set the connection environment variables. The default URL connects to `localhost:1433`, database `AcademicPortal`. Set `EDUCERTIFY_DB_URL` if your SQL Server address or database differs.

   PowerShell:

   ```powershell
   $env:EDUCERTIFY_DB_USER = "your_sql_login"
   $env:EDUCERTIFY_DB_PASSWORD = Read-Host "SQL password"
   # Optional: $env:EDUCERTIFY_DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=AcademicPortal;encrypt=true;trustServerCertificate=true;loginTimeout=5;"
   ```

   Bash:

   ```bash
   export EDUCERTIFY_DB_USER='your_sql_login'
   read -r -s -p 'SQL password: ' EDUCERTIFY_DB_PASSWORD; echo
   export EDUCERTIFY_DB_PASSWORD
   # Optional: export EDUCERTIFY_DB_URL='jdbc:sqlserver://localhost:1433;databaseName=AcademicPortal;encrypt=true;trustServerCertificate=true;loginTimeout=5;'
   ```

   The default URL trusts the local SQL Server certificate for development. Use a validated certificate and an appropriate JDBC URL when connecting to another server. Keep real credentials out of source files, shell history, and Git.

5. Build and run from the repository root:

   ```bash
   mvn clean verify
   mvn javafx:run
   ```

## Academic Context

This project was developed as an academic project to practice Java application development, relational database design, JDBC, CRUD operations, and layered application structure. The original project team: Jana Ahmed Farid, Shrouk Ashraf Fathy, Rahma Ahmed Samy, Youssef Ahmed Abd El-Tawab Nagy, and Salma Mohsen Said.

## Current Scope / Limitations

This is a desktop application that depends on a locally configured SQL Server database. It has no hosted production deployment. The example SQL query script is for coursework demonstrations and may modify the seed data.
