package hotel.ui;

import hotel.model.*;
import hotel.service.HotelService;
import hotel.util.UIHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class RoomsPanel {

    private final HotelService hotelService;
    private TableView<RoomRow> table;
    private ObservableList<RoomRow> tableData;

    public RoomsPanel(HotelService service) {
        this.hotelService = service;
    }

    public Node getView() {
        VBox root = new VBox(16);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: #0d1f1a;");

        root.getChildren().add(UIHelper.sectionHeader("🛏  Room Management"));

        HBox mainRow = new HBox(20);
        VBox form  = buildAddRoomForm();
        VBox tbl   = buildRoomTable();
        HBox.setHgrow(tbl, Priority.ALWAYS);
        mainRow.getChildren().addAll(form, tbl);
        root.getChildren().add(mainRow);

        root.getChildren().add(buildSortBar());
        root.getChildren().add(buildRAFCard());

        return root;
    }

    private VBox buildAddRoomForm() {
        VBox card = UIHelper.card("➕  Add New Room");
        card.setMinWidth(280);
        card.setMaxWidth(300);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);

        Label lRoomNo = UIHelper.label("Room Number:");
        TextField tfRoomNo = UIHelper.textField("e.g. 103");

        Label lType = UIHelper.label("Room Type:");
        ComboBox<RoomType> cbType = UIHelper.styledComboBox();
        cbType.setItems(FXCollections.observableArrayList(RoomType.values()));
        cbType.getSelectionModel().selectFirst();
        cbType.setMaxWidth(Double.MAX_VALUE);

        Label lPrice = UIHelper.label("Price / Night (₹):");
        TextField tfPrice = UIHelper.textField("e.g. 2500");
        tfPrice.setText(String.valueOf(RoomType.STANDARD.getPricePerNight()));

        Label lAmen = UIHelper.label("Amenities:");
        CheckBox cbWifi      = new CheckBox("Free WiFi");
        CheckBox cbBreakfast = new CheckBox("Breakfast");
        cbWifi.setTextFill(Color.WHITE);
        cbWifi.setFont(Font.font("Segoe UI", 12));
        cbBreakfast.setTextFill(Color.WHITE);
        cbBreakfast.setFont(Font.font("Segoe UI", 12));
        HBox amenBox = new HBox(10, cbWifi, cbBreakfast);

        Label lPremium = UIHelper.label("Premium Charge (₹):");
        TextField tfPremium = UIHelper.textField("e.g. 2000");
        tfPremium.setVisible(false);
        lPremium.setVisible(false);
        amenBox.setVisible(false);
        lAmen.setVisible(false);

        cbType.setOnAction(e -> {
            RoomType rt = cbType.getValue();
            if (rt != null) tfPrice.setText(String.valueOf(rt.getPricePerNight()));
            boolean isSuite  = rt == RoomType.SUITE;
            boolean isDeluxe = rt == RoomType.DELUXE;
            tfPremium.setVisible(isSuite);
            lPremium.setVisible(isSuite);
            amenBox.setVisible(isDeluxe);
            lAmen.setVisible(isDeluxe);
        });

        Label statusLbl = new Label("");
        statusLbl.setFont(Font.font("Segoe UI", 12));
        statusLbl.setWrapText(true);

        Button btnAdd = UIHelper.primaryButton("Add Room");
        btnAdd.setMaxWidth(Double.MAX_VALUE);

        btnAdd.setOnAction(e -> {
            try {
                int roomNo    = Integer.parseInt(tfRoomNo.getText().trim());
                double price  = Double.parseDouble(tfPrice.getText().trim());
                RoomType rt   = cbType.getValue();

                if (hotelService.findRoom(roomNo).isPresent()) {
                    statusLbl.setText("Room " + roomNo + " already exists.");
                    statusLbl.setTextFill(Color.web(UIHelper.DANGER));
                    return;
                }

                Room room;
                if (rt == RoomType.STANDARD) {
                    room = new StandardRoom(roomNo, price);
                } else if (rt == RoomType.DELUXE) {
                    room = new DeluxeRoom(roomNo, price, cbWifi.isSelected(), cbBreakfast.isSelected());
                } else {
                    double premium = tfPremium.getText().isBlank() ? 2000
                        : Double.parseDouble(tfPremium.getText().trim());
                    room = new SuiteRoom(roomNo, price, premium);
                }

                hotelService.addRoom(room);
                hotelService.saveRoomsToRandomAccessFile();
                refreshTable();

                statusLbl.setText("✅  Room " + roomNo + " (" + rt.getDisplayName() + ") added.");
                statusLbl.setTextFill(Color.web(UIHelper.SUCCESS));
                tfRoomNo.clear();
                tfPrice.setText(String.valueOf(rt.getPricePerNight()));
                cbWifi.setSelected(false);
                cbBreakfast.setSelected(false);
                tfPremium.clear();

            } catch (NumberFormatException ex) {
                statusLbl.setText("Please enter valid numbers.");
                statusLbl.setTextFill(Color.web(UIHelper.DANGER));
            }
        });

        grid.add(lRoomNo,  0, 0); grid.add(tfRoomNo,  1, 0);
        grid.add(lType,    0, 1); grid.add(cbType,    1, 1);
        grid.add(lPrice,   0, 2); grid.add(tfPrice,   1, 2);
        grid.add(lAmen,    0, 3); grid.add(amenBox,   1, 3);
        grid.add(lPremium, 0, 4); grid.add(tfPremium, 1, 4);

        ColumnConstraints cc0 = new ColumnConstraints(115);
        ColumnConstraints cc1 = new ColumnConstraints();
        cc1.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(cc0, cc1);

        // Tariff reference card
        VBox tariffCard = new VBox(6);
        tariffCard.setStyle(
            "-fx-background-color: #0a3d2b33;" +
            "-fx-background-radius: 6;" +
            "-fx-padding: 10;"
        );
        Label tariffTitle = new Label("Default Tariffs");
        tariffTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        tariffTitle.setTextFill(Color.web(UIHelper.ACCENT2));
        tariffCard.getChildren().add(tariffTitle);

        for (RoomType rt : RoomType.values()) {
            Label rtLbl = new Label(
                rt.getDisplayName() + ":  ₹" + rt.getPricePerNight() + " / night" +
                "   (3 nights = ₹" + rt.calculateCost(3).intValue() + ")"
            );
            rtLbl.setFont(Font.font("Segoe UI", 11));
            rtLbl.setTextFill(Color.web("#aaaaaa"));
            tariffCard.getChildren().add(rtLbl);
        }

        card.getChildren().addAll(grid, btnAdd, statusLbl, tariffCard);
        return card;
    }

    private VBox buildRoomTable() {
        VBox card = UIHelper.card("📋  All Rooms");
        HBox.setHgrow(card, Priority.ALWAYS);

        table = new TableView<>();
        table.setStyle(
            "-fx-background-color: #0a3d2b;" +
            "-fx-text-fill: white;" +
            "-fx-table-cell-border-color: #0d1f1a;"
        );
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<RoomRow, Integer> colNo = new TableColumn<>("Room #");
        colNo.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        colNo.setMinWidth(70);

        TableColumn<RoomRow, String> colType = new TableColumn<>("Type");
        colType.setCellValueFactory(new PropertyValueFactory<>("roomType"));

        TableColumn<RoomRow, String> colPrice = new TableColumn<>("Price / Night");
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<RoomRow, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle(item.equals("AVAILABLE")
                    ? "-fx-text-fill: #2ecc71; -fx-font-weight: bold;"
                    : "-fx-text-fill: #1a7a4a; -fx-font-weight: bold;");
            }
        });

        TableColumn<RoomRow, String> colGuest = new TableColumn<>("Guest");
        colGuest.setCellValueFactory(new PropertyValueFactory<>("guest"));

        TableColumn<RoomRow, String> colTariff = new TableColumn<>("Tariff (3 nights)");
        colTariff.setCellValueFactory(new PropertyValueFactory<>("tariff3nights"));

        table.getColumns().addAll(colNo, colType, colPrice, colStatus, colGuest, colTariff);
        table.setMinHeight(320);

        tableData = FXCollections.observableArrayList();
        refreshTable();
        table.setItems(tableData);

        card.getChildren().add(table);
        return card;
    }

    public void refreshTable() {
        if (tableData == null) return;
        tableData.clear();
        java.util.Iterator<Room> it = hotelService.getAllRooms().iterator();
        while (it.hasNext()) {
            Room r = it.next();
            tableData.add(new RoomRow(
                r.getRoomNumber(),
                r.getRoomType(),
                "₹" + (int) r.getBasePrice(),
                r.isBooked() ? "BOOKED" : "AVAILABLE",
                r.isBooked() ? r.getGuestName() : "—",
                "₹" + (int) r.calculateTariff(3)
            ));
        }
    }

    private HBox buildSortBar() {
        HBox bar = new HBox(12);
        bar.setAlignment(Pos.CENTER_LEFT);

        Label lbl = UIHelper.label("Sort rooms:");
        Button sortByPrice  = UIHelper.secondaryButton("By Price ↑");
        Button sortByNumber = UIHelper.secondaryButton("By Room No. ↑");

        sortByPrice.setOnAction(e -> {
            tableData.clear();
            hotelService.getRoomsSortedByPrice().forEach(r -> tableData.add(new RoomRow(
                r.getRoomNumber(), r.getRoomType(),
                "₹" + (int) r.getBasePrice(),
                r.isBooked() ? "BOOKED" : "AVAILABLE",
                r.isBooked() ? r.getGuestName() : "—",
                "₹" + (int) r.calculateTariff(3)
            )));
        });

        sortByNumber.setOnAction(e -> {
            tableData.clear();
            hotelService.getRoomsSortedByNumber().forEach(r -> tableData.add(new RoomRow(
                r.getRoomNumber(), r.getRoomType(),
                "₹" + (int) r.getBasePrice(),
                r.isBooked() ? "BOOKED" : "AVAILABLE",
                r.isBooked() ? r.getGuestName() : "—",
                "₹" + (int) r.calculateTariff(3)
            )));
        });

        bar.getChildren().addAll(lbl, sortByPrice, sortByNumber);
        return bar;
    }

    private VBox buildRAFCard() {
        VBox card = UIHelper.card("💾  Direct Record Access");
        card.setMaxHeight(120);

        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);

        Label lbl = UIHelper.label("Record index (0-based):");
        TextField tfIndex = UIHelper.textField("0");
        tfIndex.setMaxWidth(80);

        Button btnRead = UIHelper.secondaryButton("Read Record");

        Label resultLbl = new Label("Result will appear here…");
        resultLbl.setFont(Font.font("Segoe UI", 12));
        resultLbl.setTextFill(Color.web(UIHelper.ACCENT2));

        btnRead.setOnAction(e -> {
            try {
                hotelService.saveRoomsToRandomAccessFile();
                int idx = Integer.parseInt(tfIndex.getText().trim());
                resultLbl.setText(hotelService.readRoomFromRAF(idx));
            } catch (NumberFormatException ex) {
                resultLbl.setText("Please enter a valid index.");
            }
        });

        row.getChildren().addAll(lbl, tfIndex, btnRead);
        card.getChildren().addAll(row, resultLbl);
        return card;
    }

    // TableView row POJO
    public static class RoomRow {
        private final Integer roomNumber;
        private final String roomType, price, status, guest, tariff3nights;

        public RoomRow(Integer roomNumber, String roomType, String price,
                       String status, String guest, String tariff3nights) {
            this.roomNumber    = roomNumber;
            this.roomType      = roomType;
            this.price         = price;
            this.status        = status;
            this.guest         = guest;
            this.tariff3nights = tariff3nights;
        }

        public Integer getRoomNumber()   { return roomNumber; }
        public String getRoomType()      { return roomType; }
        public String getPrice()         { return price; }
        public String getStatus()        { return status; }
        public String getGuest()         { return guest; }
        public String getTariff3nights() { return tariff3nights; }
    }
}
