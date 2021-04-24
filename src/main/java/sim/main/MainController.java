package sim.main;

import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sim.data.SimDB;
import sim.engine.FightResult;
import sim.engine.Simulation;
import sim.items.ItemSlot;
import sim.items.ItemsController;
import sim.rotation.RotationController;
import sim.settings.Race;
import sim.settings.Settings;
import sim.settings.SettingsController;
import sim.stats.StatsController;
import sim.talents.TalentButton;
import sim.talents.TalentsController;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    HBox statsView;
    @FXML
    JFXTextField raceSelect;
    @FXML
    JFXTextField fightDuration;
    @FXML
    JFXTextField targetLevel;
    @FXML
    JFXTextField targetArmor;
    @FXML
    JFXTextField targetResistance;
    @FXML
    JFXTextField initialRage;
    @FXML
    JFXTextField simulations;
    @FXML
    JFXCheckBox heroicStrike9;
    @FXML
    JFXCheckBox battleShout7;
    @FXML
    SplitPane mainPane;
    @FXML
    JFXTabPane tabPane;
    @FXML
    JFXButton resultsButton;
    @FXML
    JFXButton simulateButton;
    @FXML
    JFXProgressBar simulationProgress;
    @FXML
    VBox rightSection;

    Settings settings;

    StatsController statsController;
    ItemsController itemsController;

    private FightResult fightResult;

    public MainController(Settings settings){
        this.settings = settings;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(settings.getCharacterSetup().getRace() == null){
            settings.getCharacterSetup().setRace(SimDB.RACES[0]);
        }

        FXMLLoader statsLoader = new FXMLLoader(getClass().getResource("/sim/stats/fxml/StatsView.fxml"));
        statsController = new StatsController(settings.getCharacterSetup());
        statsLoader.setController(statsController);

        try{
            rightSection.getChildren().add(rightSection.getChildren().size(), statsLoader.load());
        }catch(IOException e){
            e.printStackTrace();
        }

        loadSettings();
        initRaceSelect();

        tabPane.getTabs().add(createNewTab());
        Tab newTab = new Tab("+");

        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue == newTab){
                tabPane.getSelectionModel().select(oldValue);
                tabPane.getTabs().add(tabPane.getTabs().size() - 1, new Tab("test"));
            }
        });

        tabPane.getTabs().add(newTab);
        simulationProgress.setVisible(false);

        simulateButton.setOnMouseClicked(e -> {
            saveSettings();
            Simulation simulation = new Simulation(settings);

            fightResult = simulation.run(simulationProgress);
        });

        resultsButton.setOnMouseClicked(e -> {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("fxml/ResultsView.fxml"));
            fxmlLoader.setController(new ResultsController(fightResult));
            Scene scene = null;
            try {
                scene = new Scene(fxmlLoader.load(), 600, 400);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Stage stage = new Stage();
            stage.setTitle("Fight Results");
            stage.setScene(scene);
            stage.show();
        });
    }

    private Tab createNewTab(){
        ScrollPane scrollPane = new ScrollPane();
        StackPane stackPane = new StackPane();

        stackPane.setMinSize(1600, 920);

        FXMLLoader itemsLoader = new FXMLLoader(getClass().getResource("/sim/items/fxml/ItemsView.fxml"));
        FXMLLoader talentsLoader = new FXMLLoader(getClass().getResource("/sim/talents/fxml/Talents.fxml"));
        FXMLLoader settingsLoader = new FXMLLoader(getClass().getResource("/sim/settings/fxml/SettingsView.fxml"));
        FXMLLoader rotationLoader = new FXMLLoader(getClass().getResource("/sim/rotation/fxml/RotationView.fxml"));

        itemsController = new ItemsController(settings.getCharacterSetup());
        TalentsController talentsController = new TalentsController(settings.getCharacterSetup());
        SettingsController settingsController = new SettingsController(settings, statsController);
        RotationController rotationController = new RotationController();

        itemsLoader.setController(itemsController);
        talentsLoader.setController(talentsController);
        settingsLoader.setController(settingsController);
        rotationLoader.setController(rotationController);

        try{
            stackPane.getChildren().add(itemsLoader.load());
            stackPane.getChildren().add(settingsLoader.load());
            stackPane.getChildren().add(talentsLoader.load());
            stackPane.getChildren().add(rotationLoader.load());
            stackPane.setAlignment(Pos.TOP_LEFT);

            stackPane.getChildren().get(0).setTranslateX(700);
            stackPane.getChildren().get(0).setTranslateY(30);

            stackPane.getChildren().get(1).setTranslateX(30);
            stackPane.getChildren().get(1).setTranslateY(30);

            stackPane.getChildren().get(2).setTranslateX(0);
            stackPane.getChildren().get(2).setTranslateY(450);

            stackPane.getChildren().get(3).setTranslateX(960);
            stackPane.getChildren().get(3).setTranslateY(480);
        }catch(IOException e){
            e.printStackTrace();
        }

        scrollPane.setContent(stackPane);

        for(ItemSlot itemSlot : itemsController.getItemSlots()){
            itemSlot.selectedItemProperty().addListener((obs, oldValue, newValue) -> {
                statsController.refreshDisplay();
            });
            itemSlot.selectedEnchantProperty().addListener((obs, oldValue, newValue) -> {
                statsController.refreshDisplay();
            });
        }

        for(TalentButton talentButton : talentsController.getTalentButtons().values()){
            talentButton.pointsProperty().addListener((obs, oldValue, newValue) -> {
                if(oldValue.intValue() >= 0){
                    settings.getCharacterSetup().getActiveTalents().put(talentButton.getTalent().getId(), newValue.intValue());
                    statsController.refreshDisplay();
                }
                if(newValue.intValue() == 0){
                    settings.getCharacterSetup().getActiveTalents().remove(talentButton.getTalent().getId());
                    statsController.refreshDisplay();
                }
            });
        }

        Tab tab = new Tab("Set 1");

        tab.setContent(scrollPane);

        return tab;
    }

    public void saveSettings(){
        settings.setFightDuration(Integer.parseInt(fightDuration.getText()));
        settings.setTargetLevel(Integer.parseInt(targetLevel.getText()));
        settings.setTargetArmor(Integer.parseInt(targetArmor.getText()));
        settings.setTargetResistance(Integer.parseInt(targetResistance.getText()));
        settings.setInitialRage(Integer.parseInt(initialRage.getText()));
        settings.setIterations(Integer.parseInt(simulations.getText()));
        settings.setHeroicStrike9(heroicStrike9.isSelected());
        settings.setBattleShout7(battleShout7.isSelected());
    }

    private void loadSettings(){
        if(settings.getCharacterSetup().getRace() == null){
            settings.getCharacterSetup().setRace(SimDB.RACES[0]);
        }

        raceSelect.setText(settings.getCharacterSetup().getRace().getName());
        fightDuration.setText(settings.getFightDuration() + "");
        targetLevel.setText(settings.getTargetLevel() + "");
        targetArmor.setText(settings.getTargetArmor() + "");
        targetResistance.setText(settings.getTargetResistance() + "");
        initialRage.setText(settings.getInitialRage() + "");
        simulations.setText(settings.getIterations() + "");
        heroicStrike9.setSelected(settings.isHeroicStrike9());
        battleShout7.setSelected(settings.isBattleShout7());
    }

    private void initRaceSelect(){
        JFXListView<Race> raceSelection = new JFXListView<>();
        raceSelection.getStylesheets().add(this.getClass().getResource("/sim/settings/css/Settings.css").toExternalForm());
        raceSelection.setItems(FXCollections.observableArrayList(SimDB.RACES));

        CustomPopup racePopup = new CustomPopup();
        racePopup.setContent(raceSelection);

        raceSelection.setMaxWidth(75);

        raceSelect.setOnMouseClicked(e -> {
            raceSelection.refresh();
            racePopup.show(raceSelect, 0, raceSelect.getHeight());
        });

        raceSelection.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue != null){
                raceSelect.setText(newValue.getName());
                settings.getCharacterSetup().setRace(newValue);
                itemsController.refreshItemSelect();
                statsController.refreshDisplay();
                racePopup.hide();
            }
        });
    }
}
