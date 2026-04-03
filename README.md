# Simple Java Client-Server (Distributed Systems Homework)

This project demonstrates basic client-server communication using Java sockets and MySQL.

## What It Does
- Client sends a request to server using TCP socket on port `5000`
- Server handles two operations:
  - `REGISTER`: insert one student
  - `FILTER`: search by `id`, `age`, or `gender`
- Server talks to MySQL database (`student_db.students`) and sends response back to client

## Database (XAMPP MySQL)
Use your existing SQL:

```sql
CREATE DATABASE student_db;

USE student_db;

CREATE TABLE students (
    id INT PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100),
    gender VARCHAR(10),
    age INT
);
```

## Requirements
1. Java JDK 8+ installed
2. MySQL running from XAMPP
3. MySQL JDBC driver jar (mysql-connector-j)

Download example:
- `mysql-connector-j-8.3.0.jar`

Put the jar in the `utils` folder:

```text
Client-Server/
  Client.java
  Server.java
  utils/
    DBConnection.java
    mysql-connector-j-8.3.0.jar
```

  Important:
  - Use `mysql-connector-j-8.3.0.jar` (runtime driver)
  - Do not use `mysql-connector-j-8.3.0-javadoc.jar` (documentation only)

## Compile (Windows CMD/PowerShell)
Run in project folder:

```bash
javac -cp ".;utils/*" Server.java Client.java utils/DBConnection.java
```

## Run
1. Start MySQL in XAMPP
2. Run server in terminal 1:

```bash
java -cp ".;utils/*" Server
```

3. Run client in terminal 2:

```bash
java -cp ".;utils/*" Client
```

## Protocol Messages
### Register
Client sends:

```text
REGISTER,id,name,email,gender,age
```

Example:

```text
REGISTER,1,Sam,sam@gmail.com,Male,22
```

### Filter
Client sends:

```text
FILTER,field,value
```

Examples:

```text
FILTER,age,22
FILTER,gender,Male
FILTER,id,1
```

## Communication Flow
### Register flow
1. Client -> Server: `REGISTER,1,Sam,sam@gmail.com,Male,22`
2. Server -> DB: `INSERT INTO students ...`
3. Server -> Client: `Student Registered`

### Filter flow
1. Client -> Server: `FILTER,age,22`
2. Server -> DB: `SELECT * FROM students WHERE age = 22`
3. Server -> Client: matching rows or `No students found`

## Notes
- DB credentials are now in `utils/DBConnection.java`:
  - URL: `jdbc:mysql://localhost:3306/student_db`
  - USER: `root`
  - PASS: empty string
- Change them in `utils/DBConnection.java` if your XAMPP setup is different.
