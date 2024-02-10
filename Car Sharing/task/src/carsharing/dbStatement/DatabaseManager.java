package carsharing.dbStatement;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class DatabaseManager {
    /**
     * Execute all create table
     *
     * @param connection
     * @throws SQLException
     */
    public static void up(Connection connection) throws SQLException {
        createCompanyTable(connection);
        createCarTable(connection);
        createCustomerTable(connection);
    }

    /**
     * Execute all delete table
     *
     * @param connection
     * @throws SQLException
     */
    public static void down(Connection connection) throws SQLException {
        deleteCustomerTable(connection);
        deleteCarTable(connection);
        deleteCompanyTable(connection);
    }

    /**
     * Create Company table
     *
     * @param connection
     * @throws SQLException
     */
    private static void createCompanyTable(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        // SQL statement to create the COMPANY table
        String sql = "CREATE TABLE IF NOT EXISTS company (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "name VARCHAR(255) UNIQUE NOT NULL" +
                ")";
        // Execute the SQL statement
        statement.executeUpdate(sql);
    }

    /**
     * Create car table
     *
     * @param connection
     * @throws SQLException
     */
    private static void createCarTable(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        // SQL statement to create the COMPANY table
        String sql = "CREATE TABLE IF NOT EXISTS car (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "name VARCHAR(255) UNIQUE NOT NULL," +
                "company_id INT NOT NULL," +
                "CONSTRAINT company_fk FOREIGN KEY (company_id) REFERENCES company(id)"+
                ")";
        // Execute the SQL statement
        statement.executeUpdate(sql);
    }

    /**
     * Create customer table
     *
     * @param connection
     * @throws SQLException
     */
    private static void createCustomerTable(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        // SQL statement to create the COMPANY table
        String sql = "CREATE TABLE IF NOT EXISTS customer (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "name VARCHAR(255) UNIQUE NOT NULL," +
                "rented_car_id INT DEFAULT NULL," +
                "CONSTRAINT rented_car_fk FOREIGN KEY (rented_car_id) REFERENCES car(id) ON DELETE SET NULL"+
                ")";
        // Execute the SQL statement
        statement.executeUpdate(sql);
    }

    /**
     * Delete company table
     *
     * @param connection
     * @throws SQLException
     */
    private static void deleteCompanyTable(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        String sql = "DROP TABLE company;";
        statement.executeUpdate(sql);
    }

    /**
     * Delete car table
     *
     * @param connection
     * @throws SQLException
     */
    private static void deleteCarTable(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        String sql = "DROP TABLE car;";
        statement.executeUpdate(sql);
    }

    /**
     * Delete customer table
     *
     * @param connection
     * @throws SQLException
     */
    private static void deleteCustomerTable(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        String sql = "DROP TABLE customer;";
        statement.executeUpdate(sql);
    }
}
