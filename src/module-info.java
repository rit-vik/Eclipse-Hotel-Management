module hotel.management {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;

    exports hotel.ui;
    exports hotel.model;
    exports hotel.service;
    exports hotel.io;
    exports hotel.util;
    exports hotel.generics;
}
