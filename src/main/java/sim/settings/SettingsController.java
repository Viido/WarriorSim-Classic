package sim.settings;

import com.google.gson.Gson;
import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.PopupWindow;
import javafx.util.Callback;
import javafx.util.Duration;
import sim.items.Enchants;
import sim.items.Item;
import sim.items.Items;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SettingsController implements Initializable {
    @FXML
    JFXTextField raceSelect;
    @FXML
    VBox auraList;

    @FXML
    VBox worldBuffs;
    @FXML
    VBox debuffs;
    @FXML
    VBox consumables;
    @FXML
    VBox raidBuffs;

    Auras auras;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initRaceSelect();
        initAuras();
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
                racePopUp.hide();
            }
        });
    }

    private void initAuras(){
        Gson gson = new Gson();
        auras = gson.fromJson(new InputStreamReader(getClass().getResourceAsStream("data/auras.json")), Auras.class);

        for(Aura aura : auras.getAuras()){
            AuraSelect auraSelect = new AuraSelect(aura);
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
        }
    }
}
