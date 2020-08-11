package sim.warrior;

import sim.items.Item;
import sim.items.ItemSet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static sim.main.SimDB.ITEM_SETS;

public class SetBonusControl implements Serializable {
    private Warrior warrior;
    private Integer itemSetId;
    private List<Integer> activeItems = new ArrayList<>();
    private List<Integer> activeSetBonuses = new ArrayList<>();

    public SetBonusControl(Warrior warrior, Integer itemSetId){
        this.warrior = warrior;
        this.itemSetId = itemSetId;
    }

    public void addItem(Item item){
        activeItems.add(item.getId());

        for(int i = 0; i < ITEM_SETS.get(itemSetId).getSetBonuses().size(); i++){
            ItemSet.ItemSetBonus itemSetBonus = ITEM_SETS.get(itemSetId).getSetBonuses().get(i);

            if(activeItems.size() >= itemSetBonus.getCount() && !activeSetBonuses.contains(i)){
                activeSetBonuses.add(i);

                warrior.getStats().addStats(itemSetBonus);
            }
        }
    }

    public void removeItem(Item item){
        activeItems.remove(Integer.valueOf(item.getId()));

        for(int i = 0; i < ITEM_SETS.get(itemSetId).getSetBonuses().size(); i++){
            ItemSet.ItemSetBonus itemSetBonus = ITEM_SETS.get(itemSetId).getSetBonuses().get(i);

            if(activeItems.size() < itemSetBonus.getCount() && activeSetBonuses.contains(i)){
                activeSetBonuses.remove(Integer.valueOf(i));

                warrior.getStats().removeStats(itemSetBonus);
            }
        }
    }

    public Warrior getWarrior() {
        return warrior;
    }

    public Integer getItemSetId() {
        return itemSetId;
    }

    public List<Integer> getActiveItems() {
        return activeItems;
    }

    public List<Integer> getActiveSetBonuses() {
        return activeSetBonuses;
    }
}
