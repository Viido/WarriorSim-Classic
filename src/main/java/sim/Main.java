package sim;

import com.google.gson.Gson;
import com.jfoenix.controls.JFXMasonryPane;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import sim.items.ItemsController;
import sim.main.MainController;
import sim.settings.Race;
import sim.settings.Settings;
import sim.stats.StatsController;
import sim.warrior.Warrior;
import sim.settings.SettingsController;
import sim.talents.TalentsController;

import java.io.*;

public class Main extends Application {
    Settings settings = new Settings();

    @Override
    public void start(Stage primaryStage) throws Exception{
        loadData();
//
//        ScrollPane scrollPane = new ScrollPane();
//
//        JFXMasonryPane root = new JFXMasonryPane();
//        root.setMinHeight(1080);
//        root.setMinWidth(1920);
//        scrollPane.setContent(root);
//
        if(settings.getWarrior().getRace() == null){
            Gson gson = new Gson();
            Race[] races = gson.fromJson(new InputStreamReader(getClass().getResourceAsStream("settings/data/races.json")), Race[].class);
            settings.getWarrior().setRace(races[0]);
        }
//
//
//        FXMLLoader statsLoader = new FXMLLoader(getClass().getResource("stats/fxml/StatsView.fxml"));
//        FXMLLoader itemsLoader = new FXMLLoader(getClass().getResource("items/fxml/ItemsView.fxml"));
//        FXMLLoader talentsLoader = new FXMLLoader(getClass().getResource("talents/fxml/Talents.fxml"));
//        FXMLLoader settingsLoader = new FXMLLoader(getClass().getResource("settings/fxml/SettingsView.fxml"));
//
//        StatsController statsController = new StatsController(settings.getWarrior());
//        ItemsController itemsController = new ItemsController(settings.getWarrior());
//        TalentsController talentsController = new TalentsController(settings.getWarrior());
//    SettingsController settingsController = new SettingsController(settings);
//
//        statsLoader.setController(statsController);
//        itemsLoader.setController(itemsController);
//        talentsLoader.setController(talentsController);
//   settingsLoader.setController(settingsController);
//
//        root.getChildren().add(statsLoader.load());
//        root.getChildren().add(itemsLoader.load());
//        root.getChildren().add(talentsLoader.load());
//        root.getChildren().add(settingsLoader.load());
//
//        root.setStyle("-fx-background-color: #282828");


        primaryStage.setMaximized(true);
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("main/fxml/MainView.fxml"));

        MainController mainController = new MainController(settings);
        mainLoader.setController(mainController);


        primaryStage.setTitle("WarriorSim Classic");
        primaryStage.setScene(new Scene(mainLoader.load()));
        primaryStage.getIcons().add(new Image(getClass().getResource("/images/app/warrior-wow-icon.png").toExternalForm()));



        primaryStage.setOnCloseRequest(e -> {
            mainController.saveSettings();
            saveData();
            primaryStage.close();
        });

        primaryStage.show();

        //mainController.setDividerPosition();
    }


    public static void main(String[] args) {
        launch(args);
    }

    private void saveData(){
        try{
            FileOutputStream fileOut = new FileOutputStream("settings.ser", false);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(settings);
            out.close();
            fileOut.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void loadData(){
        try {
            FileInputStream fileIn = new FileInputStream("settings.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            settings = (Settings) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException | ClassNotFoundException c) {
            c.printStackTrace();
        }
    }
}


