package sim.items;

import java.io.Serializable;
import java.util.List;

public class ItemSet {
    private int id;
    private String name;
    private List<ItemSetBonus> setBonuses;
    private List<Integer> itemIds;


    public static class ItemSetBonus implements Serializable {
        private int count;
        private int ap;
        private int sta;
        private int allRes;
        private int spellId;
        private int armor;
        private int hit;
        private int crit;
        private int block;
        private int blockValue;
        private int fireRes;
        private int weaponSkill;
        private int defense;
        private int parry;
        private int dodge;
        private String weaponSkillType;
        private String description;


        public int getSpellId() {
            return spellId;
        }

        public int getCount() {
            return count;
        }

        public String getDescription() {
            if(description == null){
                if(ap != 0){
                    return "+" + ap + " Attack Power.";
                }

                if(sta != 0){
                    return "+" + sta + " Stamina.";
                }

                if(allRes != 0){
                    return "+" + allRes + " All Resistances.";
                }

                if(armor != 0){
                    return "+" + armor + " Armor.";
                }

                if(hit != 0){
                    return "Improves your chance to hit by " + hit + "%.";
                }

                if(crit != 0){
                    return "Improves your chance to get a critical strike by " + crit + "%.";
                }

                if(block != 0){
                    return "Increases your chance to block attacks with a shield by " + block + "%.";
                }

                if(blockValue != 0){
                    return "Increases the block value of your shield by " + blockValue + ".";
                }

                if(fireRes != 0){
                    return "+" + fireRes + " Fire Resistance.";
                }

                if(weaponSkill != 0){
                    return "Increased " + weaponSkillType.substring(0, 1).toUpperCase() + weaponSkillType.substring(1) + "s +" + weaponSkill + ".";
                }

                if(defense != 0){
                    return "Increased Defense +" + defense + ".";
                }

                if(parry != 0){
                    return "Increases your chance to parry an attack by " + parry + "%.";
                }

                if(dodge != 0){
                    return "Increases your chance to dodge an attack by " + dodge + "%.";
                }
            }

            return description;
        }

        public int getAp() {
            return ap;
        }

        public int getSta() {
            return sta;
        }

        public int getAllRes() {
            return allRes;
        }

        public int getArmor() {
            return armor;
        }

        public int getHit() {
            return hit;
        }

        public int getCrit() {
            return crit;
        }

        public int getBlock() {
            return block;
        }

        public int getBlockValue() {
            return blockValue;
        }

        public int getFireRes() {
            return fireRes;
        }

        public int getWeaponSkill() {
            return weaponSkill;
        }

        public int getDefense() {
            return defense;
        }

        public int getParry() {
            return parry;
        }

        public int getDodge() {
            return dodge;
        }

        public String getWeaponSkillType() {
            return weaponSkillType;
        }
    }

    public int getId() {
        return id;
    }

    public List<Integer> getItemIds() {
        return itemIds;
    }

    public String getName() {
        return name;
    }

    public List<ItemSetBonus> getSetBonuses() {
        return setBonuses;
    }
}
