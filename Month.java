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
        return monthNumber[monthNumber - 1];
    }

    @Override
    public String toString() {
        return getMonthName();
    }
}