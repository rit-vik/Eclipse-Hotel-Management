package hotel.model;

// Week 2: Enumeration with constructor and methods
public enum RoomType {
    STANDARD(1500, "Standard"),
    DELUXE(3500, "Deluxe"),
    SUITE(7000, "Suite");

    // Enum fields
    private final int pricePerNight;
    private final String displayName;

    // Enum constructor (Week 2)
    RoomType(int pricePerNight, String displayName) {
        this.pricePerNight = pricePerNight;
        this.displayName   = displayName;
    }

    // Enum methods
    public int getPricePerNight() { return pricePerNight; }
    public String getDisplayName() { return displayName; }

    // Week 2: method using wrapper class for arithmetic
    public Double calculateCost(int nights) {
        // Autoboxing: int -> Integer, result autoboxed to Double
        Integer n = nights;
        return (double) (pricePerNight * n);  // unboxing
    }

    @Override
    public String toString() { return displayName + " (₹" + pricePerNight + "/night)"; }
}
