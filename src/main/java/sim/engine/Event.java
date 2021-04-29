package sim.engine;

import sim.items.Spell;

public class Event implements Comparable<Event>{
    private EventType type;
    private double time;
    private Spell spell;

    public enum EventType{
        AUTOATTACK_MH,
        AUTOATTACK_OH,
        HEROIC_STRIKE,
        CLEAVE,
        GLOBAL_COOLDOWN,
        BLOODTHIRST,
        BLOODTHIRST_CD,
        WHIRLWIND,
        WHIRLWIND_CD,
        OVERPOWER,
        HAMSTRING,
        DEATH_WISH,
        MIGHTY_RAGE,
        RECKLESSNESS,
        ABILITY,
        ABILITY_CD,
        EXECUTE,
        MORTAL_STRIKE,
        SHIELD_SLAM,
        SPELL_FADE,
        SPELL_PROC
    }

    public Event(EventType type, int time) {
        this.type = type;
        this.time = time;
    }

    public Event(EventType type, int time, Spell spell){
        this.type = type;
        this.time = time;
        this.spell = spell;
    }

    public Event(){
        this.time = 0;
    }

    public void setType(EventType type) {
        this.type = type;
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

    public Spell getSpell() {
        return spell;
    }

    public void setSpell(Spell spell) {
        this.spell = spell;
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
