package sim.items;

public class Spell {
    private int id;
    private String name;
    private String type;
    private String effect;
    private String school;
    private int value;
    private int stacks;
    private int minDamage;
    private int maxDamage;
    private int cooldown;
    private int duration;
    private double ppm;
    private double procChance;
    private boolean gcd;
    private boolean aoe;
    private String description;
    private String icon;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getEffect() {
        return effect;
    }

    public String getSchool() {
        return school;
    }

    public int getValue() {
        return value;
    }

    public int getStacks() {
        return stacks;
    }

    public int getMinDamage() {
        return minDamage;
    }

    public int getMaxDamage() {
        return maxDamage;
    }

    public int getCooldown() {
        return cooldown;
    }

    public int getDuration() {
        return duration;
    }

    public double getPpm() {
        return ppm;
    }

    public double getProcChance() {
        return procChance;
    }

    public boolean isGcd() {
        return gcd;
    }

    public boolean isAoe() {
        return aoe;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }
}
