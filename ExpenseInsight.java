import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Calendar;
import javax.swing.border.Border;

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

        // Create and add the top panel
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.setBackground(new Color(0, 128, 128));
        monthLabel = new JLabel(month.getMonthName());
        monthLabel.setForeground(new Color(255, 255, 255));
        budgetLabel = new JLabel("BUDGET: " + BUDGET);
        budgetLabel.setForeground(new Color(255, 255, 255));
        budgetLimitLabel = new JLabel("BUDGET LIMIT: " + BUDGET_LIMIT);
        budgetLimitLabel.setForeground(new Color(255, 255, 255));
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
        topPanel.add(budgetLimitLabel);
        topPanel.add(expenseLabel);
        topPanel.add(totalLabel);
        topPanel.add(nextButton);
        getContentPane().add(topPanel, BorderLayout.NORTH);

        // Create and add the left panel for buttons
        JPanel leftPanel = new JPanel(new GridLayout(4, 1, 0, 10));
        leftPanel.setBackground(new Color(0, 128, 128));
        JButton logoutButton = new JButton("LOGOUT");
        logoutButton.setBackground(new Color(173, 216, 230));
        setLimitButton = new JButton("SET BUDGET");
        setLimitButton.setBackground(new Color(173, 216, 230));
        JButton annualReportButton = new JButton("ANNUAL REPORT");
        annualReportButton.setBackground(new Color(173, 216, 230));

        leftPanel.add(logoutButton);
        leftPanel.add(setLimitButton);
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

        // adds the button for annual report which shows a bargraph
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

        // goes to the previous month 
        previousButton.addActionListener(e -> {
            month.previous();
            updateMonthDisplay();
        });
        // goes to the next month
        nextButton.addActionListener(e -> {
            month.next();
            updateMonthDisplay();
        });
        // logouts of the current user and calls the loginUI again
        logoutButton.addActionListener(e -> {
            this.dispose();
            LoginUI loginUI = new LoginUI();
            loginUI.setVisible(true);
        });
        // a button for setting the limit
        setLimitButton.addActionListener(e -> {
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
        });

        setSize(800, 600);
        setVisible(true);
        loadExpensesForCurrentMonth();
    }
    // gets the current month in a string format
    private String getCurrentMonthKey() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        return currentYear + "-" + String.format("%02d", month.getMonthNumber());
    }
    // updates the month displayed currently
    private void updateMonthDisplay() {
        monthLabel.setText(month.getMonthName());
        updateCalendar();
        loadExpensesForCurrentMonth();
    }
    // loads the expenses for the current month
    private void loadExpensesForCurrentMonth() {
        try {
            String monthKey = getCurrentMonthKey();
            Map<String, Integer> budget = expenseTracker.getBudget(monthKey);
            if (budget != null) {
                BUDGET = budget.get("amount");
                BUDGET_LIMIT = budget.get("limit");
                budgetLabel.setText("BUDGET: " + BUDGET);
                budgetLimitLabel.setText("BUDGET LIMIT: " + BUDGET_LIMIT);
            }

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
    // loads the calendar
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
    // adds the expenses
    public void addExpense(String category, int amount, int day) {
        try {
            String monthKey = getCurrentMonthKey();
            expenseTracker.saveExpense(monthKey, category, amount, day);

            dayExpenses[day - 1].addExpense(category, amount);

            EXPENSE += amount;
            TOTAL = BUDGET - EXPENSE;

            expenseLabel.setText("EXPENSE: " + EXPENSE);
            totalLabel.setText("BUDGET LEFT: " + TOTAL);

            updateCalendar();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding expense: " + e.getMessage());
        }
    }

    public DayExpense[] getDayExpenses() {
        return dayExpenses;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
    }
}

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
