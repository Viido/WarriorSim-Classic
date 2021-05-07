package sim.engine.abilities;

import sim.engine.AttackTable;
import sim.engine.Fight;
import sim.engine.Target;

import java.util.concurrent.ThreadLocalRandom;


public class AutoAttackMH extends Ability{
    public AutoAttackMH(Fight fight) {
        super(0, 0, fight, null);
        setType(EventType.AUTOATTACK_MH);
    }

    @Override
    public double calcBaseDamage() {
        int baseDamage = ThreadLocalRandom.current().nextInt(warrior.getMainHand().getMinDmg(), warrior.getMainHand().getMaxDmg() + 1);

        return baseDamage + warrior.getAp() / 14.0 * warrior.getMainHand().getBaseSpeed();
    }

    @Override
    public AbilityResult calculateResult(Target target, AttackTable.RollType rollType) {
        if(rollType == AttackTable.RollType.DODGE || rollType == AttackTable.RollType.MISS){
            return new AbilityResult(getType(), rollType, 0);
        }

        double damage = calcBaseDamage();

        if(rollType == AttackTable.RollType.CRIT){
            damage *= 2;

            warrior.applyFlurry();
        }else{
            warrior.decrementFlurry();
        }

        if(rollType == AttackTable.RollType.GLANCING){
            damage *= warrior.glancingBlow(warrior.getMainHand());
        }

        damage *= warrior.getDamageMod();


        damage = applyArmorMitigation(damage, target);

        return new AbilityResult(getType(), rollType, damage);
    }

    @Override
    public void useAbility() {
        AttackTable.RollType rollType = warrior.getMainHandTable().rollTable();

        AbilityResult abilityResult = calculateResult(fight.getTarget(), rollType);

        if(rollType != AttackTable.RollType.MISS && rollType != AttackTable.RollType.DODGE){
            fight.rollMainHandProcs();
        }

        warrior.generateRage(abilityResult.getDamage());

        fight.getResults().addAbilityResult(abilityResult);

        fight.log(getType() + " " + abilityResult.toString());
    }

    @Override
    public boolean isUsable() {
        return true;
    }
}
