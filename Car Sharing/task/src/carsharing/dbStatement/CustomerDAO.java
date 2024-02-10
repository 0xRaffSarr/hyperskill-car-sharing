package carsharing.dbStatement;

import carsharing.model.Customer;

import java.sql.SQLException;
import java.util.List;

public interface CustomerDAO {
    void createCustomer(Customer customer) throws SQLException;
    List<Customer> getAll() throws SQLException;
    Customer getById(int id) throws SQLException;
    void update(Customer customer) throws SQLException;
    void delete(Customer customer) throws SQLException;
}
