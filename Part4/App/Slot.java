package App;

public class Slot {
	// declarations
    private String number;
    private String status;
    
    // constructor
    public Slot(String number, String status) {
        this.number = number;
        this.status = status;
    }
    
    // getters
    public String getNumber() {
        return number;
    }

    public String getStatus() {
        return status;
    }

    // setter
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return number + "," + status; // write on Slot & Availability.txt
    }
}
