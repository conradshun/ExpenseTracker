import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginUI extends JFrame{
	private static final long serialVersionUID = 1L;
	public JFrame frame;
    private JTextField userField, passField;
    private JButton loginButton, signinButton;
    private UserAccount userAccount; // Instance of UserAccount to manage users

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    LoginUI window = new LoginUI();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public LoginUI() {
        userAccount = new UserAccount(); // Initialize UserAccount
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBackground(new Color(0, 0, 160));
        frame.setTitle("Expense Insight");
        frame.setBounds(100, 100, 350, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());

        // Create a label for the title and set a large font
        JLabel titleLabel = new JLabel("EXPENSE INSIGHT");
        titleLabel.setBackground(new Color(0, 0, 160));
        titleLabel.setFont(new Font("Sitka Display", Font.BOLD | Font.ITALIC, 24)); // Set font size and style
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center the label

        // Initialize buttons and text fields
        loginButton = new JButton("Log In");
        loginButton.setBackground(new Color(0, 255, 255));
        signinButton = new JButton("Sign In");
        signinButton.setBackground(new Color(0, 255, 255));
        userField = new JTextField(20); // Set width of text field

        // Use GridLayout for aligning labels and text fields
        JPanel userInputField = new JPanel(); 
        userInputField.setBackground(new Color(0, 0, 160));
        JLabel label = new JLabel("Username:");
        label.setForeground(new Color(255, 255, 255));
        userInputField.add(label);
        userInputField.add(userField);

        // Create a panel for the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(0, 0, 160));
        buttonPanel.setLayout(new FlowLayout()); // Align buttons horizontally
        buttonPanel.add(loginButton);
        buttonPanel.add(signinButton);

        // Create a panel for the title and input fields
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(titleLabel, BorderLayout.NORTH); // Add title label to the top
        contentPanel.add(userInputField, BorderLayout.CENTER);
        passField = new JTextField(20); // Set width of text field
        
        JPanel passInputField = new JPanel();
        passInputField.setBackground(new Color(0, 0, 160));
        userInputField.add(passInputField);
        JLabel label_1 = new JLabel("Password:");
        label_1.setForeground(new Color(255, 255, 255));
        passInputField.add(label_1);
        passInputField.add(passField);

        // Add components to the frame
        frame.getContentPane().add(contentPanel, BorderLayout.CENTER);
        frame.getContentPane().add(buttonPanel, BorderLayout.SOUTH); // Add button panel to the bottom

        // Add action listener for the login button
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                userChecker();
            }
        });
        
        signinButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                registerUser ();
            }
        });
    }

    // checks the username and password if it is in the database
    public void userChecker() {
        String username = userField.getText().trim(); // Get username and trim whitespace
        String password = passField.getText().trim(); // Get password and trim whitespace

        // Check for empty fields
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Username and password cannot be blank.");
            return; // Exit the method if validation fails
        }

        // Call login method from UserAccount
        if (userAccount.login(username, password)) {
            JOptionPane.showMessageDialog(frame, "Login successful!");
            
            // Create and show ExpenseInsight application
            ExpenseInsight expenseInsightApp = new ExpenseInsight();
            expenseInsightApp.setSize(800, 600); // Set the size of the ExpenseInsight frame
            expenseInsightApp.setVisible(true); // Make it visible
            
            // Close the LoginUI frame
            frame.dispose(); 
        } else {
            JOptionPane.showMessageDialog(frame, "Invalid username or password.");
        }
    }

    // registers a new user into the database
    public void registerUser () {
        String username = userField.getText().trim(); // Get username and trim whitespace
        String password = passField.getText().trim(); // Get password and trim whitespace
        
        // Check for empty fields
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Username and password cannot be blank.");
            return; // Exit the method if validation fails
        }

        // Call create account method in UserAccount
        if (userAccount.createAccount(username, password)) {
            JOptionPane.showMessageDialog(frame, "Account created successfully !");
            // Optionally, clear the text fields after registration
            userField.setText("");
            passField.setText("");
        } else {
            JOptionPane.showMessageDialog(frame, "Username already exists. Please choose a different username.");
        }
    }
}
