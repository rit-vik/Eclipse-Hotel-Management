package hotel.ui;

import hotel.generics.Pair;
import hotel.model.Customer;
import hotel.model.Room;
import hotel.model.RoomType;
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

public class BillingPanel {

    private final HotelService hotelService;

    public BillingPanel(HotelService service) {
        this.hotelService = service;
    }

    public Node getView() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: #0d1f1a;");
        root.getChildren().add(UIHelper.sectionHeader("💰  Billing & Tariff Calculator"));

        HBox row = new HBox(20);
        row.getChildren().addAll(buildBillLookup(), buildEnumTariffCalc());
        root.getChildren().add(row);

        return root;
    }

    private VBox buildBillLookup() {
        VBox card = UIHelper.card("🧾  Generate Invoice");
        HBox.setHgrow(card, Priority.ALWAYS);

        Label lRoom = UIHelper.label("Room Number:");
        TextField tfRoom = UIHelper.textField("e.g. 201");

        Label lblResult = new Label("");
        lblResult.setFont(Font.font("Segoe UI", 13));
        lblResult.setWrapText(true);
        lblResult.setTextFill(Color.web(UIHelper.ACCENT2));

        VBox billDisplay = new VBox(10);
        billDisplay.setVisible(false);

        Button btnGen = UIHelper.primaryButton("Generate Invoice");
        btnGen.setOnAction(e -> {
            try {
                int roomNo = Integer.parseInt(tfRoom.getText().trim());
                Customer c = hotelService.getCustomerByRoom(roomNo);
                Room r     = hotelService.findRoom(roomNo).orElse(null);

                if (c == null || r == null) {
                    lblResult.setText("No active booking found for Room " + roomNo);
                    billDisplay.setVisible(false);
                    return;
                }

                Pair<Integer, Double> bill = hotelService.calculateBill(roomNo);
                Integer nights  = (int) c.getNights();
                Double  total   = bill.getSecond();
                Double  perNight = r.getBasePrice();

                buildBillDisplay(billDisplay, c, r, nights, total, perNight);
                billDisplay.setVisible(true);
                lblResult.setText("");

            } catch (NumberFormatException ex) {
                lblResult.setText("Please enter a valid room number.");
                billDisplay.setVisible(false);
            }
        });

        card.getChildren().addAll(lRoom, tfRoom, btnGen, lblResult, billDisplay);
        return card;
    }

    private void buildBillDisplay(VBox container, Customer c, Room r,
                                   Integer nights, Double total, Double perNight) {
        container.getChildren().clear();
        container.setStyle(
            "-fx-background-color: #112219;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #26c97a;" +
            "-fx-border-radius: 10;" +
            "-fx-padding: 16;"
        );

        Label title = new Label("HOTEL INVOICE");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        title.setTextFill(Color.web(UIHelper.ACCENT2));

        Separator sep = new Separator();

        String[][] rows = {
            {"Guest",       c.getName()},
            {"Contact",     c.getContactNumber()},
            {"Room",        r.getRoomNumber() + "  (" + r.getRoomType() + ")"},
            {"Check-In",    c.getCheckInDate().toString()},
            {"Check-Out",   c.getCheckOutDate().toString()},
            {"Nights",      nights + " nights"},
            {"Rate / Night","₹" + perNight.intValue()},
            {"Total",       "₹" + String.format("%.2f", total)},
        };

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(9);

        for (int i = 0; i < rows.length; i++) {
            Label k = new Label(rows[i][0] + ":");
            k.setTextFill(Color.web(UIHelper.TEXT_GREY));
            k.setFont(Font.font("Segoe UI", 12));

            boolean isTotal = (i == rows.length - 1);
            Label v = new Label(rows[i][1]);
            v.setFont(Font.font("Segoe UI", isTotal ? FontWeight.BOLD : FontWeight.NORMAL,
                                isTotal ? 18 : 13));
            v.setTextFill(isTotal ? Color.web(UIHelper.SUCCESS) : Color.WHITE);

            grid.add(k, 0, i);
            grid.add(v, 1, i);
        }

        ColumnConstraints cc0 = new ColumnConstraints(100);
        ColumnConstraints cc1 = new ColumnConstraints();
        cc1.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(cc0, cc1);

        container.getChildren().addAll(title, sep, grid);
    }

    private VBox buildEnumTariffCalc() {
        VBox card = UIHelper.card("📋  Tariff Calculator");
        card.setMinWidth(300);

        Label lType = UIHelper.label("Room Type:");
        ComboBox<RoomType> cbType = UIHelper.styledComboBox();
        cbType.setItems(FXCollections.observableArrayList(RoomType.values()));
        cbType.getSelectionModel().selectFirst();
        cbType.setMaxWidth(Double.MAX_VALUE);

        Label lNights = UIHelper.label("Number of Nights:");
        TextField tfNights = UIHelper.textField("e.g. 3");
        tfNights.setText("3");

        Button btnCalc = UIHelper.primaryButton("Calculate Cost");

        VBox resultBox = new VBox(8);
        resultBox.setVisible(false);
        resultBox.setPadding(new Insets(14));
        resultBox.setStyle(
            "-fx-background-color: #26c97a22;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #26c97a;" +
            "-fx-border-radius: 8;"
        );

        btnCalc.setOnAction(e -> {
            try {
                RoomType rt  = cbType.getValue();
                Integer nights = Integer.parseInt(tfNights.getText().trim());
                Double cost  = rt.calculateCost(nights);

                resultBox.getChildren().clear();

                Label rtLabel = new Label(rt.getDisplayName());
                rtLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
                rtLabel.setTextFill(Color.WHITE);

                Label ppn = new Label("₹" + rt.getPricePerNight() + " / night");
                ppn.setFont(Font.font("Segoe UI", 12));
                ppn.setTextFill(Color.web(UIHelper.TEXT_GREY));

                Label nLabel = new Label(nights + " nights");
                nLabel.setFont(Font.font("Segoe UI", 12));
                nLabel.setTextFill(Color.web(UIHelper.TEXT_GREY));

                Label totalLabel = new Label("₹" + String.format("%.0f", cost));
                totalLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
                totalLabel.setTextFill(Color.web(UIHelper.ACCENT2));

                resultBox.getChildren().addAll(rtLabel, ppn, nLabel, totalLabel);
                resultBox.setVisible(true);

            } catch (NumberFormatException ex) {
                resultBox.setVisible(false);
            }
        });

        // Room type reference list
        VBox typeList = new VBox(8);
        Label listTitle = new Label("All Room Types");
        listTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        listTitle.setTextFill(Color.web(UIHelper.ACCENT2));
        typeList.getChildren().add(listTitle);

        for (RoomType rt : RoomType.values()) {
            HBox rowBox = new HBox(10);
            rowBox.setAlignment(Pos.CENTER_LEFT);
            Label nm = new Label(rt.getDisplayName());
            nm.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
            nm.setTextFill(Color.WHITE);
            nm.setMinWidth(80);
            Label pr = new Label("₹" + rt.getPricePerNight() + " / night");
            pr.setFont(Font.font("Segoe UI", 12));
            pr.setTextFill(Color.web(UIHelper.ACCENT2));
            rowBox.getChildren().addAll(nm, pr);
            typeList.getChildren().add(rowBox);
        }

        card.getChildren().addAll(lType, cbType, lNights, tfNights, btnCalc, resultBox, typeList);
        return card;
    }
}
