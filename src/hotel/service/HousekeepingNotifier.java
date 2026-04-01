package hotel.service;

// Week 3: Thread creation via Runnable interface
// Simulates background housekeeping notification when a room is booked
public class HousekeepingNotifier implements Runnable {

    private final int roomNumber;
    private final String guestName;

    public HousekeepingNotifier(int roomNumber, String guestName) {
        this.roomNumber = roomNumber;
        this.guestName  = guestName;
    }

    @Override
    public void run() {
        System.out.println("[HousekeepingNotifier] Starting preparation for Room "
            + roomNumber + " — Guest: " + guestName);
        String[] tasks = {"Making bed", "Restocking minibar", "Preparing welcome kit"};

        for (String task : tasks) {
            System.out.println("[HousekeepingNotifier] Room " + roomNumber + " — " + task);
            try {
                Thread.sleep(500);   // Week 3: sleep()
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("[HousekeepingNotifier] Room " + roomNumber + " ready!");
    }
}
