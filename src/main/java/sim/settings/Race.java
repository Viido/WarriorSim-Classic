package sim.settings;

import com.google.gson.annotations.SerializedName;
import sim.warrior.Stats;

import java.io.Serializable;

public class Race implements Serializable {
    private int id;
    private String name;
    @SerializedName(value = "stats")
    private Stats baseStats;

    public String getName() {
        return name;
    }

    public Stats getBaseStats() {
        return baseStats;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }
}
