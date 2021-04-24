package sim.settings;

import java.io.Serializable;

public class Settings implements Serializable {
    private int fightDuration = 60;
    private int targetLevel = 63;
    private int targetArmor = 3731;
    private int targetResistance = 0;
    private int initialRage = 80;
    private int iterations = 100000;
    private boolean heroicStrike9 = false;
    private boolean battleShout7 = false;
    private CharacterSetup characterSetup = new CharacterSetup();

    public int getFightDuration() {
        return fightDuration;
    }

    public void setFightDuration(int fightDuration) {
        this.fightDuration = fightDuration;
    }

    public int getTargetLevel() {
        return targetLevel;
    }

    public void setTargetLevel(int targetLevel) {
        this.targetLevel = targetLevel;
    }

    public int getTargetArmor() {
        return targetArmor;
    }

    public void setTargetArmor(int targetArmor) {
        this.targetArmor = targetArmor;
    }

    public int getTargetResistance() {
        return targetResistance;
    }

    public void setTargetResistance(int targetResistance) {
        this.targetResistance = targetResistance;
    }

    public int getInitialRage() {
        return initialRage;
    }

    public void setInitialRage(int initialRage) {
        this.initialRage = initialRage;
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public boolean isHeroicStrike9() {
        return heroicStrike9;
    }

    public void setHeroicStrike9(boolean heroicStrike9) {
        this.heroicStrike9 = heroicStrike9;
    }

    public boolean isBattleShout7() {
        return battleShout7;
    }

    public void setBattleShout7(boolean battleShout7) {
        this.battleShout7 = battleShout7;
    }

    public CharacterSetup getCharacterSetup() {
        return characterSetup;
    }

    public void setCharacterSetup(CharacterSetup characterSetup) {
        this.characterSetup = characterSetup;
    }
}
