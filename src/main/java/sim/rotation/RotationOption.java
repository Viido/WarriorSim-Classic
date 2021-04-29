package sim.rotation;

import sim.engine.Event;

import java.io.Serializable;

public class RotationOption implements Serializable {
    private String image;
    private String name;
    private int index;
    private Event.EventType event;
    private boolean selected = false;
    private boolean enabled;
    private int rageThreshold;
    private int timeThreshold;
    private ThresholdType type;

    public enum ThresholdType{
        RAGE_ABOVE,
        RAGE_BELOW,
        TIME_REMAINING
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public Event.EventType getEvent() {
        return event;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getIndex() {
        return index;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getRageThreshold() {
        return rageThreshold;
    }

    public void setRageThreshold(int rageThreshold) {
        this.rageThreshold = rageThreshold;
    }

    public int getTimeThreshold() {
        return timeThreshold;
    }

    public void setTimeThreshold(int timeThreshold) {
        this.timeThreshold = timeThreshold;
    }

    public ThresholdType getType() {
        return type;
    }

    public String toString(){
        return name;
    }
}
