package hotel.io;

import hotel.model.Customer;
import hotel.model.Room;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

// Week 5: FileWriter (character stream) for activity log
// Week 5: FileReader to read log
// Week 5: FileInputStream / FileOutputStream (byte streams) for file copy
// Week 6: RandomAccessFile for fixed-length room records
// Week 6: ObjectOutputStream / ObjectInputStream for serialization

public class FileDataManager {

    private static final String LOG_FILE       = "hotel_activity.log";
    private static final String BOOKINGS_FILE  = "bookings.dat";
    private static final String RAF_FILE       = "rooms.raf";

    // Fixed sizes for RAF record:
    // roomNumber(4) + roomType(40 chars = 80 bytes UTF) + basePrice(8) + isBooked(1) + guestName(60 chars = 120 bytes)
    // We'll write: int(4) + 20-char padded String(40) + double(8) + boolean(1) + 30-char padded String(60) = 113 bytes
    private static final int ROOM_TYPE_LEN  = 20;
    private static final int GUEST_NAME_LEN = 30;
    // Record size: 4 (int roomNo) + 40 (roomType chars*2) + 8 (double price) + 1 (boolean) + 60 (guestName chars*2)
    // Using writeChars: each char = 2 bytes
    private static final int RECORD_SIZE =
        4 + (ROOM_TYPE_LEN * 2) + 8 + 1 + (GUEST_NAME_LEN * 2); // = 213 bytes

    // ================================================================
    // Week 5: Character Stream — FileWriter (append mode for logging)
    // ================================================================
    public void appendLog(String message) {
        // Week 5: try-with-resources FileWriter
        try (FileWriter fw = new FileWriter(LOG_FILE, true)) {
            fw.write(java.time.LocalDateTime.now() + " | " + message + "\n");
        } catch (IOException e) {
            System.err.println("[Log Error] " + e.getMessage());
        }
    }

    // Week 5: FileReader — read the activity log
    public List<String> readLog() {
        List<String> lines = new ArrayList<>();
        File f = new File(LOG_FILE);
        if (!f.exists()) return lines;

        try (BufferedReader br = new BufferedReader(new FileReader(LOG_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.err.println("[Log Read Error] " + e.getMessage());
        }
        return lines;
    }

    // ================================================================
    // Week 5: Byte Streams — copy log file (FileInputStream/OutputStream)
    // ================================================================
    public void copyLogFile(String destination) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(LOG_FILE);
            fos = new FileOutputStream(destination);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            System.out.println("[FileIO] Log copied to " + destination);
        } catch (IOException e) {
            System.err.println("[Copy Error] " + e.getMessage());
        } finally {
            try { if (fis != null) fis.close(); } catch (IOException ignored) {}
            try { if (fos != null) fos.close(); } catch (IOException ignored) {}
        }
    }

    // ================================================================
    // Week 6: RandomAccessFile — write all rooms as fixed-size records
    // ================================================================
    public void writeRoomsToRAF(List<Room> rooms) {
        try (RandomAccessFile raf = new RandomAccessFile(RAF_FILE, "rw")) {
            raf.setLength(0); // clear
            for (Room room : rooms) {
                writeRoomRecord(raf, room);
            }
        } catch (IOException e) {
            System.err.println("[RAF Write Error] " + e.getMessage());
        }
    }

    private void writeRoomRecord(RandomAccessFile raf, Room room) throws IOException {
        raf.writeInt(room.getRoomNumber());
        writeFixedString(raf, room.getRoomType(), ROOM_TYPE_LEN);
        raf.writeDouble(room.getBasePrice());
        raf.writeBoolean(room.isBooked());
        writeFixedString(raf, room.getGuestName(), GUEST_NAME_LEN);
    }

    // Pad or truncate string to fixed char length
    private void writeFixedString(RandomAccessFile raf, String s, int len) throws IOException {
        StringBuilder sb = new StringBuilder(s == null ? "" : s);
        while (sb.length() < len) sb.append(' ');
        String padded = sb.substring(0, len);
        raf.writeChars(padded); // each char = 2 bytes
    }

    // Week 6: RandomAccessFile — seek to record by index and read
    public String readRoomFromRAF(int index) {
        try (RandomAccessFile raf = new RandomAccessFile(RAF_FILE, "r")) {
            long pos = (long) index * RECORD_SIZE;
            if (pos >= raf.length()) return "Record not found";
            raf.seek(pos);

            int roomNo    = raf.readInt();
            String rType  = readFixedString(raf, ROOM_TYPE_LEN).trim();
            double price  = raf.readDouble();
            boolean booked = raf.readBoolean();
            String guest  = readFixedString(raf, GUEST_NAME_LEN).trim();

            return String.format("Room #%d | Type: %s | Price: ₹%.0f | Booked: %s | Guest: %s",
                roomNo, rType, price, booked, guest.isEmpty() ? "None" : guest);
        } catch (IOException e) {
            return "[RAF Read Error] " + e.getMessage();
        }
    }

    private String readFixedString(RandomAccessFile raf, int len) throws IOException {
        char[] chars = new char[len];
        for (int i = 0; i < len; i++) {
            chars[i] = raf.readChar();
        }
        return new String(chars);
    }

    // ================================================================
    // Week 6: Serialization — persist bookings list to file
    // ================================================================
    public void serializeBookings(List<Customer> customers) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                 new FileOutputStream(BOOKINGS_FILE))) {
            oos.writeObject(customers);
            System.out.println("[Serialization] Bookings saved (" + customers.size() + " records).");
        } catch (IOException e) {
            System.err.println("[Serialize Error] " + e.getMessage());
        }
    }

    // Week 6: Deserialization — load bookings from file
    @SuppressWarnings("unchecked")
    public List<Customer> deserializeBookings() {
        File f = new File(BOOKINGS_FILE);
        if (!f.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(
                 new FileInputStream(BOOKINGS_FILE))) {
            return (List<Customer>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[Deserialize Error] " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
