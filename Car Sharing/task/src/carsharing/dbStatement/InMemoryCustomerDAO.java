package carsharing.dbStatement;

import carsharing.model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class InMemoryCustomerDAO implements CustomerDAO{

    Connection conn;

    public InMemoryCustomerDAO(Connection connection) {
        this.conn = connection;
    }

    @Override
    public void createCustomer(Customer customer) throws SQLException{
        String sql = "INSERT INTO customer (name) VALUES (?)";

        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, customer.getName());

        preparedStatement.executeUpdate();
    }

    @Override
    public List<Customer> getAll() throws SQLException {
        List<Customer> customers = new ArrayList<>();

        String sql = "SELECT * FROM customer";

        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            Integer rentedCar = resultSet.getInt("rented_car_id");
            if (resultSet.wasNull()) {
                rentedCar = null;
            }

            customers.add(new Customer(resultSet.getInt("id"), resultSet.getString("name"), rentedCar));
        }

        return customers;
    }

    @Override
    public Customer getById(int id) throws SQLException {
        String sql = "SELECT * FROM customer WHERE id = ?";

        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setInt(1, id);

        ResultSet resultSet = preparedStatement.executeQuery();

        if(resultSet.first()) {
            Integer rentedCar = resultSet.getInt("rented_car_id");
            if (resultSet.wasNull()) {
                rentedCar = null;
            }

            return new Customer(resultSet.getInt("id"), resultSet.getString("name"), rentedCar);
        }

        return null;
    }

    @Override
    public void update(Customer customer) throws SQLException {
        String sql = "UPDATE customer SET name = ?, rented_car_id = ? WHERE id = ?";

        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, customer.getName());

        if(customer.getRentedCarId().isEmpty()) preparedStatement.setNull(2, Types.INTEGER);
        else preparedStatement.setInt(2, customer.getRentedCarId().get());

        preparedStatement.setInt(3, customer.getId());

        preparedStatement.executeUpdate();
    }

    @Override
    public void delete(Customer customer) throws SQLException {
        String sql = "DELETE FROM customer WHERE id = ?";

        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setInt(1, customer.getId());

        preparedStatement.executeUpdate();

    }
}
