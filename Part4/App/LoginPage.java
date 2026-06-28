package App;

import java.util.Scanner;

public class LoginPage { 
	// class attributes
    private AdminPage adminPage;
    private UserManager userManager;
    private Scanner input;

    public LoginPage() { 
    	// initializing the class attributes which can be reused anywhere in this class
    	adminPage = new AdminPage();
        userManager = new UserManager();
        input = new Scanner(System.in);
    }

    public void accountMenu(UserType userType) { // receiving user's identity
        boolean running = true;
        String choice;
        while (running) {
        	do {
            System.out.println("\n=== Welcome to the EV Charging Reservation System ===");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Back");
            System.out.print("Select your action: ");
            choice = input.nextLine();
            
            if (!(choice.equals("1") || choice.equals("2") || choice.equals("3"))) 
            {
                System.out.println("Invalid input.");
            }
            
        	} while(!(choice.equals("1") || choice.equals("2") || choice.equals("3")));

            switch (Integer.parseInt(choice)) {
                case 1:
                    login(userType); // login based on user's type
                    break;
                case 2:
                    register(userType); // register based on user's type
                    break;
                case 3:
                    running = false; // terminates the while loop
                    break;
                default:
                    System.out.println("Invalid input.");
            }
        }
    }

    private void login(UserType userType) { // get user's username and password
        System.out.println("\nLogin as " + userType);
        System.out.print("Enter username: ");
        String username = input.nextLine();
        System.out.print("Enter password: ");
        String password = input.nextLine();

        if (userManager.authenticateUser(username, password, userType)) { // authenticate user
            System.out.println("Login successful!");
            User currentUser = userManager.getUserByUsername(username); // Get the User object by username
            
            if (userType == UserType.ADMIN) { // direct to admin's page
                adminPage.adminMenu();
            }
            else { // direct to customer page
            	CustomerPage customerPage = new CustomerPage(currentUser);
             	customerPage.customerMenu(); 
            }
        } else {
            System.out.println("Invalid credentials."); // show error if user authentication failed
        }
    }

    private void register(UserType userType) { //register user
        System.out.println("\nRegistering as " + userType);
        System.out.print("Enter username: ");
        String username = input.nextLine();
        System.out.print("Enter password: ");
        String password = input.nextLine();

        User newUser = new User(username, password, userType); // use User class to store user's input
        if (userManager.registerUser(newUser)) { // pass user's inputs to registerUser in userManager
            System.out.println("Registration successful!");
        } else {
            System.out.println("Username already exists.");
        }
    }
}
