import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class represents a user account and handles database interactions
 * such as creating, authenticating, updating, and deleting user accounts.
 */
public class UserAccount {
    private int id; // Unique ID for the user
    private String username; // User's username
    private String password; // User's password

    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/expense_tracker";
    private static final String DB_USER = "root"; // Replace with your database username
    private static final String DB_PASSWORD = "your_password"; // Replace with your database password

    /**
     * Constructor to create a UserAccount object.
     *
     * @param username the username of the user.
     * @param password the password of the user.
     */
    public UserAccount(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    public UserAccount() {
    	this.username = null;
    	this.password = null;
    }

    /**
     * Establishes a connection to the database.
     *
     * @return A Connection object to interact with the database.
     * @throws SQLException If a database access error occurs.
     */
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * Saves a new user to the database.
     *
     * @return true if the user was saved successfully, false otherwise.
     */
    public boolean save(String username, String password) {
        String query = "INSERT INTO UserAccount (username, password) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Authenticates a user by verifying username and password in the database.
     *
     * @param username the username of the user.
     * @param password the password of the user.
     * @return true if authentication is successful, false otherwise.
     */
    public static boolean authenticate(String username, String password) {
        String query = "SELECT * FROM UserAccount WHERE username = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Returns true if a matching record is found
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates the password of the current user in the database.
     *
     * @param newPassword the new password to set.
     * @return true if the password was updated successfully, false otherwise.
     */
    public boolean updatePassword(String newPassword) {
        String query = "UPDATE UserAccount SET password = ? WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newPassword);
            stmt.setString(2, username);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                this.password = newPassword;
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Deletes a user account from the database.
     *
     * @param username the username of the account to delete.
     * @return true if the account was deleted successfully, false otherwise.
     */
    public static boolean deleteAccount(String username) {
        String query = "DELETE FROM UserAccount WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks if a username already exists in the database.
     *
     * @param username the username to check.
     * @return true if the username exists, false otherwise.
     */
    public static boolean usernameExists(String username) {
        String query = "SELECT * FROM UserAccount WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
