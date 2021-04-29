package sim.settings;

import com.google.gson.Gson;
import sim.data.SimDB;
import sim.engine.Event;
import sim.rotation.RotationOption;

import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Settings implements Serializable {
    private int fightDuration = 60;
    private int targetLevel = 63;
    private int targetArmor = 3731;
    private int targetResistance = 0;
    private int initialRage = 80;
    private int iterations = 100000;
    private boolean heroicStrike9 = false;
    private boolean battleShout7 = false;
    private boolean multitarget = false;
    private int extraTargets = 3;
    private int extraTargetLevel = 60;
    private int extraTargetArmor = 3000;
    private CharacterSetup characterSetup = new CharacterSetup();
    private Map<Event.EventType, RotationOption> rotationOptions = new HashMap<>();

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

    public boolean isMultitarget() {
        return multitarget;
    }

    public void setMultitarget(boolean multitarget) {
        this.multitarget = multitarget;
    }

    public int getExtraTargets() {
        return extraTargets;
    }

    public void setExtraTargets(int extraTargets) {
        this.extraTargets = extraTargets;
    }

    public int getExtraTargetLevel() {
        return extraTargetLevel;
    }

    public void setExtraTargetLevel(int extraTargetsLevel) {
        this.extraTargetLevel = extraTargetsLevel;
    }

    public int getExtraTargetArmor() {
        return extraTargetArmor;
    }

    public void setExtraTargetArmor(int extraTargetArmor) {
        this.extraTargetArmor = extraTargetArmor;
    }

    public Map<Event.EventType, RotationOption> getRotationOptions() {
        return rotationOptions;
    }
}
