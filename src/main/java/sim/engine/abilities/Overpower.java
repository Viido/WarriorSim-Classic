package sim.engine.abilities;

import sim.engine.Fight;
import sim.rotation.RotationOption;

import java.util.concurrent.ThreadLocalRandom;

public class Overpower extends Ability{
    public Overpower(int cooldown, int rageCost, Fight fight, RotationOption rotationOption) {
        super(cooldown, rageCost, fight, rotationOption);
    }

    @Override
    protected double calcBaseDamage() {
        int baseDamage = ThreadLocalRandom.current().nextInt(warrior.getMainHand().getMinDmg(), warrior.getMainHand().getMaxDmg() + 1) + 45;
        return baseDamage + (warrior.isHeroicStrike9() ? 157 : 138) + warrior.getAp() / 14.0 * warrior.getMainHand().getBaseSpeed();
    }

    @Override
    public boolean isUsable() {
        return false;
    }
}
