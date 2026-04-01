# 🏨 Hotel Management System
### Object-Oriented Software Design Lab — JavaFX Application
### Weeks 1–10 Integrated Project

---

## 📁 Project Structure

```
HotelManagementSystem/
├── build.sh                          ← Compile & run script
├── README.md
└── src/
    ├── module-info.java
    └── hotel/
        ├── model/                    ← Week 1, 2: OOP + Enum
        │   ├── Room.java             ← Abstract class (Week 1)
        │   ├── StandardRoom.java     ← Inheritance + override (Week 1)
        │   ├── DeluxeRoom.java       ← Inheritance + override (Week 1)
        │   ├── SuiteRoom.java        ← Inheritance + override (Week 1)
        │   ├── RoomType.java         ← Enum + constructors/methods (Week 2)
        │   └── Customer.java         ← Encapsulation + Serializable (Week 1, 6)
        │
        ├── generics/                 ← Week 7: Generics
        │   ├── Pair.java             ← Generic class with two type params
        │   └── RoomStore.java        ← Bounded generic <T extends Room>
        │
        ├── service/                  ← Week 3, 4, 8: Threads + Collections
        │   ├── HotelService.java     ← Core logic (ArrayList, HashMap, synchronized)
        │   └── HousekeepingNotifier.java  ← Runnable thread (Week 3)
        │
        ├── io/                       ← Week 5, 6: File I/O
        │   └── FileDataManager.java  ← FileWriter/Reader, RAF, Serialization
        │
        ├── util/
        │   └── UIHelper.java         ← JavaFX styling helpers
        │
        └── ui/                       ← Week 9, 10: JavaFX GUI
            ├── HotelApp.java         ← Main Application, Stage + Scene
            ├── DashboardPanel.java   ← Live stats dashboard
            ├── RoomsPanel.java       ← Room management + TableView
            ├── BookingPanel.java     ← Booking form + confirmation
            ├── CheckoutPanel.java    ← Checkout + guest bill
            ├── BillingPanel.java     ← Invoice + tariff calculator
            └── LogPanel.java         ← Activity log + file ops
```

---

## 🗺️ OSDL Concept Map

| Week | Concept | Where Used in HMS |
|------|---------|-------------------|
| **1** | OOP — Classes, Inheritance, Polymorphism, Abstraction | `Room` (abstract), `StandardRoom`, `DeluxeRoom`, `SuiteRoom`; `calculateTariff()` overridden in each; `Customer` (encapsulation) |
| **2** | Wrapper Classes, Enum, Autoboxing/Unboxing | `RoomType` enum with prices + `calculateCost()`; `Integer nights`, `Double total` wrapper objects in billing; autoboxing in `bookRoom()` |
| **3** | Multithreading — Thread class & Runnable interface | `HousekeepingNotifier implements Runnable` starts on every booking; `Thread.sleep()` simulates tasks; BookingPanel thread demo |
| **4** | Synchronization — synchronized, wait(), notify() | `bookRoom()` and `checkoutRoom()` are `synchronized`; `wait(3000)` if room unavailable; `notifyAll()` after state change |
| **5** | File I/O — FileWriter, FileReader, byte streams | `FileWriter` appends every action to `hotel_activity.log`; `BufferedReader(FileReader)` reads log back; `FileInputStream/OutputStream` for binary copy |
| **6** | RandomAccessFile + Serialization/Deserialization | `rooms.raf` stores fixed-size room records; `seek(index * RECORD_SIZE)` for direct access; `ObjectOutputStream` serializes bookings to `bookings.dat` |
| **7** | Generics — type params, bounded types, generic methods | `Pair<T,U>` associates `<Integer, Double>` room-bill; `RoomStore<T extends Room>` type-safe store; `isPriceAffordable<N extends Number>` bounded method |
| **8** | Collections — ArrayList, HashMap, Iterator, sort | `ArrayList<Room>` stores rooms; `HashMap<Integer, Customer>` maps room→booking; `Iterator<Room>` for filtering; `Collections.sort()` for ordering |
| **9** | JavaFX — Stage, Scene, Controls, EventHandling | Tab-based GUI with `TableView`, `GridPane` forms, `ComboBox`, `DatePicker`, `Alert`, `Button.setOnAction()` |
| **10**| Complete integrated Hotel Management Application | All weeks combined; modular panels; in-memory + file persistence; standalone JavaFX desktop app |

---

## 🚀 How to Run

### Requirements
- JDK 11 or later
- JavaFX SDK 11+ ([download from openjfx.io](https://openjfx.io))

### Option A: Using build script (Linux/macOS)
```bash
chmod +x build.sh
./build.sh
```

### Option B: Manual compile + run
```bash
# Set your JavaFX path
export FX=/path/to/javafx-sdk/lib

# Compile all sources
find src -name "*.java" > sources.txt
javac --module-path $FX --add-modules javafx.controls -d out/classes @sources.txt

# Run
java --module-path $FX --add-modules javafx.controls -cp out/classes hotel.ui.HotelApp
```

### Option C: Install JavaFX on Ubuntu/Debian
```bash
sudo apt install openjfx
javac --module-path /usr/share/openjfx/lib --add-modules javafx.controls \
      -d out/classes @sources.txt
java  --module-path /usr/share/openjfx/lib --add-modules javafx.controls \
      -cp out/classes hotel.ui.HotelApp
```

### Option D: Using IntelliJ IDEA
1. Open the `HotelManagementSystem` folder as a project
2. Go to **File → Project Structure → Libraries** → add your JavaFX `lib/` folder
3. Go to **Run → Edit Configurations** → Add VM options:
   ```
   --module-path /path/to/javafx/lib --add-modules javafx.controls
   ```
4. Set Main class: `hotel.ui.HotelApp`
5. Click Run ▶

---

## 🎯 Features

### 📊 Dashboard
- Real-time stats: total rooms, available, booked, occupancy %
- Auto-refreshes every 3 seconds (JavaFX Timeline)
- Room type summary & concept map

### 🛏 Room Management
- Add Standard, Deluxe, Suite rooms with custom prices
- Enum-based default pricing (Week 2)
- TableView with live availability status
- Sort by price or room number (Week 8: Collections.sort)
- RandomAccessFile record reader (Week 6)

### 📋 Booking
- Full booking form: name, contact, room, check-in/out dates
- Live cost preview using `Pair<Long, Double>` (Week 7)
- Polymorphic `calculateTariff()` call (Week 1)
- Synchronized booking prevents race conditions (Week 4)
- Background `HousekeepingNotifier` thread (Week 3)
- Thread demo button shows concurrent booking attempt

### 🚪 Checkout
- Room selector with guest info display
- Confirmation Alert before checkout (Week 9)
- Synchronized `checkoutRoom()` notifies waiting threads (Week 4)
- Full bill shown before confirmation

### 💰 Billing
- Invoice generator with wrapper-class arithmetic (Week 2)
- Enum tariff calculator with autoboxing demo (Week 2)
- Pair<Integer, Double> for bill representation (Week 7)

### 📁 Activity Log
- Reads all logged actions from `hotel_activity.log` via FileReader (Week 5)
- Copy log button uses byte stream copy (Week 5)
- File operations info panel (Week 5, 6)
- Generics summary (Week 7)

---

## 📝 Files Created at Runtime
| File | Created by | Purpose |
|------|-----------|---------|
| `hotel_activity.log` | FileWriter (Week 5) | Activity log |
| `bookings.dat` | ObjectOutputStream (Week 6) | Serialized bookings |
| `rooms.raf` | RandomAccessFile (Week 6) | Fixed-size room records |

---

