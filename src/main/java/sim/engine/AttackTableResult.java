package sim.engine;

import java.util.HashMap;
import java.util.Map;

public class AttackTableResult {
    Map<AttackTable.RollType, Double> rollTable = new HashMap<>();
    double damage = 0;
    double swingCount = 0;

    public void addAttack(AttackTable.RollType roll, double damage){
        double count = rollTable.getOrDefault(roll, 0.0);
        rollTable.put(roll, count + 1);

        this.damage += damage;
        swingCount++;
    }

    public void merge(AttackTableResult attackTableResult){
        attackTableResult.getRollTable().forEach((key, value) -> rollTable.merge(key, value, Double::sum));

        damage += attackTableResult.getDamage();
        swingCount += attackTableResult.getSwingCount();
    }

    public void averageResults(int fightCount){
        rollTable.replaceAll((k, v) -> v / fightCount);

        damage /= fightCount;
        swingCount /= fightCount;
    }

    public Map<AttackTable.RollType, Double> getRollTable() {
        return rollTable;
    }

    public double getDamage() {
        return damage;
    }

    public double getSwingCount() {
        return swingCount;
    }

    @Override
    public String toString() {
        String result = "";

        for(AttackTable.RollType rollType : rollTable.keySet()){
            result += rollType + ": " + rollTable.get(rollType) + " (" + rollTable.get(rollType)/swingCount*100 + "%)\n";
        }

        result += "Swings: " + swingCount + " Damage: " + damage;

        return result;
    }
}
