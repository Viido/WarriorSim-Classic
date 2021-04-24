package sim.stats;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import sim.data.SimDB;
import sim.engine.warrior.Stats;
import sim.engine.warrior.Warrior;
import sim.items.Enchant;
import sim.items.Item;
import sim.settings.Aura;
import sim.settings.CharacterSetup;
import sim.data.Constants;
import sim.settings.SetBonusControl;

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

    CharacterSetup characterSetup;
    Warrior warrior;

    DecimalFormat df = new DecimalFormat();

    public StatsController(CharacterSetup characterSetup){
        this.characterSetup = characterSetup;
        this.warrior = characterSetup.getWarrior();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(dfs);

        loadStats();
        refreshDisplay();
    }

    private void loadStats(){
        Stats stats = warrior.getStats();

        for(Item item : characterSetup.getEquippedItems()){
            stats.addStats(item);
        }

        for(Enchant enchant : characterSetup.getEquippedEnchants()){
            stats.addStats(enchant);
        }

        for(Aura aura : characterSetup.getActiveAuras().values()){
            stats.addStats(aura);
        }

        for(SetBonusControl setBonusControl : characterSetup.getSetBonuses().values()){
            for(Integer i : setBonusControl.getActiveSetBonuses()){
                stats.addStats(SimDB.ITEM_SETS.get(setBonusControl.getItemSetId()).getSetBonuses().get(i));
            }
        }
    }

    private void setWeaponSkillText(){
        if(characterSetup.getEquippedItems()[Constants.OFFHAND] != null){
            if(!characterSetup.getEquippedItems()[Constants.OFFHAND].getType().equals("shield")){
                weaponSkill.setText(warrior.getWeaponSkillMH() + " | "  + warrior.getWeaponSkillOH());
            }else{
                weaponSkill.setText(warrior.getWeaponSkillMH() + "");
            }
        }else{
            weaponSkill.setText(warrior.getWeaponSkillMH() + "");
        }
    }

    private void setCritChanceText(){
        if(characterSetup.getEquippedItems()[Constants.OFFHAND] != null){
            if(!characterSetup.getEquippedItems()[Constants.OFFHAND].getType().equals("shield")){
                critChance.setText(df.format(warrior.getCritMH() + (warrior.getWeaponSkillMH() - 300) * 0.04) + "% | "  + df.format(warrior.getCritOH() + (warrior.getWeaponSkillOH() - 300) * 0.04) + "%");
            }else{
                critChance.setText(df.format(warrior.getCritMH() + (warrior.getWeaponSkillMH() - 300) * 0.04) + "%");
            }
        }else{
            critChance.setText(df.format(warrior.getCritMH() + (warrior.getWeaponSkillMH() - 300) * 0.04) + "%");
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
        hitChance.setText(warrior.getHit() + "%");
        setCritChanceText();
        haste.setText(df.format(warrior.getHaste() * 100 - 100) + "%");
        defense.setText(warrior.getDefense() + "");
        blockChance.setText(df.format(warrior.getBlock()) + "%");
        blockValue.setText(warrior.getBlockValue() + "");
        parryChance.setText(df.format(warrior.getParry()) + "%");
        dodgeChance.setText(df.format(warrior.getDodge()) + "%");
    }
}
