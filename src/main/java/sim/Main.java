package sim;

import com.google.gson.Gson;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sim.talents.TalentTree;
import sim.talents.TalentsController;

import java.io.InputStreamReader;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("talents/fxml/Talents.fxml"));

        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 720, 420));
        primaryStage.getScene().getStylesheets().add(getClass().getResource("talents/css/Talents.css").toExternalForm());

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
