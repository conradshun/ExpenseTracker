import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.*;
import javax.swing.border.Border;

/**
 * The main class for the Expense Tracker application.
 * This class manages the overall UI and functionality of the expense tracking system.
 */
public class ExpenseInsight extends JFrame {

    private static final long serialVersionUID = 1L;
    private int currentUserId;

    private int BUDGET = 0;
    private int BUDGET_LIMIT = 0;
    private int EXPENSE = 0;
    private int TOTAL = BUDGET - EXPENSE;

    private JLabel budgetLabel;
    private JLabel budgetLimitLabel;
    private JLabel expenseLabel;
    private JLabel totalLabel;
    private JLabel monthLabel;

    private JButton setLimitButton;
    private JButton nextButton;
    private JButton previousButton;

    private JPanel calendarPanel;
    private Map<Integer, JButton> dayButtons;

    private Month month;

    private DayExpense[] dayExpenses;

    private ExpenseTracker expenseTracker;
    private int monthlyBudgetLimit;

    /**
     * Constructor for the ExpenseInsight class.
     * Initializes the UI components and loads initial data.
     *
     * @param userId The ID of the current user
     */
    public ExpenseInsight(int userId) {
        this.currentUserId = userId;
        expenseTracker = new ExpenseTracker(userId);

        setBackground(new Color(0, 0, 64));
        setTitle("Expense Insight");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());

        // Initialize month with current month
        Calendar cal = Calendar.getInstance();
        month = new Month(cal.get(Calendar.MONTH) + 1); // Calendar.MONTH is zero-based

        initializeBudgetData();
        initializeUIComponents();
        setSize(800, 600);
        setVisible(true);
        loadExpensesForCurrentMonth();
    }

    /**
     * Initializes the budget data for the current month.
     */
    private void initializeBudgetData() {
        try {
            String monthKey = getCurrentMonthKey();
            Map<String, Integer> budget = expenseTracker.getBudget(monthKey);
            if (budget != null) {
                BUDGET = budget.get("amount");
                BUDGET_LIMIT = budget.get("limit");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading budget data: " + e.getMessage());
        }

        dayExpenses = new DayExpense[31];
        for (int i = 0; i < dayExpenses.length; i++) {
            dayExpenses[i] = new DayExpense();
        }
    }

    /**
     * Initializes all UI components of the application.
     */
    private void initializeUIComponents() {
        initializeTopPanel();
        initializeLeftPanel();
        initializeCalendarPanel();
    }

    /**
     * Initializes the top panel of the UI, which contains the month navigation and budget information.
     */
    private void initializeTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.setBackground(new Color(0, 128, 128));
        
        monthLabel = new JLabel(month.getMonthName());
        budgetLabel = new JLabel("BUDGET: " + BUDGET);
        budgetLimitLabel = new JLabel("BUDGET LIMIT: " + BUDGET_LIMIT);
        expenseLabel = new JLabel("EXPENSE: " + EXPENSE);
        totalLabel = new JLabel("BUDGET LEFT: " + TOTAL);

        JLabel[] labels = {monthLabel, budgetLabel, budgetLimitLabel, expenseLabel, totalLabel};
        for (JLabel label : labels) {
            label.setForeground(new Color(255, 255, 255));
        }

        previousButton = new JButton("Previous");
        nextButton = new JButton("Next");
        previousButton.setBackground(new Color(173, 216, 230));
        nextButton.setBackground(new Color(173, 216, 230));

        topPanel.add(previousButton);
        topPanel.add(monthLabel);
        topPanel.add(budgetLabel);
        topPanel.add(budgetLimitLabel);
        topPanel.add(expenseLabel);
        topPanel.add(totalLabel);
        topPanel.add(nextButton);

        getContentPane().add(topPanel, BorderLayout.NORTH);

        previousButton.addActionListener(e -> {
            month.previous();
            updateMonthDisplay();
        });

        nextButton.addActionListener(e -> {
            month.next();
            updateMonthDisplay();
        });
    }

    /**
     * Initializes the left panel of the UI, which contains action buttons.
     */
    private void initializeLeftPanel() {
        JPanel leftPanel = new JPanel(new GridLayout(4, 1, 0, 10));
        leftPanel.setBackground(new Color(0, 128, 128));

        JButton logoutButton = new JButton("LOGOUT");
        setLimitButton = new JButton("SET BUDGET");
        JButton annualReportButton = new JButton("ANNUAL REPORT");

        JButton[] buttons = {logoutButton, setLimitButton, annualReportButton};
        for (JButton button : buttons) {
            button.setBackground(new Color(173, 216, 230));
            leftPanel.add(button);
        }

        getContentPane().add(leftPanel, BorderLayout.WEST);

        logoutButton.addActionListener(e -> {
            this.dispose();
            LoginUI loginUI = new LoginUI();
            loginUI.setVisible(true);
        });

        setLimitButton.addActionListener(e -> setBudgetAndLimit());

        annualReportButton.addActionListener(e -> generateAnnualReport());
    }

    /**
     * Initializes the calendar panel of the UI.
     */
    private void initializeCalendarPanel() {
        calendarPanel = new JPanel(new GridLayout(0, 7));
        calendarPanel.setBackground(new Color(0, 128, 128));
        dayButtons = new HashMap<>();

        String[] dayNames = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        Border border = BorderFactory.createLineBorder(Color.WHITE);

        for (String dayName : dayNames) {
            JLabel dayLabel = new JLabel(dayName, SwingConstants.CENTER);
            dayLabel.setBorder(border);
            dayLabel.setForeground(Color.BLACK);
            dayLabel.setBackground(new Color(255, 215, 0));
            dayLabel.setOpaque(true);
            calendarPanel.add(dayLabel);
        }

        updateCalendar();
        getContentPane().add(calendarPanel, BorderLayout.CENTER);
    }

    /**
     * Generates the current month key in the format "YYYY-MM".
     *
     * @return String representation of the current month key
     */
    private String getCurrentMonthKey() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        return currentYear + "-" + String.format("%02d", month.getMonthNumber());
    }

    /**
     * Updates the month display and reloads expenses for the new month.
     */
    private void updateMonthDisplay() {
        monthLabel.setText(month.getMonthName());
        updateCalendar();
        loadExpensesForCurrentMonth();
    }

    /**
     * Loads and displays expenses for the current month.
     */
    private void loadExpensesForCurrentMonth() {
        try {
            String monthKey = getCurrentMonthKey();
            Map<String, Integer> budget = expenseTracker.getBudget(monthKey);
            if (budget != null && !budget.isEmpty()) {
                BUDGET = budget.get("amount");
                BUDGET_LIMIT = budget.get("limit");
                monthlyBudgetLimit = BUDGET_LIMIT; // Set the monthly budget limit
            } else {
                BUDGET = 0;
                BUDGET_LIMIT = 0;
                monthlyBudgetLimit = -1; // Use -1 to indicate no budget limit set
            }
            budgetLabel.setText("BUDGET: " + BUDGET);
            budgetLimitLabel.setText("BUDGET LIMIT: " + (monthlyBudgetLimit == -1 ? "Not Set" : monthlyBudgetLimit));

            Map<String, Map<String, Integer>> monthExpenses = expenseTracker.getExpenses(monthKey);
            EXPENSE = expenseTracker.getTotal(monthKey);
            TOTAL = BUDGET - EXPENSE;

            updateExpenseLabels();

            dayExpenses = new DayExpense[month.getDaysInMonth()];
            for (int i = 0; i < dayExpenses.length; i++) {
                dayExpenses[i] = new DayExpense();
            }

            for (Map.Entry<String, Map<String, Integer>> entry : monthExpenses.entrySet()) {
                int day = Integer.parseInt(entry.getKey());
                Map<String, Integer> dayExpense = entry.getValue();
                for (Map.Entry<String, Integer> expense : dayExpense.entrySet()) {
                    String category = expense.getKey();
                    int amount = expense.getValue();
                    dayExpenses[day - 1].addExpense(category, amount);
                }
            }

            updateCalendar();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading expenses: " + e.getMessage());
        }
    }

    /**
     * Updates the calendar display with the current month's data.
     */
    private void updateCalendar() {
        calendarPanel.removeAll();

        String[] dayNames = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        Border border = BorderFactory.createLineBorder(Color.WHITE);

        for (String dayName : dayNames) {
            JLabel dayLabel = new JLabel(dayName, SwingConstants.CENTER);
            dayLabel.setBorder(border);
            dayLabel.setForeground(Color.BLACK);
            dayLabel.setBackground(new Color(255, 215, 0));
            dayLabel.setOpaque(true);
            calendarPanel.add(dayLabel);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
        calendar.set(Calendar.MONTH, month.getMonthNumber() - 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int daysInMonth = month.getDaysInMonth();

        for (int i = 1; i < firstDayOfWeek; i++) {
            calendarPanel.add(new JLabel(""));
        }

        for (int i = 1; i <= daysInMonth; i++) {
            JButton dayButton = new JButton(String.valueOf(i));
            int day = i;
            try {
                boolean hasExpenses = expenseTracker.hasExpensesForDate(getCurrentMonthKey(), day);
                dayButton.setBackground(hasExpenses ? new Color(255, 99, 71) : new Color(173, 216, 230));
            } catch (SQLException e) {
                dayButton.setBackground(new Color(173, 216, 230));
            }
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

        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    /**
     * Adds a new expense to the current month and updates the UI.
     *
     * @param category The category of the expense
     * @param amount The amount of the expense
     * @param day The day of the month for the expense
     */
    public void addExpense(String category, int amount, int day) {
        try {
            String monthKey = getCurrentMonthKey();
            expenseTracker.saveExpense(monthKey, category, amount, day);

            dayExpenses[day - 1].addExpense(category, amount);

            EXPENSE += amount;
            TOTAL = BUDGET - EXPENSE;

            updateExpenseLabels();
            updateCalendar();

            // Check if the total expense has exceeded the monthly budget limit
            if (EXPENSE > monthlyBudgetLimit && monthlyBudgetLimit != -1) {
                showMonthlyLimitExceededNotification();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding expense: " + e.getMessage());
        }
    }

    private void showMonthlyLimitExceededNotification() {
        String message = String.format("Your total expenses of $%d have exceeded the monthly budget limit of $%d.", EXPENSE, monthlyBudgetLimit);
        JOptionPane.showMessageDialog(this, message, "Monthly Budget Limit Exceeded", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Updates the expense labels in the UI.
     */
    private void updateExpenseLabels() {
        expenseLabel.setText("EXPENSE: " + EXPENSE);
        totalLabel.setText("BUDGET LEFT: " + TOTAL);
    }

    /**
     * Prompts the user to set a new budget and limit, then saves it.
     */
    private void setBudgetAndLimit() {
        String budgetInput = JOptionPane.showInputDialog("Enter your budget:");
        String limitInput = JOptionPane.showInputDialog("Enter your budget limit:");
        if (budgetInput != null && limitInput != null) {
            try {
                int budget = Integer.parseInt(budgetInput);
                int limit = Integer.parseInt(limitInput);
                String monthKey = getCurrentMonthKey();
                BUDGET = budget;
                BUDGET_LIMIT = limit;
                TOTAL = BUDGET - EXPENSE;
                budgetLabel.setText("BUDGET: " + BUDGET);
                budgetLimitLabel.setText("BUDGET LIMIT: " + BUDGET_LIMIT);
                totalLabel.setText("BUDGET LEFT: " + TOTAL);

                expenseTracker.saveBudget(monthKey, budget, limit);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid budget or limit amount. Please enter numbers.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error saving budget: " + ex.getMessage());
            }
        }
    }

    /**
     * Generates and displays the annual report.
     */
    private void generateAnnualReport() {
        SwingUtilities.invokeLater(() -> {
            try {
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                Map<String, Integer> expenses = expenseTracker.getAnnualExpenses(currentYear);
                Map<String, Integer> income = expenseTracker.getAnnualIncome(currentYear);
                BarGraph barGraph = new BarGraph(expenses, income);
                barGraph.setVisible(true);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error generating annual report: " + ex.getMessage());
            }
        });
    }

    /**
     * Gets the array of DayExpense objects for the current month.
     *
     * @return Array of DayExpense objects
     */
    public DayExpense[] getDayExpenses() {
        return dayExpenses;
    }

    /**
     * Gets the current budget limit.
     *
     * @return The current budget limit
     */
    public int getBUDGET_LIMIT() {
        return BUDGET_LIMIT;
    }

    /**
     * Gets the number of days in the current month.
     *
     * @return The number of days in the current month
     */
    public int getDaysInMonth() {
        return month.getDaysInMonth();
    }

    /**
     * Gets the monthly budget limit for the current month.
     *
     * @return The monthly budget limit
     */
    public int getMonthlyBudgetLimit() {
        return monthlyBudgetLimit;
    }

    public int getCurrentMonthTotalExpense() {
        return EXPENSE;
    }
}

/**
 * Represents a single day's expenses.
 */
class DayExpense {
    private Map<String, Integer> expenses;

    /**
     * Constructor for DayExpense.
     * Initializes the expenses map.
     */
    public DayExpense() {
        this.expenses = new HashMap<>();
    }

    /**
     * Adds an expense to the day.
     *
     * @param category The category of the expense
     * @param amount The amount of the expense
     */
    public void addExpense(String category, int amount) {
        expenses.put(category, expenses.getOrDefault(category, 0) + amount);
    }

    /**
     * Gets all expenses for the day.
     *
     * @return Map of expenses, where key is the category and value is the amount
     */
    public Map<String, Integer> getExpenses() {
        return expenses;
    }

    /**
     * Calculates the total expense for the day.
     *
     * @return The total expense amount
     */
    public int getTotalExpense() {
        return expenses.values().stream().mapToInt(Integer::intValue).sum();
    }
}

