import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextField userField, passField;
    private JButton loginButton, signinButton;
    private UserAccount userAccount; // Instance of UserAccount to manage users

    public LoginUI() {
        setTitle("Expense Insight");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        userAccount = new UserAccount(); // Initialize UserAccount

        // Create a label for the title and set a large font
        JLabel titleLabel = new JLabel("EXPENSE INSIGHT");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Set font size and style
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center the label

        // Initialize buttons and text fields
        loginButton = new JButton("Log In");
        signinButton = new JButton("Sign In");
        userField = new JTextField(20); // Set width of text field
        passField = new JTextField(20); // Set width of text field

        // Use GridLayout for aligning labels and text fields
        JPanel userInputField = new JPanel(); 
        userInputField.add(new JLabel("Username:"));
        userInputField.add(userField);
        
        JPanel passInputField = new JPanel();
        passInputField.add(new JLabel("Password:"));
        passInputField.add(passField);

        // Create a panel for the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout()); // Align buttons horizontally
        buttonPanel.add(loginButton);
        buttonPanel.add(signinButton);

        // Create a panel for the title and input fields
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(titleLabel, BorderLayout.NORTH); // Add title label to the top
        contentPanel.add(userInputField, BorderLayout.CENTER); // Add username field to the center
        contentPanel.add(passInputField, BorderLayout.SOUTH); // Add password field to the bottom

        // Add components to the frame
        add(contentPanel, BorderLayout.CENTER); // Add content panel to the center
        add(buttonPanel, BorderLayout.SOUTH); // Add button panel to the bottom
        pack();
        // Add action listener for the login button
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                userChecker();
            }
        });
        // add action listener for the signin button
        signinButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		registerUser();
        	}
        });
    }
    // checks the user and password if it is in the database
    public void userChecker() {
        String username = userField.getText(); // Get username from text field
        String password = passField.getText(); // Get password from text field

        // Call login method from UserAccount
        if (userAccount.login(username, password)) {
            // If login is successful, proceed to the next step (e.g., open another window)
            JOptionPane.showMessageDialog(this, "Login successful!");
            //  add code here to transition to main app
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.");
        }
    }

    // registers a new user into the database
    public void registerUser() {
    	String username = userField.getText();
    	String password = passField.getText();
    	
    	// Call create account method in UserAccount
        if (userAccount.createAccount(username, password)) {
            JOptionPane.showMessageDialog(this, "Account created successfully!");
            // Optionally, clear the text fields after registration
            userField.setText("");
            passField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Username already exists. Please choose a different username.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LoginUI().setVisible(true);
            }
        });
    }
}
