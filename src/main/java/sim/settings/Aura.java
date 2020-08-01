package sim.settings;

import java.io.Serializable;

public class Aura implements Serializable {
    private int id;
    private String name;
    private String type;
    private String group;
    private int spellCrit;
    private int crit;
    private int ap;
    private int str;
    private int agi;
    private int sta;
    private int health;
    private int haste;
    private int statMod;
    private int staMod;
    private int dmgMod;
    private int fireRes;
    private int natureRes;
    private int frostRes;
    private int arcaneRes;
    private int shadowRes;
    private int dmg;
    private int armor;
    private String faction;
    private String icon;
    private String description;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getGroup() {
        return group;
    }

    public int getSpellCrit() {
        return spellCrit;
    }

    public int getCrit() {
        return crit;
    }

    public int getAp() {
        return ap;
    }

    public int getStr() {
        return str;
    }

    public int getAgi() {
        return agi;
    }

    public int getSta() {
        return sta;
    }

    public int getHealth() {
        return health;
    }

    public int getHaste() {
        return haste;
    }

    public int getStatMod() {
        return statMod;
    }

    public int getStaMod() {
        return staMod;
    }

    public int getDmgMod() {
        return dmgMod;
    }

    public int getFireRes() {
        return fireRes;
    }

    public int getNatureRes() {
        return natureRes;
    }

    public int getFrostRes() {
        return frostRes;
    }

    public int getArcaneRes() {
        return arcaneRes;
    }

    public int getShadowRes() {
        return shadowRes;
    }

    public int getDmg() {
        return dmg;
    }

    public int getArmor() {
        return armor;
    }

    public String getFaction() {
        return faction;
    }

    public String getIcon() {
        return icon;
    }

    public String getDescription() {
        return description;
    }
}
