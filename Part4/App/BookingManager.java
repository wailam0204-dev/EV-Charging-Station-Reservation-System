package App;

import java.io.*;
import java.util.*;

public class BookingManager {
    private final String BOOKINGS_FILE = "Bookings.txt"; // declare constant for Booking.txt
    private List<Booking> bookings = new ArrayList<>(); // to store bookings read from Booking.txt

    public BookingManager() {
        loadBookings(); // allow all BookingManager typed object to automatically run loadBookings method first
    }

    private void loadBookings() {
        try (Scanner scanner = new Scanner(new File(BOOKINGS_FILE))) // reads through booking.txt if there's no error
        {
            while (scanner.hasNextLine()) 
            {
            	// splitting username, slot number, date, time into 4 parts
                String[] parts = scanner.nextLine().split(",");
                if (parts.length == 4) 
                {
                	 String username = parts[0];
                     String slotNumber = parts[1];
                     String date = parts[2];
                     String time = parts[3];
                     bookings.add(new Booking(username, slotNumber, date, time)); // adds into bookings array
                 }
             }
            
        } catch (IOException e) {
        	System.out.println("Error loading bookings" );
        }
    }

    // return booking list read from Booking.txt
    public List<Booking> getAllBookings() {
        return new ArrayList<>(bookings);
    }
    
    // adds and save new bookings into Bookings.txt
    public void addBooking(Booking booking) {
        bookings.add(booking);
        saveBookings();
    }

    // removes booking from Bookings.txt (for booking cancellations)
    public boolean removeBooking(String username,String date, String time) {
        Iterator<Booking> it = bookings.iterator(); // to loop through bookings array list
        boolean removed = false; // default false to handle error
        while (it.hasNext()) {
            Booking b = it.next();
            if (b.getUsername().equals(username) &&b.getDate().equals(date) && b.getTime().equals(time)) {
                it.remove();
                removed = true; // set to true if booking is removed successfully
                break;
            }
        }
        if (removed) // if booking is removed successfully
        {
            saveBookings(); // saves the updated array into Booking.txt
        }
        return removed;
    }

    // saves bookings array into Booking.txt
    private void saveBookings() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(BOOKINGS_FILE))) {
            for (Booking booking : bookings) {
                writer.println(booking);
            }
        } catch (IOException e) {
            System.out.println("Error saving bookings.");
        }
    }
    
    // get the logged in user's booking only
    public List<Booking> getBookingsByUser(String username) {
        List<Booking> userBookings = new ArrayList<>();
        for (Booking b : bookings) {
            if (b.getUsername().equals(username)) {
                userBookings.add(b);
            }
        }
        return userBookings;
    }
    
    //Return all bookings on the given date (regardless of user)
    public List<Booking> getBookingsByDate(String date) {
        List<Booking> result = new ArrayList<>();
        for (Booking b : bookings) {
            if (b.getDate().equals(date)) {
                result.add(b);
            }
        }
        return result;
    }
}
