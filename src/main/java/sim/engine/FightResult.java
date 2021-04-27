package sim.engine;

import java.util.HashMap;
import java.util.Map;

public class FightResult {
    private Map<Event.EventType, AttackTableResult> attackTableResults = new HashMap<>();

    public void addAttackResult(Event.EventType eventType, AttackTable.RollType rollType, double damage){
        if(attackTableResults.containsKey(eventType)){
            attackTableResults.get(eventType).addAttack(rollType, damage);
        }else{
            AttackTableResult attackTableResult = new AttackTableResult();
            attackTableResult.addAttack(rollType, damage);
            attackTableResults.put(eventType, attackTableResult);
        }
    }

    public void merge(FightResult fightResult){
        for(Event.EventType eventType : fightResult.getAttackTableResults().keySet()){
            if (attackTableResults.containsKey(eventType)) {
                attackTableResults.get(eventType).merge(fightResult.getAttackTableResults().get(eventType));
            }else{
                AttackTableResult attackTableResult = new AttackTableResult();
                attackTableResult.merge(fightResult.getAttackTableResults().get(eventType));
                attackTableResults.put(eventType, attackTableResult);
            }
        }
    }

    public Map<Event.EventType, AttackTableResult> getAttackTableResults() {
        return attackTableResults;
    }

    public void averageResults(int fightCount){
        for(Event.EventType eventType : attackTableResults.keySet()){
            attackTableResults.get(eventType).averageResults(fightCount);
        }
    }

    public double getAverageDamage(){
        double damage = 0;

        for(AttackTableResult attackTableResult : attackTableResults.values()){
            damage += attackTableResult.getDamage();
        }

        return damage;
    }

    @Override
    public String toString() {
        String result = "";

        for(Event.EventType eventType : attackTableResults.keySet()){
            result += eventType + "{\n" + attackTableResults.get(eventType) + "\n";
        }

        result += "}\n";

        return result;
    }
}
