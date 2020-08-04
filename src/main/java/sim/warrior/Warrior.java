package sim.warrior;

import sim.items.Enchant;
import sim.items.Item;
import sim.settings.Aura;
import sim.settings.Race;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static sim.warrior.Constants.*;

public class Warrior implements Serializable {
    Race race;
    Item[] equippedItems = new Item[17];
    Enchant[] equippedEnchants = new Enchant[17];
    Map<Integer, Aura> activeAuras = new HashMap<>();
    Map<Integer, Integer> activeTalents = new HashMap<>();
    Aura tempEnchantMH; // TODO find a better way to handle dual-wield temp enchants
    Aura tempEnchantOH;
    Stats stats = new Stats(this);

    public Item[] getEquippedItems() {
        return equippedItems;
    }

    public void equipItem(int slot, Item newItem){
        if(equippedItems[slot] != null){
            stats.removeStats(equippedItems[slot]);
        }
        equippedItems[slot] = newItem;
        stats.addStats(newItem);
    }

    public void unequipItem(int slot){
        stats.removeStats(equippedItems[slot]);
        equippedItems[slot] = null;
    }

    public Enchant[] getEquippedEnchants() {
        return equippedEnchants;
    }

    public void equipEnchant(int slot, Enchant newEnchant){
        if(equippedEnchants[slot] != null){
            stats.removeStats(equippedEnchants[slot]);
        }
        equippedEnchants[slot] = newEnchant;
        stats.addStats(newEnchant);
    }

    public void unequipEnchant(int slot){
        stats.removeStats(equippedEnchants[slot]);
        equippedEnchants[slot] = null;
    }

    public Map<Integer, Aura> getActiveAuras() {
        return activeAuras;
    }

    public void addAura(Aura aura){
        activeAuras.put(aura.getId(), aura);
        stats.addStats(aura);
    }

    public void removeAura(Aura aura){
        activeAuras.remove(aura.getId());
        stats.removeStats(aura);
    }

    public Map<Integer, Integer> getActiveTalents() {
        return activeTalents;
    }

    public Aura getTempEnchantMH() {
        return tempEnchantMH;
    }

    public void setTempEnchantMH(Aura tempEnchantMH) {
        this.tempEnchantMH = tempEnchantMH;
    }

    public Aura getTempEnchantOH() {
        return tempEnchantOH;
    }

    public void setTempEnchantOH(Aura tempEnchantOH) {
        this.tempEnchantOH = tempEnchantOH;
    }

    public Race getRace() {
        return race;
    }

    public void setRace(Race race) {
        this.race = race;
    }

    public Stats getStats() {
        return stats;
    }

    public float getStatMod(){
        float mod = 1;

        if(getActiveAuras().containsKey(SPIRIT_OF_ZANDALAR)){
            mod *= getActiveAuras().get(SPIRIT_OF_ZANDALAR).getStatMod();
        }

        if(getActiveAuras().containsKey(BLESSING_OF_KINGS)){
            mod *= getActiveAuras().get(BLESSING_OF_KINGS).getStatMod();
        }

        return mod;
    }

    public float getStaMod(){
        float mod = 1;

        if(getActiveAuras().containsKey(DMT_STA)){
            mod *= getActiveAuras().get(DMT_STA).getStaMod();
        }

        return mod;
    }

    public int getStr(){
        return (int) Math.floor((race.getBaseStats().getStr() + stats.getStr()) * getStatMod());
    }

    public int getAgi(){
        return (int) Math.floor((race.getBaseStats().getAgi() + stats.getAgi()) * getStatMod());
    }

    public int getSta(){
        return (int) Math.floor((race.getBaseStats().getSta() + stats.getSta()) * getStatMod() * getStaMod());
    }

    public int getAp(){
        return getStr() * 2 + race.getBaseStats().getAp() + stats.getAp() + (tempEnchantMH != null ? tempEnchantMH.getAp() : 0) + (tempEnchantOH != null ? tempEnchantOH.getAp() : 0);
    }

    public int getHit(){
        return stats.getHit();
    }

    public float getCrit(){
        return stats.getCrit() + getAgi()/20.0f + (tempEnchantMH != null ? tempEnchantMH.getCrit() : 0) + (tempEnchantOH != null ? tempEnchantOH.getCrit() : 0);
    }

    public int getHp(){
        return (int) Math.floor((race.getBaseStats().getHp() + getSta() * 10 + stats.getHp()) * (race.getId() == TAUREN ? 1.05f : 1));
    }

    // TODO: Verify how Toughness talent works (only pure armor from items? enchants? agi from items?)
    public float getArmor(){
        int armorFromItems = 0;

        for(Item item : getEquippedItems()){
            if(item != null){
                armorFromItems += item.getArmor();
            }
        }

        float toughnessMod = activeTalents.containsKey(TOUGHNESS) ? 0.02f * activeTalents.get(TOUGHNESS) : 0;

        return stats.getArmor() + armorFromItems * toughnessMod + getAgi() * 2;
    }

    public int getWeaponSkillMH(){
        int weaponSkill = 300;

        if(equippedItems[MAINHAND] == null){
            return weaponSkill;
        }

        for(Item item : getEquippedItems()){
            if(item != null){
                if(item.getWeaponSkillType() != null){
                    if(item.getWeaponSkillType().equals(equippedItems[MAINHAND].getType())){
                        weaponSkill += item.getWeaponSkill();
                    }
                    if(item.getWeaponSkillType().equals("varied") && equippedItems[MAINHAND].getType().equals("axe") || equippedItems[MAINHAND].getType().equals("dagger") || equippedItems[MAINHAND].getType().equals("sword")){
                        weaponSkill += item.getWeaponSkill();
                    }
                }
            }
        }

        weaponSkill += equippedItems[MAINHAND].getWeaponSkill();

        if(equippedItems[OFFHAND] != null){
            weaponSkill += (equippedItems[OFFHAND].getType().equals(equippedItems[MAINHAND].getType()) ? equippedItems[OFFHAND].getWeaponSkill() : 0);
        }

        if(race.getId() == HUMAN){
            if(equippedItems[MAINHAND].getType().equals("mace") || equippedItems[MAINHAND].getType().equals("sword")){
                weaponSkill += 5;
            }
        }
        if(race.getId() == ORC){
            if(equippedItems[MAINHAND].getType().equals("axe")){
                weaponSkill += 5;
            }
        }

        return weaponSkill;
    }

    public int getWeaponSkillOH(){
        int weaponSkill = 300;

        if(equippedItems[OFFHAND] == null){
            return weaponSkill;
        }

        for(Item item : getEquippedItems()){
            if(item != null){
                if(item.getWeaponSkillType() != null){
                    if(item.getWeaponSkillType().equals(equippedItems[OFFHAND].getType())){
                        weaponSkill += item.getWeaponSkill();
                    }
                    if(item.getWeaponSkillType().equals("varied") && equippedItems[OFFHAND].getType().equals("axe") || equippedItems[OFFHAND].getType().equals("dagger") || equippedItems[OFFHAND].getType().equals("sword")){
                        weaponSkill += item.getWeaponSkill();
                    }
                }
            }
        }


        weaponSkill += equippedItems[OFFHAND].getWeaponSkill();

        if(equippedItems[MAINHAND] != null){
            weaponSkill += (equippedItems[MAINHAND].getType().equals(equippedItems[OFFHAND].getType()) ? equippedItems[MAINHAND].getWeaponSkill() : 0);
        }

        if(race.getId() == HUMAN){
            if(equippedItems[OFFHAND].getType().equals("mace") || equippedItems[OFFHAND].getType().equals("sword")){
                weaponSkill += 5;
            }
        }
        if(race.getId() == ORC){
            if(equippedItems[OFFHAND].getType().equals("axe")){
                weaponSkill += 5;
            }
        }

        return weaponSkill;
    }
}
