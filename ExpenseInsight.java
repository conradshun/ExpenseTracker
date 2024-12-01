import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Calendar;
import javax.swing.border.Border;

// Holds the expenses for a specific day
class DayExpense {
    private Map<String, Integer> expenses;

    public DayExpense() {
        this.expenses = new HashMap<>();
    }

    public void addExpense(String category, int amount) {
        expenses.put(category, expenses.getOrDefault(category, 0) + amount);
    }

    public Map<String, Integer> getExpenses() {
        return expenses;
    }
}

public class ExpenseInsight extends JFrame {

    private static final long serialVersionUID = 1L;

    private int BUDGET = 0;
    private int EXPENSE = 0;
    private int TOTAL = BUDGET - EXPENSE;

    private JLabel budgetLabel;
    private JLabel expenseLabel;
    private JLabel totalLabel;
    private JLabel monthLabel;

    private JButton setLimitButton;
    private JButton nextButton; // Next button
    private JButton previousButton; // Previous button

    private JPanel calendarPanel;
    private Map<Integer, JButton> dayButtons;

    private int limit = 0;
    private Month month;

    private DayExpense[] dayExpenses;

    // Instance of ExpenseTracker to manage expenses
    private ExpenseTracker expenseTracker;

    public ExpenseInsight() {
        // Initialize ExpenseTracker with the database URL
        expenseTracker = new ExpenseTracker("jdbc:mysql://localhost:3306/expense_tracker");

        setBackground(new Color(0, 0, 64));
        setTitle("Expense Insight");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());

        dayExpenses = new DayExpense[31]; // Assuming max 31 days in a month
        for (int i = 0; i < dayExpenses.length; i++) {
            dayExpenses[i] = new DayExpense();
        }

        // Create and add the top panel for income, expense, and total
        JPanel topPanel = new JPanel(new FlowLayout()); // Use FlowLayout for better arrangement
        topPanel.setBackground(new Color(0, 128, 128));
        month = new Month(1);
        monthLabel = new JLabel(month.getMonthName());
        monthLabel.setForeground(new Color(255, 255, 255));
        budgetLabel = new JLabel("BUDGET: " + BUDGET);
        budgetLabel.setForeground(new Color(255, 255, 255));
        expenseLabel = new JLabel("EXPENSE: " + EXPENSE);
        expenseLabel.setForeground(new Color(255, 255, 255));
        totalLabel = new JLabel("BUDGET LEFT: " + TOTAL);
        totalLabel.setForeground(new Color(255, 255, 255));

        // Add buttons for navigating months
        previousButton = new JButton("Previous");
        previousButton.setBackground(new Color(173, 216, 230));
        nextButton = new JButton("Next");
        nextButton.setBackground(new Color(173, 216, 230));

        topPanel.add(previousButton);
        topPanel.add(monthLabel);
        topPanel.add(budgetLabel);
        topPanel.add(expenseLabel);
        topPanel.add(totalLabel);
        topPanel.add(nextButton); // Ensure next button is added to the panel
        getContentPane().add(topPanel, BorderLayout.NORTH);

        // Create and add the left panel for buttons
        JPanel leftPanel = new JPanel(new GridLayout(5, 1, 0, 10)); // Changed to 5 rows to accommodate the new button
        leftPanel.setBackground(new Color(0, 128, 128));
        JButton logoutButton = new JButton("LOGOUT");
        logoutButton.setBackground(new Color(173, 216, 230));
        setLimitButton = new JButton("SET LIMIT");
        setLimitButton.setBackground(new Color(173, 216, 230));
        JButton addBudgetButton = new JButton("ADD BUDGET");
        addBudgetButton.setBackground(new Color(173, 216, 230));
        JButton annualReportButton = new JButton("ANNUAL REPORT");
        annualReportButton.setBackground(new Color(173, 216, 230));
      
        leftPanel.add(logoutButton);
        leftPanel.add(setLimitButton);
        leftPanel.add(addBudgetButton);
        leftPanel.add(annualReportButton);
        getContentPane().add(leftPanel, BorderLayout.WEST);

        // Create and add the calendar panel
        calendarPanel = new JPanel(new GridLayout(6, 7)); // Changed to 6 rows and 7 columns for day labels
        calendarPanel.setBackground(new Color(0, 128, 128));
        dayButtons = new HashMap<>();

        // Add day labels
        String[] dayNames = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        Border border = BorderFactory.createLineBorder(Color.WHITE);

        for (String dayName : dayNames) {
            JLabel dayLabel = new JLabel(dayName, SwingConstants.CENTER);
            dayLabel.setBorder(border);
            dayLabel.setForeground(Color.BLACK); // Change text color to black for better contrast
            dayLabel.setBackground(new Color(255, 215, 0)); // Set background color to gold
            dayLabel.setOpaque(true); // Make the label opaque to show the background color
            calendarPanel.add(dayLabel);
        }

        updateCalendar(); // Initial calendar setup
        getContentPane().add(calendarPanel, BorderLayout.CENTER);

        // Set up action listeners for navigation buttons
        previousButton.addActionListener(e -> {
            month.previous();
            updateMonthDisplay();
        });

        nextButton.addActionListener(e -> {
            month.next();
            updateMonthDisplay();
        });
        
        logoutButton.addActionListener(e -> {
            // Dispose of the current ExpenseInsight frame
            this.dispose();
            // Open the LoginUI
            LoginUI loginUI = new LoginUI(); // Create the LoginUI instance
            loginUI.setVisible(true); // Set it to be visible
        });

     // Set up action listener for adding budget
        addBudgetButton.addActionListener(e -> {
            String budgetInput = JOptionPane.showInputDialog("Enter your budget:");
            if (budgetInput != null) {
                try {
                    BUDGET = Integer.parseInt(budgetInput);
                    TOTAL = BUDGET - EXPENSE;
                    budgetLabel.setText("BUDGET: " + BUDGET);
                    totalLabel.setText("BUDGET LEFT: " + TOTAL);
                    
                    // Set the budget for the current month and year
                    int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                    String monthKey = currentYear + "-" + String.format("%02d", month.getMonthNumber()); // Format as "YYYY-MM"
                    
                    // Wrap the setBudget call in a try-catch block to handle SQLException
                    try {
                        expenseTracker.setBudget(monthKey, BUDGET); // Update budget in ExpenseTracker
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Error updating budget: " + ex.getMessage());
                    }
                    
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid budget amount. Please enter a number.");
                }
            }
        });

        // Set up action listener for setting limit
        setLimitButton.addActionListener(e -> {
            String limitInput = JOptionPane.showInputDialog("Set your expense limit:");
            if (limitInput != null) {
                try {
                    limit = Integer.parseInt(limitInput);
                    JOptionPane.showMessageDialog(this, "Expense limit set to: " + limit);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid limit amount. Please enter a number.");
                }
            }
        });
        
        setSize(800, 600);
        setVisible(true);
    }

    private void updateMonthDisplay() {
        monthLabel.setText(month.getMonthName());
        updateCalendar(); // Update the calendar display for the new month
        loadExpensesForCurrentMonth(); // Load expenses for the current month
    }

    private void loadExpensesForCurrentMonth() {
        // Load expenses from ExpenseTracker for the current month
        try {
            EXPENSE = expenseTracker.getTotal(month.getMonthName());
            expenseLabel.setText("EXPENSE: " + EXPENSE);
            TOTAL = BUDGET - EXPENSE;
            totalLabel.setText("BUDGET LEFT: " + TOTAL);
            
            dayExpenses = new DayExpense[month.getDaysInMonth()]; // Reset for the new month
            for (int i = 0; i < dayExpenses.length; i++) {
                dayExpenses[i] = new DayExpense();
            }
            
            // Assuming getExpenses returns a Map<String, Integer>
            Map<String, Integer> expenses = expenseTracker.getExpenses(month.getMonthName());
            for (Map.Entry<String, Integer> entry : expenses.entrySet()) {
                // Split the key to get day and category
                String[] parts = entry.getKey().split("-");
                if (parts.length == 2) {
                    int day = Integer.parseInt(parts[0]); // Get the day
                    String category = parts[1]; // Get the category

                    // Add the expense to the corresponding day
                    dayExpenses[day - 1].addExpense(category, entry.getValue());
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading expenses: " + e.getMessage());
        }
    }

    private void updateCalendar() {
        // Clear the calendar panel
        calendarPanel.removeAll();

        // Add day labels again
        String[] dayNames = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        Border border = BorderFactory.createLineBorder(Color.WHITE);

        for (String dayName : dayNames) {
            JLabel dayLabel = new JLabel(dayName, SwingConstants.CENTER);
            dayLabel.setBorder(border);
            dayLabel.setForeground(Color.BLACK);
            dayLabel.setBackground(new Color(255, 215, 0)); // gold
            dayLabel.setOpaque(true);
            calendarPanel.add(dayLabel);
        }

        // Calculate the first day of the month for the current month and year (2024)
        Calendar calendar = Calendar.getInstance();
        calendar.set(2024, month.getMonthNumber() - 1, 1); // Set to the first day of the month
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); // 1 = Sunday, 2 = Monday, ..., 7 = Saturday

        // Add empty labels for days before the first day of the month
        for (int i = 1; i < firstDayOfWeek; i++) {
            calendarPanel.add(new JLabel("")); // Empty label for alignment
        }

        // Add buttons for each day of the month
        for (int i = 1; i <= month.getDaysInMonth(); i++) {
            JButton dayButton = new JButton(String.valueOf(i));
            int day = i;
            dayButton.setBackground(new Color(173, 216, 230));
            dayButton.setBorder(border);
            dayButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    DayDisplayUI dayDisplay = new DayDisplayUI(ExpenseInsight.this, day, dayExpenses[day - 1].getExpenses());
                    dayDisplay.setVisible(true);
                }
            });
            dayButtons.put(i, dayButton);
            calendarPanel.add(dayButton);
        }

        calendarPanel.revalidate(); // Refresh the calendar panel
        calendarPanel.repaint(); // Repaint the calendar panel to show updates
    }

    void addExpense(String category, int amount, int day) {
        if (amount <= 0) {
            JOptionPane.showMessageDialog(null, "Expense amount must be greater than zero.");
            return;
        }

        dayExpenses[day - 1].addExpense(category, amount); // Use the specific day
        EXPENSE += amount; // Increase the total expenses
        totalLabel.setText("BUDGET LEFT: " + (BUDGET - EXPENSE)); // Update the total label

        // Check if the expenses exceed the limit
        if (EXPENSE > limit) {
            JOptionPane.showMessageDialog(null, "Warning: Your expenses have exceeded the limit of " + limit + "!");
        }

        // Update the expense label
        expenseLabel.setText("EXPENSE: " + EXPENSE);
        
        // Add the expense to the ExpenseTracker
        try {
            expenseTracker.addExpense(month.getMonthName(), category, amount);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding expense: " + e.getMessage());
        }

        // Update the displayed expenses in the DayDisplayUI (assuming you have a reference to it)
        DayDisplayUI dayDisplay = new DayDisplayUI(this, day, dayExpenses[day - 1].getExpenses());
        dayDisplay.setVisible(true);
    }

    public DayExpense[] getDayExpenses() {
        return dayExpenses;
    }

    public static void main(String[] args) {
        new ExpenseInsight();
    }
}
