package sim.engine.warrior;

import sim.engine.warrior.Warrior;
import sim.items.Item;

public class Weapon {
    private String type;
    private String slot;
    private int minDmg;
    private int maxDmg;
    private double baseSpeed;
    private double currentSpeed;
    private String eleDmgType;
    private int eleDmgMin;
    private int eleDmgMax;
    private int skill;
    private double swingTimerEnd = 0;

    private Warrior warrior;

    public Weapon(Item item, Warrior warrior) {
        type = item.getType();
        slot = item.getSlot();
        this.warrior = warrior;

        if(!type.equals("shield")){
            minDmg = item.getMinDmg();
            maxDmg = item.getMaxDmg();
            baseSpeed = item.getSpeed();
            currentSpeed = item.getSpeed();

            if(item.getEleDmgType() != null){
                eleDmgType = item.getEleDmgType();
                eleDmgMin = item.getEleDmgMin();
                eleDmgMax = item.getEleDmgMax();
            }
        }
    }

    public String getType() {
        return type;
    }

    public int getMinDmg() {
        return minDmg;
    }

    public int getMaxDmg() {
        return maxDmg;
    }

    public double getBaseSpeed() {
        return baseSpeed;
    }

    public String getEleDmgType() {
        return eleDmgType;
    }

    public int getEleDmgMin() {
        return eleDmgMin;
    }

    public int getEleDmgMax() {
        return eleDmgMax;
    }

    public int getSkill() {
        return skill;
    }

    public double getCurrentSpeed() {
        return baseSpeed / warrior.getHaste();
    }

    public double getNormalizedSpeed(){
        if(type.equals("dagger")){
            return 1.7;
        }

        if(slot.equals("2h")){
            return 3.3;
        }

        return 2.4;
    }

    public double getSwingTimerEnd() {
        return swingTimerEnd;
    }

    public void setSwingTimerEnd(double swingTimerEnd) {
        this.swingTimerEnd = swingTimerEnd;
    }

    public double getRemainingSwingTimer(double currentTime){
        if(currentTime > swingTimerEnd){
            throw new ArithmeticException("currenttime > swingttimerend");
        }
        return swingTimerEnd - currentTime;
    }

    public void setSkill(int skill) {
        this.skill = skill;
    }
}
