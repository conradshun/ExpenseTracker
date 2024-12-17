import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ExpenseTracker {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/expensetrackerdb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Conradqt4ever";
    private int currentUserId;

    public ExpenseTracker() {
        this.currentUserId = -1;
    }

    public ExpenseTracker(int userId) {
        this.currentUserId = userId;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public boolean authenticateUser(String username, String password) throws SQLException {
        String query = "SELECT id, password FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                if (storedPassword.equals(password)) {
                    this.currentUserId = rs.getInt("id");
                    return true;
                }
            }
            return false;
        }
    }

    public int getUserId(String username) throws SQLException {
        String query = "SELECT id FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
            throw new SQLException("User not found");
        }
    }

    public boolean registerUser(String username, String password) throws SQLException {
        String query = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            return false; // Username already exists
        }
    }

    public void saveBudget(String month, int amount, int limit) throws SQLException {
        String query = "INSERT INTO budgets (month, budget, budget_limit) VALUES (?, ?, ?) " +
                       "ON DUPLICATE KEY UPDATE budget = ?, budget_limit = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, month);
            stmt.setInt(2, amount);
            stmt.setInt(3, limit);
            stmt.setInt(4, amount);
            stmt.setInt(5, limit);
            stmt.executeUpdate();
        }
    }

    public Map<String, Integer> getBudget(String month) throws SQLException {
        String query = "SELECT budget, budget_limit FROM budgets WHERE month = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, month);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Map<String, Integer> budget = new HashMap<>();
                budget.put("amount", rs.getInt("budget"));
                budget.put("limit", rs.getInt("budget_limit"));
                return budget;
            }
            return null;
        }
    }

    public void saveExpense(String month, String category, int amount, int day) throws SQLException {
        String query = "INSERT INTO expenses (month, category, amount, day) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, month);
            stmt.setString(2, category);
            stmt.setInt(3, amount);
            stmt.setInt(4, day);
            stmt.executeUpdate();
        }
    }

    public Map<String, Map<String, Integer>> getExpenses(String month) throws SQLException {
        Map<String, Map<String, Integer>> expenses = new HashMap<>();
        String query = "SELECT day, category, amount FROM expenses WHERE month = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, month);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String day = String.valueOf(rs.getInt("day"));
                String category = rs.getString("category");
                int amount = rs.getInt("amount");
                expenses.computeIfAbsent(day, k -> new HashMap<>()).put(category, amount);
            }
            return expenses;
        }
    }

    public int getTotal(String month) throws SQLException {
        String query = "SELECT SUM(amount) as total FROM expenses WHERE month = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, month);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
            return 0;
        }
    }

    public boolean hasExpensesForDate(String month, int day) throws SQLException {
        String query = "SELECT COUNT(*) as count FROM expenses WHERE month = ? AND day = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, month);
            stmt.setInt(2, day);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
            return false;
        }
    }

    public Map<String, Integer> getAnnualExpenses(int year) throws SQLException {
        Map<String, Integer> monthlyTotals = new HashMap<>();
        String query = "SELECT month, SUM(amount) as total FROM expenses WHERE month LIKE ? GROUP BY month";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, year + "-%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String month = rs.getString("month");
                int total = rs.getInt("total");
                monthlyTotals.put(month, total);
            }
            return monthlyTotals;
        }
    }
}

