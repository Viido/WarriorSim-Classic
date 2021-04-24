package sim.settings;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import sim.data.SimDB;
import sim.stats.StatsController;

import java.net.URL;
import java.util.*;

public class SettingsController implements Initializable {
    @FXML
    VBox worldBuffs;
    @FXML
    VBox debuffs;
    @FXML
    VBox consumables;
    @FXML
    VBox raidBuffs;

    CharacterSetup characterSetup;
    Settings settings;

    StatsController statsController;

    public SettingsController(Settings settings, StatsController statsController){
        this.characterSetup = settings.getCharacterSetup();
        this.settings = settings;
        this.statsController = statsController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initAuras();
    }

    private void initAuras(){
        Map<String, List<AuraSelect>> buffGroups = new HashMap<>();

        for(Aura aura : SimDB.AURAS.getAuras()){
            AuraSelect auraSelect = new AuraSelect(aura, characterSetup);

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

            auraSelect.getCheckBox().selectedProperty().addListener((obs, oldValue, newValue) -> {
                if(newValue){
                    if(auraSelect.getCheckBoxOH() != null){
                        characterSetup.setTempEnchantMH(aura);
                    }else{
                        characterSetup.addAura(aura);
                    }
                }else{
                    if(auraSelect.getCheckBoxOH() != null ){
                        if(characterSetup.getTempEnchantMH().getId() == aura.getId()){
                            characterSetup.setTempEnchantMH(null);
                        }
                    }else{
                        characterSetup.removeAura(aura);
                    }
                }

                statsController.refreshDisplay();
            });

            if(auraSelect.getCheckBoxOH() != null){
                auraSelect.getCheckBoxOH().selectedProperty().addListener((obs, oldValue, newValue) -> {
                    if(newValue){
                        characterSetup.setTempEnchantOH(aura);
                    }else if(characterSetup.getTempEnchantOH().getId() == aura.getId()){
                        characterSetup.setTempEnchantOH(null);
                    }

                    statsController.refreshDisplay();
                });
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
