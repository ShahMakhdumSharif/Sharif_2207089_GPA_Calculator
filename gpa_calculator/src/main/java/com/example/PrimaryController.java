package com.example;

import java.io.IOException;
import javafx.fxml.FXML;

// Primary controller
public class PrimaryController {

    // Start -> secondary
    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
}
