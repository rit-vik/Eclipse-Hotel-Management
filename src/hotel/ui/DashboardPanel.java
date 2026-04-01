package hotel.ui;

import hotel.model.Room;
import hotel.service.HotelService;
import hotel.util.UIHelper;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DashboardPanel {

    private final HotelService hotelService;

    // Stat value labels updated by the timeline
    private Label totalRoomsVal, availableVal, bookedVal, occupancyVal;

    // Card container for room-type rows — content rebuilt on every tick
    private VBox summaryCard;

    public DashboardPanel(HotelService service) {
        this.hotelService = service;
    }

    public Node getView() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: #0d1f1a;");

        root.getChildren().add(buildBanner());
        root.getChildren().add(buildStatsRow());

        // Keep a reference to the summary card so we can repopulate it live
        summaryCard = UIHelper.card("🛏  Room Type Summary");
        populateSummaryCard();
        root.getChildren().add(summaryCard);

        // One timeline refreshes both stat numbers and the summary card
        Timeline refresher = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
            refreshStats();
            populateSummaryCard();
        }));
        refresher.setCycleCount(Timeline.INDEFINITE);
        refresher.play();

        return root;
    }

    // ----------------------------------------------------------------
    // Banner
    // ----------------------------------------------------------------
    private HBox buildBanner() {
        HBox banner = new HBox(14);
        banner.setAlignment(Pos.CENTER_LEFT);
        banner.setPadding(new Insets(18, 24, 18, 24));
        banner.setStyle(
            "-fx-background-color: linear-gradient(to right, #0a3d2b, #1a7a4a);" +
            "-fx-background-radius: 12;"
        );

        Label icon = new Label("🌘");
        icon.setFont(Font.font("Segoe UI", 32));

        VBox text = new VBox(4);
        Label title = new Label("Welcome to Eclipse Hotel");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        title.setTextFill(Color.WHITE);
        Label sub = new Label("Manage rooms, bookings, billing and guest records from one place.");
        sub.setFont(Font.font("Segoe UI", 13));
        sub.setTextFill(Color.web("#b2dfcb"));
        text.getChildren().addAll(title, sub);

        banner.getChildren().addAll(icon, text);
        return banner;
    }

    // ----------------------------------------------------------------
    // Stats row
    // ----------------------------------------------------------------
    private HBox buildStatsRow() {
        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER);

        totalRoomsVal = new Label(String.valueOf(hotelService.getTotalRoomCount()));
        availableVal  = new Label(String.valueOf(hotelService.getAvailableRoomCount()));
        bookedVal     = new Label(String.valueOf(
            hotelService.getTotalRoomCount() - hotelService.getAvailableRoomCount()));
        occupancyVal  = new Label(computeOccupancy() + "%");

        VBox c1 = buildStatCard(totalRoomsVal, "Total Rooms", "#26a96c");
        VBox c2 = buildStatCard(availableVal,  "Available",   "#2ecc71");
        VBox c3 = buildStatCard(bookedVal,     "Booked",      "#f0a500");
        VBox c4 = buildStatCard(occupancyVal,  "Occupancy",   "#1abc9c");

        for (VBox c : new VBox[]{c1, c2, c3, c4}) {
            HBox.setHgrow(c, Priority.ALWAYS);
            c.setMaxWidth(Double.MAX_VALUE);
        }
        row.getChildren().addAll(c1, c2, c3, c4);
        return row;
    }

    private VBox buildStatCard(Label valueLabel, String title, String color) {
        VBox card = new VBox(6);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(22));
        card.setStyle(
            "-fx-background-color: " + color + "22;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: " + color + ";" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 2;"
        );
        valueLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 38));
        valueLabel.setTextFill(Color.web(color));

        Label nameLbl = new Label(title);
        nameLbl.setFont(Font.font("Segoe UI", 13));
        nameLbl.setTextFill(Color.web("#aaaaaa"));
        card.getChildren().addAll(valueLabel, nameLbl);
        return card;
    }

    private void refreshStats() {
        totalRoomsVal.setText(String.valueOf(hotelService.getTotalRoomCount()));
        availableVal.setText(String.valueOf(hotelService.getAvailableRoomCount()));
        int booked = hotelService.getTotalRoomCount() - hotelService.getAvailableRoomCount();
        bookedVal.setText(String.valueOf(booked));
        occupancyVal.setText(computeOccupancy() + "%");
    }

    private int computeOccupancy() {
        int total = hotelService.getTotalRoomCount();
        if (total == 0) return 0;
        return (int)((double)(total - hotelService.getAvailableRoomCount()) / total * 100);
    }

    // ----------------------------------------------------------------
    // Room Type Summary — rebuilt from live HotelService data each tick
    // ----------------------------------------------------------------
    private static final Map<String, String> TYPE_COLOURS = new LinkedHashMap<>();
    static {
        TYPE_COLOURS.put("Standard", "#26a96c");
        TYPE_COLOURS.put("Deluxe",   "#1abc9c");
        TYPE_COLOURS.put("Suite",    "#f0a500");
    }

    private void populateSummaryCard() {
        // Preserve the card title + separator (indices 0 and 1), clear the rest
        while (summaryCard.getChildren().size() > 2) {
            summaryCard.getChildren().remove(2);
        }

        List<Room> allRooms = (List<Room>) hotelService.getAllRooms();

        if (allRooms.isEmpty()) {
            Label empty = new Label("No rooms added yet.");
            empty.setFont(Font.font("Segoe UI", 13));
            empty.setTextFill(Color.web("#aaaaaa"));
            summaryCard.getChildren().add(empty);
            return;
        }

        // Aggregate counts and availability per room type from live data
        Map<String, int[]> stats = new LinkedHashMap<>(); // type -> [total, available]
        for (Room r : allRooms) {
            String type = r.getRoomType();
            stats.putIfAbsent(type, new int[]{0, 0});
            stats.get(type)[0]++;
            if (!r.isBooked()) stats.get(type)[1]++;
        }

        for (Map.Entry<String, int[]> entry : stats.entrySet()) {
            String type  = entry.getKey();
            int total    = entry.getValue()[0];
            int avail    = entry.getValue()[1];
            int booked   = total - avail;
            String color = TYPE_COLOURS.getOrDefault(type, "#26c97a");

            // Read price from the first room of this type
            double price = allRooms.stream()
                .filter(r -> r.getRoomType().equals(type))
                .mapToDouble(Room::getBasePrice)
                .findFirst()
                .orElse(0);

            HBox rowBox = new HBox(10);
            rowBox.setAlignment(Pos.CENTER_LEFT);
            rowBox.setPadding(new Insets(10, 12, 10, 12));
            rowBox.setStyle(
                "-fx-background-color: " + color + "18;" +
                "-fx-background-radius: 8;"
            );

            Label dot = new Label("●");
            dot.setTextFill(Color.web(color));
            dot.setFont(Font.font("Segoe UI", 14));

            Label nameLbl = new Label(type);
            nameLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
            nameLbl.setTextFill(Color.WHITE);
            nameLbl.setMinWidth(80);

            String countText = total + (total == 1 ? " room" : " rooms") +
                "   (" + avail + " available,  " + booked + " booked)";
            Label countLbl = new Label(countText);
            countLbl.setTextFill(Color.web("#aaaaaa"));
            countLbl.setFont(Font.font("Segoe UI", 12));

            Region sp = new Region();
            HBox.setHgrow(sp, Priority.ALWAYS);

            Label priceLbl = new Label("₹" + (int) price + " / night");
            priceLbl.setTextFill(Color.web(color));
            priceLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));

            rowBox.getChildren().addAll(dot, nameLbl, countLbl, sp, priceLbl);
            summaryCard.getChildren().add(rowBox);
        }
    }
}