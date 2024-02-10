package carsharing.model;

import java.util.Optional;

public class Car {
    private int id;
    private String name;
    private int companyId;
    private Optional<Company> company = Optional.empty();

    public Car(int id, String name, int company) {
        this(name, company);
        this.id = id;
    }

    public Car(String name, int company) {
        this.name = name;
        this.companyId = company;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public void setCompany(Company company) {
        this.company = Optional.ofNullable(company);
    }

    public Optional<Company> getCompany() {
        return this.company;
    }

    @Override
    public String toString() {
        return this.getId() + ". " + this.getName();
    }
}
