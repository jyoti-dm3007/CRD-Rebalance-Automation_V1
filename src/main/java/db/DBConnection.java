package db;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection getConnection() throws Exception {

        return DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/dev_db",
                "admin",
                "password123"
        );
    }
}