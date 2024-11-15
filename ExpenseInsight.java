import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Calendar;
import javax.swing.border.Border;

// holds the expenses
class DayExpense {
    private int expense;

    public DayExpense() {
        this.expense = 0;
    }

    public void addExpense(int amount) {
        this.expense += amount;
    }

    public int getExpense() {
        return this.expense;
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

    public ExpenseInsight() {
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
        topPanel.setBackground(new Color(0, 0, 64));
        month = new Month(1);
        monthLabel = new JLabel(month.getMonthName());
        monthLabel.setForeground(new Color(255, 255, 255));
        monthLabel.setBackground(new Color(255, 255, 255));
        budgetLabel = new JLabel("BUDGET: " + BUDGET);
        budgetLabel.setForeground(new Color(255, 255, 255));
        expenseLabel = new JLabel("EXPENSE: " + EXPENSE);
        expenseLabel.setForeground(new Color(255, 255, 255));
        totalLabel = new JLabel("BUDGET LEFT: " + TOTAL);
        totalLabel.setForeground(new Color(255, 255, 255));
        
        // Add buttons for navigating months
        previousButton = new JButton("Previous");
        previousButton.setBackground(new Color(0, 255, 255));
        nextButton = new JButton("Next");
        nextButton.setBackground(new Color(0, 255, 255));
        
        topPanel.add(previousButton);
        topPanel.add(monthLabel);
        topPanel.add(budgetLabel);
        topPanel.add(expenseLabel);
        topPanel.add(totalLabel);
        topPanel.add(nextButton); // Ensure next button is added to the panel
        getContentPane().add(topPanel, BorderLayout.NORTH);

     // Create and add the left panel for buttons
        JPanel leftPanel = new JPanel(new GridLayout(5, 1, 0, 10)); // Changed to 5 rows to accommodate the new button
        leftPanel.setBackground(new Color(0, 0, 64));
        JButton logoutButton = new JButton("LOGOUT");
        logoutButton.setBackground(new Color(0, 255, 255));
        setLimitButton = new JButton("SET LIMIT");
        setLimitButton.setBackground(new Color(0, 255, 255));
        JButton addBudgetButton = new JButton("ADD BUDGET");
        addBudgetButton.setBackground(new Color(0, 255, 255));
        JButton annualReportButton = new JButton("ANNUAL REPORT");
        annualReportButton.setBackground(new Color(0, 255, 255));
        JButton addExpenseButton = new JButton("ADD EXPENSE");
        leftPanel.add(logoutButton);
        leftPanel.add(setLimitButton);
        leftPanel.add(addBudgetButton); // Add Budget button
        leftPanel.add(annualReportButton);
        getContentPane().add(leftPanel, BorderLayout.WEST);
        
        // Create and add the calendar panel
        calendarPanel = new JPanel(new GridLayout(6, 7));
        calendarPanel.setBackground(new Color(0, 0, 64));
        dayButtons = new HashMap<>();
        createCalendar();
        getContentPane().add(calendarPanel, BorderLayout.CENTER);
        
        // Action listener for the Add Budget button
        addBudgetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = JOptionPane.showInputDialog("Enter budget amount:");
                if (input != null) {
                    try {
                        int budgetAmount = Integer.parseInt(input);
                        BUDGET += budgetAmount; // Add the input amount to the existing budget
                        budgetLabel.setText("BUDGET: " + BUDGET); // Update the budget label
                        TOTAL = BUDGET - EXPENSE; // Update total
                        totalLabel.setText("TOTAL: " + TOTAL); // Update the total label
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Invalid input. Please enter a number.");
                    }
                }
            }
        });
        
        // set action listener for the adding expense button
        addExpenseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = JOptionPane.showInputDialog("Enter expense amount:");
                if (input != null) {
                    try {
                        int expenseAmount = Integer.parseInt(input);
                        addExpense(expenseAmount); // Call the method to add expense
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Invalid input. Please enter a number.");
                    }
                }
            }
        });
        
     // Set action listener for the Set Limit button
        setLimitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (BUDGET == 0) {
                    JOptionPane.showMessageDialog(null, "Please set a budget first before setting a limit.");
                    return; // Exit if budget is 0
                }
                
                String input;
                int limitAmount;
                do {
                    input = JOptionPane.showInputDialog("Set your limit (must be less than or equal to current budget of " + BUDGET + "):");
                    if (input == null) {
                        return; // Exit if the dialog is closed
                    }
                    try {
                        limitAmount = Integer.parseInt(input);
                        if (limitAmount > BUDGET) {
                            JOptionPane.showMessageDialog(null, "Limit must be less than or equal to your current budget.");
                        } else {
                            limit = limitAmount; // Set the limit if valid
                            JOptionPane.showMessageDialog(null, "Limit set to: " + limit);
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Invalid input. Please enter a number.");
                        limitAmount = -1; // Reset to trigger loop again
                    }
                } while (limitAmount > BUDGET || limitAmount < 0); // Repeat until valid limit is set
            }
        });
        
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create and show the LoginUI
                LoginUI loginUI = new LoginUI();
                loginUI.frame.setVisible(true);
                // Close the ExpenseInsight frame
                dispose(); // This will close the current ExpenseInsight frame
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

        // Add labels for the days of the week with boxes around them
        String[] dayNames = {"Sunday", "Monday", "Tueday", "Wedday", "Thursday", "Friday", "Saturday"};
        for (String dayName : dayNames) {
            JPanel dayPanel = new JPanel(); // Create a panel for each day name
            dayPanel.setLayout(new BorderLayout()); // Set layout to BorderLayout
            
            JLabel dayLabel = new JLabel(dayName, SwingConstants.CENTER); // Center align the text
            dayPanel.add(dayLabel, BorderLayout.CENTER); // Add label to the center of the panel

            // Create a border around the panel
            Border border = BorderFactory.createLineBorder(Color.BLACK); // Black border
            dayPanel.setBorder(border); // Set the border to the panel

            calendarPanel.add(dayPanel); // Add the panel to the calendar panel
        }

        // Get the number of days in the month
        int daysInMonth = month.getDaysInMonth();
        
        // Get the first day of the month (1=Sunday, 2=Monday, ..., 7=Saturday)
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month.getMonthNumber() - 1); // Month is 0-indexed
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); // 1=Sunday, 2=Monday, ..., 7=Saturday

        // Adjust the first day of the week to start on Monday, fixing December issue
        int adjustedFirstDay = (firstDayOfWeek == Calendar.MONDAY) ? 1 : firstDayOfWeek - 2;
        if (adjustedFirstDay < 0) {
            adjustedFirstDay = 0; // Ensure adjustedFirstDay is not negative
        }

        // Add empty labels for days before the first day of the month
        for (int i = 0; i < adjustedFirstDay; i++) {
            JLabel emptyLabel = new JLabel(""); // Create an empty label
            calendarPanel.add(emptyLabel); // Add it to the calendar panel
        }

        // Create buttons for each day of the month
        for (int day = 1; day <= daysInMonth; day++) {
            final int finalDay = day;
            JButton dayButton = new JButton(String.valueOf(day));
            dayButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Show a dialog with the option to add an expense
                    JPanel expensePanel = new JPanel();
                    expensePanel.add(new JLabel("Enter expense for " + finalDay + " " + month.getMonthName() + ":"));
                    
                    JTextField expenseField = new JTextField(10);
                    expensePanel.add(expenseField);
                    
                    int result = JOptionPane.showConfirmDialog(null, expensePanel, "Add Expense", JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        try {
                            int expenseAmount = Integer.parseInt(expenseField.getText());
                            dayExpenses[finalDay - 1].addExpense(expenseAmount); // Store expense for the day
                            addExpense(expenseAmount); // Update total expenses
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(null, "Invalid input. Please enter a number.");
                        }
                    }
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
    
    private void addExpense(int amount) {
        EXPENSE += amount; // Increase the total expenses
        totalLabel.setText("TOTAL: " + (BUDGET - EXPENSE)); // Update the total label

        // Check if the expenses exceed the limit
        if (EXPENSE > limit) {
            JOptionPane.showMessageDialog(null, "Warning: Your expenses have exceeded the limit of " + limit + "!");
        }

        // Update the expense label
        expenseLabel.setText("EXPENSE: " + EXPENSE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ExpenseInsight frame = new ExpenseInsight();
            frame.setSize(800, 600);
            frame.setVisible(true);
        });
    }
}

