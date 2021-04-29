package sim.engine.abilities;

import sim.engine.Fight;
import sim.rotation.RotationOption;

public class Bloodthirst extends Ability {
    public Bloodthirst(Fight fight, RotationOption rotationOption) {
        super(6000, 30, fight, rotationOption);
        setType(EventType.BLOODTHIRST);
        getCooldownEvent().setType(EventType.BLOODTHIRST_CD);
    }

    @Override
    public double calcBaseDamage() {
        return warrior.getAp() * 0.45;
    }

    @Override
    public boolean isUsable() {
        return super.isUsable() && !warrior.isOnGlobalCooldown();
    }
}
