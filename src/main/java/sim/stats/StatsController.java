package sim.stats;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import sim.warrior.Constants;
import sim.warrior.Warrior;

import java.net.URL;
import java.util.ResourceBundle;

public class StatsController implements Initializable {
    @FXML
    Label str;
    @FXML
    Label agi;
    @FXML
    Label sta;
    @FXML
    Label hp;
    @FXML
    Label armor;
    @FXML
    Label ap;
    @FXML
    Label weaponSkill;
    @FXML
    Label hitChance;
    @FXML
    Label critChance;

    Warrior warrior;

    public StatsController(Warrior warrior){
        this.warrior = warrior;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        warrior.getStats().refreshStats();
        refreshDisplay();
    }

    private void setWeaponSkillText(){
        if(warrior.getEquippedItems()[Constants.OFFHAND] != null){
            weaponSkill.setText(warrior.getWeaponSkillMH() + "|"  + warrior.getWeaponSkillOH());
        }else{
            weaponSkill.setText(warrior.getWeaponSkillMH() + "");
        }
    }

    public void refreshDisplay(){
        str.setText(warrior.getStr() + "");
        agi.setText(warrior.getAgi() + "");
        sta.setText(warrior.getSta() + "");
        hp.setText(warrior.getHp() + "");
        armor.setText(warrior.getArmor() + "");
        ap.setText(warrior.getAp() + "");
        setWeaponSkillText();
        hitChance.setText(warrior.getHit() + "");
        critChance.setText(warrior.getCrit() + "");
    }
}
