public class User{

    private String username;
    private String password;

    /*
        Constructor for User objects, and initializes username a
        and password
    */
    public User (String username, String password){
        this.username = username;
        this.password = password;
    }
    /*
        Method to get username
        @param none
        @returns string username
    */
    public String getUsername(){
        return username;
    }
     /*
        Method to get password
        @param none
        @returns string password
    */
    public String getPassword(){
        return password;
    }
}