package com.academicportal.dao;

import com.academicportal.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportsDAO {

    public static class ReportResult {
        public final List<String> columns;
        public final List<List<String>> rows;

        public ReportResult(List<String> columns, List<List<String>> rows) {
            this.columns = columns;
            this.rows = rows;
        }
    }

    public ReportResult runReport(int reportNumber) throws SQLException {
        return runQuery(getSql(reportNumber));
    }

    private String getSql(int reportNumber) {
        String lastMonthStart = "DATEADD(MONTH, DATEDIFF(MONTH, 0, GETDATE()) - 1, 0)";
        String thisMonthStart = "DATEADD(MONTH, DATEDIFF(MONTH, 0, GETDATE()), 0)";

        return switch (reportNumber) {
            case 1 -> """
                    SELECT TOP 1
                        p.ProgramTitle,
                        COUNT(*) AS SignupCount
                    FROM Enrollment e
                    JOIN [Program] p ON e.ProgramID = p.ProgramID
                    GROUP BY p.ProgramTitle
                    ORDER BY SignupCount DESC
                    """;

            case 2 -> """
                    SELECT
                        p.ProgramID,
                        p.ProgramTitle
                    FROM [Program] p
                    LEFT JOIN Enrollment e
                        ON p.ProgramID = e.ProgramID
                        AND e.SignupDate >= %s
                        AND e.SignupDate < %s
                    WHERE e.ProgramID IS NULL
                    ORDER BY p.ProgramID
                    """.formatted(lastMonthStart, thisMonthStart);

            case 3 -> """
                    SELECT TOP 1
                        i.InstructorID,
                        CONCAT(i.FirstName, ' ', i.LastName) AS InstructorName,
                        COUNT(e.StudentID) AS SignupCount
                    FROM Instructor i
                    JOIN [Program] p ON i.InstructorID = p.InstructorID
                    JOIN Enrollment e ON p.ProgramID = e.ProgramID
                    WHERE e.SignupDate >= %s
                      AND e.SignupDate < %s
                    GROUP BY i.InstructorID, i.FirstName, i.LastName
                    ORDER BY SignupCount DESC
                    """.formatted(lastMonthStart, thisMonthStart);

            case 4 -> """
                    SELECT DISTINCT
                        s.StudentID,
                        CONCAT(s.FirstName, ' ', s.LastName) AS StudentName,
                        p.ProgramTitle
                    FROM Enrollment e
                    JOIN Student s ON e.StudentID = s.StudentID
                    JOIN [Program] p ON e.ProgramID = p.ProgramID
                    WHERE e.SignupDate >= %s
                      AND e.SignupDate < %s
                      AND NOT EXISTS (
                          SELECT 1
                          FROM UnitProgress up
                          JOIN [Unit] u ON up.UnitID = u.UnitID
                          WHERE up.StudentID = s.StudentID
                            AND u.ProgramID = p.ProgramID
                            AND up.UnitStatus = 'Completed'
                            AND up.CompletionDate >= %s
                            AND up.CompletionDate < %s
                      )
                    ORDER BY s.StudentID
                    """.formatted(lastMonthStart, thisMonthStart, lastMonthStart, thisMonthStart);

            case 5 -> """
                    SELECT
                        c.CategoryName,
                        p.ProgramTitle,
                        p.DifficultyLevel,
                        p.RegistrationFee
                    FROM Category c
                    JOIN [Program] p ON c.CategoryID = p.CategoryID
                    WHERE p.[Status] = 'Active'
                    ORDER BY c.CategoryName, p.ProgramTitle
                    """;

            case 6 -> """
                    SELECT
                        s.StudentID,
                        CONCAT(s.FirstName, ' ', s.LastName) AS StudentName,
                        s.Email,
                        COUNT(c.CredentialID) AS CredentialsCount
                    FROM Student s
                    LEFT JOIN Credential c ON s.StudentID = c.StudentID
                    GROUP BY s.StudentID, s.FirstName, s.LastName, s.Email
                    ORDER BY s.StudentID
                    """;

            default -> throw new IllegalArgumentException("Invalid report number");
        };
    }

    private ReportResult runQuery(String sql) throws SQLException {
        List<String> columns = new ArrayList<>();
        List<List<String>> rows = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                columns.add(metaData.getColumnLabel(i));
            }

            while (resultSet.next()) {
                List<String> row = new ArrayList<>();

                for (int i = 1; i <= columnCount; i++) {
                    Object value = resultSet.getObject(i);
                    row.add(value == null ? "" : value.toString());
                }

                rows.add(row);
            }
        }

        return new ReportResult(columns, rows);
    }
}