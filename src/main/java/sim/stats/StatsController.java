package sim.stats;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import sim.warrior.Constants;
import sim.warrior.Warrior;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
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
    @FXML
    Label haste;
    @FXML
    Label defense;
    @FXML
    Label blockChance;
    @FXML
    Label blockValue;
    @FXML
    Label parryChance;
    @FXML
    Label dodgeChance;

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
            weaponSkill.setText(warrior.getWeaponSkillMH() + " | "  + warrior.getWeaponSkillOH());
        }else{
            weaponSkill.setText(warrior.getWeaponSkillMH() + "");
        }
    }

    private void setCritChanceText(){
        if(warrior.getEquippedItems()[Constants.OFFHAND] != null){
            critChance.setText(warrior.getCritMH() + "% | "  + warrior.getCritOH() + "%");
        }else{
            critChance.setText(warrior.getCritMH() + "%");
        }
    }

    public void refreshDisplay(){
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(dfs);

        str.setText(warrior.getStr() + "");
        agi.setText(warrior.getAgi() + "");
        sta.setText(warrior.getSta() + "");
        hp.setText(warrior.getHp() + "");
        armor.setText(warrior.getArmor() + "");
        ap.setText(warrior.getAp() + "");
        setWeaponSkillText();
        hitChance.setText(warrior.getHit() + "%");
        setCritChanceText();
        haste.setText(df.format(warrior.getHaste() * 100 - 100) + "%");
        defense.setText(warrior.getDefense() + "");
        blockChance.setText(warrior.getBlock() + "%");
        blockValue.setText(warrior.getBlockValue() + "");
        parryChance.setText(warrior.getParry() + "%");
        dodgeChance.setText(warrior.getDodge() + "%");
    }
}
