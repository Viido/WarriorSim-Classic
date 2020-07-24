package sim.items;

import com.google.gson.annotations.SerializedName;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class Weapon extends Item{
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

    public Weapon(){
        slot = "";
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

    private String getWeaponType(){
        if(slot.equals("main")){
            return "Main Hand " + type.substring(0, 1).toUpperCase() + type.substring(1);
        }else if(slot.equals("off")){
            return "Off Hand " + type.substring(0, 1).toUpperCase() + type.substring(1);
        }else if(slot.equals("2h")){
            return "Two-Hand " + type.substring(0, 1).toUpperCase() + type.substring(1);
        }else if(type.equals("bow") || type.equals("crossbow") || type.equals("gun")){
            return "Ranged " + type.substring(0, 1).toUpperCase() + type.substring(1);
        }else{
            return "One-Hand " + type.substring(0, 1).toUpperCase() + type.substring(1);
        }
    }

    @Override
    public String getTooltip(){
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(dfs);

        String tooltip = "";

        tooltip += getName() + "\n";
        tooltip += getWeaponType() + "\n";
        tooltip += minDmg + " - " + maxDmg + " Damage\t\tSpeed " + df.format(speed) + "\n";
        tooltip += eleDmgType != null ? "+ " + eleDmgMin + " - " + eleDmgMax + " " + eleDmgType.substring(0, 1).toUpperCase() + eleDmgType.substring(1) + " Damage\n" : "";
        tooltip += "(" + df.format((minDmg + maxDmg + eleDmgMin + eleDmgMax)/2/speed) + " damage per second)\n";
        tooltip += getArmor() != 0 ? getArmor() + " Armor\n" : "";
        tooltip += getStrength() != 0 ? "+" + getStrength() + " Strength\n" : "";
        tooltip += getAgility() != 0 ? "+" + getAgility() + " Agility\n" : "";
        tooltip += getStamina() != 0 ? "+" + getStamina() + " Stamina\n" : "";
        tooltip += getIntellect() != 0 ? "+" + getIntellect() + " Intellect\n" : "";
        tooltip += getSpirit() != 0 ? "+" + getSpirit() + " Spirit\n" : "";
        tooltip += getArcaneRes() != 0 ? "+" + getArcaneRes() + " Arcane Resistance\n" : "";
        tooltip += getFireRes() != 0 ? "+" + getFireRes() + " Fire Resistance\n" : "";
        tooltip += getFrostRes() != 0 ? "+" + getFrostRes() + " Frost Resistance\n" : "";
        tooltip += getNatureRes() != 0 ? "+" + getNatureRes() + " Nature Resistance\n" : "";
        tooltip += getShadowRes() != 0 ? "+" + getShadowRes() + " Shadow Resistance\n" : "";
        tooltip += getCrit() != 0 ? "Equip: Improves your chance to get a critical strike by " + getCrit() + "%.\n" : "";
        tooltip += getHit() != 0 ? "Equip: Improves your chance to hit by " + getHit() + "%.\n" : "";
        tooltip += getAttackPower() != 0 ? "Equip: +" + getAttackPower() + " Attack Power.\n" : "";
        tooltip += getWeaponSkill() != 0 ? "Equip: Increased " + type.substring(0, 1).toUpperCase() + type.substring(1) + "s +" + getWeaponSkill() + ".\n" : "";
        tooltip += getBlock() != 0 ? "Equip: Increases your chance to block attacks with a shield by " + getBlock() + "%.\n" : "";
        tooltip += getBlockValue() != 0 ? "Equip: Increases the block value of your shield by " + getBlockValue() + ".\n" : "";
        tooltip += getDodge() != 0 ? "Equip: Increases your chance to dodge an attack by " + getDodge() + "%.\n" : "";
        tooltip += getParry() != 0 ? "Equip: Increases your chance to parry an attack by " + getParry() + "%.\n" : "";
        tooltip += getDefense() != 0 ? "Equip: Increased Defense +" + getDefense() + ".\n" : "";


        return tooltip;
    }
}
