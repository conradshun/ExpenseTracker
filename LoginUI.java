import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A simple login interface integrated with the UserAccount system for authentication.
 */
public class LoginUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginUI() {
        // Set up the frame
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 250);
        setLayout(new BorderLayout());

        // Create UI components
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login");
        JButton signUpButton = new JButton("Sign Up");

        inputPanel.add(usernameLabel);
        inputPanel.add(usernameField);
        inputPanel.add(passwordLabel);
        inputPanel.add(passwordField);

        // Add buttons to the panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);
        buttonPanel.add(signUpButton);

        // Add components to the frame
        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners
        loginButton.addActionListener(new LoginAction());
        signUpButton.addActionListener(new SignUpAction());

        // Display the frame
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * ActionListener for the login button.
     */
    private class LoginAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            // Authenticate the user
            if (UserAccount.authenticate(username, password)) {
                JOptionPane.showMessageDialog(LoginUI.this, "Login successful!");
                // Proceed to the next screen or application dashboard
            } else {
                JOptionPane.showMessageDialog(LoginUI.this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * ActionListener for the sign-up button.
     */
    private class SignUpAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = JOptionPane.showInputDialog(LoginUI.this, "Enter a new username:");
            if (username == null || username.trim().isEmpty()) {
                JOptionPane.showMessageDialog(LoginUI.this, "Username cannot be empty.");
                return;
            }

            if (UserAccount.usernameExists(username)) {
                JOptionPane.showMessageDialog(LoginUI.this, "Username already exists. Please choose another one.");
                return;
            }

            String password = JOptionPane.showInputDialog(LoginUI.this, "Enter a new password:");
            if (password == null || password.trim().isEmpty()) {
                JOptionPane.showMessageDialog(LoginUI.this, "Password cannot be empty.");
                return;
            }

            // Save the new user account
            UserAccount newUser = new UserAccount(username, password);
            if (newUser.save()) {
                JOptionPane.showMessageDialog(LoginUI.this, "Account created successfully! You can now log in.");
            } else {
                JOptionPane.showMessageDialog(LoginUI.this, "Failed to create account. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        // Launch the login interface
        SwingUtilities.invokeLater(LoginUI::new);
    }
}
