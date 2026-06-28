package App;

import java.util.*;
import java.io.*;

public class WalletAccount {
    // File where all wallet balances are stored
    private final String FILE_NAME = "Wallet.txt";

    // Method to get a user's current wallet balance
    public double getBalance(String username) {
        try (Scanner scanner = new Scanner(new File(FILE_NAME))) {
            // Read file line by line
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(","); // Split each line into [username, balance]
                if (parts.length == 2 && parts[0].equals(username)) {
                    return Double.parseDouble(parts[1]); // Return the found user's balance
                }
            }
        } 
        catch (FileNotFoundException e) {
            // If file doesn't exist yet, show message (first-time use)
            System.out.println("Wallet file not found. A new one will be created upon top-up.");
        }
        return 0.0; // Return 0 if user not found
    }

    // Method to update a user's balance (or add them if new)
    public void updateBalance(String username, double newBalance) {
        List<String> lines = new ArrayList<>(); // To store updated lines
        boolean found = false; // Flag to check if user was found

        // Read all current wallet entries
        try (Scanner scanner = new Scanner(new File(FILE_NAME))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(","); // Split into username and balance

                if (parts.length == 2 && parts[0].equals(username)) {
                    // If this is the user we're updating, replace with new balance
                    lines.add(username + "," + newBalance);
                    found = true;
                } 
                else {
                    // Keep other users as-is
                    lines.add(line);
                }
            }
        } catch (FileNotFoundException e) {
            // File doesn't exist yet — it's fine for first-time use
        }

        // If the user wasn’t found, add a new entry at the end
        if (!found) {
            lines.add(username + "," + newBalance);
        }

        // Write the updated list of users and balances back to the file
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (String l : lines) {
                writer.println(l); // Write each line back into the file
            }
        } catch (IOException e) {
            // If something goes wrong while writing
            System.out.println("Error writing to wallet file.");
        }
    }
}
