package sim.settings;

import sim.data.SimDB;
import sim.engine.warrior.Stats;
import sim.engine.warrior.Warrior;
import sim.items.Enchant;
import sim.items.Item;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static sim.data.Constants.*;

public class CharacterSetup implements Serializable {
    private Race race;
    private Item[] equippedItems = new Item[17];
    private Enchant[] equippedEnchants = new Enchant[17];
    private Map<Integer, Aura> activeAuras = new HashMap<>();
    private Map<Integer, Integer> activeTalents = new HashMap<>();
    private Aura tempEnchantMH; // TODO find a better way to handle dual-wield temp enchants
    private Aura tempEnchantOH;
    private Map<Integer, SetBonusControl> setBonuses = new HashMap<>();

    private transient Warrior warrior = new Warrior(this);

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        warrior = new Warrior(this);
    }

    public Item[] getEquippedItems() {
        return equippedItems;
    }

    public void equipItem(int slot, Item newItem){
        if(equippedItems[slot] != null){
            warrior.getStats().removeStats(equippedItems[slot]);

            if(equippedItems[slot].getItemSetId() != 0){
                setBonuses.get(equippedItems[slot].getItemSetId()).removeItem(equippedItems[slot]);

                if(setBonuses.get(equippedItems[slot].getItemSetId()).getActiveItems().size() == 0){
                    setBonuses.remove(equippedItems[slot].getItemSetId());
                }
            }
        }

        equippedItems[slot] = newItem;
        warrior.getStats().addStats(newItem);

        if(newItem.getItemSetId() != 0){
            setBonuses.computeIfAbsent(newItem.getItemSetId(), k -> new SetBonusControl(this, newItem.getItemSetId())).addItem(newItem);
        }
    }

    public void unequipItem(int slot){
        warrior.getStats().removeStats(equippedItems[slot]);

        if(equippedItems[slot].getItemSetId() != 0){
            setBonuses.get(equippedItems[slot].getItemSetId()).removeItem(equippedItems[slot]);

            if(setBonuses.get(equippedItems[slot].getItemSetId()).getActiveItems().size() == 0){
                setBonuses.remove(equippedItems[slot].getItemSetId());
            }
        }

        equippedItems[slot] = null;
    }

    public Enchant[] getEquippedEnchants() {
        return equippedEnchants;
    }

    public void equipEnchant(int slot, Enchant newEnchant){
        if(equippedEnchants[slot] != null){
            warrior.getStats().removeStats(equippedEnchants[slot]);
        }
        equippedEnchants[slot] = newEnchant;
        warrior.getStats().addStats(newEnchant);
    }

    public void unequipEnchant(int slot){
        if(equippedEnchants[slot] != null){
            warrior.getStats().removeStats(equippedEnchants[slot]);
            equippedEnchants[slot] = null;
        }
    }

    public Map<Integer, Aura> getActiveAuras() {
        return activeAuras;
    }

    public void addAura(Aura aura){
        activeAuras.put(aura.getId(), aura);
        warrior.getStats().addStats(aura);
    }

    public void removeAura(Aura aura){
        activeAuras.remove(aura.getId());
        warrior.getStats().removeStats(aura);
    }

    public int getArmorReduction(){
        int armorReduction = 0;

        for(Aura aura : activeAuras.values()){
            if(aura.getType().equals("debuff")){
                armorReduction += aura.getArmor();
            }
        }

        return armorReduction;
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

    public Warrior getWarrior() {
        return warrior;
    }

    public SetBonusControl getSetBonusControl(Integer itemSetId){
        return setBonuses.get(itemSetId);
    }

    public Map<Integer, SetBonusControl> getSetBonuses(){
        return setBonuses;
    }

    @Override
    public String toString() {
        return "CharacterSetup{" +
                "race=" + race +
                ", equippedItems=" + Arrays.toString(equippedItems) +
                ", equippedEnchants=" + Arrays.toString(equippedEnchants) +
                ", activeAuras=" + activeAuras +
                ", activeTalents=" + activeTalents +
                ", tempEnchantMH=" + tempEnchantMH +
                ", tempEnchantOH=" + tempEnchantOH +
                ", setBonuses=" + setBonuses +
                ", warrior=" + warrior +
                '}';
    }
}
