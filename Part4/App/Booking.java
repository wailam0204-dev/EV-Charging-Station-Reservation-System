package App;

public class Booking {
	// declaring variables
    private String username;
    private String slotNumber;
    private String date;
    private String time;
    
    // constructor
    public Booking(String username, String slotNumber, String date, String time) {
        this.username = username;
        this.slotNumber = slotNumber;
        this.date = date;
        this.time = time;
    }
    
    //getters
    public String getUsername() {
        return username;
    }

    public String getSlotNumber() {
        return slotNumber;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
    @Override
    public String toString() {
        return username + "," + slotNumber + "," + date + "," + time; // write on Booking.txt
    }
}
