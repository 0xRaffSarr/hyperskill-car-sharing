package carsharing.dbStatement;

import carsharing.model.Car;
import carsharing.model.Company;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InMemoryCarDao implements CarDAO{
    Connection conn;

    public InMemoryCarDao(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void createCar(Car car) throws SQLException {
        String sql = "INSERT INTO car (name, company_id) VALUES (?, ?)";

        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, car.getName());
        preparedStatement.setInt(2, car.getCompanyId());

        preparedStatement.executeUpdate();
    }

    @Override
    public List<Car> getAll() throws SQLException {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT car.id, car.name, car.company_id, company.name AS companyName FROM car " +
                "INNER JOIN company ON car.company_id = company.id";

        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            Car car = new Car(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getInt("company_id"));
            car.setCompany(new Company(car.getCompanyId(), resultSet.getString("companyName")));

            cars.add(car);
        }

        return cars;
    }

    @Override
    public Car getById(int id) throws SQLException {
        String sql = "SELECT car.id, car.name, car.company_id, company.name AS companyName FROM car " +
                "INNER JOIN company ON car.company_id = company.id WHERE car.id = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setInt(1, id);

        ResultSet resultSet = preparedStatement.executeQuery();

        if(resultSet.first()) {
            Car car = new Car(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getInt("company_id"));
            car.setCompany(new Company(car.getCompanyId(), resultSet.getString("companyName")));

            return car;
        }

        return null;
    }

    @Override
    public List<Car> getByCompany(Company company) throws SQLException {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT * FROM car WHERE company_id = ?";

        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setInt(1, company.getId());
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            Car car = new Car(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getInt("company_id"));
            car.setCompany(company);

            cars.add(car);
        }

        return cars;
    }

    @Override
    public List<Car> getNotRented() throws SQLException {
        List<Car> cars = new ArrayList<>();

        String sql = "SELECT car.*, company.name AS companyName" +
                "FROM car" +
                "LEFT JOIN customer ON car.car_id = customer.rented_car_id" +
                "LEFT JOIN company ON car.company_id = company.id" +
                "WHERE customer.rented_car_id IS NULL;";

        PreparedStatement preparedStatement = this.conn.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            Car car = new Car(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getInt("company_id"));
            car.setCompany(new Company(resultSet.getInt("company_id"), resultSet.getString("companyName")));

            cars.add(car);
        }

        return cars;
    }

    public List<Car> getNotRentedByCompany(Company company) throws SQLException {
        List<Car> cars = new ArrayList<>();

        String sql = "SELECT car.*, company.name AS companyName " +
                "FROM car " +
                "LEFT JOIN customer ON car.id = customer.rented_car_id " +
                "LEFT JOIN company ON car.company_id = company.id " +
                "WHERE customer.rented_car_id IS NULL AND company.id = ?;";

        PreparedStatement preparedStatement = this.conn.prepareStatement(sql);
        preparedStatement.setInt(1, company.getId());
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            Car car = new Car(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getInt("company_id"));
            car.setCompany(new Company(resultSet.getInt("company_id"), resultSet.getString("companyName")));

            cars.add(car);
        }

        return cars;
    }

    @Override
    public void update(Car car) throws SQLException {
        String sql = "UPDATE car SET name = ?, company_id = ? WHERE id = ?";

        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, car.getName());
        preparedStatement.setInt(2, car.getCompanyId());
        preparedStatement.setInt(3, car.getId());

        preparedStatement.executeUpdate();
    }

    @Override
    public void delete(Car car) throws SQLException {
        String sql = "DELETE FROM car WHERE id = ?";

        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setInt(1, car.getId());

        preparedStatement.executeUpdate();
    }
}
