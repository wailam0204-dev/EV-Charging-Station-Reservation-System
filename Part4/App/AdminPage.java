package App;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

public class AdminPage {
    private SlotManager slotManager; //Allows admin to update slot status
    private Scanner input; // to get user input

    public AdminPage() { // a constructor to create objects for both classes
        slotManager = new SlotManager();
        input = new Scanner(System.in);
    }

    public void adminMenu() {
        boolean keepGoing = true; // keep the menu running until 3.Log Out is chosen
        String choice; // stores user input

        while (keepGoing) { // stays true until 3. Log Out is chosen
            do { 
            	//displays the menu
                System.out.println("\n=== Admin Page ===");
                System.out.println("1. Change slot status");
                System.out.println("2. Generate report");
                System.out.println("3. Log Out");
                System.out.print("Please enter your choice: ");

                choice = input.nextLine(); // stores user input

                if (!(choice.equals("1") || choice.equals("2") || choice.equals("3"))) // data validation
                {
                    System.out.println("Invalid input.");
                }
            } while (!(choice.equals("1") || choice.equals("2") || choice.equals("3")));

            switch (Integer.parseInt(choice)) { // parses String to integer
                case 1:
                    changeSlotStatus(); // direct to changeSlotStatus method
                    break;
                case 2:
                    generateReport(); // direct to generateReport method
                    break;
                case 3:
                    System.out.println("Exiting...");
                    keepGoing = false; // terminates the while loop above
                    break;
                default:
                    System.out.println("Invalid input."); // default message for invalid inputs
            }
        }
    }

    private void changeSlotStatus() {
        String slotNumber, newStatus;
        final String separator = "--------------------------------------"; // a line to tidy up the output screen

        do {
        	// gets the slot number to be updated
            System.out.print("Enter slot number to update (1-4): "); 
            slotNumber = input.nextLine();

            // data validation
            if (!slotNumber.matches("[1-4]")) {
                System.out.println("\n" + separator); // uses the separator to keep the screen clean
                System.out.println("Invalid Input! Please enter slot number (1-4)");
            }
        } while (!slotNumber.matches("[1-4]"));

        do {
        	// gets the new availability
            System.out.print("Enter new availability (Available/Unavailable): ");
            newStatus = input.nextLine();

            // data validation
            if (!newStatus.equalsIgnoreCase("Available") && !newStatus.equalsIgnoreCase("Unavailable")) {
                System.out.println("\n" + separator); // uses the separator to keep the screen clean
                System.out.println("Invalid Input! Please enter (Available/Unavailable)");
            }
        } while (!newStatus.equalsIgnoreCase("Available") && !newStatus.equalsIgnoreCase("Unavailable"));

        // updates the corresponding slot status
        if (slotManager.updateSlotStatus(slotNumber, newStatus)) {
            System.out.println("Slot availability updated successfully.");
        } else {
            System.out.println("Failed to update slot."); // will only be printed if false (fails to load file)
        }
    }

    private void generateReport() { // shows the availability of slots and customer bookings
        try (Scanner fileScanner = new Scanner(new File("Slots & Availability.txt"))) 
        {
        	// Header
            System.out.printf("\n%-10s | %-15s%n", "Slot", "Availability");
            System.out.println("------------------------------");

            while (fileScanner.hasNextLine()) 
            {
            	//reads the file
                String line = fileScanner.nextLine();
                String[] parts = line.split(","); // splits "slot number" & "availability" into two
                if (parts.length != 2) continue;  // skips the rest of the loops if format is wrong
                
                // assign data read into variables
                String slotNumber = parts[0];
                String status = parts[1];

                System.out.printf("%-10s | %-15s%n", slotNumber, status); // prints the current availability of the slot
            }
        } catch (IOException exc) {
            System.out.println("Error generating slot report: " + exc.getMessage());
        }

        System.out.println("\n=========== Booking Dates Available ==========="); // Header
        BookingManager bm = new BookingManager(); 
        List<Booking> bookings = bm.getAllBookings(); // returns the booking array list

        // returns back to adminMenu if there's no booking at all
        if (bookings.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }
        
        // search for dates in Bookings.txt and print once for each different date found
        Set<String> uniqueDates = new TreeSet<>();
        for (Booking b : bookings) {
            uniqueDates.add(b.getDate());
        }

        for (String date : uniqueDates) {
            System.out.println("- " + date);
        }
        System.out.println("- ALL (view all bookings)"); // allows admin to view all bookings without being filtered by dates

        System.out.print("\nEnter a date from above to view bookings:");
        String choice = input.nextLine().trim();

        //display all bookings without being filtered by dates
        if (choice.equalsIgnoreCase("ALL")) {
            displayBookings(bookings);
            return; // returns back to adminMenu
        }

        //data validation
        if (!choice.matches("\\d{4}-\\d{2}-\\d{2}")) {
            System.out.println("Invalid input. Please enter a valid date in YYYY-MM-DD format.");
            return; // returns back to adminMenu
        }
        
        // Validate if the date exists using LocalDate
        try {
            LocalDate parsedDate = LocalDate.parse(choice);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date.");
            return;
        }
        
        // get all bookings filtered by date
        List<Booking> filtered = new ArrayList<>();
        for (Booking b : bookings) {
            if (b.getDate().equals(choice)) {
                filtered.add(b);
            }
        }
        
        if (filtered.isEmpty()) { // data validation
            System.out.println("No bookings found on " + choice + ".");
        } else {
            displayBookings(filtered); // display all bookings filtered by date
        }
    }

    private void displayBookings(List<Booking> bookings) {
    	// header with formating
        System.out.println("\n================= Booking Details =================");
        System.out.printf("%-20s | %-6s | %-12s | %-6s%n", "Customer Name", "Slot", "Date", "Time");
        System.out.println("---------------------------------------------------");

        for (Booking b : bookings) {
        	// display bookings
            System.out.printf("%-20s | %-6s | %-12s | %-6s%n", b.getUsername(), b.getSlotNumber(), b.getDate(), b.getTime());
        }
    }

}