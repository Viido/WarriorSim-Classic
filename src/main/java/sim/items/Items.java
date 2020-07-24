package sim.items;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Items {
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

    public List<Item> getOneHandedWeaponsByType(String type){
        return oneHandedWeapons.stream().filter(x -> x.getType().equals(type)).collect(Collectors.toList());
    }

    public List<Item> getTwoHandedWeaponsByType(String type){
        return twoHandedWeapons.stream().filter(x -> x.getType().equals(type)).collect(Collectors.toList());
    }
}
