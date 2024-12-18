import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * The DayDisplayUI class represents a dialog for displaying and adding expenses for a specific day.
 * It extends JDialog to create a modal window for expense entry and display.
 */
public class DayDisplayUI extends JDialog {
    private static final long serialVersionUID = 1L;
    private JTextField expenseField;
    private JTextField categoryField;
    private JPanel expenseDisplayPanel;
    private ExpenseInsight parent;
    private int day;
    private JLabel warningLabel;
    private int monthlyBudgetLimit;

    /**
     * Constructor for the DayDisplayUI class.
     * 
     * @param parent The parent ExpenseInsight frame
     * @param day The day of the month for which expenses are being displayed/added
     * @param expenses A map of existing expenses for the day
     */
    public DayDisplayUI(ExpenseInsight parent, int day, Map<String, Integer> expenses) {
        super(parent, "Expense for Day " + day, true);
        this.parent = parent;
        this.day = day;
        this.monthlyBudgetLimit = parent.getMonthlyBudgetLimit();
        setSize(400, 350);
        getContentPane().setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(0, 128, 128));

        initializeComponents(expenses);
        createLayout();

        setLocationRelativeTo(parent);
    }

    /**
     * Initializes the UI components of the dialog.
     * 
     * @param expenses A map of existing expenses for the day
     */
    private void initializeComponents(Map<String, Integer> expenses) {
        expenseDisplayPanel = new JPanel();
        expenseDisplayPanel.setLayout(new BoxLayout(expenseDisplayPanel, BoxLayout.Y_AXIS));
        expenseDisplayPanel.setBackground(new Color(255, 215, 0));
        updateExpenseDisplay(expenses);

        expenseField = new JTextField(10);
        categoryField = new JTextField(10);

        warningLabel = new JLabel();
        warningLabel.setForeground(Color.RED);
        warningLabel.setHorizontalAlignment(SwingConstants.CENTER);
        updateWarningLabel();
    }

    /**
     * Creates the layout of the dialog, arranging all components.
     */
    private void createLayout() {
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(new Color(0, 128, 128));

        JScrollPane scrollPane = new JScrollPane(expenseDisplayPanel);
        scrollPane.setPreferredSize(new Dimension(380, 150));
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = createInputPanel();
        contentPanel.add(inputPanel, BorderLayout.SOUTH);

        getContentPane().add(contentPanel, BorderLayout.CENTER);

        JButton saveButton = new JButton("Save");
        saveButton.setBackground(new Color(173, 216, 230));
        saveButton.addActionListener(e -> saveExpense());
        getContentPane().add(saveButton, BorderLayout.SOUTH);

        getContentPane().add(warningLabel, BorderLayout.NORTH);
    }

    /**
     * Creates the input panel for entering new expenses.
     * 
     * @return JPanel containing input fields for expense entry
     */
    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        inputPanel.setBackground(Color.DARK_GRAY);

        JLabel expenseLabel = new JLabel("EXPENSE:");
        JLabel categoryLabel = new JLabel("CATEGORY:");

        JLabel[] labels = {expenseLabel, categoryLabel};
        for (JLabel label : labels) {
            label.setForeground(Color.WHITE);
            label.setHorizontalAlignment(SwingConstants.RIGHT);
        }

        inputPanel.add(expenseLabel);
        inputPanel.add(expenseField);
        inputPanel.add(categoryLabel);
        inputPanel.add(categoryField);

        return inputPanel;
    }

    /**
     * Saves the entered expense to the parent ExpenseInsight object.
     * This method is called when the save button is clicked.
     */
    private void saveExpense() {
        String expenseText = expenseField.getText();
        String categoryText = categoryField.getText();

        if (!expenseText.isEmpty() && !categoryText.isEmpty()) {
            try {
                int expense = Integer.parseInt(expenseText);
                if (expense > 0) {
                    int currentMonthExpense = parent.getCurrentMonthTotalExpense();
                    int newTotalExpense = currentMonthExpense + expense;

                    if (monthlyBudgetLimit != -1 && newTotalExpense > monthlyBudgetLimit) {
                        showLimitExceededNotification(newTotalExpense, monthlyBudgetLimit);
                    }

                    parent.addExpense(categoryText, expense, day);
                    updateExpenseDisplay(parent.getDayExpenses()[day - 1].getExpenses());
                    expenseField.setText("");
                    categoryField.setText("");
                    updateWarningLabel();
                } else {
                    JOptionPane.showMessageDialog(this, "Expense must be greater than zero.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid expense amount. Please enter a number.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please fill out both fields.");
        }
    }

    /**
     * Updates the expense display panel with the current expenses for the day.
     * 
     * @param expenses A map of the current expenses for the day
     */
    private void updateExpenseDisplay(Map<String, Integer> expenses) {
        expenseDisplayPanel.removeAll();
        if (expenses != null && !expenses.isEmpty()) {
            for (Map.Entry<String, Integer> entry : expenses.entrySet()) {
                JLabel expenseLabel = new JLabel(entry.getKey() + ": $" + entry.getValue());
                expenseLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                expenseDisplayPanel.add(expenseLabel);
            }
        } else {
            JLabel noExpenseLabel = new JLabel("No expenses recorded for this day.");
            noExpenseLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            expenseDisplayPanel.add(noExpenseLabel);
        }
        expenseDisplayPanel.revalidate();
        expenseDisplayPanel.repaint();
    }

    /**
     * Updates the warning label based on whether the daily limit has been exceeded.
     */
    private void updateWarningLabel() {
        int currentMonthExpense = parent.getCurrentMonthTotalExpense();
        if (monthlyBudgetLimit != -1 && currentMonthExpense >= monthlyBudgetLimit) {
            warningLabel.setText("Warning: Monthly budget limit exceeded!");
        } else {
            warningLabel.setText("");
        }
    }

    /**
     * Displays a notification when the daily expense limit is exceeded or equaled.
     * 
     * @param totalExpense The total expense for the day after adding the new expense
     * @param monthlyLimit The daily expense limit
     */
    private void showLimitExceededNotification(int totalExpense, int monthlyLimit) {
        String message = String.format("Your total expenses of $%d will exceed the monthly budget limit of $%d.", totalExpense, monthlyLimit);
        JOptionPane.showMessageDialog(this, message, "Monthly Budget Limit Exceeded", JOptionPane.WARNING_MESSAGE);
    }
}

