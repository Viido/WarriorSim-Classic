package sim.items;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Enchant implements Serializable {
    private int id;
    @SerializedName(value = "spellid")
    private boolean spellId;
    private String name;
    private String[] slot;
    private int haste;
    private int agi;
    private int str;
    private int sta;
    private int defense;
    private int block;
    private int blockValue;
    private int ap;
    private int crit;
    @SerializedName(value = "health")
    private int hp;
    @SerializedName(value = "bonusdmg")
    private int bonusDmg;
    private String description;
    private int phase;
    private String quality;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isSpellId() {
        return spellId;
    }

    public void setSpellId(boolean spellId) {
        this.spellId = spellId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getSlot() {
        return slot;
    }

    public void setSlot(String[] slot) {
        this.slot = slot;
    }

    public double getHaste() {
        return 1 + haste/100.0d;
    }

    public void setHaste(int haste) {
        this.haste = haste;
    }

    public int getAgi() {
        return agi;
    }

    public void setAgi(int agi) {
        this.agi = agi;
    }

    public int getStr() {
        return str;
    }

    public void setStr(int str) {
        this.str = str;
    }

    public int getSta() {
        return sta;
    }

    public void setSta(int sta) {
        this.sta = sta;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public int getBlock() {
        return block;
    }

    public void setBlock(int block) {
        this.block = block;
    }

    public int getBlockValue() {
        return blockValue;
    }

    public void setBlockValue(int blockValue) {
        this.blockValue = blockValue;
    }

    public int getAp() {
        return ap;
    }

    public void setAp(int ap) {
        this.ap = ap;
    }

    public int getCrit() {
        return crit;
    }

    public void setCrit(int crit) {
        this.crit = crit;
    }

    public int getBonusDmg() {
        return bonusDmg;
    }

    public void setBonusDmg(int bonusDmg) {
        this.bonusDmg = bonusDmg;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPhase() {
        return phase;
    }

    public void setPhase(int phase) {
        this.phase = phase;
    }

    public int getHp() {
        return hp;
    }

    @Override
    public String toString(){
        return name;
    }

    public String getColor(){
        switch(quality){
            case "legendary": return "#ff8000";
            case "epic": return "#a335ee";
            case "rare": return "#0070dd";
            case "uncommon": return "#1eff00";
        }

        return "";
    }
}
