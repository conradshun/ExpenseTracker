import javax.swing.*;
import java.awt.*;

public class ExpenseDisplayUI {
    public static void main(String[] args) {

        //KURT PLS DO YOUR MAGIC HERE HAHAHAHAH
        
    	//CONNECT TO THE MONTH CLASS or something...
        // Create the main frame
        JFrame frame = new JFrame("Friday, Jan 13 2023");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setLayout(new GridLayout(3, 1, 10, 10)); // 3 rows, 1 column, with spacing

        // Set background color for the frame
        frame.getContentPane().setBackground(Color.DARK_GRAY);

        // Create panel for each input
        JPanel expensePanel = new JPanel(new BorderLayout());
        JPanel categoryPanel = new JPanel(new BorderLayout());
        JPanel limitPanel = new JPanel(new BorderLayout());

        // Set background color for each panel
        Color panelColor = new Color(210, 200, 200);
        expensePanel.setBackground(panelColor);
        categoryPanel.setBackground(panelColor);
        limitPanel.setBackground(panelColor);

        // Expense input
        JLabel expenseLabel = new JLabel(" EXPENSE:");
        JTextField expenseField = new JTextField();
        expensePanel.add(expenseLabel, BorderLayout.WEST);
        expensePanel.add(expenseField, BorderLayout.CENTER);

        // Category input
        JLabel categoryLabel = new JLabel(" CATEGORY:");
        JTextField categoryField = new JTextField();
        categoryPanel.add(categoryLabel, BorderLayout.WEST);
        categoryPanel.add(categoryField, BorderLayout.CENTER);

        // Set limit input
        JLabel limitLabel = new JLabel(" SET LIMIT:");
        JTextField limitField = new JTextField();
        limitPanel.add(limitLabel, BorderLayout.WEST);
        limitPanel.add(limitField, BorderLayout.CENTER);

        // Add panels to the frame
        frame.add(expensePanel);
        frame.add(categoryPanel);
        frame.add(limitPanel);

        // Display the frame
        frame.setVisible(true);
    }
}
