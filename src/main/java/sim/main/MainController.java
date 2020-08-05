package sim.main;

import com.google.gson.Gson;
import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.*;
import sim.Main;
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
    JFXButton simulate;
    @FXML
    VBox rightSection;

    Settings settings;
    Race[] races;

    StatsController statsController;

    public MainController(Settings settings){
        this.settings = settings;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Gson gson = new Gson();
        races = gson.fromJson(new InputStreamReader(getClass().getResourceAsStream("/sim/settings/data/races.json")), Race[].class);

        FXMLLoader statsLoader = new FXMLLoader(getClass().getResource("/sim/stats/fxml/StatsView.fxml"));
        statsController = new StatsController(settings.getWarrior());
        statsLoader.setController(statsController);

        try{
            rightSection.getChildren().add(rightSection.getChildren().size(), statsLoader.load());
        }catch(IOException e){
            e.printStackTrace();
        }

        if(settings.getWarrior().getRace() == null){
            settings.getWarrior().setRace(races[0]);
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
    }

    private Tab createNewTab(){
        ScrollPane scrollPane = new ScrollPane();
        StackPane stackPane = new StackPane();

        stackPane.setMinSize(1600, 920);



        FXMLLoader itemsLoader = new FXMLLoader(getClass().getResource("/sim/items/fxml/ItemsView.fxml"));
        FXMLLoader talentsLoader = new FXMLLoader(getClass().getResource("/sim/talents/fxml/Talents.fxml"));
        FXMLLoader settingsLoader = new FXMLLoader(getClass().getResource("/sim/settings/fxml/SettingsView.fxml"));
        FXMLLoader rotationLoader = new FXMLLoader(getClass().getResource("/sim/rotation/fxml/RotationView.fxml"));


        ItemsController itemsController = new ItemsController(settings.getWarrior());



        TalentsController talentsController = new TalentsController(settings.getWarrior());
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
                    settings.getWarrior().getActiveTalents().put(talentButton.getTalent().getId(), newValue.intValue());
                    statsController.refreshDisplay();
                }
                if(newValue.intValue() == 0){
                    settings.getWarrior().getActiveTalents().remove(talentButton.getTalent().getId());
                    statsController.refreshDisplay();
                }
            });
        }

        Tab tab = new Tab("Set 1");

        tab.setContent(scrollPane);

        return tab;
    }

    public void setDividerPosition(){
        mainPane.setDividerPosition(0, 0.9);
    }

    public void saveSettings(){
        settings.setFightDuration(Integer.parseInt(fightDuration.getText()));
        settings.setTargetLevel(Integer.parseInt(targetLevel.getText()));
        settings.setTargetArmor(Integer.parseInt(targetArmor.getText()));
        settings.setTargetResistance(Integer.parseInt(targetResistance.getText()));
        settings.setInitialRage(Integer.parseInt(initialRage.getText()));
        settings.setSimulations(Integer.parseInt(simulations.getText()));
        settings.setHeroicStrike9(heroicStrike9.isSelected());
        settings.setBattleShout7(battleShout7.isSelected());
    }

    private void loadSettings(){
        if(settings.getWarrior().getRace() == null){
            settings.getWarrior().setRace(races[0]);
        }

        raceSelect.setText(settings.getWarrior().getRace().getName());
        fightDuration.setText(settings.getFightDuration() + "");
        targetLevel.setText(settings.getTargetLevel() + "");
        targetArmor.setText(settings.getTargetArmor() + "");
        targetResistance.setText(settings.getTargetResistance() + "");
        initialRage.setText(settings.getInitialRage() + "");
        simulations.setText(settings.getSimulations() + "");
        heroicStrike9.setSelected(settings.isHeroicStrike9());
        battleShout7.setSelected(settings.isBattleShout7());
    }

    private void initRaceSelect(){

        JFXListView<Race> raceSelection = new JFXListView<>();
        raceSelection.getStylesheets().add(this.getClass().getResource("/sim/settings/css/Settings.css").toExternalForm());
        raceSelection.setItems(FXCollections.observableArrayList(races));

        JFXPopup racePopUp = new JFXPopup();
        racePopUp.setPopupContent(raceSelection);

        raceSelect.setOnMouseClicked(e -> {
            raceSelection.refresh();
            racePopUp.setStyle("-fx-background-color: black");
            racePopUp.show(raceSelect, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT, 0, 25);
        });

        raceSelection.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue != null){
                raceSelect.setText(newValue.getName());
                settings.getWarrior().setRace(newValue);
                statsController.refreshDisplay();
                racePopUp.hide();
            }
        });
    }
}
