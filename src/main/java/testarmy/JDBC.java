package testarmy;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.Scanner;
public class JDBC {
    static final String DATABASE_URL = "jdbc:mysql://sql11.freesqldatabase.com/sql11517150";
    protected static  String DATABASE_USERNAME = "sql11517150";
    protected int returnGeneratedKeys = Statement.RETURN_GENERATED_KEYS;
    protected static  String DATABASE_PASSWORD = "vG3TwK9hpD";
    protected static  String SELECT_QUERY = "SELECT * FROM registration WHERE email_id = ? and password = ?";
    protected String name;


    public boolean validate(String emailId, String password){

        try (Connection connection = DriverManager
                .getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);

             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_QUERY)) {
            preparedStatement.setString(1, emailId);
            preparedStatement.setString(2, password);

            System.out.println(preparedStatement);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }


        } catch (SQLException e) {
            printSQLException(e);
        }
        return false;
    }
    public boolean loginUser(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("podaj login");
        String login = scanner.nextLine();
        System.out.println("podaj hasło");
        String password = scanner.nextLine();
        String sqlUser = "SELECT id,name,password FROM user WHERE login=? AND password=?;";
        PreparedStatement statement = connection.prepareStatement(sqlUser);
        String sqlPswd = "SELECT password FROM user WHERE login=?;";
        PreparedStatement pswd = connection.prepareStatement(sqlPswd);
        pswd.setString(1, login);
        pswd.executeUpdate();
        statement.setString(1, login);
        if (BCrypt.checkpw(password, sqlPswd)) {
            statement.setString(2, password);
//            String sqlName = "SELECT name FROM user WHERE login=?;";
//            PreparedStatement name = connection.prepareStatement(sqlName);
//            name.setString(1,login);
            statement.executeUpdate();
        } System.out.println("błędne hasło");
        return true;
    }

    public static void printSQLException(SQLException ex) {
        for (Throwable e: ex) {
            if (e instanceof SQLException) {
                e.printStackTrace(System.err);
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while (t != null) {
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
    }
    private static void createTableTask(Connection connection) throws SQLException {
        String sqlCreateTableTask = """
                    CREATE TABLE IF NOT EXISTS task(
                            id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                            title VARCHAR(255) NOT NULL,
                            description TEXT NOT NULL,
                            done BOOLEAN NOT NULL DEFAULT FALSE,
                            created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            priority ENUM ('wysoki', 'średni', 'niski') NOT NULL DEFAULT 'niski',
                            user_id INT NOT NULL,
                            FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE);
                                                 """;
        PreparedStatement preparedStatement = connection.prepareStatement(sqlCreateTableTask);
        preparedStatement.executeUpdate();
    }

    private static void createTableUser(Connection connection) throws SQLException {
        String sqlCreateTableUser = """
                    CREATE TABLE IF NOT EXISTS user(
                            id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                            name VARCHAR(128) NOT NULL,
                            surname VARCHAR(255) NOT NULL,
                            login VARCHAR(128) NOT NULL UNIQUE,
                            password CHAR(60) NOT NULL,
                            email VARCHAR(255) UNIQUE);
                                                 """;
        PreparedStatement preparedStatement = connection.prepareStatement(sqlCreateTableUser);
        preparedStatement.executeUpdate();
    }

    public static void addTask(Connection connection) throws SQLException {
        String sqlAddTask = """
                     INSERT INTO task(title,description,priority,user_id) VALUES
                    (?,?,?,?)
                        """;
        Scanner scanner = new Scanner(System.in);
        System.out.println("podaj nazwę zadania");
        String title = scanner.nextLine();
        System.out.println("podaj opis zadania");
        String description = scanner.nextLine();
        System.out.println("podaj priorytet zadania (niski, średni, lub wysoki)");
        String priority = scanner.nextLine();
        System.out.println("podaj numer użytkownika do którego należy zadanie");
        String userId = scanner.nextLine();
        PreparedStatement preparedStatement = connection.prepareStatement(sqlAddTask, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, title);
        preparedStatement.setString(2, description);
        preparedStatement.setString(3, priority);
        preparedStatement.setInt(4, Integer.parseInt(userId));
        preparedStatement.executeUpdate();
    }
    private static void updateStatusOfTask(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("podaj numer zadania");
        String id = scanner.nextLine();
        System.out.println("wybierz status (1 - zadanie wykonane/0 - zadanie nie wykonane)");
        String status = scanner.nextLine();
        String sqlUpdate = "UPDATE task SET done = ? WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdate);
        preparedStatement.setString(1,status);
        preparedStatement.setInt(2,Integer.parseInt(id));
        preparedStatement.executeUpdate();
    }
    private static void createDatabase(Connection connection) throws SQLException {
        String sqlCreateDatabase = """
                    CREATE DATABASE IF NOT EXISTS to_do_app;
                                        """;
        PreparedStatement preparedStatement = connection.prepareStatement(sqlCreateDatabase);
        preparedStatement.execute();
    }

    private static ResultSet createUser(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("podaj imię");
        String name = scanner.nextLine();
        System.out.println("podaj nazwisko");
        String surname = scanner.nextLine();
        System.out.println("podaj login");
        String login = scanner.nextLine();
        System.out.println("podaj hasło");
        String password = scanner.nextLine();
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
        System.out.println("podaj email");
        String email = scanner.nextLine();
        String sqlAddTask = """
                     INSERT INTO user(name,surname,login,password,email) VALUES
                    (?,?,?,?,?)
                        """;
        PreparedStatement preparedStatement = connection.prepareStatement(sqlAddTask, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, name);
        preparedStatement.setString(2, surname);
        preparedStatement.setString(3, login);
        preparedStatement.setString(4, password);
        preparedStatement.setString(5, email);
        preparedStatement.executeUpdate();
        String sqlSearchId = "SELECT id FROM user ORDER BY id DESC LIMIT 1;";
        PreparedStatement statement = connection.prepareStatement(sqlSearchId);
        statement.executeUpdate();
        return preparedStatement.getResultSet();

    }
    private static void deleteTask(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("podaj id zadania do skasowania");
        String id = scanner.nextLine();
        String sqlDelete = "DELETE FROM task WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlDelete);
        preparedStatement.setInt(1,Integer.parseInt(id));
        preparedStatement.executeUpdate();
    }

    private static ResultSet searchUser(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("podaj id użytkownika do wyszukania");
        String id = scanner.nextLine();
        String sqlSearch = "SELECT * FROM user WHERE id= ?";
        PreparedStatement statement = connection.prepareStatement(sqlSearch);
        statement.setInt(1, Integer.parseInt(id));
        ResultSet resultSetSearch = statement.executeQuery();
   //     System.out.println("imię, nazwisko, login, hasło, email");
        return resultSetSearch;
    }

    static ResultSet searchTask(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("podaj id zadania do wyszukania");
        String id = scanner.nextLine();
        String sqlSearch = "SELECT * FROM task WHERE id= ?";
        PreparedStatement statement = connection.prepareStatement(sqlSearch);
        statement.setInt(1, Integer.parseInt(id));
        ResultSet resultSetSearch = statement.executeQuery();
        System.out.println("nazwa, opis, wykonano, priorytet, user id");
        return resultSetSearch;
    }
    static ResultSet searchAllTasks(Connection connection) throws SQLException {
        String sqlSearch = "SELECT id, name, description FROM task";
        PreparedStatement statement = connection.prepareStatement(sqlSearch);
        ResultSet resultSetSearch = statement.executeQuery();
        System.out.println("nazwa, opis, wykonano, priorytet, user id");
        return resultSetSearch;
    }

    static ResultSet searchAllUsers(Connection connection) throws SQLException {
        String sqlSearch = "SELECT id, name, surname FROM user";
        PreparedStatement statement = connection.prepareStatement(sqlSearch);
        ResultSet resultSetSearch = statement.executeQuery();
       // System.out.println("nazwa, opis, wykonano, priorytet, user id");
        return resultSetSearch;
    }


    public static void showAllColumnsFromResultSet(ResultSet resultSet) throws SQLException {
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        for (int i = 2; i <= resultSetMetaData.getColumnCount(); i++){
            System.out.print(resultSetMetaData.getColumnLabel(i) + ", ");
        }
        System.out.println();
        while (resultSet.next()){
            for (int i = 2; i <= resultSetMetaData.getColumnCount(); i++){
                System.out.print(resultSet.getString(i) + ", ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) throws SQLException {
        Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);
        connection.setCatalog("sql11517150");
        showAllColumnsFromResultSet(searchAllUsers(connection));
        System.out.println("wybierz akcję: 1 - wyszukaj zadanie, 2 - wyszukaj użytkownika, 3 - zmień status zadania, 4 - usuń zadanie");
        Scanner scanner = new Scanner(System.in);
        String action = scanner.nextLine();
        switch (action) {
            case "1":
                showAllColumnsFromResultSet(searchTask(connection));
                break;
            case "2":
                showAllColumnsFromResultSet(searchUser(connection));
                break;
            case "3":
                updateStatusOfTask(connection);
                break;
            case "4":
                deleteTask(connection);
                break;
            default:
                System.out.println("wybierz akcję");

        //addTask(connection);


    }
}
}
