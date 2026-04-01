package hotel.service;

import hotel.model.*;
import hotel.generics.Pair;
import hotel.io.FileDataManager;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

// Week 4: Synchronization — shared resource accessed by multiple threads
// Week 8: Collections — ArrayList, HashMap, Iterator
public class HotelService {

    // Week 8: ArrayList and HashMap for data storage
    private final List<Room> rooms = new ArrayList<>();
    private final Map<Integer, Customer> bookings = new HashMap<>(); // roomNo -> Customer
    private int nextCustomerId = 1;

    // Week 4: shared counter protected by synchronization
    private int availableRoomCount = 0;

    private final FileDataManager fileManager;

    public HotelService() {
        fileManager = new FileDataManager();
        initializeSampleRooms();
    }

    // Seed some rooms on startup
    private void initializeSampleRooms() {
        rooms.add(new StandardRoom(101, 1500));
        rooms.add(new StandardRoom(102, 1500));
        rooms.add(new DeluxeRoom(201, 3500, true, true));
        rooms.add(new DeluxeRoom(202, 3500, true, false));
        rooms.add(new SuiteRoom(301, 7000, 2000));
        rooms.add(new SuiteRoom(302, 7000, 2000));
        availableRoomCount = rooms.size();
    }

    // ---------- Room Management ----------

    // Week 4: synchronized — prevents race condition when adding room
    public synchronized void addRoom(Room room) {
        rooms.add(room);
        availableRoomCount++;
        logActivity("ROOM_ADDED", "Room " + room.getRoomNumber() + " added");
    }

    // Week 8: Iterator usage
    public List<Room> getAllRooms() {
        return Collections.unmodifiableList(rooms);
    }

    // Week 8: filtered list using Iterator
    public List<Room> getAvailableRooms() {
        List<Room> available = new ArrayList<>();
        Iterator<Room> it = rooms.iterator();           // Week 8: Iterator
        while (it.hasNext()) {
            Room r = it.next();
            if (!r.isBooked()) available.add(r);
        }
        return available;
    }

    public Optional<Room> findRoom(int roomNumber) {
        return rooms.stream()
                    .filter(r -> r.getRoomNumber() == roomNumber)
                    .findFirst();
    }

    // ---------- Booking (Week 4: synchronized + wait/notify) ----------

    // Week 4: Synchronized booking — notifies waiting threads when room booked
    public synchronized boolean bookRoom(int roomNumber, String guestName,
                                         String contact, LocalDate checkIn, LocalDate checkOut) {
        Optional<Room> opt = findRoom(roomNumber);
        if (opt.isEmpty()) return false;

        Room room = opt.get();
        if (room.isBooked()) {
            // Week 4: if no room available, wait
            try { wait(3000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            return false;
        }

        room.setBooked(true);
        room.setGuestName(guestName);
        availableRoomCount--;

        Customer customer = new Customer(nextCustomerId++, guestName, contact,
                                         roomNumber, checkIn, checkOut);
        bookings.put(roomNumber, customer);

        // Week 3/4: launch background notification thread (Week 3: Runnable)
        Thread notifThread = new Thread(new HousekeepingNotifier(roomNumber, guestName));
        notifThread.setDaemon(true);
        notifThread.start();

        // Week 6: serialize bookings to file
        fileManager.serializeBookings(new ArrayList<>(bookings.values()));
        logActivity("BOOKED", "Room " + roomNumber + " booked by " + guestName);

        notifyAll();  // Week 4: notify waiting threads
        return true;
    }

    // Week 4: Synchronized checkout
    public synchronized boolean checkoutRoom(int roomNumber) {
        Optional<Room> opt = findRoom(roomNumber);
        if (opt.isEmpty() || !opt.get().isBooked()) return false;

        Room room = opt.get();
        String guestName = room.getGuestName();
        room.setBooked(false);
        room.setGuestName("");
        availableRoomCount++;
        bookings.remove(roomNumber);

        // Week 6: persist updated bookings
        fileManager.serializeBookings(new ArrayList<>(bookings.values()));
        logActivity("CHECKOUT", "Room " + roomNumber + " checked out by " + guestName);

        notifyAll();  // Week 4: notify threads waiting for a room
        return true;
    }

    // ---------- Customer Management ----------

    public Collection<Customer> getAllCustomers() {
        return bookings.values();
    }

    public Customer getCustomerByRoom(int roomNumber) {
        return bookings.get(roomNumber);
    }

    // ---------- Billing (Week 2: Wrapper classes + enum) ----------

    // Week 7: Pair<RoomNumber, TotalBill> using generics
    public Pair<Integer, Double> calculateBill(int roomNumber) {
        Customer c = bookings.get(roomNumber);
        Room r = findRoom(roomNumber).orElse(null);
        if (c == null || r == null) return new Pair<>(roomNumber, 0.0);

        // Week 2: Autoboxing — int -> Integer; result Double wrapper
        Integer nights = (int) c.getNights();
        Double total = r.calculateTariff(nights);     // unboxing in calculateTariff
        return new Pair<>(roomNumber, total);
    }

    // ---------- Room sorting (Week 8: Collections.sort) ----------

    public List<Room> getRoomsSortedByPrice() {
        List<Room> sorted = new ArrayList<>(rooms);
        // Week 8: Collections.sort with Comparator
        Collections.sort(sorted, Comparator.comparingDouble(Room::getBasePrice));
        return sorted;
    }

    public List<Room> getRoomsSortedByNumber() {
        List<Room> sorted = new ArrayList<>(rooms);
        Collections.sort(sorted, Comparator.comparingInt(Room::getRoomNumber));
        return sorted;
    }

    // Week 5: Log activity using FileWriter (character stream)
    private void logActivity(String type, String message) {
        fileManager.appendLog(type + " | " + message);
    }

    // Week 6: RandomAccessFile — read room info by index
    public void saveRoomsToRandomAccessFile() {
        fileManager.writeRoomsToRAF(rooms);
    }

    public String readRoomFromRAF(int index) {
        return fileManager.readRoomFromRAF(index);
    }

    public int getAvailableRoomCount() { return availableRoomCount; }
    public int getTotalRoomCount()     { return rooms.size(); }
    public List<String> getActivityLog() { return fileManager.readLog(); }
}
