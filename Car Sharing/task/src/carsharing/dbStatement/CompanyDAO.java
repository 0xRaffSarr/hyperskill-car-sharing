package carsharing.dbStatement;

import carsharing.model.Company;

import java.sql.SQLException;
import java.util.List;

public interface CompanyDAO {
    void createCompany(Company company) throws SQLException;
    List<Company> getAll() throws SQLException;
    Company getById(int id) throws SQLException;
    void update(Company company) throws SQLException;
    void delete(Company company) throws SQLException;
}
