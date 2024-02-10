package carsharing.dbStatement;

import carsharing.model.Car;
import carsharing.model.Company;

import java.sql.SQLException;
import java.util.List;

public interface CarDAO {
    void createCar(Car car) throws SQLException;
    List<Car> getAll() throws SQLException;
    Car getById(int id) throws SQLException;
    List<Car> getByCompany(Company company) throws SQLException;
    List<Car> getNotRented() throws SQLException;
    void update(Car car) throws SQLException;
    void delete(Car car) throws SQLException;
}
