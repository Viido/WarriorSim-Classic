package sim.warrior;

import sim.items.Enchant;
import sim.items.Item;
import sim.settings.Aura;

import java.io.Serializable;

public class Stats implements Serializable {
    private int str;
    private int agi;
    private int sta;
    private int hp;
    private int armor;
    private int ap;
    private int hit;
    private int crit;
    private Warrior warrior;

    public Stats(Warrior warrior){
        this.warrior = warrior;
    }

    public int getStr() {
        return str;
    }

    public void setStr(int str) {
        this.str = str;
    }

    public int getAgi() {
        return agi;
    }

    public void setAgi(int agi) {
        this.agi = agi;
    }

    public int getSta() {
        return sta;
    }

    public void setSta(int sta) {
        this.sta = sta;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getArmor() {
        return armor;
    }

    public void setArmor(int armor) {
        this.armor = armor;
    }

    public int getAp() {
        return ap;
    }

    public void setAp(int ap) {
        this.ap = ap;
    }

    public int getHit() {
        return hit;
    }

    public void setHit(int hit) {
        this.hit = hit;
    }

    public int getCrit() {
        return crit;
    }

    public void setCrit(int crit) {
        this.crit = crit;
    }

    public void refreshStats(){
        str = 0;
        agi = 0;
        sta = 0;
        ap = 0;
        hit = 0;
        crit = 0;
        armor = 0;
        hp = 0;

        for(Item item : warrior.getEquippedItems()){
            addStats(item);
        }

        for(Enchant enchant : warrior.getEquippedEnchants()){
            addStats(enchant);
        }

        for(Aura aura : warrior.getActiveAuras().values()){
            addStats(aura);
        }
    }

    public void removeStats(Item item){
        str -= item.getStr();
        agi -= item.getAgi();
        sta -= item.getSta();
        ap -= item.getAp();
        hit -= item.getHit();
        crit -= item.getCrit();
        armor -= item.getArmor();
    }

    public void addStats(Item item){
        if(item != null){
            str += item.getStr();
            agi += item.getAgi();
            sta += item.getSta();
            ap += item.getAp();
            hit += item.getHit();
            crit += item.getCrit();
            armor += item.getArmor();
        }
    }

    public void removeStats(Enchant enchant){
        str -= enchant.getStr();
        agi -= enchant.getAgi();
        sta -= enchant.getSta();
        ap -= enchant.getAp();
        hp -= enchant.getHp();
        crit -= enchant.getCrit();
    }

    public void addStats(Enchant enchant){
        if(enchant != null){
            str += enchant.getStr();
            agi += enchant.getAgi();
            sta += enchant.getSta();
            ap += enchant.getAp();
            hp += enchant.getHp();
            crit += enchant.getCrit();
        }
    }

    public void removeStats(Aura aura){
        if(!aura.getType().equals("debuff")){
            str -= aura.getStr();
            agi -= aura.getAgi();
            sta -= aura.getSta();
            ap -= aura.getAp();
            crit -= aura.getCrit();
            armor -= aura.getArmor();
            hp -= aura.getHp();
        }
    }

    public void addStats(Aura aura){
        if(!aura.getType().equals("debuff")){
            str += aura.getStr();
            agi += aura.getAgi();
            sta += aura.getSta();
            ap += aura.getAp();
            crit += aura.getCrit();
            armor += aura.getArmor();
            hp += aura.getHp();
        }
    }
}
