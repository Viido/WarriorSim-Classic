package sim.main;

import com.google.gson.Gson;
import sim.items.Item;
import sim.items.ItemSet;
import sim.items.Items;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public final class SimDB {
    public static Map<Integer, Item> ITEMS = new HashMap<>();
    public static Map<Integer, ItemSet> ITEM_SETS = new HashMap<>();

    private SimDB(){}

    static{
        Gson gson = new Gson();
        Items itemsTemp = gson.fromJson(new InputStreamReader(SimDB.class.getResourceAsStream("/sim/items/data/items.json")), Items.class);
        ItemSet[] itemSetArray = gson.fromJson(new InputStreamReader(SimDB.class.getResourceAsStream("/sim/items/data/itemsets.json")), ItemSet[].class);

        for(ItemSet itemSet : itemSetArray){
            ITEM_SETS.put(itemSet.getId(), itemSet);
        }

        for(Item item : itemsTemp.getAllItems()){
            ITEMS.put(item.getId(), item);
        }
    }
}
