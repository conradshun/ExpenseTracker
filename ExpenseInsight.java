import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class ExpenseInsight extends JFrame {

    private static final long serialVersionUID = 1L;

    private int INCOME = 0;
    private int EXPENSE = 0;
    private int TOTAL = INCOME - EXPENSE;

    private JLabel incomeLabel;
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

    public ExpenseInsight() {
        setTitle("Expense Insight");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create and add the top panel for income, expense, and total
        JPanel topPanel = new JPanel(new FlowLayout()); // Use FlowLayout for better arrangement
        month = new Month(1);
        monthLabel = new JLabel(month.getMonthName());
        incomeLabel = new JLabel("INCOME: " + INCOME);
        expenseLabel = new JLabel("EXPENSE: " + EXPENSE);
        totalLabel = new JLabel("TOTAL: " + TOTAL);
        
        // Add buttons for navigating months
        previousButton = new JButton("Previous");
        nextButton = new JButton("Next");
        
        topPanel.add(previousButton);
        topPanel.add(monthLabel);
        topPanel.add(incomeLabel);
        topPanel.add(expenseLabel);
        topPanel.add(totalLabel);
        topPanel.add(nextButton); // Ensure next button is added to the panel
        add(topPanel, BorderLayout.NORTH);

        // Create and add the left panel for buttons
        JPanel leftPanel = new JPanel(new GridLayout(4, 1, 0, 10));
        JButton logoutButton = new JButton("LOGOUT");
        setLimitButton = new JButton("SET LIMIT");
        JButton addBudgetButton = new JButton("ADD BUDGET");
        JButton annualReportButton = new JButton("ANNUAL REPORT");
        leftPanel.add(logoutButton);
        leftPanel.add(setLimitButton);
        leftPanel.add(addBudgetButton);
        leftPanel.add(annualReportButton);
        add(leftPanel, BorderLayout.WEST);

        // Create and add the calendar panel
        calendarPanel = new JPanel(new GridLayout(6, 7));
        dayButtons = new HashMap<>();
        createCalendar();
        add(calendarPanel, BorderLayout.CENTER);

        // Set action listener for the Set Limit button
        setLimitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = JOptionPane.showInputDialog("Set your limit:");
                if (input != null) {
                    try {
                        limit = Integer.parseInt(input);
                        JOptionPane.showMessageDialog(null, "Limit set to: " + limit);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Invalid input. Please enter a number.");
                    }
                }
            }
        });

        // Action listener for the Previous button
        previousButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                month.previous(); // Move to the previous month
                updateMonthDisplay();
                createCalendar(); // Refresh the calendar
            }
        });

        // Action listener for the Next button
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                month.next(); // Move to the next month
                updateMonthDisplay();
                createCalendar(); // Refresh the calendar
            }
        });

        pack();
    }

    private void createCalendar() {
        calendarPanel.removeAll(); // Clear existing buttons
        dayButtons.clear(); // Clear the map

        // Assuming a month with 30 days for simplicity; replace with actual days in month
        int daysInMonth = month.getDaysInMonth();
        for (int day = 1; day <= daysInMonth; day++) {
            final int finalDay = day;
            JButton dayButton = new JButton(String .valueOf(day));
            dayButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Handle day button click
                    JOptionPane.showMessageDialog(null, "Selected day: " + finalDay + " " + month.getMonthName());
                }
            });
            dayButtons.put(day, dayButton);
            calendarPanel.add(dayButton);
        }

        calendarPanel.revalidate(); // Refresh the panel
        calendarPanel.repaint(); // Repaint the panel
    }

    private void updateMonthDisplay() {
        monthLabel.setText(month.getMonthName()); // Update the month label
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ExpenseInsight frame = new ExpenseInsight();
            frame.setSize(800, 600);
            frame.setVisible(true);
        });
    }
}
