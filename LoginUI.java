import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class LoginUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private ExpenseTracker expenseTracker;

    public LoginUI() {
        expenseTracker = new ExpenseTracker();

        setTitle("Expense Insight");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel with dark teal background
        JPanel panel = new JPanel();
        panel.setLayout(null); // Using absolute positioning for exact layout
        panel.setBackground(new Color(0, 128, 128)); // Teal background

        // Title label
        JLabel titleLabel = new JLabel("EXPENSE INSIGHT");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(100, 30, 250, 30);
        panel.add(titleLabel);

        // Username label and field
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setBounds(50, 80, 100, 25);
        panel.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(50, 105, 300, 25);
        panel.add(usernameField);

        // Password label and field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setBounds(50, 140, 100, 25);
        panel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(50, 165, 300, 25);
        panel.add(passwordField);

        // Buttons
        loginButton = new JButton("Log In");
        loginButton.setBounds(90, 210, 100, 30);
        loginButton.setBackground(new Color(200, 200, 200));
        panel.add(loginButton);

        registerButton = new JButton("Register");
        registerButton.setBounds(210, 210, 100, 30);
        registerButton.setBackground(new Color(200, 200, 200));
        panel.add(registerButton);

        add(panel);
        // creates the login button and listener
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                try {
                    if (expenseTracker.authenticateUser(username, password)) {
                        int userId = expenseTracker.getUserId(username);
                        JOptionPane.showMessageDialog(LoginUI.this, "Login successful!");
                        LoginUI.this.dispose();
                        new ExpenseInsight(userId).setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(LoginUI.this, "Invalid username or password.");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(LoginUI.this, "Error during login: " + ex.getMessage());
                }
            }
        });
        // creates the register button and listener
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                // error handling if fields are empty
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginUI.this, "Username and password cannot be empty", "Registration Failed", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                try {
                    if (expenseTracker.registerUser(username, password)) {
                        JOptionPane.showMessageDialog(LoginUI.this, "Registration successful! You can now log in.");
                    } else {
                        JOptionPane.showMessageDialog(LoginUI.this, "Username already exists", "Registration Failed", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(LoginUI.this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LoginUI().setVisible(true);
            }
        });
    }
}
