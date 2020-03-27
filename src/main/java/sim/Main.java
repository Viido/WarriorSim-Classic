package sim;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("talents/fxml/Talents.fxml"));

        primaryStage.setTitle("WarriorSim Classic");
        primaryStage.setScene(new Scene(root, 720, 420));
        primaryStage.getScene().getStylesheets().add(getClass().getResource("talents/css/Talents.css").toExternalForm());
        primaryStage.getIcons().add(new Image(getClass().getResource("/images/app/warrior-wow-icon.png").toExternalForm()));

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
