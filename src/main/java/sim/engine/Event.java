package sim.engine;

import java.util.List;

public class Event implements Comparable<Event>{
    private EventType type;
    private double time;

    public enum EventType{
        AUTOATTACK_MH,
        AUTOATTACK_OH,
        HEROIC_STRIKE,
        GLOBAL_COOLDOWN,
        BLOODTHIRST,
        BLOODTHIRST_CD,
        WHIRLWIND,
        WHIRLWIND_CD
    }

    public Event(EventType type, int time) {
        this.type = type;
        this.time = time;
    }

    public EventType getType() {
        return type;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }


    @Override
    public int compareTo(Event o) {
        if(time < o.time){
            return -1;
        }else if(time > o.time){
            return 1;
        }

        return 0;
    }

    @Override
    public String toString() {
        return "Event{" +
                "type='" + type + '\'' +
                ", time=" + time +
                '}';
    }
}
