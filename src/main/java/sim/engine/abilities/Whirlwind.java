package sim.engine.abilities;

import sim.engine.Fight;
import sim.rotation.RotationOption;

import java.util.concurrent.ThreadLocalRandom;

public class Whirlwind extends Ability{
    public Whirlwind(Fight fight, RotationOption rotationOption) {
        super(12000, 25, fight, rotationOption);
        setType(EventType.WHIRLWIND);
        cooldownEvent.setType(EventType.WHIRLWIND_CD);
    }

    @Override
    public double calcBaseDamage() {
        int baseDamage = ThreadLocalRandom.current().nextInt(warrior.getMainHand().getMinDmg(), warrior.getMainHand().getMaxDmg() + 1);

        return baseDamage + warrior.getAp() / 14.0 * warrior.getMainHand().getNormalizedSpeed();
    }

    @Override
    public void useAbility() {
        super.useAbility();

        if(fight.getSettings().isMultitarget()){
            for(int i = 0; i < fight.getSettings().getExtraTargets(); i++){
                AbilityResult abilityResult = calculateResult(fight.getExtraTarget(), warrior.getExtraTargetTable().rollTable());

                fight.getResults().addAbilityResult(abilityResult);
                fight.log(getType() + " " + abilityResult.toString());
            }
        }
    }

    @Override
    public boolean isUsable() {
        return super.isUsable() && !warrior.isOnGlobalCooldown();
    }
}
