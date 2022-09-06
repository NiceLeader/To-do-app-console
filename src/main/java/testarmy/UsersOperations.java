package testarmy;

import java.sql.*;
import java.util.Scanner;

public class UsersOperations {
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
    public static void main(String[] args) {

    }
}
