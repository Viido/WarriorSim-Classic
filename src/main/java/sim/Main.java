package sim;

import com.jfoenix.controls.JFXMasonryPane;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sim.items.ItemsController;
import sim.main.Warrior;
import sim.settings.SettingsController;
import sim.talents.TalentsController;

import java.io.*;

public class Main extends Application {
    Warrior warrior = new Warrior();

    @Override
    public void start(Stage primaryStage) throws Exception{
        loadData();

        JFXMasonryPane root = new JFXMasonryPane();

        FXMLLoader itemsLoader = new FXMLLoader(getClass().getResource("items/fxml/ItemsView.fxml"));
        FXMLLoader talentsLoader = new FXMLLoader(getClass().getResource("talents/fxml/Talents.fxml"));
        FXMLLoader settingsLoader = new FXMLLoader(getClass().getResource("settings/fxml/SettingsView.fxml"));

        ItemsController itemsController = new ItemsController(warrior);
        TalentsController talentsController = new TalentsController(warrior);
        SettingsController settingsController = new SettingsController(warrior);

        itemsLoader.setController(itemsController);
        talentsLoader.setController(talentsController);
        settingsLoader.setController(settingsController);

        root.getChildren().add(itemsLoader.load());
        root.getChildren().add(talentsLoader.load());
        root.getChildren().add(settingsLoader.load());

        root.setStyle("-fx-background-color: #282828");

        primaryStage.setTitle("WarriorSim Classic");
        primaryStage.setScene(new Scene(root));
        primaryStage.getIcons().add(new Image(getClass().getResource("/images/app/warrior-wow-icon.png").toExternalForm()));

        primaryStage.setMaximized(true);


        primaryStage.setOnCloseRequest(e -> {
            saveData();
            primaryStage.close();
        });

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    private void saveData(){
        try{
            FileOutputStream fileOut = new FileOutputStream("warrior.ser", false);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);

            out.writeObject(warrior);
            out.close();
            fileOut.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void loadData(){
        try {
            FileInputStream fileIn = new FileInputStream("warrior.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            warrior = (Warrior) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException | ClassNotFoundException c) {
            c.printStackTrace();
        }
    }
}


