IF DB_ID('AcademicPortal') IS NULL
BEGIN
    CREATE DATABASE AcademicPortal;
END;
GO

USE AcademicPortal;
GO

CREATE TABLE Student (
    StudentID INT IDENTITY PRIMARY KEY,
    FirstName NVARCHAR(50) NOT NULL,
    LastName NVARCHAR(50) NOT NULL,
    Email NVARCHAR(100) NOT NULL UNIQUE CHECK (Email LIKE '%@%.%'),
    Phone NVARCHAR(20) NOT NULL,
    RegistrationDate DATETIME NOT NULL DEFAULT GETDATE()
);

CREATE TABLE Instructor (
    InstructorID INT IDENTITY PRIMARY KEY,
    FirstName NVARCHAR(50) NOT NULL,
    LastName NVARCHAR(50) NOT NULL,
    Email NVARCHAR(100) NOT NULL UNIQUE CHECK (Email LIKE '%@%.%'),
    Expertise NVARCHAR(100) NOT NULL
);

CREATE TABLE Category (
    CategoryID INT IDENTITY PRIMARY KEY,
    CategoryName NVARCHAR(100) NOT NULL,
    Description NVARCHAR(255)
);

CREATE TABLE Program (
    ProgramID INT IDENTITY PRIMARY KEY,
    ProgramTitle NVARCHAR(100) NOT NULL,
    DifficultyLevel NVARCHAR(50) NOT NULL CHECK (DifficultyLevel IN ('Beginner', 'Intermediate', 'Advanced')),
    RegistrationFee DECIMAL(10,2) NOT NULL CHECK (RegistrationFee >= 0),
    Status NVARCHAR(50) NOT NULL CHECK (Status IN ('Active', 'Inactive')),

    InstructorID INT NOT NULL,
    CategoryID INT NOT NULL,

    FOREIGN KEY (InstructorID) REFERENCES Instructor(InstructorID),
    FOREIGN KEY (CategoryID) REFERENCES Category(CategoryID)
);

CREATE TABLE Unit (
    UnitID INT IDENTITY PRIMARY KEY,
    UnitTitle NVARCHAR(100) NOT NULL,
    EstimatedCompletionTime INT NOT NULL CHECK (EstimatedCompletionTime > 0),
    SequenceOrder INT NOT NULL CHECK (SequenceOrder > 0),

    ProgramID INT NOT NULL,
    FOREIGN KEY (ProgramID) REFERENCES Program(ProgramID)
);

CREATE TABLE Enrollment (
    StudentID INT NOT NULL,
    ProgramID INT NOT NULL,
    SignupDate DATETIME NOT NULL DEFAULT GETDATE(),
    ProgressPercentage INT NOT NULL DEFAULT 0 CHECK (ProgressPercentage BETWEEN 0 AND 100),
    CompletionStatus NVARCHAR(50) NOT NULL CHECK (CompletionStatus IN ('In Progress', 'Completed', 'Dropped')),

    PRIMARY KEY (StudentID, ProgramID),

    FOREIGN KEY (StudentID) REFERENCES Student(StudentID) ON DELETE CASCADE,
    FOREIGN KEY (ProgramID) REFERENCES Program(ProgramID)
);

CREATE TABLE UnitProgress (
    StudentID INT NOT NULL,
    UnitID INT NOT NULL,
    UnitStatus NVARCHAR(50) NOT NULL CHECK (UnitStatus IN ('Not Started', 'In Progress', 'Completed')),
    CompletionDate DATETIME NULL,

    PRIMARY KEY (StudentID, UnitID),

    FOREIGN KEY (StudentID) REFERENCES Student(StudentID) ON DELETE CASCADE,
    FOREIGN KEY (UnitID) REFERENCES Unit(UnitID)
);

CREATE TABLE Credential (
    CredentialID INT IDENTITY PRIMARY KEY,
    VerificationCode NVARCHAR(50) NOT NULL UNIQUE,
    GrantedDate DATETIME NOT NULL DEFAULT GETDATE(),

    StudentID INT NOT NULL,
    ProgramID INT NOT NULL,

    FOREIGN KEY (StudentID) REFERENCES Student(StudentID),
    FOREIGN KEY (ProgramID) REFERENCES Program(ProgramID),
    CONSTRAINT UQ_Credential_Student_Program UNIQUE (StudentID, ProgramID)
);

INSERT INTO Student (FirstName, LastName, Email, Phone)
VALUES
('Student', 'One', 'student1@example.com', '0000000001'),
('Student', 'Two', 'student2@example.com', '0000000002'),
('Student', 'Three', 'student3@example.com', '0000000003'),
('Student', 'Four', 'student4@example.com', '0000000004'),
('Student', 'Five', 'student5@example.com', '0000000005');

INSERT INTO Instructor (FirstName, LastName, Email, Expertise)
VALUES
('Instructor', 'One', 'instructor1@example.com', 'Databases'),
('Instructor', 'Two', 'instructor2@example.com', 'Software Engineering');

INSERT INTO Category (CategoryName, Description)
VALUES
('Computer Science', 'Core CS and programming topics'),
('Information Systems', 'Business and IT systems integration'),
('Web Development', 'Frontend and Backend technologies');

INSERT INTO Program (ProgramTitle, DifficultyLevel, RegistrationFee, Status, InstructorID, CategoryID)
VALUES
('Database Fundamentals', 'Beginner', 500, 'Active', 1, 1),
('Advanced Databases', 'Intermediate', 700, 'Active', 1, 1),
('Web Development Basics', 'Beginner', 800, 'Active', 2, 3),
('Business Systems', 'Beginner', 600, 'Active', 2, 2);

INSERT INTO Unit (UnitTitle, EstimatedCompletionTime, SequenceOrder, ProgramID)
VALUES
('Intro to Databases', 5, 1, 1),
('SQL Basics', 7, 2, 1),
('Advanced SQL Queries', 8, 1, 2),
('Database Design', 6, 2, 2),
('HTML Basics', 4, 1, 3),
('CSS Basics', 5, 2, 3),
('JavaScript Intro', 6, 3, 3),
('System Analysis Basics', 6, 1, 4),
('Business Requirements', 5, 2, 4);

INSERT INTO Enrollment (StudentID, ProgramID, SignupDate, ProgressPercentage, CompletionStatus)
VALUES
(1, 1, '2026-03-10', 50, 'In Progress'),
(1, 2, '2026-04-05', 20, 'In Progress'),
(2, 1, '2026-04-15', 100, 'Completed'),
(3, 3, '2026-05-01', 10, 'In Progress'),
(4, 4, '2026-04-20', 0, 'In Progress');

INSERT INTO UnitProgress (StudentID, UnitID, UnitStatus, CompletionDate)
VALUES
(1, 1, 'Completed', '2026-04-10'),
(1, 2, 'In Progress', NULL),
(2, 1, 'Completed', '2026-04-07'),
(2, 2, 'Completed', '2026-04-15'),
(3, 5, 'Not Started', NULL);

INSERT INTO Credential (VerificationCode, GrantedDate, StudentID, ProgramID)
VALUES
('CERT-001', '2026-04-16', 2, 1);
