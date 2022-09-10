package testarmy;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.Scanner;

import static java.lang.System.exit;

public class JDBC {
    static final String DATABASE_URL = "jdbc:mysql://sql11.freesqldatabase.com/sql11517150";
    protected static String DATABASE_USERNAME = "sql11517150";
    protected int returnGeneratedKeys = Statement.RETURN_GENERATED_KEYS;
    protected static String DATABASE_PASSWORD = "vG3TwK9hpD";
    protected static String SELECT_QUERY = "SELECT * FROM registration WHERE email_id = ? and password = ?";
    protected String name;
    protected static String salt = BCrypt.gensalt();


    public boolean validate(String emailId, String password) {

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

    public static boolean loginUser(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Podaj login");
        String login = scanner.nextLine();
        System.out.println("Podaj hasło");
        String password = scanner.nextLine();
        String sqlUser = "SELECT id,name,password FROM user WHERE login=? AND password=?;";
        PreparedStatement statement = connection.prepareStatement(sqlUser);
        String sqlPswd = "SELECT password FROM user WHERE login=?;";
        PreparedStatement pass = connection.prepareStatement(sqlPswd);
        pass.setString(1, login);
        pass.execute();
        statement.setString(1, login);
      //  getUserId(connection,login);
        //        if (BCrypt.checkpw(password, sqlPswd)) {
//            statement.setString(2, password);
//           String sqlName = "SELECT name FROM user WHERE login=?;";
//           PreparedStatement name = connection.prepareStatement(sqlName);
//           name.setString(1,login);
//            statement.executeUpdate();
//        }else {
//        System.out.println("błędne hasło");}
        boolean i = pass.execute();
        if (i == true) {
            System.out.println("Zalogowałeś się!");
        } else {
            System.out.println("Nie zalogowałeś się!");
        }
        return true;
    }

    public static void printSQLException(SQLException ex) {
        for (Throwable e : ex) {
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
                        priority ENUM ('low', 'medium', 'high') NOT NULL DEFAULT 'niski',
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
        System.out.println("Podaj nazwę zadania");
        String title = scanner.nextLine();
        System.out.println("Podaj opis zadania");
        String description = scanner.nextLine();
        System.out.println("Podaj priorytet zadania (low, medium, lub high)");
        String priority = scanner.nextLine();
        System.out.println("Podaj numer użytkownika do którego należy zadanie");
        String userId = scanner.nextLine();
        PreparedStatement preparedStatement = connection.prepareStatement(sqlAddTask, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, title);
        preparedStatement.setString(2, description);
        preparedStatement.setString(3, priority);
        preparedStatement.setInt(4, Integer.parseInt(userId));
        int i = preparedStatement.executeUpdate();
        if (i > 0) {
            System.out.println("Dodano zadanie");
        } else {
            System.out.println("Błąd! Nie dodano zadania!");
        }
    }

    private static void updateStatusOfTask(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Podaj numer zadania");
        String id = scanner.nextLine();
        System.out.println("Wybierz status (1 - zadanie wykonane/0 - zadanie nie wykonane)");
        String status = scanner.nextLine();
        String sqlUpdate = "UPDATE task SET done = ? WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdate);
        preparedStatement.setString(1, status);
        preparedStatement.setInt(2, Integer.parseInt(id));
        int i = preparedStatement.executeUpdate();
        if (i > 0) {
            System.out.println("Zaktualizowano status zadania");
        } else {
            System.out.println("Błąd! Nie zaktualizowano statusu zadania!");
        }
    }
    //int/string
//if(resultSearch!=null && resultSetSearch.next()){int/string = resultSetSearch.getString(column name)}
    private static void createDatabase(Connection connection) throws SQLException {
        String sqlCreateDatabase = """
                CREATE DATABASE IF NOT EXISTS to_do_app;
                                    """;
        PreparedStatement preparedStatement = connection.prepareStatement(sqlCreateDatabase);
        preparedStatement.execute();
    }

    private static ResultSet createUser(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Podaj imię");
        String name = scanner.nextLine();
        System.out.println("Podaj nazwisko");
        String surname = scanner.nextLine();
        System.out.println("Podaj login");
        String login = scanner.nextLine();
        System.out.println("Podaj hasło");
        String password = scanner.nextLine();
//        String hashed = BCrypt.hashpw(password, salt);
        System.out.println("Podaj email");
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
        //     statement.execute();
        boolean i = statement.execute();
        if (i == true) {
            System.out.println("Dodano użytkownika");
        } else {
            System.out.println("Błąd! Nie dodano użytkownika!");
        }
        return preparedStatement.getResultSet();

    }

    private static void deleteTask(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Podaj id zadania do skasowania");
        String id = scanner.nextLine();
        String sqlDelete = "DELETE FROM task WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlDelete);
        preparedStatement.setInt(1, Integer.parseInt(id));
        int i = preparedStatement.executeUpdate();
        if (i > 0) {
            System.out.println("Usunięto zadanie");
        } else {
            System.out.println("Błąd! Nie usunięto zadania!");
        }
    }
    private static void deleteUser(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("podaj id użytkownika do skasowania");
        String id = scanner.nextLine();
        String sqlDelete = "DELETE FROM user WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlDelete);
        preparedStatement.setInt(1, Integer.parseInt(id));
        int i = preparedStatement.executeUpdate();
        if (i > 0) {
            System.out.println("usunięto użytkownika");
        } else {
            System.out.println("Błąd! Nie usunięto użytkownika!");
        }
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
    private static ResultSet getUserId(Connection connection, String login) throws SQLException {
        String sqlUserId = "SELECT id FROM user WHERE login=?;";
        PreparedStatement statementUserId = connection.prepareStatement(sqlUserId);
        statementUserId.setString(1, login);
        int idOfUser = Integer.parseInt(sqlUserId);
        System.out.println("Numer użytkownika: "+idOfUser);
        ResultSet resultSetSearch = statementUserId.executeQuery();
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
        for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
            System.out.print(resultSetMetaData.getColumnLabel(i) + ", ");
        }
        System.out.println();
        while (resultSet.next()) {
            for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
                System.out.print(resultSet.getString(i) + ", ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) throws SQLException {
        Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);
        connection.setCatalog("sql11517150");
        System.out.println("wybierz akcję:\n 1 - logowanie,\n 2 - dodaj użytkownika,\n 3 - zamknij program");
        Scanner scannerLogin = new Scanner(System.in);
        String login = scannerLogin.nextLine();
        if (login.equals("1") || login.equals("2") || login.equals("3")) {
            switch (login) {
                case "1":
                    System.out.println("logowanie");
                    loginUser(connection);
                    break;
                case "2":
                    createUser(connection);
                    break;
                case "3":
                    exit(1);
                    break;
                default:
                    System.out.println("wybierz akcję: 1 - logowanie \n, 2 - dodaj użytkownika \n, 3 - zamknij program");
            }
        } else {
            System.out.println("błędna wartość");
        }
//showAllColumnsFromResultSet(searchAllUsers(connection));
        do {
            System.out.println("Wybierz akcję: \n 1 - Wyszukaj zadanie,\n 2 - Wyszukaj użytkownika,\n 3 - Zmień status zadania,\n 4 - Usuń zadanie,\n 5 - Dodaj zadanie,\n 6 - Zamknij program");
            Scanner scannerAction = new Scanner(System.in);
            String action = scannerAction.nextLine();
            if (action.equals("1") || action.equals("2") || action.equals("3") || action.equals("4") || action.equals("5")|| action.equals("6")|| action.equals("7")) {
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
                    case "5":
                        addTask(connection);
                        break;
                    case "6":
                        deleteUser(connection);
                        break;
                    case "7":
                        exit(1);
                        break;
                    default:
                        System.out.println("Wybierz akcję:\n 1 - Wyszukaj zadanie,\n 2 - Wyszukaj użytkownika,\n 3 - Zmień status zadania,\n 4 - Usuń zadanie,\n 5 - Dodaj zadanie,\n 6 - Zamknij program");
                }


            } else {
                System.out.println("błędna wartość");
            }
        } while (true);
    }
}