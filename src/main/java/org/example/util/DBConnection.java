package org.example.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    public static Connection getConnection() throws SQLException {

        String url = "jdbc:mysql://localhost:3306/fsd_internal";
        String username = "root";
        String password = "Arshi@786";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con = DriverManager.getConnection(url, username, password);

            System.out.println("Database Connected Successfully");

            return con;
        } catch (SQLException e) {
            System.out.println("Database connection Failed!!");
            throw e;
        } catch (ClassNotFoundException e) {
            System.out.println("jdbc driver not found");
            throw new SQLException("MYSQL jdbc driver not found.", e);
        }

    }

}
