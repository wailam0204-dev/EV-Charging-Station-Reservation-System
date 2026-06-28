package App;

import java.io.*;
import java.util.*;

public class SlotManager {
    private static final String FILE_NAME = "Slots & Availability.txt";
    private List<Slot> slots; // declare slots as a list type Slot

    public SlotManager() {
        slots = new ArrayList<>(); //create object slots as an ArrayList
        loadSlots();
    }

    private void loadSlots() {
        try (Scanner fileScanner = new Scanner(new File(FILE_NAME))) { // reads Slots & Availability.txt
            while (fileScanner.hasNextLine()) { // loops through all lines in the file
                String[] parts = fileScanner.nextLine().split(","); // splits slots and availability into parts and stores into parts[]
                if (parts.length == 2) {
                    slots.add(new Slot(parts[0], parts[1])); // adds the data of each line into the array list in each loop
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading slots.");
        }
    }

    public boolean updateSlotStatus(String slotNumber, String newStatus) 
    {
    	try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) 
    	{
    		for (Slot slot : slots) // loops through all data in slots ArrayList
    		{ 
                if (slot.getNumber().equals(slotNumber)) // check one by one if the number stored in slots ArrayList matches with the slotNumber input by the user
                { 
                    slot.setStatus(newStatus); // sets the status of the slotNumber to the status entered by the user
                }
                writer.println(slot); // calls the toString() method of Slot
    		}
    		return true;
    		
    	} catch (IOException e) {
            System.out.println("Error saving slots.");
        }
    
        return false;
    }

}
