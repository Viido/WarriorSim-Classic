package sim.items;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

public class Item implements Serializable {
    private int id;
    private String name;
    private int armor;
    private int str;
    private int agi;
    private int sta;
    @SerializedName(value = "int")
    private int intellect;
    @SerializedName(value = "spi")
    private int spirit;
    private int crit;
    private int hit;
    private int ap;
    @SerializedName(value = "skill")
    private int weaponSkill;
    @SerializedName(value = "skillType")
    private String weaponSkillType;
    private int defense;
    private int block;
    private int blockValue;
    private int parry;
    private int dodge;
    private int fireRes;
    private int frostRes;
    private int shadowRes;
    private int natureRes;
    private int arcaneRes;
    private int mp5;
    private int phase;
    @SerializedName(value = "spell")
    private int spellId;
    @SerializedName(value = "spell2")
    private int spell2Id;
    private String icon;
    private String quality;
    private String faction;
    private boolean unique;
    private String slot;
    private String type;
    @SerializedName(value = "min")
    private int minDmg;
    @SerializedName(value = "max")
    private int maxDmg;
    private double speed;
    private String eleDmgType;
    private int eleDmgMin;
    private int eleDmgMax;
    private int shieldBlockValue;
    private int itemSetId;

    public Item(){
        slot = "";
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getArmor() {
        return armor;
    }

    public int getStr() {
        return str;
    }

    public int getAgi() {
        return agi;
    }

    public int getSta() {
        return sta;
    }

    public int getIntellect() {
        return intellect;
    }

    public int getSpirit() {
        return spirit;
    }

    public int getCrit() {
        return crit;
    }

    public int getHit() {
        return hit;
    }

    public int getAp() {
        return ap;
    }

    public int getWeaponSkill() {
        return weaponSkill;
    }

    public String getWeaponSkillType() {
        return weaponSkillType;
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

    public int getFireRes() {
        return fireRes;
    }

    public int getFrostRes() {
        return frostRes;
    }

    public int getShadowRes() {
        return shadowRes;
    }

    public int getNatureRes() {
        return natureRes;
    }

    public int getArcaneRes() {
        return arcaneRes;
    }

    public int getMp5() {
        return mp5;
    }

    public int getPhase() {
        return phase;
    }

    public String getIcon() {
        return icon;
    }

    public String getQuality() {
        return quality;
    }

    public String getFaction() {
        return faction;
    }

    public int getSpellId() {
        return spellId;
    }

    public int getSpell2Id() {
        return spell2Id;
    }

    public boolean isUnique() {
        return unique;
    }

    public String getSlot() {
        return slot;
    }

    public String getType() {
        return type;
    }

    public int getMinDmg() {
        return minDmg;
    }

    public int getMaxDmg() {
        return maxDmg;
    }

    public double getSpeed() {
        return speed;
    }

    public String getEleDmgType() {
        return eleDmgType;
    }

    public int getEleDmgMin() {
        return eleDmgMin;
    }

    public int getEleDmgMax() {
        return eleDmgMax;
    }

    public int getShieldBlockValue() {
        return shieldBlockValue;
    }

    public int getItemSetId() {
        return itemSetId;
    }

    public String getColor(){
        switch(quality){
            case "legendary": return "#ff8000";
            case "epic": return "#a335ee";
            case "rare": return "#0070dd";
            case "uncommon": return "#1eff00";
        }

        return "";
    }

    public String getWeaponSkillText(){
        if(weaponSkill > 0 && weaponSkillType == null){
            return "Equip: Increased " + type.substring(0, 1).toUpperCase() + type.substring(1) + "s +" + weaponSkill + ".";
        }

        if(weaponSkillType.equals("varied")){
            return "Equip: Increased Axes +7.\nEquip: Increased Daggers +7.\nEquip: Increased Swords +7.";
        }

        return "Equip: Increased " + weaponSkillType.substring(0, 1).toUpperCase() + weaponSkillType.substring(1) + "s +" + weaponSkill + ".";
    }

    public String getSlotString(){
        if(slot.equals("main")){
            return "Main Hand";
        }else if(slot.equals("off") || type.equals("shield")){
            return "Off Hand";
        }else if(slot.equals("2h")){
            return "Two-Hand";
        }else if(type.equals("bow") || type.equals("crossbow") || type.equals("gun")){
            return "Ranged";
        }else{
            return "One-Hand";
        }
    }

    public List<String> getTooltip() {
        List<String> tooltip = new ArrayList<>();

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(dfs);

        tooltip.add(name);

        if(getType() != null){
            tooltip.add(getSlotString() + "\t" + type.substring(0, 1).toUpperCase() + type.substring(1));
        }

        if(speed != 0){
            tooltip.add(minDmg + " - " + maxDmg + " Damage" + "\t" + "Speed " + df.format(speed));
        }

        if(eleDmgType != null){
            tooltip.add("+ " + eleDmgMin + " - " + eleDmgMax + " " + eleDmgType.substring(0, 1).toUpperCase() + eleDmgType.substring(1) + " Damage");
        }

        if(speed != 0){
            tooltip.add("(" + df.format((minDmg + maxDmg + eleDmgMin + eleDmgMax)/2.0/speed) + " damage per second)");
        }

        if(shieldBlockValue != 0){
            tooltip.add(shieldBlockValue + " Block");
        }

        if(armor != 0){
            tooltip.add(armor + " Armor");
        }

        if(str != 0){
            tooltip.add("+" + str + " Strength");
        }

        if(agi != 0){
            tooltip.add("+" + agi + " Agility");
        }

        if(sta != 0){
            tooltip.add("+" + sta + " Stamina");
        }

        if(intellect != 0){
            tooltip.add("+" + intellect + " Intellect");
        }

        if(spirit != 0){
            tooltip.add("+" + spirit + " Spirit");
        }

        if(arcaneRes != 0){
            tooltip.add("+" + arcaneRes + " Arcane Resistance");
        }

        if(fireRes != 0){
            tooltip.add("+" + fireRes + " Fire Resistance");
        }

        if(frostRes != 0){
            tooltip.add("+" + frostRes + " Frost Resistance");
        }

        if(natureRes != 0){
            tooltip.add("+" + natureRes + " Nature Resistance");
        }

        if(shadowRes != 0){
            tooltip.add("+" + shadowRes + " Shadow Resistance");
        }

        if(crit != 0){
            tooltip.add("Equip: Improves your chance to get a critical strike by " + crit + "%.");
        }

        if(hit != 0){
            tooltip.add("Equip: Improves your chance to hit by " + hit + "%.");
        }

        if(ap != 0){
            tooltip.add("Equip: +" + ap + " Attack Power.");
        }

        if(weaponSkill != 0){
            tooltip.add(getWeaponSkillText());
        }

        if(block != 0){
            tooltip.add("Equip: Increases your chance to block attacks with a shield by " + block + "%.");
        }

        if(blockValue != 0){
            tooltip.add("Equip: Increases the block value of your shield by " + blockValue + ".");
        }
        if(parry != 0){
            tooltip.add("Equip: Increases your chance to parry an attack by " + parry + "%.");
        }

        if(dodge != 0){
            tooltip.add("Equip: Increases your chance to dodge an attack by " + dodge + "%.");
        }

        if(defense != 0){
            tooltip.add("Equip: Increased Defense +" + defense + ".");
        }

        if(mp5 != 0){
            tooltip.add("Equip: Restores " + mp5 + " mana per 5 sec.");
        }

        return tooltip;
    }
}
