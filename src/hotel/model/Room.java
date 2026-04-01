package hotel.model;

import java.io.Serializable;

// Week 1: Abstract class demonstrating Abstraction + Encapsulation
// Week 6: Implements Serializable for object persistence
public abstract class Room implements Serializable {
    private static final long serialVersionUID = 1L;

    // Week 1: Encapsulation — private fields
    private int roomNumber;
    private String roomType;
    private double basePrice;
    private boolean isBooked;
    private String guestName;

    // Week 1: Constructor
    public Room(int roomNumber, String roomType, double basePrice) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.basePrice = basePrice;
        this.isBooked = false;
        this.guestName = "";
    }

    // Week 1: Abstract method — polymorphism via override
    public abstract double calculateTariff(int nights);

    // Week 1: Concrete method in abstract class
    public String getSummary() {
        return String.format("Room %d | %-10s | ₹%.0f/night | %s",
            roomNumber, roomType, basePrice, isBooked ? "BOOKED (" + guestName + ")" : "AVAILABLE");
    }

    // Week 1: Getters and Setters (Encapsulation)
    public int getRoomNumber()         { return roomNumber; }
    public String getRoomType()        { return roomType; }
    public double getBasePrice()       { return basePrice; }
    public boolean isBooked()          { return isBooked; }
    public String getGuestName()       { return guestName; }

    public void setBooked(boolean booked)      { this.isBooked = booked; }
    public void setGuestName(String name)      { this.guestName = name; }
    public void setBasePrice(double price)     {
        if (price > 0) this.basePrice = price;
    }

    @Override
    public String toString() { return getSummary(); }
}
