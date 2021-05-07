package sim.engine;

import java.util.concurrent.ThreadLocalRandom;

public class AttackTable {
    private double miss = 0;
    private double parry = 0;
    private double dodge = 0;
    private double block = 0;
    private double glancing = 0;
    private double crit = 0;

    private int weaponSkill;
    private int targetLevel;
    private int hitChance;
    private double critChance;
    private int agility;
    private boolean dualWielding;
    private boolean yellowTable;
    private int playerLevel;

    public enum RollType{
        HIT,
        CRIT,
        GLANCING,
        MISS,
        DODGE,
        PARRY,
        BLOCK
    }

    public AttackTable(int weaponSkill, int targetLevel, int playerLevel, int hitChance, double critChance, int agility, boolean dualWielding, boolean yellowTable) {
        this.weaponSkill = weaponSkill;
        this.targetLevel = targetLevel;
        this.playerLevel = playerLevel;
        this.hitChance = hitChance;
        this.critChance = critChance;
        this.agility = agility;
        this.dualWielding = dualWielding;
        this.yellowTable = yellowTable;

        updateTable();
    }

    public double getMiss() {
        return miss;
    }

    public void setMiss(double miss) {
        this.miss = miss;
    }

    public double getParry() {
        return parry;
    }

    public void setParry(double parry) {
        this.parry = parry;
    }

    public double getDodge() {
        return dodge;
    }

    public void setDodge(double dodge) {
        this.dodge = dodge;
    }

    public double getBlock() {
        return block;
    }

    public void setBlock(double block) {
        this.block = block;
    }

    public double getGlancing() {
        return glancing;
    }

    public void setGlancing(double glancing) {
        this.glancing = glancing;
    }

    public double getCrit() {
        return crit;
    }

    public void setCrit(double crit) {
        this.crit = crit;
    }

    public void setYellowTable(boolean yellowTable) {
        this.yellowTable = yellowTable;
        updateTable();
    }

    public void updateTable(){
        int targetDefense = targetLevel * 5;
        int skillDiff = targetDefense - weaponSkill;
        int cappedWeaponSkill = Math.min(weaponSkill, playerLevel * 5);

        if(skillDiff > 10){
            miss = 5 + skillDiff * 0.2;
        }else{
            miss = 5 + skillDiff * 0.1;
        }

        if(dualWielding && !yellowTable){
            miss = miss + 19;
        }

        if(skillDiff > 10){
            miss = Math.max(miss - (hitChance - 1), 0);
        }else{
            miss = Math.max(miss - hitChance, 0);
        }

        dodge = 5 + skillDiff * 0.1;

        if(!yellowTable){
            glancing = Math.max(10 + (targetDefense - cappedWeaponSkill) * 2, 0);
        }

        if(cappedWeaponSkill - targetDefense < 0){
            crit = critChance + (cappedWeaponSkill - targetDefense) * 0.2;
        }else{
            crit = critChance + (cappedWeaponSkill - targetDefense) * 0.04;
        }

        if(targetLevel - playerLevel > 2 && critChance - agility/20.0 > 0){
            crit = crit - Math.min(1.8, critChance - agility/20.0);
        }
    }

    public RollType rollTable(){
        double roll = ThreadLocalRandom.current().nextDouble(0, 100);

        double range = miss;

        if(roll < range){
            return RollType.MISS;
        }

        range += dodge;

        if(roll < range){
            return RollType.DODGE;
        }

        range += glancing;

        if(roll < range){
            return RollType.GLANCING;
        }

        range += crit;

        if(roll < range){
            return RollType.CRIT;
        }

        return RollType.HIT;
    }

    @Override
    public String toString() {
        return "AttackTable{" +
                "miss=" + miss +
                ", parry=" + parry +
                ", dodge=" + dodge +
                ", block=" + block +
                ", glancing=" + glancing +
                ", crit=" + crit +
                '}';
    }
}
