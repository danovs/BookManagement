import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            try (Connection conn = getConnection(); Scanner scanner = new Scanner(System.in)) {

                while (true) {
                    System.out.println("\n\nМеню: ");
                    System.out.println("1. Добавить книгу");
                    System.out.println("2. Показать все книги");
                    System.out.println("3. Обновить книгу");
                    System.out.println("4. Удалить книгу");
                    System.out.println("5. Выход");
                    System.out.print("\nВыберите опцию: ");

                    String choice = scanner.nextLine();

                    switch (choice) {
                        case "1":
                            addBook(conn, scanner);
                            break;
                        case "2":
                            showAllBooks(conn);
                            break;
                        case "3":
                            updateBook(conn, scanner);
                            break;
                        case "4":
                            deleteBook(conn, scanner);
                            break;
                        case "5":
                            System.out.println("Завершение программы");
                            return;
                        default:
                            System.out.println("Неправильный выбор!");
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("Соединение преравно!");

            System.out.println(ex);
        }
    }

    public static Connection getConnection() throws SQLException, IOException {
        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(Paths.get("database.properties"))) {
            props.load(in);
        }
        String url = props.getProperty("url");
        String username = props.getProperty("username");
        String password = props.getProperty("password");

        return DriverManager.getConnection(url, username, password);
    }

    public static void addBook(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Введите название книги: ");
        String title = scanner.nextLine();
        System.out.print("Введите автора книги: ");
        String author = scanner.nextLine();
        System.out.print("Введите год книги: ");
        int year = scanner.nextInt();

        String query = "INSERT INTO bookcatalog(title, author, year) VALUES (?, ?, ?)";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, title);
            statement.setString(2, author);
            statement.setInt(3, year);
            statement.executeUpdate();
            System.out.println("Книга добавлено успешно!");
        } catch (SQLException e) {
            System.out.println("Ошибка при добавлении книги: " + e.getMessage());
        }
    }

    public static void showAllBooks(Connection conn) throws SQLException{
        String query = "SELECT * FROM bookcatalog";
        try (Statement statement = conn.createStatement()) {
            ResultSet result = statement.executeQuery(query);
            while (result.next()) {
                System.out.printf("ID: %d, Название: %s, Автор: %s, Год: %d",
                        result.getInt("ID"),
                        result.getString("Title"),
                        result.getString("Author"),
                        result.getInt("Year"));
            }
        }
    }

    public static void deleteBook(Connection conn, Scanner scanner) throws SQLException{
        System.out.print("Введите ID книги, которую хотите удалить: ");
        int id = scanner.nextInt();
        String query = "DELETE FROM bookcatalog WHERE ID = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, id);
            int rowDeleted = statement.executeUpdate();
            if (rowDeleted > 0) {
                System.out.println("Книга удалена успешно.");
            } else {
                System.out.println("Книга с таким ID не найдна");
            }
        }
    }

    public static void updateBook(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Введите ID книги, которую хотите изменить: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Введите название книги: ");
        String title = scanner.nextLine();

        System.out.print("Введите автора книги: ");
        String author = scanner.nextLine();

        System.out.print("Введите год книги: ");
        int year = scanner.nextInt();

        String query = "UPDATE bookcatalog SET Title = ?, Author = ?, Year = ? WHERE ID = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, title);
            statement.setString(2, author);
            statement.setInt(3, year);
            statement.setInt(4, id);

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Книга обновлена успешно");
            } else {
                System.out.println("Книга с таким ID не найдена");
            }
        }
    }
}
