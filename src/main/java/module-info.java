module home_bookkeeping {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.xml;

    opens ru.home_bookkeeping;
    opens ru.home_bookkeeping.backend.model;
    opens styles;
    exports ru.home_bookkeeping;
    exports ru.home_bookkeeping.frontend;
    opens ru.home_bookkeeping.frontend;
}