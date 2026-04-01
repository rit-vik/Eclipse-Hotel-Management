package hotel.util;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class UIHelper {

    public static final String BG_DARK    = "#0d1f1a";
    public static final String BG_CARD    = "#112219";
    public static final String BG_PANEL   = "#0a3d2b";
    public static final String ACCENT     = "#1a7a4a";
    public static final String ACCENT2    = "#26c97a";
    public static final String TEXT_WHITE = "white";
    public static final String TEXT_GREY  = "#aaaaaa";
    public static final String SUCCESS    = "#2ecc71";
    public static final String DANGER     = "#e74c3c";
    public static final String INFO       = "#1abc9c";

    private static final String FONT      = "Segoe UI";
    private static final String MONO      = "Consolas";

    public static Label sectionHeader(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font(FONT, FontWeight.BOLD, 17));
        lbl.setTextFill(Color.web(ACCENT2));
        lbl.setPadding(new Insets(0, 0, 6, 0));
        return lbl;
    }

    public static Label label(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font(FONT, 13));
        lbl.setTextFill(Color.WHITE);
        return lbl;
    }

    public static TextField textField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setFont(Font.font(FONT, 13));
        tf.setStyle(
            "-fx-background-color: #0a3d2b;" +
            "-fx-text-fill: white;" +
            "-fx-prompt-text-fill: #447755;" +
            "-fx-border-color: #1a7a4a;" +
            "-fx-border-radius: 5;" +
            "-fx-background-radius: 5;" +
            "-fx-padding: 7 10 7 10;"
        );
        return tf;
    }

    public static Button primaryButton(String text) {
        Button btn = new Button(text);
        btn.setFont(Font.font(FONT, FontWeight.BOLD, 13));
        String base =
            "-fx-background-color: #1a7a4a;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 9 22 9 22;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;";
        String hover =
            "-fx-background-color: #145c38;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 9 22 9 22;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;";
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e  -> btn.setStyle(base));
        return btn;
    }

    public static Button secondaryButton(String text) {
        Button btn = new Button(text);
        btn.setFont(Font.font(FONT, FontWeight.BOLD, 13));
        btn.setStyle(
            "-fx-background-color: #0a3d2b;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 9 22 9 22;" +
            "-fx-background-radius: 6;" +
            "-fx-border-color: #1a7a4a;" +
            "-fx-border-radius: 6;" +
            "-fx-cursor: hand;"
        );
        return btn;
    }

    public static Button successButton(String text) {
        Button btn = new Button(text);
        btn.setFont(Font.font(FONT, FontWeight.BOLD, 13));
        btn.setStyle(
            "-fx-background-color: #27ae60;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 9 22 9 22;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;"
        );
        return btn;
    }

    public static VBox card(String title) {
        VBox card = new VBox(10);
        card.setStyle(
            "-fx-background-color: #112219;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #0a3d2b;" +
            "-fx-border-radius: 10;" +
            "-fx-border-width: 1;"
        );
        card.setPadding(new Insets(16));
        if (title != null && !title.isEmpty()) {
            card.getChildren().add(sectionHeader(title));
            Separator sep = new Separator();
            sep.setStyle("-fx-background-color: #1a7a4a;");
            card.getChildren().add(sep);
        }
        return card;
    }

    public static <T> ComboBox<T> styledComboBox() {
        ComboBox<T> cb = new ComboBox<>();
        cb.setStyle(
            "-fx-background-color: #0a3d2b;" +
            "-fx-text-fill: white;" +
            "-fx-border-color: #1a7a4a;" +
            "-fx-border-radius: 5;" +
            "-fx-background-radius: 5;"
        );
        return cb;
    }

    public static DatePicker styledDatePicker() {
        DatePicker dp = new DatePicker();
        dp.setStyle(
            "-fx-background-color: #0a3d2b;" +
            "-fx-text-fill: white;" +
            "-fx-border-color: #1a7a4a;" +
            "-fx-border-radius: 5;" +
            "-fx-background-radius: 5;"
        );
        return dp;
    }

    public static void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showSuccess(String message) {
        showAlert("Success", message, Alert.AlertType.INFORMATION);
    }

    public static void showError(String message) {
        showAlert("Error", message, Alert.AlertType.ERROR);
    }
}
