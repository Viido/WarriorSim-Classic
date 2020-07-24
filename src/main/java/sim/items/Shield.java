package sim.items;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class Shield extends Item{
    private String type;
    private int shieldBlockValue;

    public String getType() {
        return type;
    }

    public int getShieldBlockValue() {
        return shieldBlockValue;
    }

    @Override
    public String getTooltip(){
        String tooltip = "";

        tooltip += getName() + "\n";
        tooltip += "Off Hand Shield\n";
        tooltip += getArmor() != 0 ? getArmor() + " Armor\n" : "";
        tooltip += shieldBlockValue + " Block\n";
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
