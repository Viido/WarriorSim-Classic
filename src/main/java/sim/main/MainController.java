package sim.main;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    VBox itemsView;
    @FXML
    HBox settingsView;
    @FXML
    BorderPane talentsView;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println(itemsView.getLayoutX());
    }
}
