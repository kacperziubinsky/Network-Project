import java.sql.*;
import java.util.Scanner;

public class DBHandler {

    private static final String DB_URL = "jdbc:mysql://server.hostline.pl:3306/ziubinsk_sieci";
    private static final String DB_USER = "ziubinsk_sieci";
    private static final String DB_PASSWORD = "sigmadygmat";
    private static Connection connection = null;


    private static void connectToDatabase() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                System.out.println("Połączono z bazą danych!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean createUser(String username, String password) {
        try {
            connectToDatabase();

            String checkQuery = "SELECT * FROM users WHERE username = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                System.out.println("Użytkownik o tej nazwie już istnieje.");
                rs.close();
                checkStmt.close();
                return false;
            }

            String insertQuery = "INSERT INTO users (username, pass) VALUES (?, ?)";
            PreparedStatement insertStmt = connection.prepareStatement(insertQuery);
            insertStmt.setString(1, username);
            insertStmt.setString(2, password);

            int rowsAffected = insertStmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Użytkownik został pomyślnie zarejestrowany.");
                return true;
            } else {
                System.out.println("Błąd podczas rejestracji użytkownika.");
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean loginUser(String username, String password) {
        try {
            connectToDatabase();

            String query = "SELECT * FROM users WHERE username = ? AND pass = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return true;
            } else {
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean addPost(String title, String content) {
        try {
            connectToDatabase();

            String query = "INSERT INTO posts (author, content) VALUES (?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, title);
            stmt.setString(2, content);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Post został pomyślnie dodany.");
                return true;
            } else {
                System.out.println("Błąd podczas dodawania postu.");
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void viewPosts() {
        try {
            connectToDatabase();

            String query = "SELECT * FROM postd ";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                int postID = rs.getInt("postID");
                String title = rs.getString("title");
                String content = rs.getString("content");
                String username = rs.getString("username");
                System.out.println("Post ID: " + postID);
                System.out.println("Autor: " + username);
                System.out.println("Tytuł: " + title);
                System.out.println("Treść: " + content);
                System.out.println("------------------------------");

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Połączenie z bazą danych zostało zamknięte.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Main, który może teraz używać tych metod
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nWybierz opcję:");
            System.out.println("1. Logowanie");
            System.out.println("2. Rejestracja");
            System.out.println("3. Dodaj post");
            System.out.println("4. Wyświetl posty");
            System.out.println("5. Wyjście");

            int choice = scanner.nextInt();
            scanner.nextLine();  // Konsumowanie nowej linii

            switch (choice) {
                case 1:
                    System.out.print("Podaj nazwę użytkownika: ");
                    String usernameLogin = scanner.nextLine();
                    System.out.print("Podaj hasło: ");
                    String passwordLogin = scanner.nextLine();
                    loginUser(usernameLogin, passwordLogin);
                    break;
                case 2:
                    System.out.print("Podaj nazwę użytkownika: ");
                    String usernameReg = scanner.nextLine();
                    System.out.print("Podaj hasło: ");
                    String passwordReg = scanner.nextLine();
                    createUser(usernameReg, passwordReg);
                    break;
                case 3:
                    System.out.print("Podaj tytuł postu: ");
                    String title = scanner.nextLine();
                    System.out.print("Podaj treść postu: ");
                    String content = scanner.nextLine();
                    addPost(title, content);
                    break;
                case 4:
                    viewPosts();
                    break;
                case 5:
                    closeConnection();
                    return;
                default:
                    System.out.println("Nieprawidłowy wybór. Spróbuj ponownie.");
            }
        }
    }
}
