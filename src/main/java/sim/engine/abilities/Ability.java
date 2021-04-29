package sim.engine.abilities;

import sim.engine.AttackTable;
import sim.engine.Event;
import sim.engine.Fight;
import sim.engine.Target;
import sim.engine.warrior.Warrior;
import sim.rotation.RotationOption;

import static sim.data.Constants.IMPALE;

public abstract class Ability extends Event{
    protected int cooldown;
    protected int rageCost;
    protected boolean nextSwing = false;
    protected Warrior warrior;
    protected Fight fight;

    protected Event cooldownEvent = new Event();

    private boolean onCooldown = false;
    private RotationOption rotationOption;

    public Ability(int cooldown, int rageCost, Fight fight, RotationOption rotationOption) {
        super();
        this.cooldown = cooldown;
        this.rageCost = rageCost;
        this.warrior = fight.getWarrior();
        this.fight = fight;
        this.rotationOption = rotationOption;
    }

    protected abstract double calcBaseDamage();

    public boolean isUsable(){
        return isEnabled() && isReady() && isOverThreshold() && warrior.getRage() >= rageCost;
    }

    protected AbilityResult calculateResult(Target target, AttackTable.RollType rollType){
        if(rollType == AttackTable.RollType.DODGE || rollType == AttackTable.RollType.MISS){
            return new AbilityResult(getType(), rollType, 0);
        }

        double damage = calcBaseDamage();

        if(rollType == AttackTable.RollType.CRIT){
            damage *= 2 + warrior.getCharacterSetup().getActiveTalents().getOrDefault(IMPALE, 0) * 0.1;

            warrior.applyFlurry();
        }

        damage *= warrior.getDamageMod();


        damage = applyArmorMitigation(damage, target);

        return new AbilityResult(getType(), rollType, damage);
    }

    public void useAbility(){
        AttackTable.RollType rollType = warrior.getYellowTable().rollTable();

        AbilityResult abilityResult = calculateResult(fight.getTarget(), rollType);

        if(rollType != AttackTable.RollType.MISS && rollType != AttackTable.RollType.DODGE){
            fight.rollMainHandProcs();
        }

        if(nextSwing){
            if(abilityResult.getRollType() != AttackTable.RollType.CRIT){
                warrior.decrementFlurry();
            }
        }else{
            onCooldown = true;
        }

        warrior.removeRage(rageCost);

        fight.getResults().addAbilityResult(abilityResult);

        fight.log(getType() + " " + abilityResult.toString());
    }

    protected double applyArmorMitigation(double damage, Target target) {
        double armorMitigation = target.getArmor() / (target.getArmor() + 400 + 85.0 * warrior.getLevel());

        return damage * (1 - armorMitigation);
    }

    public boolean isReady(){
        return !onCooldown;
    }

    public boolean isEnabled(){
        return rotationOption.isSelected() && rotationOption.isEnabled();
    }

    public boolean isOverThreshold(){
        if(rotationOption.getType() == RotationOption.ThresholdType.RAGE_ABOVE){
            return warrior.getRage() >= rotationOption.getRageThreshold();
        }

        if(rotationOption.getType() == RotationOption.ThresholdType.RAGE_BELOW){
            return warrior.getRage() <= rotationOption.getRageThreshold();
        }
        if(rotationOption.getType() == RotationOption.ThresholdType.TIME_REMAINING){
            return fight.getCurrentTime() <= rotationOption.getTimeThreshold();
        }

        return true;
    }

    public void finishCooldown(){
        onCooldown = false;
    }

    public double getCooldown() {
        return cooldown;
    }

    public double getRageCost() {
        return rageCost;
    }

    public Warrior getWarrior(){
        return warrior;
    }

    public Event getCooldownEvent() {
        return cooldownEvent;
    }
}
