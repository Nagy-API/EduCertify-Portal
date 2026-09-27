USE AcademicPortal;
GO

-- Dates for last month reports
DECLARE @StartOfLastMonth DATE = DATEADD(MONTH, DATEDIFF(MONTH, 0, GETDATE()) - 1, 0);
DECLARE @StartOfThisMonth DATE = DATEADD(MONTH, DATEDIFF(MONTH, 0, GETDATE()), 0);

-- 2 INSERT queries
DECLARE @NewStudentID INT;

INSERT INTO Student (FirstName, LastName, Email, Phone)
VALUES ('Student', 'Six', 'student6@example.com', '0000000006');

SET @NewStudentID = SCOPE_IDENTITY();

INSERT INTO Enrollment (StudentID, ProgramID, SignupDate, ProgressPercentage, CompletionStatus)
VALUES (@NewStudentID, 2, GETDATE(), 30, 'In Progress');

-- 2 UPDATE queries
UPDATE Student
SET Email = 'student1.updated@example.com'
WHERE StudentID = 1;

UPDATE Enrollment
SET ProgressPercentage = 75,
    CompletionStatus = 'In Progress'
WHERE StudentID = 1 AND ProgramID = 1;

-- 2 DELETE queries
DELETE FROM Enrollment
WHERE StudentID = 3 AND ProgramID = 3;

DELETE FROM Student
WHERE StudentID = 5;

-- Simple SELECT queries
SELECT * FROM Student;
SELECT * FROM Instructor;
SELECT * FROM Category;
SELECT * FROM Program;
SELECT * FROM Unit;
SELECT * FROM Enrollment;
SELECT * FROM UnitProgress;
SELECT * FROM Credential;

-- JOIN query: show students with their programs and progress
SELECT
    s.FirstName,
    s.LastName,
    p.ProgramTitle,
    e.ProgressPercentage,
    e.CompletionStatus
FROM Enrollment e
JOIN Student s ON e.StudentID = s.StudentID
JOIN Program p ON e.ProgramID = p.ProgramID;

-- Students who completed programs
SELECT
    s.FirstName,
    s.LastName,
    p.ProgramTitle
FROM Enrollment e
JOIN Student s ON e.StudentID = s.StudentID
JOIN Program p ON e.ProgramID = p.ProgramID
WHERE e.CompletionStatus = 'Completed';

-- Number of students in each program
SELECT
    p.ProgramTitle,
    COUNT(e.StudentID) AS NumberOfStudents
FROM Program p
LEFT JOIN Enrollment e ON p.ProgramID = e.ProgramID
GROUP BY p.ProgramTitle;

-- Report 1: Most popular program by signups
SELECT TOP 1
    p.ProgramTitle,
    COUNT(e.StudentID) AS NumberOfSignups
FROM Program p
JOIN Enrollment e ON p.ProgramID = e.ProgramID
GROUP BY p.ProgramTitle
ORDER BY NumberOfSignups DESC;

-- Report 2: Programs with zero signups last month
SELECT
    p.ProgramTitle
FROM Program p
LEFT JOIN Enrollment e
    ON p.ProgramID = e.ProgramID
    AND e.SignupDate >= @StartOfLastMonth
    AND e.SignupDate < @StartOfThisMonth
WHERE e.StudentID IS NULL;

-- Report 3: Instructor with the most signups last month
SELECT TOP 1
    i.FirstName,
    i.LastName,
    COUNT(e.StudentID) AS TotalSignups
FROM Instructor i
JOIN Program p ON i.InstructorID = p.InstructorID
JOIN Enrollment e ON p.ProgramID = e.ProgramID
WHERE e.SignupDate >= @StartOfLastMonth
  AND e.SignupDate < @StartOfThisMonth
GROUP BY i.FirstName, i.LastName
ORDER BY TotalSignups DESC;

-- Report 4: Students signed up but completed no units last month
SELECT DISTINCT
    s.FirstName,
    s.LastName
FROM Student s
JOIN Enrollment e ON s.StudentID = e.StudentID
WHERE e.SignupDate >= @StartOfLastMonth
  AND e.SignupDate < @StartOfThisMonth
  AND s.StudentID NOT IN (
      SELECT StudentID
      FROM UnitProgress
      WHERE UnitStatus = 'Completed'
        AND CompletionDate >= @StartOfLastMonth
        AND CompletionDate < @StartOfThisMonth
  );

-- Report 5: Active programs under each category
SELECT
    c.CategoryName,
    p.ProgramTitle
FROM Category c
JOIN Program p ON c.CategoryID = p.CategoryID
WHERE p.Status = 'Active';

-- Report 6: Student email and number of credentials
SELECT
    s.Email,
    COUNT(c.CredentialID) AS TotalCredentials
FROM Student s
LEFT JOIN Credential c ON s.StudentID = c.StudentID
GROUP BY s.StudentID, s.Email;
