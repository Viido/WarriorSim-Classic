package sim.engine.abilities;

import sim.engine.AttackTable;
import sim.engine.Event;
import sim.engine.Fight;
import sim.engine.Target;

import java.util.concurrent.ThreadLocalRandom;

import static sim.data.Constants.DUAL_WIELD_SPEC;

public class AutoAttackOH extends Ability{
    double dualWieldPenalty;

    public AutoAttackOH(Fight fight) {
        super(0, 0, fight, null);
        setType(Event.EventType.AUTOATTACK_OH);
        dualWieldPenalty = (0.5 * (1 + warrior.getCharacterSetup().getActiveTalents().getOrDefault(DUAL_WIELD_SPEC, 0) * 0.05));
    }

    @Override
    protected double calcBaseDamage() {
        int baseDamage = ThreadLocalRandom.current().nextInt(warrior.getOffHand().getMinDmg(), warrior.getOffHand().getMaxDmg() + 1);

        return baseDamage + warrior.getAp() / 14.0 * warrior.getOffHand().getBaseSpeed();
    }

    @Override
    protected AbilityResult calculateResult(Target target, AttackTable.RollType rollType) {
        if(rollType == AttackTable.RollType.DODGE || rollType == AttackTable.RollType.MISS){
            return new AbilityResult(getType(), rollType, 0);
        }

        double damage = calcBaseDamage();

        damage *= dualWieldPenalty;

        if(rollType == AttackTable.RollType.CRIT){
            damage *= 2;

            warrior.applyFlurry();
        }else{
            warrior.decrementFlurry();
        }

        if(rollType == AttackTable.RollType.GLANCING){
            damage *= warrior.glancingBlow(warrior.getOffHand());
        }

        damage *= warrior.getDamageMod();


        damage = applyArmorMitigation(damage, target);

        return new AbilityResult(getType(), rollType, damage);
    }

    @Override
    public void useAbility() {
        warrior.getOffHandTable().setYellowTable(warrior.isHeroicStrikeQueued());
        AttackTable.RollType rollType = warrior.getOffHandTable().rollTable();

        AbilityResult abilityResult = calculateResult(fight.getTarget(), rollType);

        if(rollType != AttackTable.RollType.MISS && rollType != AttackTable.RollType.DODGE){
            fight.rollOffHandProcs();
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
