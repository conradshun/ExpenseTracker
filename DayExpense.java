import java.util.HashMap;
import java.util.Map;

// Holds the expenses for a specific day
class DayExpense {
    private Map<String, Integer> expenses;

    public DayExpense() {
        this.expenses = new HashMap<>();
    }

    public void addExpense(String category, int amount) {
        expenses.put(category, expenses.getOrDefault(category, 0) + amount);
    }

    public Map<String, Integer> getExpenses() {
        return expenses;
    }
}