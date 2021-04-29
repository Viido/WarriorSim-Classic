package sim.main;

import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sim.data.SimDB;
import sim.engine.Event;
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

import static sim.data.Constants.*;

public class MainController implements Initializable {
    @FXML
    HBox statsView;
    @FXML
    JFXTextField raceSelect;
    @FXML
    JFXTextField fightDuration;
    @FXML
    JFXTextField executeDuration;
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
    JFXCheckBox multiTarget;
    @FXML
    JFXTextField extraTargets;
    @FXML
    JFXTextField extraTargetLevel;
    @FXML
    JFXTextField extraTargetArmor;
    @FXML
    SplitPane mainPane;
    @FXML
    JFXTabPane tabPane;
    @FXML
    JFXButton simulateButton;
    @FXML
    JFXProgressBar simulationProgress;
    @FXML
    VBox rightSection;
    @FXML
    Label averageDPS;

    Settings settings;

    StatsController statsController;
    ItemsController itemsController;
    RotationController rotationController;

    private FightResult fightResult;

    private static final Logger logger = LogManager.getLogger(MainController.class);

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



        tabPane.getTabs().add(createNewTab());
        Tab newTab = new Tab("+");

        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue == newTab){
                tabPane.getSelectionModel().select(oldValue);
                tabPane.getTabs().add(tabPane.getTabs().size() - 1, new Tab("test"));
            }
        });

        tabPane.getTabs().add(newTab);
        simulationProgress.setProgress(0);
        simulationProgress.setVisible(true);

        simulateButton.setOnMouseClicked(e -> {
            logger.debug("Simulate Button clicked.");
            saveSettings();
            Simulation simulation = new Simulation(settings);

            fightResult = simulation.run(simulationProgress, averageDPS);

            logger.debug("Simulation done.");
        });

        multiTarget.selectedProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue){
                rotationController.disableOption(Event.EventType.HEROIC_STRIKE);
                rotationController.enableOption(Event.EventType.CLEAVE);
            }else{
                rotationController.disableOption(Event.EventType.CLEAVE);
                rotationController.enableOption(Event.EventType.HEROIC_STRIKE);
            }
        });

        loadSettings();
        initRaceSelect();
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
        rotationController = new RotationController(settings);

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

        talentsController.getTalentButtons().get(BLOODTHIRST).pointsProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue.intValue() == 0){
                rotationController.disableOption(Event.EventType.BLOODTHIRST);
            }
            if(newValue.intValue() == 1){
                rotationController.enableOption(Event.EventType.BLOODTHIRST);
            }
        });

        talentsController.getTalentButtons().get(MORTAL_STRIKE).pointsProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue.intValue() == 0){
                rotationController.disableOption(Event.EventType.MORTAL_STRIKE);
            }
            if(newValue.intValue() == 1){
                rotationController.enableOption(Event.EventType.MORTAL_STRIKE);
            }
        });

        talentsController.getTalentButtons().get(SHIELD_SLAM).pointsProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue.intValue() == 0){
                rotationController.disableOption(Event.EventType.SHIELD_SLAM);
            }
            if(newValue.intValue() == 1){
                rotationController.enableOption(Event.EventType.SHIELD_SLAM);
            }
        });

        talentsController.getTalentButtons().get(DEATH_WISH).pointsProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue.intValue() == 0){
                rotationController.disableOption(Event.EventType.DEATH_WISH);
            }
            if(newValue.intValue() == 1){
                rotationController.enableOption(Event.EventType.DEATH_WISH);
            }
        });

        Tab tab = new Tab("Set 1");

        tab.setContent(scrollPane);

        return tab;
    }

    public void saveSettings(){
        settings.setFightDuration(Integer.parseInt(fightDuration.getText()));
        settings.setExecuteDuration(Integer.parseInt(executeDuration.getText()));
        settings.setTargetLevel(Integer.parseInt(targetLevel.getText()));
        settings.setTargetArmor(Integer.parseInt(targetArmor.getText()));
        settings.setTargetResistance(Integer.parseInt(targetResistance.getText()));
        settings.setInitialRage(Integer.parseInt(initialRage.getText()));
        settings.setIterations(Integer.parseInt(simulations.getText()));
        settings.setHeroicStrike9(heroicStrike9.isSelected());
        settings.setBattleShout7(battleShout7.isSelected());
        settings.setMultitarget(multiTarget.isSelected());
        settings.setExtraTargets(Integer.parseInt(extraTargets.getText()));
        settings.setExtraTargetLevel(Integer.parseInt(extraTargetLevel.getText()));
        settings.setExtraTargetArmor(Integer.parseInt(extraTargetArmor.getText()));

        rotationController.saveRotationOptions();
    }

    private void loadSettings(){
        if(settings.getCharacterSetup().getRace() == null){
            settings.getCharacterSetup().setRace(SimDB.RACES[0]);
        }

        raceSelect.setText(settings.getCharacterSetup().getRace().getName());
        fightDuration.setText(settings.getFightDuration() + "");
        executeDuration.setText(settings.getExecuteDuration() + "");
        targetLevel.setText(settings.getTargetLevel() + "");
        targetArmor.setText(settings.getTargetArmor() + "");
        targetResistance.setText(settings.getTargetResistance() + "");
        initialRage.setText(settings.getInitialRage() + "");
        simulations.setText(settings.getIterations() + "");
        heroicStrike9.setSelected(settings.isHeroicStrike9());
        battleShout7.setSelected(settings.isBattleShout7());
        multiTarget.setSelected(settings.isMultitarget());
        extraTargets.setText(settings.getExtraTargets() + "");
        extraTargetLevel.setText(settings.getExtraTargetLevel() + "");
        extraTargetArmor.setText(settings.getExtraTargetArmor() + "");
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
