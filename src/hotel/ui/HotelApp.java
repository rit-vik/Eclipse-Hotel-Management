package hotel.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import hotel.service.HotelService;

public class HotelApp extends Application {

    private HotelService hotelService;

    @Override
    public void start(Stage primaryStage) {
        hotelService = new HotelService();

        primaryStage.setTitle("Eclipse Hotel — Management System");
        primaryStage.setMinWidth(1100);
        primaryStage.setMinHeight(700);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0d1f1a;");

        root.setTop(buildHeader());

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle(
            "-fx-background-color: #112219;" +
            "-fx-tab-min-width: 140px;" +
            "-fx-tab-max-width: 140px;"
        );

        Tab dashboardTab = new Tab("📊  Dashboard",   new DashboardPanel(hotelService).getView());
        Tab roomsTab     = new Tab("🛏  Rooms",        new RoomsPanel(hotelService).getView());
        Tab bookingTab   = new Tab("📋  Booking",      new BookingPanel(hotelService, tabPane).getView());
        Tab checkoutTab  = new Tab("🚪  Checkout",     new CheckoutPanel(hotelService).getView());
        Tab billingTab   = new Tab("💰  Billing",      new BillingPanel(hotelService).getView());
        Tab logTab       = new Tab("📁  Activity Log", new LogPanel(hotelService).getView());

        tabPane.getTabs().addAll(dashboardTab, roomsTab, bookingTab, checkoutTab, billingTab, logTab);
        root.setCenter(tabPane);
        root.setBottom(buildFooter());

        Scene scene = new Scene(root, 1150, 720);
        scene.setFill(Color.web("#0d1f1a"));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private HBox buildHeader() {
        HBox header = new HBox();
        header.setStyle("-fx-background-color: linear-gradient(to right, #0a3d2b, #1a7a4a);");
        header.setPadding(new Insets(14, 28, 14, 28));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(16);

        Label icon = new Label("🌘");
        icon.setFont(Font.font("Segoe UI", 30));

        VBox titleBox = new VBox(3);
        Label title = new Label("Eclipse Hotel");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        title.setTextFill(Color.WHITE);
        Label subtitle = new Label("Hotel Management System  ·  JavaFX Desktop Application");
        subtitle.setFont(Font.font("Segoe UI", 12));
        subtitle.setTextFill(Color.web("#b2dfcb"));
        titleBox.getChildren().addAll(title, subtitle);

        header.getChildren().addAll(icon, titleBox);
        return header;
    }

    private HBox buildFooter() {
        HBox footer = new HBox();
        footer.setStyle("-fx-background-color: #0a3d2b;");
        footer.setPadding(new Insets(5, 20, 5, 20));
        footer.setAlignment(Pos.CENTER);
        return footer;
    }

    public static void main(String[] args) {
        launch(args);
    }
}