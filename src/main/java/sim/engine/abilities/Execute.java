package sim.engine.abilities;

import sim.engine.AttackTable;
import sim.engine.Fight;
import sim.rotation.RotationOption;

import static sim.data.Constants.IMP_EXECUTE;

public class Execute extends Ability{
    public Execute(Fight fight, RotationOption rotationOption) {
        super(0, 15, fight, rotationOption);
        int executeTalent = fight.getSettings().getCharacterSetup().getActiveTalents().getOrDefault(IMP_EXECUTE, 0);

        if(executeTalent == 1){
            rageCost -= 2;
        }else if(executeTalent == 2){
            rageCost -= 5;
        }

        setType(EventType.EXECUTE);
    }

    @Override
    public double calcBaseDamage() {
        return 600 + (warrior.getRage() - rageCost) * 15;
    }

    @Override
    public void useAbility() {
        AttackTable.RollType rollType = warrior.getYellowTable().rollTable();

        AbilityResult abilityResult = calculateResult(fight.getTarget(), rollType);

        if(rollType != AttackTable.RollType.MISS && rollType != AttackTable.RollType.DODGE){
            fight.rollMainHandProcs();
        }

        fight.getResults().addAbilityResult(abilityResult);

        fight.log(getType() + " " + abilityResult.toString());
    }

    @Override
    public boolean isUsable() {
        return super.isUsable() && !warrior.isOnGlobalCooldown();
    }
}
