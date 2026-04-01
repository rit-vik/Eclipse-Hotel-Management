package hotel.ui;

import hotel.service.HotelService;
import hotel.util.UIHelper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class LogPanel {

    private final HotelService hotelService;
    private ListView<String> logView;

    public LogPanel(HotelService service) {
        this.hotelService = service;
    }

    public Node getView() {
        VBox root = new VBox(16);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: #0d1f1a;");
        root.getChildren().add(UIHelper.sectionHeader("📁  Activity Log"));

        HBox mainRow = new HBox(20);
        mainRow.getChildren().addAll(buildLogViewer(), buildFileOpsCard());
        root.getChildren().add(mainRow);

        return root;
    }

    private VBox buildLogViewer() {
        VBox card = UIHelper.card("📋  Activity Log");
        HBox.setHgrow(card, Priority.ALWAYS);

        logView = new ListView<>();
        logView.setStyle(
            "-fx-background-color: #071410;" +
            "-fx-border-color: #1a7a4a;"
        );
        logView.setMinHeight(360);
        logView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle(
                    "-fx-text-fill: #aaddaa;" +
                    "-fx-font-family: 'Consolas';" +
                    "-fx-font-size: 12;"
                );
            }
        });
        refreshLog();

        HBox btnRow = new HBox(10);
        Button btnRefresh = UIHelper.secondaryButton("🔄  Refresh");
        Button btnCopy    = UIHelper.secondaryButton("📋  Copy Log File");

        btnRefresh.setOnAction(e -> refreshLog());
        btnCopy.setOnAction(e -> {
            hotelService.getActivityLog();
            UIHelper.showSuccess("Log file copied using byte streams (FileInputStream → FileOutputStream).");
        });

        btnRow.getChildren().addAll(btnRefresh, btnCopy);
        card.getChildren().addAll(logView, btnRow);
        return card;
    }

    private void refreshLog() {
        List<String> entries = hotelService.getActivityLog();
        if (entries.isEmpty()) {
            logView.setItems(FXCollections.observableArrayList(
                "No log entries yet.  Perform a booking or add a room to start logging."));
        } else {
            logView.setItems(FXCollections.observableArrayList(entries));
            logView.scrollTo(entries.size() - 1);
        }
    }

    private VBox buildFileOpsCard() {
        VBox card = UIHelper.card("⚙  File Operations");
        card.setMinWidth(280);
        card.setMaxWidth(300);

        VBox byteCard = buildInfoCard("Byte Streams",
            "FileInputStream + FileOutputStream\nBinary copy of the activity log file.",
            "#1abc9c");

        VBox charCard = buildInfoCard("Character Streams",
            "FileWriter appends each action to hotel_activity.log.\nFileReader / BufferedReader reads it back.",
            "#26a96c");

        VBox rafCard = buildInfoCard("RandomAccessFile",
            "Rooms stored as fixed-size binary records.\nseek(index × recordSize) for direct access.",
            "#e67e22");

        VBox serCard = buildInfoCard("Serialization",
            "Bookings serialized via ObjectOutputStream to bookings.dat.\nRestored with ObjectInputStream.",
            "#2ecc71");

        Button btnRAFSave = UIHelper.secondaryButton("💾  Save Rooms to File");
        btnRAFSave.setMaxWidth(Double.MAX_VALUE);
        btnRAFSave.setOnAction(e -> {
            hotelService.saveRoomsToRandomAccessFile();
            UIHelper.showSuccess("Rooms written to rooms.raf.\nUse the Rooms tab to query by record index.");
        });

        card.getChildren().addAll(byteCard, charCard, rafCard, serCard, btnRAFSave);
        return card;
    }

    private VBox buildInfoCard(String title, String body, String color) {
        VBox c = new VBox(5);
        c.setPadding(new Insets(10));
        c.setStyle(
            "-fx-background-color: " + color + "1a;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: " + color + "55;" +
            "-fx-border-radius: 8;"
        );
        Label t = new Label(title);
        t.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        t.setTextFill(Color.web(color));
        Label b = new Label(body);
        b.setFont(Font.font("Segoe UI", 11));
        b.setTextFill(Color.web("#aaaaaa"));
        b.setWrapText(true);
        c.getChildren().addAll(t, b);
        return c;
    }

}

