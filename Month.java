class Month {

    private static String monthNames[] = {
        "January", "February", "March", 
        "April", "May", "June", "July", 
        "August", "September", "October", 
        "November", "December"};

    private int monthNumber;

    public Month(int n) {

        if (n < 1 || n > 12) {
            monthNumber = 1;
        } else {
            monthNumber = n;
        }
    }

    public String getMonthName() {
        return monthNames[monthNumber - 1];
    }

    public int getMonthNumber() {
        return monthNumber;
    }

    public int getDaysInMonth() {
        // Return the number of days in the month
        switch (monthNumber) {
            case 1: case 3: case 5: case 7: case 8: case 10: case 12: // 31 days
                return 31;
            case 4: case 6: case 9: case 11: // 30 days
                return 30;
            case 2: // February (assuming non-leap year for simplicity)
                return 29;
            default:
                return 30; // Default case (should not happen)
        }
    }

    public void next() {
        if (monthNumber == 12) {
            monthNumber = 1; // Wrap around to January
        } else {
            monthNumber++;
        }
    }

    public void previous() {
        if (monthNumber == 1) {
            monthNumber = 12; // Wrap around to December
        } else {
            monthNumber--;
        }
    }
    
    @Override
    public String toString() {
        return getMonthName();
    }
}
