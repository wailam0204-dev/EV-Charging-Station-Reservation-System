package App;

import java.io.*;
import java.util.*;

public class UserManager {
    private final String FILE_NAME = "UserID.txt";

    public boolean registerUser(User user) { // receive methods from User class
        if (userExists(user.getUsername())) { // passes user's username to userExists method
            return false; // returns false when userExists method returns true
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME, true))) { // access the UserID.txt if no username found
            writer.println(user); // calls toString() method in User class to store user data
            return true; // returns true to confirm a successful registration
        } catch (IOException e) {
            System.out.println("Error saving user.");
        }
        return false; // return false after exception is found or any unexpected error occurs
    }
    
    public boolean userExists(String username) {
        try (Scanner scanner = new Scanner(new File(FILE_NAME))) { // reads data from UserID.txt file
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(","); // splits username, password and userType into 3 parts
                if (parts[0].equals(username)) { // if username is found in the file
                    return true; // return true
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("User file not found.");
        }
        return false; // or else return false
    }
    
    public boolean authenticateUser(String username, String password, UserType userType) {
        try (Scanner scanner = new Scanner(new File(FILE_NAME))) { // reads data from UserID.txt file
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(","); // splits username, password and userType into 3 parts
                if (parts.length == 3 && parts[0].equals(username) // check if format and all authentication data are correct
                        && parts[1].equals(password)
                        && parts[2].equals(userType.toString())) { // converts enum value to string to compare with the string value in UserID.txt file
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("User file not found.");
        }
        return false;
    }
    
    public User getUserByUsername(String username) {
        try (Scanner scanner = new Scanner(new File(FILE_NAME))) { // Reads data from UserID.txt file
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(","); // Splits username, password, and userType into 3 parts
                if (parts[0].equals(username)) { // If username matches
                    UserType userType = UserType.valueOf(parts[2]); // Convert string to UserType enum
                    return new User(parts[0], parts[1], userType); // Return the User object with the found data
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("User file not found.");
        }
        return null; // Return null if no matching user is found
    }

    

   
}
