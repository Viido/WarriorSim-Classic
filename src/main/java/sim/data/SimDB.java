package sim.data;

import com.google.gson.Gson;
import sim.items.ItemSet;
import sim.settings.Race;
import sim.talents.Talent;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public final class SimDB {
    public static Map<Integer, ItemSet> ITEM_SETS = new HashMap<>();
    public static Items ITEMS;
    public static Enchants ENCHANTS;
    public static Auras AURAS;
    public static Race[] RACES;
    public static Talent[] TALENTS;

    private SimDB(){}

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

        ITEMS.sortItems();
    }
}
