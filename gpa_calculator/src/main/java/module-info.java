module com.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.base;
    requires transitive javafx.graphics;
    requires java.sql;

    
    opens com.example to javafx.fxml;
    exports com.example;
}
