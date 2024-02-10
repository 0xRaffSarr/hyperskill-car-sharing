package carsharing;

import carsharing.dbStatement.InMemoryCarDao;
import carsharing.dbStatement.InMemoryCompanyDao;
import carsharing.dbStatement.InMemoryCustomerDAO;
import carsharing.model.Action;
import carsharing.model.Car;
import carsharing.model.Company;
import carsharing.model.Customer;

import java.sql.*;
import java.util.*;

public class App {
    private final Connection connection;
    private final Scanner scanner;

    private static App appInstance = null;

    private InMemoryCompanyDao companyDataSource;
    private InMemoryCarDao carDataSource;

    private InMemoryCustomerDAO customerDataSource;

    /**
     * Return executable Main Menu
     *
     * @return
     */
    public Executable mainMenu() {
        Map<Integer, Action> menu = new LinkedHashMap<>();
        menu.put(1, new Action("Log in as a manager", this::managerMenu));
        menu.put(2, new Action("Log in as a customer", this::customerList));
        menu.put(3, new Action("Create a customer", this::createCustomer));
        menu.put(0, new Action("Exit", null));

        return printMenu(menu);
    }

    /**
     * Return executable manager menu
     * If return back is performed, return to main menu.
     *
     * @return
     */
    public Executable managerMenu() {
        Map<Integer, Action> menu = new LinkedHashMap<>();
        menu.put(1, new Action("Company list", this::companyList));
        menu.put(2, new Action("Create a company", this::createCompany));
        menu.put(0, new Action("Back", this::mainMenu));
        return printMenu(menu);
    }

    /**
     * Return company menu, for login as manager
     * On back return to manager menu
     *
     * @param company
     * @return
     */
    public Executable companyMenu(Company company) {
        Map<Integer, Action> menu = new LinkedHashMap<>();

        System.out.println("'" + company.getName() + "' company");
        menu.put(1, new Action("Car list", () -> this.carList(company)));
        menu.put(2, new Action("Create a car", () -> this.createCar(company)));
        menu.put(0, new Action("Back", this::managerMenu));
        return printMenu(menu);
    }

    /**
     * Return customer menu, on back return to main menu
     *
     * @param customer the user who performs the actions
     * @return
     */
    public Executable customerMenu(Customer customer) {
        Map<Integer, Action> menu = new LinkedHashMap<>();

        menu.put(1, new Action("Rent a car", () -> this.companyList(customer)));
        menu.put(2, new Action("Return a rented car", () -> this.returnARentedCar(customer)));
        menu.put(3, new Action("My rented car", () -> this.myRentedCar(customer)));
        menu.put(0, new Action("Back", this::mainMenu));

        return printMenu(menu);
    }

    /**
     * Return company list.
     * The list of companies may be empty
     *
     * @return
     */
    public Executable companyList() {
        Map<Integer, Action> menu = new LinkedHashMap<>();

        try {
            List<Company> companies = companyDataSource.getAll();

            System.out.println("Choose a company: ");

            if(companies.isEmpty()) {
                System.out.println("The company list is empty!");
                return () -> this::managerMenu;
            }
            else {
                int index = 1;
                for(Company company: companies)
                    menu.put(index++, new Action(company.getName(), () -> this.companyMenu(company)));

                menu.put(0, new Action("Back", () -> this::managerMenu));
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return printMenu(menu);
    }

    /**
     * Return company list for selection of car.
     * The list of companies may be empty
     *
     * @param customer the customer who intends to rent a car
     * @return
     */
    public Executable companyList(Customer customer) {
        Map<Integer, Action> menu = new LinkedHashMap<>();

        if(customer.getRentedCarId().isPresent()) {
            System.out.println("You've already rented a car!");
            return () -> this.customerMenu(customer);
        }

        try {
            List<Company> companies = companyDataSource.getAll();

            System.out.println("Choose a company: ");

            if(companies.isEmpty()) {
                System.out.println("The company list is empty!");
                return () -> this::managerMenu;
            }
            else {
                int index = 1;
                for(Company company: companies)
                    menu.put(index++, new Action(company.getName(), () -> this.carList(company, customer)));

                menu.put(0, new Action("Back", () -> this.customerMenu(customer)));
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return printMenu(menu);
    }

    /**
     * Create a new company, then return to manager menu
     *
     * @return
     */
    public Executable createCompany() {
        System.out.println("Enter the company name: ");
        System.out.print("> ");

        String companyName = this.scanner.nextLine();

        try {
            this.companyDataSource.createCompany(new Company(companyName));
            System.out.println("The company was created!");
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }


        return () -> this::managerMenu;
    }

    /**
     * Print a car list, then return to company menu
     *
     * @param company the company that owns the cars to be displayed
     * @return
     */
    public Executable carList(Company company) {
        try {
            List<Car> cars = carDataSource.getByCompany(company);

            if(cars.isEmpty()) System.out.println("The car list is empty!");
            else {
                int index = 1;
                for(Car car: cars) System.out.println((index++) + ". " + car.getName());
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return () -> companyMenu(company);
    }

    /**
     * Allows the selection of a car for rental
     *
     * @param company the company that owns the cars to be displayed
     * @param customer the customer who intends to rent a car
     * @return
     */
    public Executable carList(Company company, Customer customer) {
        Map<Integer, Action> menu = new LinkedHashMap<>();

        if(customer.getRentedCarId().isPresent()) {
            System.out.println("You've already rented a car!");
            return () -> this.customerMenu(customer);
        }
        else {
            try {
                List<Car> cars = this.carDataSource.getNotRentedByCompany(company);

                if(cars.isEmpty()) {
                    System.out.println("No available cars in the '" + company.getName() + "' company");
                }
                else {
                    System.out.println("Choose a car:");

                    int index = 1;
                    for(Car car: cars)
                        menu.put(index++, new Action(car.getName(), () -> this.selectCarToRent(customer, car)));
                    menu.put(0, new Action("Back", () -> this.companyList(customer)));
                }
            }
            catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

        return printMenu(menu);
    }

    /**
     * Create a new car associated at one company
     *
     * @param company the company that owns the car
     * @return
     */
    public Executable createCar(Company company) {
        System.out.println("Enter the car name:");
        System.out.print("> ");

        String carName = this.scanner.nextLine();

        try {
            this.carDataSource.createCar(new Car(carName, company.getId()));
            System.out.println("The car was added!");
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return () -> companyMenu(company);
    }

    /**
     * Return customer list, to log in
     *
     * @return
     */
    public Executable customerList() {
        Map<Integer, Action> menu = new LinkedHashMap<>();

        try {
            List<Customer> customers = customerDataSource.getAll();

            System.out.println("Customer list:");

            if(customers.isEmpty()) {
                System.out.println("The customer list is empty!");
                return () -> this::mainMenu;
            }
            else {
                int index = 1;
                for (Customer customer: customers)
                    menu.put(index++, new Action(customer.getName(), () -> this.customerMenu(customer)));

                menu.put(0, new Action("Back", this::mainMenu));
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return printMenu(menu);
    }

    /**
     * Create a new customer, then return to main menu
     *
     * @return
     */
    public Executable createCustomer() {
        System.out.println("Enter the customer name:");
        System.out.print("> ");

        String customerName = this.scanner.nextLine();

        try {
            this.customerDataSource.createCustomer(new Customer(customerName));
            System.out.println("The customer was added!");
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return this::mainMenu;
    }

    /**
     * Return a rented car.
     * If the customer has not rented a car, print a notice
     *
     * @param customer The customer who intends to return a car
     * @return
     */
    public Executable returnARentedCar(Customer customer) {
        if(customer.getRentedCarId().isEmpty())
            System.out.println("You didn't rent a car!");
        else {
            try {
                customer.setRentedCarId(null);
                customer.setRentedCar(null);
                this.customerDataSource.update(customer);

                System.out.println("You've returned a rented car!");

            }
            catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

        return () -> this.customerMenu(customer);
    }

    /**
     * Selects a car to rent and returns the controller to the customer menu
     *
     * @param customer the customer who intends to rent a car
     * @param car the car you want to rent
     * @return
     */
    public Executable selectCarToRent(Customer customer, Car car) {
        customer.setRentedCarId(car.getId());
        customer.setRentedCar(car);

        try {
            this.customerDataSource.update(customer);
            System.out.println("You rented '" + car.getName() + "'");
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return () -> this.customerMenu(customer);
    }

    /**
     * Prints information about the rented car
     *
     * @param customer the customer who rented the car
     * @return
     */
    public Executable myRentedCar(Customer customer) {
        if(customer.getRentedCarId().isEmpty())
            System.out.println("You didn't rent a car!");
        else {
            try {
                Car car = this.carDataSource.getById(customer.getRentedCarId().get());

                System.out.println("Your rented car:");
                System.out.println(car.getName());
                System.out.println("Company");
                System.out.println(car.getCompany().get().getName());
            }
            catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

        return () -> this.customerMenu(customer);
    }

    /**
     * Prints the menu based on the operations chosen.
     *
     * @param menu the menu to print
     * @return returns a callback or null
     */
    public Executable printMenu(Map<Integer, Action> menu) {
        menu.entrySet().stream()
                .map(item-> item.getKey() + ". " + item.getValue().getLabel() )
                .forEach(System.out::println);
        System.out.print("> ");

        int key = this.scanner.nextInt();
        this.scanner.nextLine();

        if (menu.containsKey(key)) {
            return menu.get(key).getCallback();
        }

        return null;
    }

    /**
     * Private constructor for singleton pattern
     *
     * @param connection
     */
    private App(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;

        this.companyDataSource = new InMemoryCompanyDao(this.connection);
        this.carDataSource = new InMemoryCarDao(this.connection);
        this.customerDataSource = new InMemoryCustomerDAO(this.connection);
    }

    /**
     * Return an existing App instance if exists, else create a new instance
     *
     * @param conn
     * @return
     */
    public static App getApp(Connection conn, Scanner scanner) {
        if(appInstance == null) appInstance = new App(conn, scanner);

        return appInstance;
    }

}
