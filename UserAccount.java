import java.util.HashMap;

public class UserAccount {
    private User currentUser;
    private boolean isLoggedIn;

    // Static storage for user accounts (simulates a database)
    private static HashMap<String, User> userDatabase = new HashMap<>();

    // Constructor
    public UserAccount() {
        this.isLoggedIn = false;
    }

    // Method to create a new account
    public boolean createAccount(String username, String password) {
        if (userDatabase.containsKey(username)) {
            System.out.println("Username already exists.");
            return false;
        }
        User newUser = new User(username, password);
        userDatabase.put(username, newUser);
        System.out.println("Account created successfully.");
        return true;
    }

    // Method for user login
    public boolean login(String username, String password) {
        User user = userDatabase.get(username);
        if (user != null && user.getPassword().equals(password)) {
            this.currentUser = user;
            this.isLoggedIn = true;
            System.out.println("Login successful.");
            return true;
        } else {
            System.out.println("Invalid username or password.");
            return false;
        }
    }

    // Method for user logout
    public void logout() {
        if (isLoggedIn) {
            this.currentUser = null;
            this.isLoggedIn = false;
            System.out.println("Logged out successfully.");
        } else {
            System.out.println("No user is currently logged in.");
        }
    }

    // Getter for login status
    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    // Getter for current user's username
    public String getCurrentUsername() {
        return (isLoggedIn && currentUser != null) ? currentUser.getUsername() : null;
    }
}
