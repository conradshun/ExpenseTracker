import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class DayDisplayUI extends JDialog {
    private static final long serialVersionUID = 1L;
    private JTextField expenseField;
    private JTextField categoryField;
    private JPanel expenseDisplayPanel;

    public DayDisplayUI(Frame parent, int day, Map<String, Integer> expenses) {
        super(parent, "Expense for " + day, true);
        setSize(400, 300);
        getContentPane().setLayout(new GridLayout(4, 1, 10, 10));
        getContentPane().setBackground(new Color(0, 128, 128));

        // Create panel for displaying expenses
        expenseDisplayPanel = new JPanel();
        expenseDisplayPanel.setLayout(new BoxLayout(expenseDisplayPanel, BoxLayout.Y_AXIS));
        expenseDisplayPanel.setBackground(new Color(255, 215, 0));
        updateExpenseDisplay(expenses); // Initial display of expenses

        // Add the expense display panel to the dialog
        getContentPane().add(expenseDisplayPanel);

        // Create panel for input fields
        JPanel inputPanel = new JPanel(new GridLayout(2, 2));
        inputPanel.setBackground(Color.DARK_GRAY);

        // Expense input
        JLabel expenseLabel = new JLabel("EXPENSE:");
        expenseLabel.setForeground(new Color(0, 0, 0));
        expenseLabel.setBackground(new Color(0, 128, 128));
        expenseField = new JTextField();
        inputPanel.add(expenseLabel);
        inputPanel.add(expenseField);

        // Category input
        JLabel categoryLabel = new JLabel("CATEGORY:");
        categoryLabel.setForeground(new Color(0, 0, 0));
        categoryLabel.setBackground(new Color(0, 128, 128));
        categoryField = new JTextField();
        inputPanel.add(categoryLabel);
        inputPanel.add(categoryField);

        // Add input panel to the dialog
        getContentPane().add(inputPanel);

        // Save button
        JButton saveButton = new JButton("Save");
        saveButton.setBackground(new Color(173, 216, 230));
        saveButton.addActionListener(e -> {
            String expenseText = expenseField.getText();
            String categoryText = categoryField.getText();

            if (!expenseText.isEmpty() && !categoryText.isEmpty()) {
                try {
                    int expense = Integer.parseInt(expenseText);
                    if (expense > 0) { // Ensure expense is positive
                        ((ExpenseInsight) parent).addExpense(categoryText, expense, day);
                        updateExpenseDisplay(((ExpenseInsight) parent).getDayExpenses()[day - 1].getExpenses()); // Update displayed expenses
                        expenseField.setText(""); // Clear the input field
                        categoryField.setText(""); // Clear the input field
                    } else {
                        JOptionPane.showMessageDialog(this, "Expense must be greater than zero.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid expense amount. Please enter a number.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please fill out both fields.");
            }
        });
        getContentPane().add(saveButton);
    }

    private void updateExpenseDisplay(Map<String, Integer> expenses) {
        expenseDisplayPanel.removeAll(); // Clear existing labels
        if (expenses != null && !expenses.isEmpty()) {
            for (Map.Entry<String, Integer> entry : expenses.entrySet()) {
                JLabel expenseLabel = new JLabel(entry.getKey() + ": " + entry.getValue());
                expenseDisplayPanel.add(expenseLabel);
            }
        } else {
            JLabel noExpenseLabel = new JLabel("No expenses recorded for this day.");
            expenseDisplayPanel.add(noExpenseLabel);
        }
        expenseDisplayPanel.revalidate(); // Refresh the panel
        expenseDisplayPanel.repaint(); // Repaint the panel
    }
}

