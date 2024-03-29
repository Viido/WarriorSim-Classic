package sim.engine.warrior;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import sim.data.SimDB;
import sim.items.Enchant;
import sim.items.Item;
import sim.items.ItemSet;
import sim.settings.Aura;
import sim.settings.CharacterSetup;
import sim.settings.SetBonusControl;

import java.io.Serializable;

public class Stats implements Serializable{
    private int str;
    private int agi;
    private int sta;
    private int hp;
    private int armor;
    private int ap;
    private int hit;
    private int crit;
    private int defense;
    private int block;
    private int blockValue;
    private int parry;
    private int dodge;
    private double haste = 1.0;

    public Stats(){}

    public Stats(Stats stats) {
        this.str = stats.str;
        this.agi = stats.agi;
        this.sta = stats.sta;
        this.hp = stats.hp;
        this.armor = stats.armor;
        this.ap = stats.ap;
        this.hit = stats.hit;
        this.crit = stats.crit;
        this.defense = stats.defense;
        this.block = stats.block;
        this.blockValue = stats.blockValue;
        this.parry = stats.parry;
        this.dodge = stats.dodge;
        this.haste = stats.haste;
    }

    public int getStr() {
        return str;
    }

    public void setStr(int str) {
        this.str = str;
    }

    public int getAgi() {
        return agi;
    }

    public void setAgi(int agi) {
        this.agi = agi;
    }

    public int getSta() {
        return sta;
    }

    public void setSta(int sta) {
        this.sta = sta;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getArmor() {
        return armor;
    }

    public void setArmor(int armor) {
        this.armor = armor;
    }

    public int getAp() {
        return ap;
    }

    public void setAp(int ap) {
        this.ap = ap;
    }

    public int getHit() {
        return hit;
    }

    public void setHit(int hit) {
        this.hit = hit;
    }

    public int getCrit() {
        return crit;
    }

    public void setCrit(int crit) {
        this.crit = crit;
    }

    public int getDefense() {
        return defense;
    }

    public int getBlock() {
        return block;
    }

    public int getBlockValue() {
        return blockValue;
    }

    public int getParry() {
        return parry;
    }

    public int getDodge() {
        return dodge;
    }

    public double getHaste() {
        return haste;
    }

    public void setHaste(double haste) {
        this.haste = haste;
    }

    /*public void refreshStats(){
        str = 0;
        agi = 0;
        sta = 0;
        ap = 0;
        hit = 0;
        crit = 0;
        armor = 0;
        hp = 0;
        defense = 0;
        block = 0;
        blockValue = 0;
        parry = 0;
        dodge = 0;
        haste = 1;

        for(Item item : characterSetup.getEquippedItems()){
            addStats(item);
        }

        for(Enchant enchant : characterSetup.getEquippedEnchants()){
            addStats(enchant);
        }

        for(Aura aura : characterSetup.getActiveAuras().values()){
            addStats(aura);
        }

        for(SetBonusControl setBonusControl : characterSetup.getSetBonuses().values()){
            for(Integer i : setBonusControl.getActiveSetBonuses()){
                addStats(SimDB.ITEM_SETS.get(setBonusControl.getItemSetId()).getSetBonuses().get(i));
            }
        }
    }*/

    public void addStats(Stats stats){
        str += stats.getStr();
        agi += stats.getAgi();
        sta += stats.getSta();
        hp += stats.getHp();
        armor += stats.getArmor();
        ap += stats.getAp();
        hit += stats.getHit();
        crit += stats.getCrit();
        defense += stats.getDefense();
        block += stats.getBlock();
        blockValue += stats.getBlockValue();
        parry += stats.getParry();
        dodge += stats.getDodge();
        haste *= stats.getHaste();
    }

    public void removeStats(Stats stats){
        str -= stats.getStr();
        agi -= stats.getAgi();
        sta -= stats.getSta();
        hp -= stats.getHp();
        armor -= stats.getArmor();
        ap -= stats.getAp();
        hit -= stats.getHit();
        crit -= stats.getCrit();
        defense -= stats.getDefense();
        block -= stats.getBlock();
        blockValue -= stats.getBlockValue();
        parry -= stats.getParry();
        dodge -= stats.getDodge();
        haste /= stats.getHaste();
    }

    public void removeStats(Item item){
        str -= item.getStr();
        agi -= item.getAgi();
        sta -= item.getSta();
        ap -= item.getAp();
        hit -= item.getHit();
        crit -= item.getCrit();
        armor -= item.getArmor();
        defense -= item.getDefense();
        block -= item.getBlock();
        blockValue -= item.getBlockValue();
        blockValue -= item.getShieldBlockValue();
        parry -= item.getParry();
        dodge -= item.getDodge();
    }

    public void addStats(Item item){
        if(item != null){
            str += item.getStr();
            agi += item.getAgi();
            sta += item.getSta();
            ap += item.getAp();
            hit += item.getHit();
            crit += item.getCrit();
            armor += item.getArmor();
            defense += item.getDefense();
            block += item.getBlock();
            blockValue += item.getBlockValue();
            blockValue += item.getShieldBlockValue();
            parry += item.getParry();
            dodge += item.getDodge();
        }
    }

    public void removeStats(Enchant enchant){
        str -= enchant.getStr();
        agi -= enchant.getAgi();
        sta -= enchant.getSta();
        ap -= enchant.getAp();
        hp -= enchant.getHp();
        crit -= enchant.getCrit();
        defense -= enchant.getDefense();
        block -= enchant.getBlock();
        blockValue -= enchant.getBlockValue();
        haste /= enchant.getHaste();
    }

    public void addStats(Enchant enchant){
        if(enchant != null){
            str += enchant.getStr();
            agi += enchant.getAgi();
            sta += enchant.getSta();
            ap += enchant.getAp();
            hp += enchant.getHp();
            crit += enchant.getCrit();
            defense += enchant.getDefense();
            block += enchant.getBlock();
            blockValue += enchant.getBlockValue();
            haste *= enchant.getHaste();
        }
    }

    public void removeStats(Aura aura){
        if(!aura.getType().equals("debuff")){
            str -= aura.getStr();
            agi -= aura.getAgi();
            sta -= aura.getSta();
            ap -= aura.getAp();
            crit -= aura.getCrit();
            armor -= aura.getArmor();
            hp -= aura.getHp();
            haste /= aura.getHaste();
        }
    }

    public void addStats(Aura aura){
        if(!aura.getType().equals("debuff")){
            str += aura.getStr();
            agi += aura.getAgi();
            sta += aura.getSta();
            ap += aura.getAp();
            crit += aura.getCrit();
            armor += aura.getArmor();
            hp += aura.getHp();
            haste *= aura.getHaste();
        }
    }

    public void addStats(ItemSet.ItemSetBonus itemSetBonus){
        sta += itemSetBonus.getSta();
        armor += itemSetBonus.getArmor();
        ap += itemSetBonus.getAp();
        hit += itemSetBonus.getHit();
        crit += itemSetBonus.getCrit();
        defense += itemSetBonus.getDefense();
        block += itemSetBonus.getBlock();
        blockValue += itemSetBonus.getBlockValue();
        parry += itemSetBonus.getParry();
        dodge += itemSetBonus.getDodge();
    }

    public void removeStats(ItemSet.ItemSetBonus itemSetBonus){
        sta -= itemSetBonus.getSta();
        armor -= itemSetBonus.getArmor();
        ap -= itemSetBonus.getAp();
        hit -= itemSetBonus.getHit();
        crit -= itemSetBonus.getCrit();
        defense -= itemSetBonus.getDefense();
        block -= itemSetBonus.getBlock();
        blockValue -= itemSetBonus.getBlockValue();
        parry -= itemSetBonus.getParry();
        dodge -= itemSetBonus.getDodge();
    }
}
