package carsharing.dbStatement;

import carsharing.model.Company;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class InMemoryCompanyDao implements CompanyDAO{

    Connection conn;

    public InMemoryCompanyDao(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void createCompany(Company company) throws SQLException {
        String sql = "INSERT INTO company (name) VALUES (?)";

        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, company.getName());

        preparedStatement.executeUpdate();
    }

    @Override
    public List<Company> getAll() throws SQLException {
        List<Company> companies = new ArrayList<>();
        String sql = "SELECT * FROM company";

        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            companies.add(new Company(resultSet.getInt("id"), resultSet.getString("name")));
        }

        return companies;
    }

    @Override
    public Company getById(int id) throws SQLException {
        String sql = "SELECT * FROM company WHERE id = ?";

        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setInt(1, id);

        ResultSet resultSet = preparedStatement.executeQuery();

        if(resultSet.first()) {
            return new Company(resultSet.getInt("id"), resultSet.getString("name"));
        }

        return null;
    }

    @Override
    public void update(Company company) throws SQLException {
        String sql = "UPDATE company SET name = ? WHERE id = ?";

        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, company.getName());
        preparedStatement.setInt(2, company.getId());

        preparedStatement.executeUpdate();
    }

    @Override
    public void delete(Company company) throws SQLException {
        String sql = "DELETE FROM company WHERE id = ?";

        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setInt(1, company.getId());

        preparedStatement.executeUpdate();
    }
}
