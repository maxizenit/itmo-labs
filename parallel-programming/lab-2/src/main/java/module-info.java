module org.itmo.carpet {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;

    opens org.itmo.carpet to javafx.fxml;
    opens org.itmo.carpet.model to javafx.fxml;

    exports org.itmo.carpet;
    exports org.itmo.carpet.model;
}