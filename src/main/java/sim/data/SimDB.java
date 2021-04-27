package sim.data;

import com.google.gson.Gson;
import sim.items.ItemSet;
import sim.items.Spell;
import sim.settings.Race;
import sim.talents.Talent;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SimDB {
    public static Map<Integer, ItemSet> ITEM_SETS = new HashMap<>();
    public static Items ITEMS;
    public static Enchants ENCHANTS;
    public static Auras AURAS;
    public static Race[] RACES;
    public static Talent[] TALENTS;
    public static Map<Integer, Spell> SPELLS = new HashMap<>();

    private SimDB(){}

    public enum SpellEffect{
        STAT_BUFF
    }

    static{
        Gson gson = new Gson();
        ITEMS = gson.fromJson(new InputStreamReader(SimDB.class.getResourceAsStream("/sim/data/items.json")), Items.class);
        ENCHANTS = gson.fromJson(new InputStreamReader(SimDB.class.getResourceAsStream("/sim/data/enchants.json")), Enchants.class);
        AURAS = gson.fromJson(new InputStreamReader(SimDB.class.getResourceAsStream("/sim/data/auras.json")), Auras.class);
        RACES = gson.fromJson(new InputStreamReader(SimDB.class.getResourceAsStream("/sim/data/races.json")), Race[].class);
        TALENTS = gson.fromJson(new InputStreamReader(SimDB.class.getResourceAsStream("/sim/data/talents.json")), Talent[].class);


        ItemSet[] itemSetArray = gson.fromJson(new InputStreamReader(SimDB.class.getResourceAsStream("/sim/data/itemsets.json")), ItemSet[].class);

        for(ItemSet itemSet : itemSetArray){
            ITEM_SETS.put(itemSet.getId(), itemSet);
        }

        Spell[] spellArray = gson.fromJson(new InputStreamReader(SimDB.class.getResourceAsStream("/sim/data/spells.json")), Spell[].class);

        for(Spell spell : spellArray){
            SPELLS.put(spell.getId(), spell);
        }

        ITEMS.sortItems();
    }
}
