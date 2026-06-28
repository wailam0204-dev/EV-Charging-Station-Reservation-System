package App;

public class User {
	// declarations
    private String username;
    private String password;
    private UserType userType;

    // constructor
    public User(String username, String password, UserType userType) {
        this.username = username;
        this.password = password;
        this.userType = userType;
    }
    
    // getter
    public String getUsername() { 
    	return username; 
    }
    
    public String getPassword() { 
    	return password; 
    }
  
    public UserType getUserType() { 
    	return userType; 
    }

    @Override
    public String toString() {
        return username + "," + password + "," + userType; // stores user data in format
    }
}
