package hotel.model;

import java.io.Serializable;
import java.time.LocalDate;

// Week 1: Encapsulation  |  Week 6: Serializable
public class Customer implements Serializable {
    private static final long serialVersionUID = 2L;

    // Week 2: Wrapper classes used for ID and contact
    private Integer customerId;
    private String name;
    private String contactNumber;
    private Integer roomNumberAllocated;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    public Customer(int customerId, String name, String contactNumber,
                    int roomNumberAllocated, LocalDate checkIn, LocalDate checkOut) {
        // Week 2: Autoboxing — primitives stored as wrapper objects
        this.customerId = customerId;
        this.name = name;
        this.contactNumber = contactNumber;
        this.roomNumberAllocated = roomNumberAllocated;
        this.checkInDate = checkIn;
        this.checkOutDate = checkOut;
    }

    // Getters
    public Integer getCustomerId()           { return customerId; }
    public String getName()                  { return name; }
    public String getContactNumber()         { return contactNumber; }
    public Integer getRoomNumberAllocated()  { return roomNumberAllocated; }
    public LocalDate getCheckInDate()        { return checkInDate; }
    public LocalDate getCheckOutDate()       { return checkOutDate; }

    public long getNights() {
        return java.time.temporal.ChronoUnit.DAYS.between(checkInDate, checkOutDate);
    }

    @Override
    public String toString() {
        return String.format("Customer #%d | %s | Room %d | %s to %s",
            customerId, name, roomNumberAllocated, checkInDate, checkOutDate);
    }
}
