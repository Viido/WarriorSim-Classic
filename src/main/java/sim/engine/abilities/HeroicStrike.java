package sim.engine.abilities;

import sim.engine.Fight;
import sim.rotation.RotationOption;

import java.util.concurrent.ThreadLocalRandom;

import static sim.data.Constants.IMP_HEROIC_STRIKE;

public class HeroicStrike extends Ability {
    private double bonusDamage;

    public HeroicStrike(Fight fight, RotationOption rotationOption) {
        super(0, 15 - fight.getSettings().getCharacterSetup().getActiveTalents().getOrDefault(IMP_HEROIC_STRIKE, 0), fight, rotationOption);
        bonusDamage = warrior.isHeroicStrike9() ? 157 : 138;
        nextSwing = true;
        setType(EventType.HEROIC_STRIKE);
    }

    @Override
    public double calcBaseDamage() {
        int baseDamage = ThreadLocalRandom.current().nextInt(warrior.getMainHand().getMinDmg(), warrior.getMainHand().getMaxDmg() + 1);

        return baseDamage + bonusDamage + warrior.getAp() / 14.0 * warrior.getMainHand().getBaseSpeed();
    }
}
