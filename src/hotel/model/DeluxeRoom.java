package hotel.model;

// Week 1: Derived class — adds breakfast surcharge
public class DeluxeRoom extends Room {

    private boolean freeWifi;
    private boolean complimentaryBreakfast;

    // Week 1: Constructor using super keyword
    public DeluxeRoom(int roomNumber, double basePrice,
                      boolean freeWifi, boolean complimentaryBreakfast) {
        super(roomNumber, "Deluxe", basePrice);
        this.freeWifi = freeWifi;
        this.complimentaryBreakfast = complimentaryBreakfast;
    }

    // Week 1: Method Overriding
    @Override
    public double calculateTariff(int nights) {
        double base = getBasePrice() * nights;
        double surcharge = complimentaryBreakfast ? 500 * nights : 0;
        return base + surcharge;
    }

    @Override
    public String getRoomType() { return "Deluxe"; }

    public boolean hasFreeWifi()             { return freeWifi; }
    public boolean hasComplimentaryBreakfast() { return complimentaryBreakfast; }
}
