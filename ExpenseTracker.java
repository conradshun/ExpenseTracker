import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ExpenseTracker {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/expensetrackerdb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Conradqt4ever";

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public void saveBudget(String month, int budget) throws SQLException {
        String query = "INSERT INTO budgets (month, budget) VALUES (?, ?) ON DUPLICATE KEY UPDATE budget = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, month);
            pstmt.setInt(2, budget);
            pstmt.setInt(3, budget);
            pstmt.executeUpdate();
        }
    }

    public void saveExpense(String month, String category, int amount, int day) throws SQLException {
        String query = "INSERT INTO expenses (month, category, amount, day) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, month);
            pstmt.setString(2, category);
            pstmt.setInt(3, amount);
            pstmt.setInt(4, day);
            pstmt.executeUpdate();
        }
    }

    public Map<String, Integer> getBudgets() throws SQLException {
        Map<String, Integer> budgets = new HashMap<>();
        String query = "SELECT month, budget FROM budgets";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                budgets.put(rs.getString("month"), rs.getInt("budget"));
            }
        }
        return budgets;
    }

    public Map<String, Map<String, Integer>> getExpenses(String month) throws SQLException {
        Map<String, Map<String, Integer>> expenses = new HashMap<>();
        String query = "SELECT day, category, amount FROM expenses WHERE month = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, month);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int day = rs.getInt("day");
                    String category = rs.getString("category");
                    int amount = rs.getInt("amount");
                    expenses.computeIfAbsent(String.valueOf(day), k -> new HashMap<>()).put(category, amount);
                }
            }
        }
        return expenses;
    }

    public int getTotal(String month) throws SQLException {
        String query = "SELECT SUM(amount) as total FROM expenses WHERE month = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, month);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        }
        return 0;
    }

    public boolean hasExpensesForDate(String month, int day) throws SQLException {
        String query = "SELECT * FROM expenses WHERE month = ? AND day = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, month);
            pstmt.setInt(2, day);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public Map<String, Integer> getAnnualExpenses(int year) throws SQLException {
        Map<String, Integer> annualExpenses = new HashMap<>();
        String query = "SELECT category, SUM(amount) as total FROM expenses WHERE YEAR(STR_TO_DATE(month, '%Y-%m')) = ? GROUP BY category";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, year);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    annualExpenses.put(rs.getString("category"), rs.getInt("total"));
                }
            }
        }
        return annualExpenses;
    }

    public int getBudget(String month) throws SQLException {
        String query = "SELECT budget FROM budgets WHERE month = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, month);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("budget");
                }
            }
        }
        return 0; // Return 0 if no budget is set for the month
    }

    public boolean authenticateUser(String username, String password) throws SQLException {
        String query = "SELECT * FROM user_account WHERE username = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean registerUser(String username, String password) throws SQLException {
        String query = "INSERT INTO user_account (username, password) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            // This exception is thrown when the username already exists
            return false;
        }
    }
}

