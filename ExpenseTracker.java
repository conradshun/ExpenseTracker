import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * The ExpenseTracker class handles all database operations for the expense tracking application.
 * It provides methods for user authentication, expense management, and budget tracking.
 */
public class ExpenseTracker {
  private static final String DB_URL = "jdbc:mysql://localhost:3306/expensetrackerdb";
  private static final String DB_USER = "root";
  private static final String DB_PASSWORD = "Conradqt4ever";
  private int currentUserId;

  /**
   * Default constructor for ExpenseTracker.
   * Initializes the currentUserId to -1.
   */
  public ExpenseTracker() {
      this.currentUserId = -1;
  }

  /**
   * Constructor for ExpenseTracker with a specific user ID.
   * 
   * @param userId The ID of the current user
   */
  public ExpenseTracker(int userId) {
      this.currentUserId = userId;
  }

  /**
   * Establishes and returns a connection to the database.
   * 
   * @return A Connection object to the database
   * @throws SQLException If a database access error occurs
   */
  private Connection getConnection() throws SQLException {
      return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
  }

  /**
   * Authenticates a user based on the provided username and password.
   * 
   * @param username The username of the user
   * @param password The password of the user
   * @return true if authentication is successful, false otherwise
   * @throws SQLException If a database access error occurs
   */
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

  /**
   * Retrieves the user ID for a given username.
   * 
   * @param username The username of the user
   * @return The user ID
   * @throws SQLException If a database access error occurs or the user is not found
   */
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

  /**
   * Registers a new user with the provided username and password.
   * 
   * @param username The username for the new user
   * @param password The password for the new user
   * @return true if registration is successful, false if the username already exists
   * @throws SQLException If a database access error occurs
   */
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

  /**
   * Saves or updates the budget for a specific month.
   * 
   * @param month The month for which the budget is being set
   * @param amount The budget amount
   * @param limit The budget limit
   * @throws SQLException If a database access error occurs
   */
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

  /**
   * Retrieves the budget for a specific month.
   * 
   * @param month The month for which to retrieve the budget
   * @return A Map containing the budget amount and limit, or null if not set
   * @throws SQLException If a database access error occurs
   */
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
              return budget.get("amount") == 0 && budget.get("limit") == 0 ? null : budget;
          }
          return null;
      }
  }

  /**
   * Saves an expense for a specific month and day.
   * 
   * @param month The month of the expense
   * @param category The category of the expense
   * @param amount The amount of the expense
   * @param day The day of the month for the expense
   * @throws SQLException If a database access error occurs
   */
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

  /**
   * Retrieves all expenses for a specific month.
   * 
   * @param month The month for which to retrieve expenses
   * @return A Map of day numbers to a Map of expense categories and amounts
   * @throws SQLException If a database access error occurs
   */
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

  /**
   * Calculates the total expenses for a specific month.
   * 
   * @param month The month for which to calculate total expenses
   * @return The total amount of expenses for the month
   * @throws SQLException If a database access error occurs
   */
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

  /**
   * Checks if there are any expenses recorded for a specific date.
   * 
   * @param month The month to check
   * @param day The day of the month to check
   * @return true if there are expenses for the given date, false otherwise
   * @throws SQLException If a database access error occurs
   */
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

  /**
   * Retrieves the annual expenses for a specific year.
   * 
   * @param year The year for which to retrieve expenses
   * @return A Map of month names to total expenses for each month
   * @throws SQLException If a database access error occurs
   */
  public Map<String, Integer> getAnnualExpenses(int year) throws SQLException {
      Map<String, Integer> annualExpenses = new HashMap<>();
      String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

      for (int i = 0; i < months.length; i++) {
          String monthKey = year + "-" + String.format("%02d", i + 1);
          int total = getTotal(monthKey);
          annualExpenses.put(months[i], total);
      }

      return annualExpenses;
  }

  /**
   * Retrieves the annual income (budget) for a specific year.
   * 
   * @param year The year for which to retrieve income
   * @return A Map of month names to total income (budget) for each month
   * @throws SQLException If a database access error occurs
   */
  public Map<String, Integer> getAnnualIncome(int year) throws SQLException {
      Map<String, Integer> annualIncome = new HashMap<>();
      String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

      for (int i = 0; i < months.length; i++) {
          String monthKey = year + "-" + String.format("%02d", i + 1);
          Map<String, Integer> budget = getBudget(monthKey);
          int income = budget != null ? budget.getOrDefault("amount", 0) : 0;
          annualIncome.put(months[i], income);
      }

      return annualIncome;
  }
}

