package sim.data;

import sim.items.Enchant;

import java.util.ArrayList;
import java.util.List;

public class Enchants {
    private List<Enchant> enchants;

    public List<Enchant> getHeadEnchants(){
        List<Enchant> result = new ArrayList<>();

        for(Enchant enchant : enchants){
            for(String slot : enchant.getSlot()){
                if(slot.equals("head")){
                    result.add(enchant);
                }
            }
        }

        return result;
    }

    public List<Enchant> getShoulderEnchants(){
        List<Enchant> result = new ArrayList<>();

        for(Enchant enchant : enchants){
            for(String slot : enchant.getSlot()){
                if(slot.equals("shoulder")){
                    result.add(enchant);
                }
            }
        }

        return result;
    }

    public List<Enchant> getBackEnchants(){
        List<Enchant> result = new ArrayList<>();

        for(Enchant enchant : enchants){
            for(String slot : enchant.getSlot()){
                if(slot.equals("back")){
                    result.add(enchant);
                }
            }
        }

        return result;
    }

    public List<Enchant> getChestEnchants(){
        List<Enchant> result = new ArrayList<>();

        for(Enchant enchant : enchants){
            for(String slot : enchant.getSlot()){
                if(slot.equals("chest")){
                    result.add(enchant);
                }
            }
        }

        return result;
    }

    public List<Enchant> getWristEnchants(){
        List<Enchant> result = new ArrayList<>();

        for(Enchant enchant : enchants){
            for(String slot : enchant.getSlot()){
                if(slot.equals("wrist")){
                    result.add(enchant);
                }
            }
        }

        return result;
    }

    public List<Enchant> getHandsEnchants(){
        List<Enchant> result = new ArrayList<>();

        for(Enchant enchant : enchants){
            for(String slot : enchant.getSlot()){
                if(slot.equals("hands")){
                    result.add(enchant);
                }
            }
        }

        return result;
    }

    public List<Enchant> getLegsEnchants(){
        List<Enchant> result = new ArrayList<>();

        for(Enchant enchant : enchants){
            for(String slot : enchant.getSlot()){
                if(slot.equals("legs")){
                    result.add(enchant);
                }
            }
        }

        return result;
    }

    public List<Enchant> getFeetEnchants(){
        List<Enchant> result = new ArrayList<>();

        for(Enchant enchant : enchants){
            for(String slot : enchant.getSlot()){
                if(slot.equals("feet")){
                    result.add(enchant);
                }
            }
        }

        return result;
    }

    public List<Enchant> getMHEnchants(){
        List<Enchant> result = new ArrayList<>();

        for(Enchant enchant : enchants){
            for(String slot : enchant.getSlot()){
                if(slot.equals("weapon") || slot.equals("2h")){
                    result.add(enchant);
                }
            }
        }

        return result;
    }

    public List<Enchant> getOHEnchants(){
        List<Enchant> result = new ArrayList<>();

        for(Enchant enchant : enchants){
            for(String slot : enchant.getSlot()){
                if(slot.equals("weapon") || slot.equals("shield")){
                    result.add(enchant);
                }
            }
        }

        return result;
    }

    public List<Enchant> getEnchantsBySlot(int slot){
        switch(slot){
            case 0:
                return getHeadEnchants();
            case 2:
                return getShoulderEnchants();
            case 3:
                return getBackEnchants();
            case 4:
                return getChestEnchants();
            case 5:
                return getWristEnchants();
            case 6:
                return getMHEnchants();
            case 7:
                return getOHEnchants();
            case 9:
                return getHandsEnchants();
            case 11:
                return getLegsEnchants();
            case 12:
                return getFeetEnchants();
        }

        return null;
    }
}
