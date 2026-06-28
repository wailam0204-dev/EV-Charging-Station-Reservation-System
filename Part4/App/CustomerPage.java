package App;

import java.io.*;
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class CustomerPage {
    private Scanner input = new Scanner(System.in); // For user input
    private User currentUser; // Stores the currently logged-in user
    private BookingManager bookingManager = new BookingManager(); // Handles booking-related actions

    // Constructor to assign the logged-in user
    public CustomerPage(User currentUser) {
        this.currentUser = currentUser;
    }

    // Main customer menu loop
    public void customerMenu() {
        String choice;
        boolean running = true;

        while (running) {
            // Input validation loop
            do {
                System.out.println("\n=== Customer Page ===");
                System.out.println("1. Booking");
                System.out.println("2. History");
                System.out.println("3. Cancel Booking");
                System.out.println("4. Top-up wallet");
                System.out.println("5. Log Out");
                System.out.print("Select your action: ");
                choice = input.nextLine();

                if (!(choice.equals("1") || choice.equals("2") || choice.equals("3") || choice.equals("4") || choice.equals("5"))) {
                    System.out.println("Invalid input.");
                }
            } while (!(choice.equals("1") || choice.equals("2") || choice.equals("3") || choice.equals("4") || choice.equals("5")));

            // Perform action based on user choice
            switch (Integer.parseInt(choice)) {
                case 1:
                    makeBooking();
                    break;
                case 2:
                    viewHistory();
                    break;
                case 3:
                    cancelBooking();
                    break;
                case 4:
                    topUp(currentUser.getUsername());
                    break;
                case 5:
                    running = false; // Exit loop
                    break;
                default:
                    System.out.println("Invalid input.");
            }
        }
    }

    // Handles booking a slot
    private void makeBooking() {
        // Step 1: Ask for booking date
        System.out.print("Enter date (YYYY-MM-DD): ");
        String date = input.nextLine();

        if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            System.out.println("Invalid date format.");
            return;
        }

        // Validate if the date exists using LocalDate
        try {
            LocalDate parsedDate = LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date.");
            return;
        }

        // Step 2: Load all slots and mark those already booked
        List<Slot> slots = loadSlots();
        List<Booking> bookedOnDate = bookingManager.getBookingsByDate(date);

        Map<String, Set<String>> slotToHours = new HashMap<>();

        for (Booking b : bookedOnDate) {
            String slotNumber = b.getSlotNumber();
            String time = b.getTime();
            slotToHours.putIfAbsent(slotNumber, new HashSet<>());
            slotToHours.get(slotNumber).add(time);
        }

        // Mark slots as fully booked if all 12 hours are taken
        for (Slot s : slots) {
            Set<String> hours = slotToHours.get(s.getNumber());
            if (hours != null && hours.size() == 12) {
                s.setStatus("Booked");
            }
        }

        // Step 3: Show slot availability
        System.out.println("\n=== Slots for " + date + " ===");
        for (Slot slot : slots) {
            String type = (slot.getNumber().equals("1") || slot.getNumber().equals("2")) ? "(Odd)" : "(Even)";
            System.out.printf("Slot %s %s: %s\n", slot.getNumber(), type, slot.getStatus());
        }

        // Step 4: Get user's slot choice
        System.out.print("Select a slot (1-" + slots.size() + "): ");
        String slotChoice = input.nextLine();
        if (!slotChoice.matches("[1-4]")) {
            System.out.println("Invalid slot selection.");
            return;
        }

        Slot selectedSlot = slots.get(Integer.parseInt(slotChoice) - 1);

        // Check if slot is already booked or unavailable
        if (selectedSlot.getStatus().equals("Booked")) {
            System.out.println("That slot is already booked on " + date + ".");
            return;
        } else if (selectedSlot.getStatus().equalsIgnoreCase("Unavailable")) {
            System.out.println("That slot is currently Unavailable on " + date + ".");
            return;
        }

        // Step 5: Ask for time and validate parity (odd/even logic)
        System.out.print("Enter time (0–23): ");
        String timeStr = input.nextLine();
        int time;
        try {
            time = Integer.parseInt(timeStr);
        } catch (NumberFormatException e) {
            System.out.println("Invalid time format.");
            return;
        }

        if (time < 0 || time > 23) {
            System.out.println("Time must be between 0 and 23.");
            return;
        }

        if (!isTimeValidForSlot(slotChoice, time)) {
            String parity = (slotChoice.equals("1") || slotChoice.equals("2")) ? "ODD" : "EVEN";
            System.out.println("Slot " + slotChoice + " only accepts " + parity + " hours.");
            return;
        }

        // Step 6: Check for time conflict
        for (Booking b : bookedOnDate) {
            if (b.getSlotNumber().equals(slotChoice) && b.getTime().equals(timeStr)) {
                System.out.println("That slot/time is already taken.");
                return;
            }
        }

        // Step 6.5: Check wallet balance and deduct RM10
        WalletAccount wallet = new WalletAccount();
        double currentBalance = wallet.getBalance(currentUser.getUsername());

        if (currentBalance < 10.00) {
            System.out.println("Insufficient balance. Minimum RM10 required to make a booking.");
            System.out.println("Please top up your wallet first.");
            return;
        }

        double updatedBalance = currentBalance - 10.00;
        wallet.updateBalance(currentUser.getUsername(), updatedBalance);
        System.out.printf("RM10.00 has been deducted. New balance: RM%.2f\n", updatedBalance);

        // Step 7: Save the booking
        Booking newB = new Booking(currentUser.getUsername(), slotChoice, date, timeStr);
        bookingManager.addBooking(newB);

        System.out.println("Booking successful for " + date + " at " + timeStr + " on slot " + slotChoice + "!");
    }

    // Show all bookings by this user
    private void viewHistory() {
        System.out.println("\n=== Your Booking History ===");
        List<Booking> bookings = bookingManager.getBookingsByUser(currentUser.getUsername());

        if (bookings.isEmpty()) {
            System.out.println("You have no bookings.");
            return;
        }

        System.out.printf("%-4s | %-10s | %-2s\n", "Slot", "Date", "Time");
        for (Booking b : bookings) {
            System.out.printf("%-4s | %-10s | %-2s\n", b.getSlotNumber(), b.getDate(), b.getTime());
        }
    }

    // Cancel a booking and refund RM10
    private void cancelBooking() {
        System.out.print("Enter date to cancel (YYYY-MM-DD): ");
        String date = input.nextLine().trim();
        if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            return;
        }

        System.out.print("Enter time to cancel (0–23): ");
        String timeStr = input.nextLine().trim();
        if (!timeStr.matches("\\d{1,2}")) {
            System.out.println("Invalid time format. Please enter an integer hour 0–23.");
            return;
        }

        int time = Integer.parseInt(timeStr);
        if (time < 0 || time > 23) {
            System.out.println("Invalid hour. Must be between 0 and 23.");
            return;
        }

        // Find matching booking to remove
        List<Booking> myBookings = bookingManager.getBookingsByUser(currentUser.getUsername());
        Booking toRemove = null;
        for (Booking b : myBookings) {
            if (b.getDate().equals(date) && b.getTime().equals(timeStr)) {
                toRemove = b;
                break;
            }
        }

        if (toRemove == null) {
            System.out.println("No booking found for you on " + date + " at " + timeStr + ".");
            return;
        }

        // Remove the booking
        boolean success = bookingManager.removeBooking(currentUser.getUsername(), date, timeStr);
        if (!success) {
            System.out.println("Failed to cancel booking. Please try again.");
            return;
        }

        System.out.println("Booking on " + date + " at " + timeStr + " for slot " + toRemove.getSlotNumber() + " has been cancelled.");

        // Refund RM10 to wallet
        WalletAccount wallet = new WalletAccount();
        double currentBalance = wallet.getBalance(currentUser.getUsername());
        double updatedBalance = currentBalance + 10.00;
        wallet.updateBalance(currentUser.getUsername(), updatedBalance);

        System.out.printf("RM 10.00 has been refunded. New wallet balance: RM %.2f\n", updatedBalance);
    }

    // Top-up wallet method
    private void topUp(String username) {
        WalletAccount wallet = new WalletAccount();
        Scanner scanner = new Scanner(System.in);

        double currentBalance = wallet.getBalance(username);
        System.out.printf("Current amount in wallet: RM%.2f\n", currentBalance);

        double amount = 0;
        do {
            System.out.print("Enter amount to top-up: ");
            try {
                amount = Double.parseDouble(scanner.nextLine());
                if (amount < 0) {
                    System.out.println("Top-up amount cannot be less than 0, please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input, please enter a number.");
            }
        } while (amount < 0);

        double newBalance = currentBalance + amount;
        wallet.updateBalance(username, newBalance);
        System.out.printf("Top-up successful! Your new balance is: RM%.2f\n", newBalance);
    }

    // Slot 1 and 2 only allow odd hours; Slot 3 and 4 allow even hours
    private boolean isTimeValidForSlot(String slotNumber, int time) {
        if (Integer.parseInt(slotNumber) == 1 || Integer.parseInt(slotNumber) == 2) {
            return time % 2 == 1; // odd hours only
        } else {
            return time % 2 == 0; // even hours only
        }
    }

    // Load all slots from file
    private List<Slot> loadSlots() {
        List<Slot> slots = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File("Slots & Availability.txt"))) {
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(",");
                if (parts.length == 2) {
                    slots.add(new Slot(parts[0], parts[1]));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading slots: " + e.getMessage());
        }
        return slots;
    }
}
