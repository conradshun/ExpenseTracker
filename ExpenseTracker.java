import java.util.HashMap;
import java.util.Map;

public class ExpenseTracker {
    private Map<String, Budget> budgets;
    private Map<String, Map<String, Integer>> expenses;
    private Map<String, Map<String, Integer>> income;

    public ExpenseTracker() {
        budgets = new HashMap<>();
        expenses = new HashMap<>();
        income = new HashMap<>();
    }
    
    // delete if unnecessary, the idea is that its kinda like a backup data
    // Method to load data from memory if needed (currently not implemented)
    public void loadData() {
        // This method would load data from memory if needed
    }

    // Method to retrieve the budget for a specific month
    public int getBudget(String month) {
        return budgets.getOrDefault(month, new Budget()).getBudget();
    }

    // Method to retrieve the limit for a specific month
    public int getLimit(String month) {
        return budgets.getOrDefault(month, new Budget()).getLimit();
    }

    // Method to retrieve all expenses for a specific month
    public Map<String, Integer> getExpenses(String month) {
        return expenses.getOrDefault(month, new HashMap<>());
    }

    // Method to retrieve the total expenses for a specific month
    public int getTotal(String month) {
        int total = 0;
        for (int amount : getExpenses(month).values()) {
            total += amount;
        }
        return total;
    }

    // Method to update the budget for a specific month
    public void setBudget(String month, int budget) {
        Budget budgetObject = budgets.getOrDefault(month, new Budget());
        budgetObject.setBudget(budget);
        budgets.put(month, budgetObject);
    }

    // Method to update the limit for a specific month
    public void setLimit(String month, int limit) {
        Budget budgetObject = budgets.getOrDefault(month, new Budget());
        budgetObject.setLimit(limit);
        budgets.put(month, budgetObject);
    }

    // Method to get Annual Expenses
    public Map<String, Integer> getAnnualExpenses() {
        Map<String, Integer> annualExpenses = new HashMap<>();
        for (Map<String, Integer> monthExpenses : expenses.values()) {
            for (Map.Entry<String, Integer> entry : monthExpenses.entrySet()) {
                String category = entry.getKey();
                int amount = entry.getValue();
                annualExpenses.put(category, annualExpenses.getOrDefault(category, 0) + amount);
            }
        }
        return annualExpenses;
    }

    // Method to get Annual Income
    public Map<String, Integer> getAnnualIncome() {
        Map<String, Integer> annualIncome = new HashMap<>();
        for (Map<String, Integer> monthIncome : income.values()) {
            for (Map.Entry<String, Integer> entry : monthIncome.entrySet()) {
                String category = entry.getKey();
                int amount = entry.getValue();
                annualIncome.put(category, annualIncome.getOrDefault(category, 0) + amount);
            }
        }
        return annualIncome;
    }

    // Method to add an expense for a specific month and category
    public void addExpense(String month, String category, int amount) {
        Map<String, Integer> monthExpenses = expenses.getOrDefault(month, new HashMap<>());
        monthExpenses.put(category, monthExpenses.getOrDefault(category, 0) + amount);
        expenses.put(month, monthExpenses);
    }

    // Method to add an income for a specific month and category
    public void addIncome(String month, String category, int amount) {
        Map<String, Integer> monthIncome = income.getOrDefault(month, new HashMap<>());
        monthIncome.put(category, monthIncome.getOrDefault(category, 0) + amount);
        income.put(month, monthIncome);
    }

    // Method to close the data structure
    public void close() {
        // No need to close anything in this case
    }

    private static class Budget {
        private int budget;
        private int limit;

        public Budget() {
            this.budget = 0;
            this.limit = 0;
        }

        public int getBudget() {
            return budget;
        }

        public void setBudget(int budget) {
            this.budget = budget;
        }

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }
    }
}
