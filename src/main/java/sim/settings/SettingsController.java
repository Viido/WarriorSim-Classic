package sim.settings;

import com.google.gson.Gson;
import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import sim.warrior.Warrior;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

public class SettingsController implements Initializable {
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
    VBox worldBuffs;
    @FXML
    VBox debuffs;
    @FXML
    VBox consumables;
    @FXML
    VBox raidBuffs;

    Auras auras;
    Warrior warrior;
    Settings settings;

    public SettingsController(Settings settings){
        this.warrior = settings.getWarrior();
        this.settings = settings;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadSettings();
        initRaceSelect();
        initAuras();
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
        raceSelect.setText(settings.getRace());
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
        String[] races = {"Human", "Dwarf", "Night Elf", "Gnome", "Orc", "Undead", "Tauren", "Troll"};

        JFXListView<String> raceSelection = new JFXListView<>();
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
                raceSelect.setText(newValue);
                settings.setRace(newValue);
                racePopUp.hide();
            }
        });
    }

    private void initAuras(){
        Gson gson = new Gson();
        auras = gson.fromJson(new InputStreamReader(getClass().getResourceAsStream("data/auras.json")), Auras.class);

        Map<String, List<AuraSelect>> buffGroups = new HashMap<>();

        for(Aura aura : auras.getAuras()){
            AuraSelect auraSelect = new AuraSelect(aura, warrior);

            if(aura.getType().equals("world")){
                worldBuffs.getChildren().add(auraSelect);
            }

            if(aura.getType().equals("debuff")){
                debuffs.getChildren().add(auraSelect);
            }

            if(aura.getType().equals("raid")){
                raidBuffs.getChildren().add(auraSelect);

            }

            if(aura.getType().equals("consumable")){
                consumables.getChildren().add(auraSelect);
            }

            if(aura.getGroup() != null){
                if(buffGroups.containsKey(aura.getGroup())){
                    buffGroups.get(aura.getGroup()).add(auraSelect);
                }else{
                    List<AuraSelect> buffs = new ArrayList<>();
                    buffs.add(auraSelect);
                    buffGroups.put(aura.getGroup(), buffs);
                }
            }
        }

        buffGroups.forEach((key, auraSelects) -> {
            for(AuraSelect auraSelect : auraSelects){
                auraSelect.checkBox.selectedProperty().addListener((obs, oldValue, newValue) -> {
                    if(newValue){
                        for(AuraSelect auraSelect1 : auraSelects){
                            if(auraSelect != auraSelect1){
                                auraSelect1.checkBox.setSelected(false);
                            }
                        }
                    }
                });
                if(auraSelect.aura.getGroup().equals("weapon")){
                    auraSelect.checkBoxOH.selectedProperty().addListener((obs, oldValue, newValue) -> {
                        if(newValue){
                            for(AuraSelect auraSelect1 : auraSelects){
                                if(auraSelect != auraSelect1){
                                    auraSelect1.checkBoxOH.setSelected(false);
                                }
                            }
                        }
                    });
                }
            }
        });
    }
}
