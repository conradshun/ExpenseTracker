import javax.swing.*;
import java.awt.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.renderer.category.BarRenderer;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * The BarGraph class creates a graphical representation of monthly financial data.
 * It extends JFrame to create a window displaying the bar graph.
 */
public class BarGraph extends JFrame {

  private static final long serialVersionUID = 1L;
  private Map<String, Integer> monthlyExpenses;
  private Map<String, Integer> monthlyIncome;

  /**
   * Constructor for the BarGraph class.
   * 
   * @param monthlyExpenses A map of monthly expenses
   * @param monthlyIncome A map of monthly income
   */
  public BarGraph(Map<String, Integer> monthlyExpenses, Map<String, Integer> monthlyIncome) {
      this.monthlyExpenses = monthlyExpenses;
      this.monthlyIncome = monthlyIncome;

      setTitle("Monthly Finance Tracker");
      setSize(800, 600);
      setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      setLayout(new BorderLayout());

      generateBarGraph();
  }

  /**
   * Generates the bar graph using the monthly expense and income data.
   * This method creates the chart, configures its appearance, and adds it to the frame.
   */
  private void generateBarGraph() {
      DefaultCategoryDataset dataset = new DefaultCategoryDataset();
      double totalExpenses = 0;
      double totalIncome = 0;

      List<String> months = new ArrayList<>();
      Collections.addAll(months, "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");

      // Populate the dataset with monthly financial data
      for (String month : months) {
          double expense = monthlyExpenses.getOrDefault(month, 0);
          double income = monthlyIncome.getOrDefault(month, 0);

          dataset.addValue(expense, "Expenses", month);
          dataset.addValue(income, "Income", month);

          totalExpenses += expense;
          totalIncome += income;

          double saved = income - expense;
          dataset.addValue(saved, "Saved", month);
      }

      double totalSavings = totalIncome - totalExpenses;

      // Create the chart
      JFreeChart chart = ChartFactory.createBarChart(
              "2024 Finance Report",
              "Month",
              "Amount",
              dataset,
              PlotOrientation.VERTICAL,
              true,
              true,
              false
      );

      // Customize the chart appearance
      Plot plot = chart.getPlot();
      plot.setBackgroundPaint(Color.WHITE);

      chart.setBackgroundPaint(new Color(172, 217, 230));

      CategoryPlot categoryPlot = chart.getCategoryPlot();
      BarRenderer renderer = (BarRenderer) categoryPlot.getRenderer();
      renderer.setSeriesPaint(0, new Color(254, 214, 0));  // Expenses color
      renderer.setSeriesPaint(1, new Color(0, 128, 128));  // Income color
      renderer.setSeriesPaint(2, new Color(34, 139, 34));  // Savings color

      // Create a chart panel and add it to the frame
      ChartPanel chartPanel = new ChartPanel(chart);

      // Create a panel to display totals
      JPanel totalPanel = new JPanel();
      totalPanel.setLayout(new GridLayout(1, 3));
      totalPanel.add(new JLabel("Total Expenses: " + String.format("%.2f", totalExpenses)));
      totalPanel.add(new JLabel("Total Income: " + String.format("%.2f", totalIncome)));
      totalPanel.add(new JLabel("Total Savings: " + String.format("%.2f", totalSavings)));

      // Add the totals panel and chart panel to the frame
      add(totalPanel, BorderLayout.NORTH);
      add(chartPanel, BorderLayout.CENTER);
  }
}
