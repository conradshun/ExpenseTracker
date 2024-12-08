import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ExpenseTracker {
    private Connection connection;

    // Constructor to establish a connection to the database
    public ExpenseTracker(String dbUrl) {
        try {
            connection = DriverManager.getConnection(dbUrl);
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
        }
    }

    // Method to load data from the database if needed (currently not implemented)
    public void loadData() {
        // This method would load data from the database if needed
    }

    // Method to retrieve the budget for a specific month
    public int getBudget(String month) throws SQLException {
        String query = "SELECT budget FROM budgets WHERE month = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, month); // Set the month parameter
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt("budget") : 0; // Return the budget or 0 if not found
        }
    }

    // Method to retrieve the limit for a specific month
    public int getLimit(String month) throws SQLException {
        String query = "SELECT limit FROM budgets WHERE month = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, month); // Set the month parameter
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt("limit") : 0; // Return the limit or 0 if not found
        }
    }

    // Method to retrieve all expenses for a specific month
    public Map<String, Integer> getExpenses(String month) throws SQLException {
        Map<String, Integer> monthExpenses = new HashMap<>();
        String query = "SELECT category, amount FROM expenses WHERE month = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, month); // Set the month parameter
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                // Populate the monthExpenses map with category and amount
                monthExpenses.put(rs.getString("category"), rs.getInt("amount"));
            }
        }
        return monthExpenses; // Return the map of expenses
    }

    // Method to retrieve the total expenses for a specific month
    public int getTotal(String month) throws SQLException {
        String query = "SELECT total FROM budgets WHERE month = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, month); // Set the month parameter
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt("total") : 0; // Return the total or 0 if not found
        }
    }

    // Method to update the budget for a specific month
    public void setBudget(String month, int budget) throws SQLException {
        String query = "UPDATE budgets SET budget = ? WHERE month = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, budget); // Set the budget parameter
            stmt.setString(2, month); // Set the month parameter
            stmt.executeUpdate(); // Execute the update
        }
    }

    // Method to update the limit for a specific month
    public void setLimit(String month, int limit) throws SQLException {
        String query = "UPDATE budgets SET limit = ? WHERE month = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, limit); // Set the limit parameter
            stmt.setString(2, month); // Set the month parameter
            stmt.executeUpdate(); // Execute the update
        }
    }

    // Method to add an expense for a specific month and category
    public void addExpense(String month, String category, int amount) throws SQLException {
        String query = "INSERT INTO expenses (month, category, amount) VALUES (?, ?, ?) " +
                       "ON DUPLICATE KEY UPDATE amount = amount + ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, month); // Set the month parameter
            stmt.setString(2, category); // Set the category parameter
            stmt.setInt(3, amount); // Set the amount parameter
            stmt.setInt(4, amount); // Set the amount parameter for updating
            stmt.executeUpdate(); // Execute the insert or update
        }
    }
    
    // Method to close the database connection
    public void close() {
        try {
            if (connection != null) {
                connection.close(); // Close the connection if it's not null
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: ");
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}
