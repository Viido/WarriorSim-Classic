package sim.items;

import com.google.gson.annotations.SerializedName;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

public class Item {
    private int id;
    private String name;
    private int armor;
    @SerializedName(value = "str")
    private int strength;
    @SerializedName(value = "agi")
    private int agility;
    @SerializedName(value = "sta")
    private int stamina;
    @SerializedName(value = "int")
    private int intellect;
    @SerializedName(value = "spi")
    private int spirit;
    private int crit;
    private int hit;
    @SerializedName(value = "ap")
    private int attackPower;
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
    private float speed;
    private String eleDmgType;
    private int eleDmgMin;
    private int eleDmgMax;
    private int shieldBlockValue;

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

    public int getStrength() {
        return strength;
    }

    public int getAgility() {
        return agility;
    }

    public int getStamina() {
        return stamina;
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

    public int getAttackPower() {
        return attackPower;
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

    public float getSpeed() {
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
        if(weaponSkill == 0){
            return "";
        }

        if(weaponSkill > 0 && weaponSkillType == null){
            return "Equip: Increased " + type.substring(0, 1).toUpperCase() + type.substring(1) + "s +" + weaponSkill + ".\n";
        }

        if(weaponSkillType.equals("varied")){
            return "Equip: Increased Axes +7.\nEquip: Increased Daggers +7.\nEquip: Increased Swords +7.\n";
        }

        return "Equip: Increased " + weaponSkillType.substring(0, 1).toUpperCase() + weaponSkillType.substring(1) + " +" + weaponSkill + ".\n";
    }

    private String getTypeString(){
        if(type == null){
            return "";
        }

        if(slot.equals("main")){
            return "Main Hand " + type.substring(0, 1).toUpperCase() + type.substring(1) + "\n";
        }else if(slot.equals("off") || type.equals("shield")){
            return "Off Hand " + type.substring(0, 1).toUpperCase() + type.substring(1) + "\n";
        }else if(slot.equals("2h")){
            return "Two-Hand " + type.substring(0, 1).toUpperCase() + type.substring(1) + "\n";
        }else if(type.equals("bow") || type.equals("crossbow") || type.equals("gun")){
            return "Ranged " + type.substring(0, 1).toUpperCase() + type.substring(1) + "\n";
        }else{
            return "One-Hand " + type.substring(0, 1).toUpperCase() + type.substring(1) + "\n";
        }
    }

    public String getTooltip() {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(dfs);

        String tooltip = "";

        tooltip += name + "\n";
        tooltip += getTypeString();
        tooltip += speed != 0 ? minDmg + " - " + maxDmg + " Damage\t\tSpeed " + df.format(speed) + "\n" : "";
        tooltip += eleDmgType != null ? "+ " + eleDmgMin + " - " + eleDmgMax + " " + eleDmgType.substring(0, 1).toUpperCase() + eleDmgType.substring(1) + " Damage\n" : "";
        tooltip += speed != 0 ? "(" + df.format((minDmg + maxDmg + eleDmgMin + eleDmgMax)/2/speed) + " damage per second)\n" : "";
        tooltip += shieldBlockValue != 0 ? shieldBlockValue + " Block\n" : "";
        tooltip += armor != 0 ? armor + " Armor\n" : "";
        tooltip += strength != 0 ? "+" + strength + " Strength\n" : "";
        tooltip += agility != 0 ? "+" + agility + " Agility\n" : "";
        tooltip += stamina != 0 ? "+" + stamina + " Stamina\n" : "";
        tooltip += intellect != 0 ? "+" + intellect + " Intellect\n" : "";
        tooltip += spirit != 0 ? "+" + spirit + " Spirit\n" : "";
        tooltip += arcaneRes != 0 ? "+" + arcaneRes + " Arcane Resistance\n" : "";
        tooltip += fireRes != 0 ? "+" + fireRes + " Fire Resistance\n" : "";
        tooltip += frostRes != 0 ? "+" + frostRes + " Frost Resistance\n" : "";
        tooltip += natureRes != 0 ? "+" + natureRes + " Nature Resistance\n" : "";
        tooltip += shadowRes != 0 ? "+" + shadowRes + " Shadow Resistance\n" : "";
        tooltip += crit != 0 ? "Equip: Improves your chance to get a critical strike by " + crit + "%.\n" : "";
        tooltip += hit != 0 ? "Equip: Improves your chance to hit by " + hit + "%.\n" : "";
        tooltip += attackPower != 0 ? "Equip: +" + attackPower + " Attack Power.\n" : "";
        tooltip += getWeaponSkillText();
        tooltip += block != 0 ? "Equip: Increases your chance to block attacks with a shield by " + block + "%.\n" : "";
        tooltip += blockValue != 0 ? "Equip: Increases the block value of your shield by " + blockValue + ".\n" : "";
        tooltip += parry != 0 ? "Equip: Increases your chance to parry an attack by " + parry + "%.\n" : "";
        tooltip += dodge != 0 ? "Equip: Increases your chance to dodge an attack by " + dodge + "%.\n" : "";
        tooltip += defense != 0 ? "Equip: Increased Defense +" + defense + ".\n" : "";

        return tooltip;
    }
}
