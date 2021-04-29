package sim.engine;

public class Target {
    private int level;
    private int armor;
    private int resistance;
    private int defense;

    public Target(int level, int armor, int resistance) {
        this.level = level;
        this.armor = Math.max(armor, 0);
        this.resistance = resistance;

        defense = level * 5;
    }

    public Target(int level){
        this.level = level;

    }

    public int getLevel() {
        return level;
    }

    public int getArmor() {
        return armor;
    }

    public int getResistance() {
        return resistance;
    }

    public int getDefense() {
        return defense;
    }
}
