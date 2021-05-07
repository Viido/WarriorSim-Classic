package sim.engine.abilities;

import sim.engine.Fight;
import sim.rotation.RotationOption;

import java.util.concurrent.ThreadLocalRandom;

import static sim.data.Constants.IMP_CLEAVE;

public class Cleave extends Ability{
    private double bonusDamage;

    public Cleave(Fight fight, RotationOption rotationOption) {
        super(0, 20, fight, rotationOption);
        setType(EventType.CLEAVE);
        bonusDamage = warrior.getCharacterSetup().getActiveTalents().getOrDefault(IMP_CLEAVE, 0) * 20;
        nextSwing = true;
    }

    @Override
    public double calcBaseDamage() {
        int baseDamage = ThreadLocalRandom.current().nextInt(warrior.getMainHand().getMinDmg(), warrior.getMainHand().getMaxDmg() + 1);

        return baseDamage + bonusDamage + warrior.getAp() / 14.0 * warrior.getMainHand().getBaseSpeed();
    }

    @Override
    public void useAbility() {
        super.useAbility();

        if(fight.getSettings().isMultitarget()){
            AbilityResult abilityResult = calculateResult(fight.getExtraTarget(), warrior.getExtraTargetTable().rollTable());

            fight.getResults().addAbilityResult(abilityResult);
            fight.log(getType() + " " + abilityResult.toString());
        }
    }
}
