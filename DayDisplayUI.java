import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class DayDisplayUI extends JDialog {
    private static final long serialVersionUID = 1L;
    private JTextField expenseField;
    private JTextField categoryField;
    private JPanel expenseDisplayPanel;
    private ExpenseInsight parent;
    private int day;

    public DayDisplayUI(ExpenseInsight parent, int day, Map<String, Integer> expenses) {
        super(parent, "Expense for Day " + day, true);
        this.parent = parent;
        this.day = day;
        setSize(400, 300);
        getContentPane().setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(0, 128, 128));

        initializeComponents(expenses);
        createLayout();

        setLocationRelativeTo(parent);
    }
    // creates the components that create the UI
    private void initializeComponents(Map<String, Integer> expenses) {
        expenseDisplayPanel = new JPanel();
        expenseDisplayPanel.setLayout(new BoxLayout(expenseDisplayPanel, BoxLayout.Y_AXIS));
        expenseDisplayPanel.setBackground(new Color(255, 215, 0));
        updateExpenseDisplay(expenses);

        expenseField = new JTextField(10);
        categoryField = new JTextField(10);
    }
    // composes the layout of the UI
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
    }
    // creates input panel of the expense and category
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
    // data handler to save the expenses under the category it was saved with
    private void saveExpense() {
        String expenseText = expenseField.getText();
        String categoryText = categoryField.getText();

        if (!expenseText.isEmpty() && !categoryText.isEmpty()) {
            try {
                int expense = Integer.parseInt(expenseText);
                if (expense > 0) {
                    parent.addExpense(categoryText, expense, day);
                    updateExpenseDisplay(parent.getDayExpenses()[day - 1].getExpenses());
                    expenseField.setText("");
                    categoryField.setText("");
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
    // updates the display of the expenses saved with the category
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
}

