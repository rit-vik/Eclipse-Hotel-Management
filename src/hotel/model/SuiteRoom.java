package hotel.model;

// Week 1: Another derived class demonstrating polymorphism
public class SuiteRoom extends Room {

    private double premiumServiceCharge;

    public SuiteRoom(int roomNumber, double basePrice, double premiumServiceCharge) {
        super(roomNumber, "Suite", basePrice);
        this.premiumServiceCharge = premiumServiceCharge;
    }

    @Override
    public double calculateTariff(int nights) {
        return (getBasePrice() + premiumServiceCharge) * nights;
    }

    @Override
    public String getRoomType() { return "Suite"; }

    public double getPremiumServiceCharge() { return premiumServiceCharge; }
}
