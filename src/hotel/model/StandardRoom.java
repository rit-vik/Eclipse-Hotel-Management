package hotel.model;

// ============================================================
// Week 1: Inheritance + Method Overriding (Runtime Polymorphism)
// ============================================================

// Standard Room: base tariff only
public class StandardRoom extends Room {

    public StandardRoom(int roomNumber, double basePrice) {
        super(roomNumber, "Standard", basePrice);   // super keyword
    }

    @Override
    public double calculateTariff(int nights) {
        // Week 2: Autoboxing — primitive -> Integer wrapper
        Integer n = nights;                          // autoboxing
        return getBasePrice() * n;                   // unboxing in arithmetic
    }

    @Override
    public String getRoomType() { return "Standard"; }
}
