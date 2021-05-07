package sim;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import sim.main.MainController;
import sim.settings.Settings;

import java.io.*;



public class Main extends Application {
    Settings settings = new Settings();

    public static boolean loggingEnabled = false;

    @Override
    public void start(Stage primaryStage) throws Exception{
        loadData();

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
        } catch (IOException | ClassNotFoundException ignored) {
        }
    }
}


