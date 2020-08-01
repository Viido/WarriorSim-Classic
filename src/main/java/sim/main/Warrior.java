package sim.main;

import sim.items.Enchant;
import sim.items.Item;
import sim.settings.Aura;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Warrior implements Serializable {
    Item[] equippedItems = new Item[17];
    Enchant[] equippedEnchants = new Enchant[17];
    Map<Integer, Aura> activeAuras = new HashMap<>();
    Map<Integer, Integer> activeTalents = new HashMap<>();

    public Item[] getEquippedItems() {
        return equippedItems;
    }

    public void equipItem(int slot, Item item){
        equippedItems[slot] = item;
    }

    public Enchant[] getEquippedEnchants() {
        return equippedEnchants;
    }

    public void equipEnchant(int slot, Enchant enchant){
        equippedEnchants[slot] = enchant;
    }

    public Map<Integer, Aura> getActiveAuras() {
        return activeAuras;
    }

    public Map<Integer, Integer> getActiveTalents() {
        return activeTalents;
    }
}
