package App;

import java.util.Scanner;

public class MainPage {
    public void display() {
        Scanner input = new Scanner(System.in);
        LoginPage loginPage = new LoginPage();

        boolean running = true;
        String choice;
        
        while (running) {
        	do {
        		// Main menu
        		System.out.println("\nLogging in as:");
        		System.out.println("1. Admin");
            	System.out.println("2. Customer");
            	System.out.println("3. Exit");
            	System.out.print("Select your role: ");
            	choice = input.nextLine();
            	
            	// data validation
            	if (!(choice.equals("1") || choice.equals("2") || choice.equals("3"))) 
            	{
            		System.out.println("Invalid input.");
            	}
            
        	} while(!(choice.equals("1") || choice.equals("2") || choice.equals("3")));

        switch (Integer.parseInt(choice)) {
                case 1:
                    loginPage.accountMenu(UserType.ADMIN); // passing user's admin identity to accountMenu
                    break;
                case 2:
                    loginPage.accountMenu(UserType.CUSTOMER); // passing user's customer identity to accountMenu
                    break;
                case 3:
                    running = false; // terminate switch case
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid input.");
            }
        }
    }

    // THIS IS THE MAIN METHOD - Entry point
public static void main(String[] args) {
    MainPage mainPage = new MainPage();
    mainPage.display();
}
}
