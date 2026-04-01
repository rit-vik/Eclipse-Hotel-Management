package hotel.ui;

import hotel.model.Customer;
import hotel.model.Room;
import hotel.service.HotelService;
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

import java.util.List;
import java.util.stream.Collectors;

public class CheckoutPanel {

    private final HotelService hotelService;
    private ComboBox<String> cbBookedRooms;
    private VBox guestInfoCard;
    private Label lblStatus;

    public CheckoutPanel(HotelService service) {
        this.hotelService = service;
    }

    public Node getView() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: #0d1f1a;");
        root.getChildren().add(UIHelper.sectionHeader("🚪  Guest Checkout"));

        HBox mainRow = new HBox(20);
        mainRow.getChildren().addAll(buildCheckoutForm(), buildGuestInfoCard());
        root.getChildren().add(mainRow);

        return root;
    }

    private VBox buildCheckoutForm() {
        VBox card = UIHelper.card("🏃  Process Checkout");
        card.setMinWidth(320);
        card.setMaxWidth(360);

        Label lRoom = UIHelper.label("Select Booked Room:");
        cbBookedRooms = UIHelper.styledComboBox();
        cbBookedRooms.setMaxWidth(Double.MAX_VALUE);
        refreshBookedRooms();
        cbBookedRooms.setOnAction(e -> showGuestInfo());

        Button btnCheckout = new Button("🚪  Checkout Guest");
        btnCheckout.setMaxWidth(Double.MAX_VALUE);
        btnCheckout.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        btnCheckout.setStyle(
            "-fx-background-color: #d4a017;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 10 20 10 20;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;"
        );

        Button btnRefresh = UIHelper.secondaryButton("🔄  Refresh");
        btnRefresh.setMaxWidth(Double.MAX_VALUE);

        lblStatus = new Label("");
        lblStatus.setFont(Font.font("Segoe UI", 13));
        lblStatus.setWrapText(true);

        btnCheckout.setOnAction(e -> handleCheckout());
        btnRefresh.setOnAction(e -> { refreshBookedRooms(); clearGuestInfo(); });

        card.getChildren().addAll(lRoom, cbBookedRooms, btnCheckout, btnRefresh, lblStatus);
        return card;
    }

    private VBox buildGuestInfoCard() {
        guestInfoCard = UIHelper.card("👤  Guest Information");
        HBox.setHgrow(guestInfoCard, Priority.ALWAYS);
        showEmptyGuestInfo();
        return guestInfoCard;
    }

    private void showGuestInfo() {
        clearGuestInfo();
        String sel = cbBookedRooms.getValue();
        if (sel == null) return;

        int roomNo  = Integer.parseInt(sel.split(" ")[0]);
        Customer c  = hotelService.getCustomerByRoom(roomNo);
        Room r      = hotelService.findRoom(roomNo).orElse(null);
        if (c == null || r == null) return;

        long nights  = c.getNights();
        double total = r.calculateTariff((int) nights);

        String[][] rows = {
            {"Guest Name",  c.getName()},
            {"Contact",     c.getContactNumber()},
            {"Room",        roomNo + "  (" + r.getRoomType() + ")"},
            {"Check-In",    c.getCheckInDate().toString()},
            {"Check-Out",   c.getCheckOutDate().toString()},
            {"Nights",      nights + " night(s)"},
            {"Base Price",  "₹" + (int) r.getBasePrice() + " / night"},
            {"Total Bill",  "₹" + String.format("%.0f", total)},
        };

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(12);

        for (int i = 0; i < rows.length; i++) {
            Label key = new Label(rows[i][0] + ":");
            key.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
            key.setTextFill(Color.web(UIHelper.TEXT_GREY));

            Label val = new Label(rows[i][1]);
            boolean isBill = rows[i][0].equals("Total Bill");
            val.setFont(Font.font("Segoe UI", isBill ? FontWeight.BOLD : FontWeight.NORMAL, isBill ? 18 : 13));
            val.setTextFill(Color.web(isBill ? UIHelper.SUCCESS : "white"));

            grid.add(key, 0, i);
            grid.add(val, 1, i);
        }

        ColumnConstraints c0 = new ColumnConstraints(120);
        ColumnConstraints c1 = new ColumnConstraints();
        c1.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(c0, c1);

        VBox billBox = new VBox(6);
        billBox.setPadding(new Insets(16));
        billBox.setAlignment(Pos.CENTER);
        billBox.setStyle(
            "-fx-background-color: #2ecc7122;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #2ecc71;" +
            "-fx-border-radius: 10;"
        );

        Label billTitle = new Label("Total Bill");
        billTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        billTitle.setTextFill(Color.web(UIHelper.SUCCESS));

        Label billAmt = new Label("₹" + String.format("%.0f", total));
        billAmt.setFont(Font.font("Segoe UI", FontWeight.BOLD, 40));
        billAmt.setTextFill(Color.web(UIHelper.SUCCESS));

        Label billBreak = new Label(
            nights + " nights  ×  ₹" + (int) r.getBasePrice() + " / night"
        );
        billBreak.setFont(Font.font("Segoe UI", 11));
        billBreak.setTextFill(Color.web(UIHelper.TEXT_GREY));

        billBox.getChildren().addAll(billTitle, billAmt, billBreak);
        guestInfoCard.getChildren().addAll(grid, billBox);
    }

    private void showEmptyGuestInfo() {
        clearGuestInfo();
        Label lbl = new Label("← Select a booked room to view guest details");
        lbl.setFont(Font.font("Segoe UI", 13));
        lbl.setTextFill(Color.web(UIHelper.TEXT_GREY));
        guestInfoCard.getChildren().add(lbl);
    }

    private void clearGuestInfo() {
        while (guestInfoCard.getChildren().size() > 2) {
            guestInfoCard.getChildren().remove(2);
        }
    }

    private void handleCheckout() {
        String sel = cbBookedRooms.getValue();
        if (sel == null) {
            lblStatus.setText("Please select a booked room.");
            lblStatus.setTextFill(Color.web(UIHelper.DANGER));
            return;
        }

        int roomNo = Integer.parseInt(sel.split(" ")[0]);
        Customer c = hotelService.getCustomerByRoom(roomNo);
        if (c == null) {
            lblStatus.setText("No booking found for Room " + roomNo);
            lblStatus.setTextFill(Color.web(UIHelper.DANGER));
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Checkout");
        confirm.setHeaderText("Checkout " + c.getName() + " from Room " + roomNo + "?");
        confirm.setContentText("This will release the room and update availability.");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                boolean success = hotelService.checkoutRoom(roomNo);
                if (success) {
                    lblStatus.setText("✅  Checkout successful. Room " + roomNo + " is now available.");
                    lblStatus.setTextFill(Color.web(UIHelper.SUCCESS));
                    refreshBookedRooms();
                    showEmptyGuestInfo();
                } else {
                    lblStatus.setText("Checkout failed. Please try again.");
                    lblStatus.setTextFill(Color.web(UIHelper.DANGER));
                }
            }
        });
    }

    private void refreshBookedRooms() {
        List<String> booked = hotelService.getAllRooms().stream()
            .filter(Room::isBooked)
            .map(r -> r.getRoomNumber() + " (" + r.getRoomType() + "  —  " + r.getGuestName() + ")")
            .collect(Collectors.toList());
        cbBookedRooms.setItems(FXCollections.observableArrayList(booked));
        if (!booked.isEmpty()) cbBookedRooms.getSelectionModel().selectFirst();
    }
}
