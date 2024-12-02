import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

public class BarGraph extends JFrame {

    private JTextField[] expenseFields;
    private JTextField[] incomeFields; // Changed from savingsFields to incomeFields
    private JButton generateGraphButton;

    public BarGraph() {
        setTitle("Monthly Finance Tracker");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(14, 3));

        expenseFields = new JTextField[12];
        incomeFields = new JTextField[12]; // Changed from savingsFields to incomeFields

        // Create input fields for each month
        for (int i = 0; i < 12; i++) {
            JLabel monthLabel = new JLabel(getMonthName(i) + " Expenses:");
            expenseFields[i] = new JTextField();
            JLabel incomeLabel = new JLabel(getMonthName(i) + " Income:"); // Changed label to Income
            incomeFields[i] = new JTextField(); // Changed from savingsFields to incomeFields

            add(monthLabel);
            add(expenseFields[i]);
            add(incomeLabel);
            add(incomeFields[i]); // Changed from savingsFields to incomeFields
        }

        generateGraphButton = new JButton("Generate Bar Graph");
        generateGraphButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateBarGraph();
            }
        });

        add(generateGraphButton);
    }

    private String getMonthName(int monthIndex) {
        return new java.text.DateFormatSymbols().getMonths()[monthIndex];
    }

    private void generateBarGraph() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        double totalExpenses = 0;
        double totalIncome = 0;

        for (int i = 0; i < 12; i++) {
            String month = getMonthName(i);
            String expenseText = expenseFields[i].getText();
            String incomeText = incomeFields[i].getText();

            double expense = 0;
            double income = 0;
            boolean hasData = false;

            // Check if there is any data for the month
            if (!expenseText.isEmpty()) {
                expense = Double.parseDouble(expenseText);
                dataset.addValue(expense, "Expenses", month);
                totalExpenses += expense;
                hasData = true; // Mark that there is data
            }

            if (!incomeText.isEmpty()) {
                income = Double.parseDouble(incomeText);
                dataset.addValue(income, "Income", month);
                totalIncome += income;
                hasData = true; // Mark that there is data
            }

            // Only add saved amount if there is data
            if (hasData) {
                double saved = income - expense;
                dataset.addValue(saved, "Saved", month);
            }
        }

        // Calculate total savings
        double totalSavings = totalIncome - totalExpenses;

        // Create the bar chart without a title
        JFreeChart chart = ChartFactory.createBarChart(
                "2024 Report", // Removed title
                "Month",
                "Amount",
                dataset
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        JFrame chartFrame = new JFrame();
        chartFrame.setTitle("Bar Graph");
        chartFrame.setSize(800, 600);
        chartFrame.setLayout(new BorderLayout());

        JPanel totalPanel = new JPanel();
        totalPanel.setLayout(new GridLayout(1, 3)); // Changed to 3 columns
        totalPanel.add(new JLabel("Total Expenses: " + String.format("%.2f", totalExpenses)));
        totalPanel.add(new JLabel("Total Income: " + String.format("%.2f", totalIncome)));
        totalPanel.add(new JLabel("Total Savings: " + String.format("%.2f", totalSavings))); // Added total savings

        chartFrame.add(totalPanel, BorderLayout.NORTH);
        chartFrame.add(chartPanel, BorderLayout.CENTER);
        chartFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BarGraph app = new BarGraph();
            app.setVisible(true);
        });
    }
}
