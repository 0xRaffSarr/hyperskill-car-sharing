package carsharing;

import carsharing.dbStatement.DatabaseManager;
import carsharing.dbStatement.InMemoryCarDao;
import carsharing.dbStatement.InMemoryCompanyDao;

import java.util.Scanner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL = "jdbc:h2:./src/carsharing/db/";

    static final String DB_USER = "";
    static final String DB_PASSWORD = "";

    public static void main(String[] args) {
        // write your code here
        Connection conn;

        try {
            // db name from args
            String databaseFileName = extractDatabaseFileName(args);

            InMemoryCompanyDao companyDataSource;
            InMemoryCarDao carDataSource;

            try {
                conn = connectDB(databaseFileName, true);
                DatabaseManager.up(conn);

                Scanner scanner = new Scanner(System.in);

                App app = App.getApp(conn, scanner);
                Executable executable = app::mainMenu;
                while (executable != null) {
                    executable = executable.execute();
                }

                scanner.close();
                //DatabaseManager.down(conn);
                conn.close();

            }
            catch (SQLException e) {
                System.out.println("Cannot connect to db: " + e.getMessage());
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Extract the DB name from the arguments
     * If no db name is provided, the default is returned
     *
     * @param args
     * @return
     */
    private static String extractDatabaseFileName(String[] args) throws Exception {
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equals("-databaseFileName")) {
                return args[i + 1];
            }
        }
        // Default database file name if not provided in arguments
        return "carsharing.mv.db";
    }

    /**
     * Creates a new connection to the DB and returns it
     *
     * @param dbName the DB name
     * @param autoCommit autocommit status - true or false
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    private static Connection connectDB(String dbName, boolean autoCommit) throws SQLException {
        String dbUrl = DB_URL + dbName;

        Connection connection = DriverManager.getConnection(dbUrl);

        // Enable auto-commit mode
        connection.setAutoCommit(autoCommit);

        return connection;
    }

}