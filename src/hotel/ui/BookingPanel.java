package hotel.ui;

import hotel.model.Room;
import hotel.service.HotelService;
import hotel.generics.Pair;
import hotel.util.UIHelper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class BookingPanel {

    private final HotelService hotelService;
    private final TabPane tabPane;

    private TextField tfName, tfContact;
    private ComboBox<String> cbRoom;
    private DatePicker dpCheckIn, dpCheckOut;
    private Label lblStatus, lblCost;

    public BookingPanel(HotelService service, TabPane tabPane) {
        this.hotelService = service;
        this.tabPane = tabPane;
    }

    public Node getView() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: #0d1f1a;");
        root.getChildren().add(UIHelper.sectionHeader("📋  Room Booking"));

        HBox mainRow = new HBox(20);
        mainRow.getChildren().addAll(buildBookingForm(), buildCurrentBookings());
        root.getChildren().add(mainRow);

        root.getChildren().add(buildThreadDemoCard());
        return root;
    }

    private VBox buildBookingForm() {
        VBox card = UIHelper.card("🛎  New Booking");
        card.setMinWidth(340);
        card.setMaxWidth(380);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(14);

        Label lName    = UIHelper.label("Guest Name:");
        tfName         = UIHelper.textField("Enter full name");

        Label lContact = UIHelper.label("Contact No.:");
        tfContact      = UIHelper.textField("10-digit number");

        Label lRoom    = UIHelper.label("Select Room:");
        cbRoom         = UIHelper.styledComboBox();
        cbRoom.setMaxWidth(Double.MAX_VALUE);
        refreshRoomComboBox();

        Label lIn  = UIHelper.label("Check-In Date:");
        dpCheckIn  = UIHelper.styledDatePicker();
        dpCheckIn.setValue(LocalDate.now());

        Label lOut = UIHelper.label("Check-Out Date:");
        dpCheckOut = UIHelper.styledDatePicker();
        dpCheckOut.setValue(LocalDate.now().plusDays(2));

        lblCost = new Label("Estimated cost will appear here");
        lblCost.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        lblCost.setTextFill(Color.web(UIHelper.ACCENT2));
        lblCost.setWrapText(true);

        cbRoom.setOnAction(e -> updateCostPreview());
        dpCheckIn.setOnAction(e -> updateCostPreview());
        dpCheckOut.setOnAction(e -> updateCostPreview());

        grid.add(lName,    0, 0); grid.add(tfName,    1, 0);
        grid.add(lContact, 0, 1); grid.add(tfContact, 1, 1);
        grid.add(lRoom,    0, 2); grid.add(cbRoom,    1, 2);
        grid.add(lIn,      0, 3); grid.add(dpCheckIn, 1, 3);
        grid.add(lOut,     0, 4); grid.add(dpCheckOut,1, 4);

        ColumnConstraints cc0 = new ColumnConstraints(110);
        ColumnConstraints cc1 = new ColumnConstraints();
        cc1.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(cc0, cc1);

        lblStatus = new Label("");
        lblStatus.setFont(Font.font("Segoe UI", 13));
        lblStatus.setWrapText(true);

        Button btnBook  = UIHelper.primaryButton("✅  Confirm Booking");
        Button btnClear = UIHelper.secondaryButton("Clear");
        btnBook.setMaxWidth(Double.MAX_VALUE);

        btnBook.setOnAction(e -> handleBooking());
        btnClear.setOnAction(e -> clearForm());

        card.getChildren().addAll(grid, lblCost, btnBook, btnClear, lblStatus);
        return card;
    }

    private void updateCostPreview() {
        try {
            String roomStr = cbRoom.getValue();
            if (roomStr == null) return;
            int roomNo = Integer.parseInt(roomStr.split(" ")[0]);

            LocalDate in  = dpCheckIn.getValue();
            LocalDate out = dpCheckOut.getValue();
            if (in == null || out == null || !out.isAfter(in)) {
                lblCost.setText("⚠  Check-out must be after check-in.");
                return;
            }

            long nights = java.time.temporal.ChronoUnit.DAYS.between(in, out);
            Room room = hotelService.findRoom(roomNo).orElse(null);
            if (room == null) return;

            double cost = room.calculateTariff((int) nights);
            Pair<Long, Double> pair = new Pair<>(nights, cost);
            lblCost.setText(String.format("🧾  %d night(s)  ×  ₹%.0f  =  ₹%.0f total",
                pair.getFirst(), (double)(int) room.getBasePrice(), pair.getSecond()));
        } catch (Exception ignored) {}
    }

    private void handleBooking() {
        String name    = tfName.getText().trim();
        String contact = tfContact.getText().trim();
        String roomStr = cbRoom.getValue();

        if (name.isEmpty() || contact.isEmpty() || roomStr == null) {
            lblStatus.setText("Please fill all fields.");
            lblStatus.setTextFill(Color.web(UIHelper.DANGER));
            return;
        }

        LocalDate in  = dpCheckIn.getValue();
        LocalDate out = dpCheckOut.getValue();
        if (in == null || out == null || !out.isAfter(in)) {
            lblStatus.setText("Invalid dates — check-out must be after check-in.");
            lblStatus.setTextFill(Color.web(UIHelper.DANGER));
            return;
        }

        int roomNo  = Integer.parseInt(roomStr.split(" ")[0]);
        boolean success = hotelService.bookRoom(roomNo, name, contact, in, out);

        if (success) {
            long nights = java.time.temporal.ChronoUnit.DAYS.between(in, out);
            Pair<Integer, Double> bill = hotelService.calculateBill(roomNo);
            lblStatus.setText(String.format(
                "✅  Room %d booked for %s!   %d nights  |  Total: ₹%.0f",
                roomNo, name, nights, bill.getSecond()));
            lblStatus.setTextFill(Color.web(UIHelper.SUCCESS));
            refreshRoomComboBox();
            updateBookingsTable();
            clearForm();
        } else {
            lblStatus.setText("Room " + roomNo + " is already booked or unavailable.");
            lblStatus.setTextFill(Color.web(UIHelper.DANGER));
        }
    }

    // ---- Current Bookings ----
    private VBox bookingsCard;

    private VBox buildCurrentBookings() {
        bookingsCard = UIHelper.card("📋  Current Bookings");
        HBox.setHgrow(bookingsCard, Priority.ALWAYS);
        updateBookingsTable();
        return bookingsCard;
    }

    private void updateBookingsTable() {
        if (bookingsCard == null) return;
        while (bookingsCard.getChildren().size() > 2) {
            bookingsCard.getChildren().remove(2);
        }

        var customers = hotelService.getAllCustomers();
        if (customers.isEmpty()) {
            Label empty = new Label("No active bookings.");
            empty.setTextFill(Color.web(UIHelper.TEXT_GREY));
            empty.setFont(Font.font("Segoe UI", 13));
            bookingsCard.getChildren().add(empty);
            return;
        }

        for (var c : customers) {
            VBox row = new VBox(5);
            row.setPadding(new Insets(10));
            row.setStyle(
                "-fx-background-color: #0a3d2b55;" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: #1a7a4a44;" +
                "-fx-border-radius: 8;"
            );

            Label nameL = new Label("👤  " + c.getName() + "     📞  " + c.getContactNumber());
            nameL.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
            nameL.setTextFill(Color.WHITE);

            Label roomL = new Label(String.format(
                "🏠  Room %d    📅  %s  →  %s    (%d nights)",
                c.getRoomNumberAllocated(), c.getCheckInDate(), c.getCheckOutDate(), c.getNights()));
            roomL.setFont(Font.font("Segoe UI", 12));
            roomL.setTextFill(Color.web(UIHelper.ACCENT2));

            Pair<Integer, Double> bill = hotelService.calculateBill(c.getRoomNumberAllocated());
            Label billL = new Label("💰  Total:  ₹" + String.format("%.0f", bill.getSecond()));
            billL.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
            billL.setTextFill(Color.web(UIHelper.SUCCESS));

            row.getChildren().addAll(nameL, roomL, billL);
            bookingsCard.getChildren().add(row);
        }
    }

    // ---- Thread demo card ----
    private VBox buildThreadDemoCard() {
        VBox card = UIHelper.card("🧵  Concurrent Booking Demo");
        card.setMaxHeight(100);

        Button btnDemo = UIHelper.secondaryButton("▶  Simulate Two Concurrent Booking Threads");
        btnDemo.setOnAction(e -> simulateConcurrentBooking());

        card.getChildren().add(btnDemo);
        return card;
    }

    private void simulateConcurrentBooking() {
        Runnable t1Task = () -> {
            System.out.println("[Thread-1] Attempting to book Room 999…");
            boolean ok = hotelService.bookRoom(999, "Thread Guest A", "9999999991",
                LocalDate.now(), LocalDate.now().plusDays(1));
            System.out.println("[Thread-1] Result: " + (ok ? "SUCCESS" : "FAILED"));
        };

        Runnable t2Task = () -> {
            System.out.println("[Thread-2] Attempting to book Room 999…");
            boolean ok = hotelService.bookRoom(999, "Thread Guest B", "9999999992",
                LocalDate.now(), LocalDate.now().plusDays(1));
            System.out.println("[Thread-2] Result: " + (ok ? "SUCCESS" : "FAILED"));
        };

        Thread t1 = new Thread(t1Task, "BookingThread-1");
        Thread t2 = new Thread(t2Task, "BookingThread-2");
        t1.start();
        t2.start();

        UIHelper.showSuccess(
            "Two booking threads launched.\n" +
            "Check the console to see synchronized execution — only one thread enters at a time."
        );
    }

    private void refreshRoomComboBox() {
        List<String> available = hotelService.getAvailableRooms().stream()
            .map(r -> r.getRoomNumber() + " (" + r.getRoomType() + "  —  ₹" + (int) r.getBasePrice() + ")")
            .collect(Collectors.toList());
        cbRoom.setItems(FXCollections.observableArrayList(available));
        if (!available.isEmpty()) cbRoom.getSelectionModel().selectFirst();
    }

    private void clearForm() {
        tfName.clear();
        tfContact.clear();
        dpCheckIn.setValue(LocalDate.now());
        dpCheckOut.setValue(LocalDate.now().plusDays(2));
        refreshRoomComboBox();
        lblCost.setText("Estimated cost will appear here");
    }
}
