package sim.data;

import com.google.gson.annotations.SerializedName;
import sim.items.Item;

import java.util.*;
import java.util.stream.Collectors;

public class Items{
    private List<Item> head;
    private List<Item> neck;
    private List<Item> shoulder;
    private List<Item> back;
    private List<Item> chest;
    private List<Item> wrist;
    private List<Item> hands;
    private List<Item> waist;
    private List<Item> legs;
    private List<Item> feet;
    private List<Item> rings;
    private List<Item> trinkets;
    private List<Item> oneHandedWeapons;
    private List<Item> twoHandedWeapons;
    @SerializedName(value = "ranged")
    private List<Item> rangedWeapons;
    private List<Item> shields;
    private Map<Integer, Item> itemMap;

    public List<Item> getHead() {
        return head;
    }

    public List<Item> getNeck() {
        return neck;
    }

    public List<Item> getShoulder() {
        return shoulder;
    }

    public List<Item> getBack() {
        return back;
    }

    public List<Item> getChest() {
        return chest;
    }

    public List<Item> getWrist() {
        return wrist;
    }

    public List<Item> getHands() {
        return hands;
    }

    public List<Item> getWaist() {
        return waist;
    }

    public List<Item> getLegs() {
        return legs;
    }

    public List<Item> getFeet() {
        return feet;
    }

    public List<Item> getRings() {
        return rings;
    }

    public List<Item> getTrinkets() {
        return trinkets;
    }

    public List<Item> getOneHandedWeapons() {
        return oneHandedWeapons;
    }

    public List<Item> getTwoHandedWeapons() {
        return twoHandedWeapons;
    }

    public List<Item> getRangedWeapons() {
        return rangedWeapons;
    }

    public List<Item> getShields() {
        return shields;
    }

    public List<Item> getMHItems(){
        List<Item> items = new ArrayList<>();

        for(Item item : oneHandedWeapons){
            if(!item.getSlot().equals("off")){
                items.add(item);
            }
        }

        items.addAll(twoHandedWeapons);

        return items;
    }

    public List<Item> getOHItems(){
        List<Item> items = new ArrayList<>();

        for(Item item : oneHandedWeapons){
            if(!item.getSlot().equals("main")){
                items.add(item);
            }
        }

        items.addAll(shields);

        return items;
    }

    public List<Item> getAllItems(){
        List<Item> items = new ArrayList<>();

        items.addAll(head);
        items.addAll(neck);
        items.addAll(shoulder);
        items.addAll(back);
        items.addAll(chest);
        items.addAll(wrist);
        items.addAll(hands);
        items.addAll(waist);
        items.addAll(legs);
        items.addAll(feet);
        items.addAll(rings);
        items.addAll(trinkets);
        items.addAll(oneHandedWeapons);
        items.addAll(twoHandedWeapons);
        items.addAll(rangedWeapons);
        items.addAll(shields);

        return items;
    }

    public List<Item> getItemsBySlot(int slot){
        switch(slot){
            case 0: return head;
            case 1: return neck;
            case 2: return shoulder;
            case 3: return back;
            case 4: return chest;
            case 5: return wrist;
            case 6: return getMHItems();
            case 7: return getOHItems();
            case 8: return getRangedWeapons();
            case 9: return hands;
            case 10: return waist;
            case 11: return legs;
            case 12: return feet;
            case 13:
            case 14:
                return rings;
            case 15:
            case 16:
                return trinkets;
        }
        return null;
    }

    public List<Item> getOHItemsByType(String type){
        if(type.equals("shield")){
            return shields;
        }

        return oneHandedWeapons.stream().filter(x -> x.getType().equals(type) && !x.getSlot().equals("main")).collect(Collectors.toList());
    }

    public List<Item> getMHOneHandedWeaponsByType(String type){
        if(type.equals("all")){
            return oneHandedWeapons.stream().filter(x -> !x.getSlot().equals("off")).collect(Collectors.toList());
        }

        return oneHandedWeapons.stream().filter(x -> x.getType().equals(type) && !x.getSlot().equals("off")).collect(Collectors.toList());
    }

    public List<Item> getTwoHandedWeaponsByType(String type){
        return twoHandedWeapons.stream().filter(x -> x.getType().equals(type)).collect(Collectors.toList());
    }

    public Item getItemById(int id){
        if(itemMap == null){
            itemMap = new HashMap<>();

            for(Item item : getAllItems()){
                itemMap.put(item.getId(), item);
            }
        }

        return itemMap.get(id);
    }

    public void sortItems(){
       Collections.sort(head);
       Collections.sort(neck);
       Collections.sort(shoulder);
       Collections.sort(back);
       Collections.sort(chest);
       Collections.sort(wrist);
       Collections.sort(hands);
       Collections.sort(waist);
       Collections.sort(legs);
       Collections.sort(feet);
       Collections.sort(rings);
       Collections.sort(trinkets);
       Collections.sort(oneHandedWeapons);
       Collections.sort(twoHandedWeapons);
       Collections.sort(rangedWeapons);
       Collections.sort(shields);
    }
}
