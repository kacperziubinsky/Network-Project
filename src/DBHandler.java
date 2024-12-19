import java.sql.*;
import java.util.*;

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
        finally {
            closeConnection();
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
        finally {
            closeConnection();
        }
    }

    public static boolean addPost(String autor, String content) {
        try {
            connectToDatabase();

            String query = "INSERT INTO posts (username, content) VALUES (?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, autor);
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
        finally {
            closeConnection();
        }
    }

    public static List<String[]> getPosts() {
        try {
            connectToDatabase();

            String query = "SELECT * FROM posts ";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            List<String[]> posts = new ArrayList<>();
            while (rs.next()) {
                String content = rs.getString("content");
                String username = rs.getString("username");
                int postid = rs.getInt("postID");
                posts.add(new String[]{String.valueOf(postid), username, content});
            }
            System.out.println(posts);
            return posts;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            closeConnection();
        }
        return null;
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

}
