package sim.engine.abilities;

import sim.engine.AttackTable;
import sim.engine.Event;

public class AbilityResult {
    private AttackTable.RollType rollType;
    private double damage;
    private Event.EventType eventType;

    public AbilityResult(Event.EventType eventType, AttackTable.RollType rollType, double damage){
        this.eventType = eventType;
        this.rollType = rollType;
        this.damage = damage;
    }

    public AttackTable.RollType getRollType() {
        return rollType;
    }

    public void setRollType(AttackTable.RollType rollType) {
        this.rollType = rollType;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public Event.EventType getEventType() {
        return eventType;
    }

    public void setEventType(Event.EventType eventType) {
        this.eventType = eventType;
    }

    @Override
    public String toString() {
        return "AbilityResult{" +
                "rollType=" + rollType +
                ", damage=" + damage +
                '}';
    }
}
