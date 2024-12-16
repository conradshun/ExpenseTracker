import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Calendar;
import javax.swing.border.Border;

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
    private String currentUser; // Added to store the current user

    private int BUDGET = 0;
    private int EXPENSE = 0;
    private int TOTAL = BUDGET - EXPENSE;

    private JLabel budgetLabel;
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
    private Map<String, Integer> monthlyBudgets;

    public ExpenseInsight(String username) { // Updated constructor signature
        this.currentUser = username;
        expenseTracker = new ExpenseTracker();

        setBackground(new Color(0, 0, 64));
        setTitle("Expense Insight");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());

        // Initialize month with current month
        Calendar cal = Calendar.getInstance();
        month = new Month(cal.get(Calendar.MONTH) + 1); // Calendar.MONTH is zero-based

        try {
            monthlyBudgets = expenseTracker.getBudgets();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading budget data: " + e.getMessage());
        }

        dayExpenses = new DayExpense[31];
        for (int i = 0; i < dayExpenses.length; i++) {
            dayExpenses[i] = new DayExpense();
        }

        // Create and add the top panel
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.setBackground(new Color(0, 128, 128));
        monthLabel = new JLabel(month.getMonthName());
        monthLabel.setForeground(new Color(255, 255, 255));
        budgetLabel = new JLabel("BUDGET: " + BUDGET);
        budgetLabel.setForeground(new Color(255, 255, 255));
        expenseLabel = new JLabel("EXPENSE: " + EXPENSE);
        expenseLabel.setForeground(new Color(255, 255, 255));
        totalLabel = new JLabel("BUDGET LEFT: " + TOTAL);
        totalLabel.setForeground(new Color(255, 255, 255));

        previousButton = new JButton("Previous");
        previousButton.setBackground(new Color(173, 216, 230));
        nextButton = new JButton("Next");
        nextButton.setBackground(new Color(173, 216, 230));

        topPanel.add(previousButton);
        topPanel.add(monthLabel);
        topPanel.add(budgetLabel);
        topPanel.add(expenseLabel);
        topPanel.add(totalLabel);
        topPanel.add(nextButton);
        getContentPane().add(topPanel, BorderLayout.NORTH);

        // Create and add the left panel for buttons
        JPanel leftPanel = new JPanel(new GridLayout(5, 1, 0, 10));
        leftPanel.setBackground(new Color(0, 128, 128));
        JButton logoutButton = new JButton("LOGOUT");
        logoutButton.setBackground(new Color(173, 216, 230));
        setLimitButton = new JButton("SET BUDGET");
        setLimitButton.setBackground(new Color(173, 216, 230));
        JButton addBudgetButton = new JButton("ADD EXPENSE");
        addBudgetButton.setBackground(new Color(173, 216, 230));
        JButton annualReportButton = new JButton("ANNUAL REPORT");
        annualReportButton.setBackground(new Color(173, 216, 230));

        leftPanel.add(logoutButton);
        leftPanel.add(setLimitButton);
        leftPanel.add(addBudgetButton);
        leftPanel.add(annualReportButton);
        getContentPane().add(leftPanel, BorderLayout.WEST);

        // Create and add the calendar panel
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

        annualReportButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                try {
                    int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                    Map<String, Integer> expenses = expenseTracker.getAnnualExpenses(currentYear);
                    // Assuming you have a BarGraph class that can handle this data
                    BarGraph barGraph = new BarGraph(expenses, new HashMap<>()); // No income data in this schema
                    barGraph.setVisible(true);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error generating annual report: " + ex.getMessage());
                }
            });
        });

        previousButton.addActionListener(e -> {
            month.previous();
            updateMonthDisplay();
        });

        nextButton.addActionListener(e -> {
            month.next();
            updateMonthDisplay();
        });

        logoutButton.addActionListener(e -> {
            this.dispose();
            LoginUI loginUI = new LoginUI();
            loginUI.setVisible(true);
        });

        setLimitButton.addActionListener(e -> {
            String budgetInput = JOptionPane.showInputDialog("Enter your budget:");
            if (budgetInput != null) {
                try {
                    int budget = Integer.parseInt(budgetInput);
                    String monthKey = getCurrentMonthKey();
                    monthlyBudgets.put(monthKey, budget);
                    BUDGET = budget;
                    TOTAL = BUDGET - EXPENSE;
                    budgetLabel.setText("BUDGET: " + BUDGET);
                    totalLabel.setText("BUDGET LEFT: " + TOTAL);

                    expenseTracker.saveBudget(monthKey, budget);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid budget amount. Please enter a number.");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error saving budget: " + ex.getMessage());
                }
            }
        });

        addBudgetButton.addActionListener(e -> {
            String category = JOptionPane.showInputDialog("Enter expense category:");
            if (category != null && !category.trim().isEmpty()) {
                String amountInput = JOptionPane.showInputDialog("Enter expense amount:");
                if (amountInput != null) {
                    try {
                        int amount = Integer.parseInt(amountInput);
                        int day = Integer.parseInt(JOptionPane.showInputDialog("Enter day of the month:"));
                        addExpense(category, amount, day);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Invalid input. Please enter numbers for amount and day.");
                    }
                }
            }
        });

        setSize(800, 600);
        setVisible(true);
        loadExpensesForCurrentMonth();
    }

    private String getCurrentMonthKey() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        return currentYear + "-" + String.format("%02d", month.getMonthNumber());
    }

    private void updateMonthDisplay() {
        monthLabel.setText(month.getMonthName());
        updateCalendar();
        loadExpensesForCurrentMonth();
    }

    private void loadExpensesForCurrentMonth() {
        try {
            String monthKey = getCurrentMonthKey();
            BUDGET = expenseTracker.getBudget(monthKey);
            budgetLabel.setText("BUDGET: " + BUDGET);

            Map<String, Map<String, Integer>> monthExpenses = expenseTracker.getExpenses(monthKey);
            EXPENSE = expenseTracker.getTotal(monthKey);
            expenseLabel.setText("EXPENSE: " + EXPENSE);

            TOTAL = BUDGET - EXPENSE;
            totalLabel.setText("BUDGET LEFT: " + TOTAL);

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

    private void addExpense(String category, int amount, int day) {
        if (amount <= 0) {
            JOptionPane.showMessageDialog(null, "Expense amount must be greater than zero.");
            return;
        }

        try {
            String monthKey = getCurrentMonthKey();
            expenseTracker.saveExpense(monthKey, category, amount, day);

            dayExpenses[day - 1].addExpense(category, amount);
            EXPENSE += amount;
            expenseLabel.setText("EXPENSE: " + EXPENSE);
            TOTAL = BUDGET - EXPENSE;
            totalLabel.setText("BUDGET LEFT: " + TOTAL);

            updateCalendar();

            DayDisplayUI dayDisplay = new DayDisplayUI(this, day, dayExpenses[day - 1].getExpenses());
            dayDisplay.setVisible(true);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error saving expense: " + ex.getMessage());
        }
    }

    public DayExpense[] getDayExpenses() {
        return dayExpenses;
    }

    public static void main(String[] args) { // Updated main method
        SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
    }
}

