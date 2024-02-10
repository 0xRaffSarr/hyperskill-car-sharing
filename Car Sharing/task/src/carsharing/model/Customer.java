package carsharing.model;

import java.util.Optional;

public class Customer {
    private int id;
    private String name;
    private Optional<Integer> rentedCarId = Optional.empty();

    private Optional<Car> rentedCar = Optional.empty();

    public Customer(String name) {
        this.name = name;
    }

    public Customer(int id, String name, Integer rentedCarId) {
        this(name);

        this.id = id;
        this.rentedCarId = Optional.ofNullable(rentedCarId);
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

    public Optional<Integer> getRentedCarId() {
        return rentedCarId;
    }

    public void setRentedCarId(Integer rentedCarId) {
        this.rentedCarId = Optional.ofNullable(rentedCarId);
    }

    public void setRentedCar(Car car) {
        this.rentedCar = Optional.ofNullable(car);
    }

    public Optional<Car> getRentedCar() {
        return this.rentedCar;
    }
}
