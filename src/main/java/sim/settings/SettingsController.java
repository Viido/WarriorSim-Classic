package sim.settings;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import sim.data.SimDB;
import sim.stats.StatsController;
import sim.warrior.Warrior;
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

    Warrior warrior;
    Settings settings;

    StatsController statsController;

    public SettingsController(Settings settings, StatsController statsController){
        this.warrior = settings.getWarrior();
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

            auraSelect.getCheckBox().selectedProperty().addListener((obs, oldValue, newValue) -> {
                if(newValue){
                    if(auraSelect.getCheckBoxOH() != null){
                        warrior.setTempEnchantMH(aura);
                    }else{
                        warrior.addAura(aura);
                    }
                }else{
                    if(auraSelect.getCheckBoxOH() != null ){
                        if(warrior.getTempEnchantMH().getId() == aura.getId()){
                            warrior.setTempEnchantMH(null);
                        }
                    }else{
                        warrior.removeAura(aura);
                    }
                }

                statsController.refreshDisplay();
            });

            if(auraSelect.getCheckBoxOH() != null){
                auraSelect.getCheckBoxOH().selectedProperty().addListener((obs, oldValue, newValue) -> {
                    if(newValue){
                        warrior.setTempEnchantOH(aura);
                    }else if(warrior.getTempEnchantOH().getId() == aura.getId()){
                        warrior.setTempEnchantOH(null);
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
