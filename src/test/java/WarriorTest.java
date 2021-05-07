import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sim.data.SimDB;
import sim.engine.*;
import sim.engine.abilities.Ability;
import sim.engine.abilities.AbilityResult;
import sim.engine.warrior.Stats;
import sim.engine.warrior.Warrior;
import sim.items.Item;
import sim.items.Spell;
import sim.rotation.RotationOption;
import sim.settings.CharacterSetup;
import sim.settings.Settings;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static sim.data.Constants.*;

public class WarriorTest {
    private static Settings settings;
    private static Warrior warrior;
    private static Fight fight;
    private static Map<Event.EventType, RotationOption> rotationOptions = new HashMap<>();
    private final double dmgReduction = 1 - (3731.0 / (3731 + 400 + 85 * 60));

    @BeforeAll
    static void setup(){
        settings = new Settings();

        settings.getCharacterSetup().equipItem(MAINHAND, SimDB.ITEMS.getItemById(23054)); // Gressil, Dawn of Ruin
        settings.getCharacterSetup().equipItem(OFFHAND, SimDB.ITEMS.getItemById(23014)); // Iblis, Blade of the Fallen Seraph
        settings.getCharacterSetup().equipItem(HEAD, SimDB.ITEMS.getItemById(12640)); // Lionheart Helm
        settings.getCharacterSetup().equipItem(NECK, SimDB.ITEMS.getItemById(18404)); // Onyxia Tooth Pendant
        settings.getCharacterSetup().equipItem(SHOULDERS, SimDB.ITEMS.getItemById(21330)); // Conqueror's Spaulders
        settings.getCharacterSetup().equipItem(CHEST, SimDB.ITEMS.getItemById(23000)); // Plated Abomination Ribcage

        settings.getCharacterSetup().getActiveTalents().put(IMPALE, 2);

        Gson gson = new Gson();

        RotationOption[] temp = gson.fromJson(new InputStreamReader(SimDB.class.getResourceAsStream("/sim/data/rotationOptions.json")), RotationOption[].class);

        for(RotationOption rotationOption : temp){
            rotationOptions.put(rotationOption.getEvent(), rotationOption);
        }

        RotationOption bloodthirst = rotationOptions.get(Event.EventType.BLOODTHIRST);
        bloodthirst.setEnabled(true);
        bloodthirst.setSelected(true);
        settings.getRotationOptions().put(bloodthirst.getEvent(), bloodthirst);

        RotationOption whirlwind = rotationOptions.get(Event.EventType.WHIRLWIND);
        whirlwind.setEnabled(true);
        whirlwind.setSelected(true);
        settings.getRotationOptions().put(whirlwind.getEvent(), whirlwind);

        RotationOption heroicStrike = rotationOptions.get(Event.EventType.HEROIC_STRIKE);
        heroicStrike.setEnabled(true);
        heroicStrike.setSelected(true);
        heroicStrike.setRageThreshold(50);
        settings.getRotationOptions().put(heroicStrike.getEvent(), heroicStrike);

        fight = new Fight(settings);
        warrior = fight.getWarrior();
    }

    @BeforeEach
    void reset(){
        fight.reset();

        testBaseStats();
    }

    @Test
    void testBaseStats(){
        assertEquals(6, warrior.getHit());
        assertEquals(10.4, warrior.getCritMH());
        assertEquals(10.4, warrior.getCritOH());
        assertEquals(632, warrior.getAp());
        assertEquals(203, warrior.getStr());
        assertEquals(108, warrior.getAgi());
        assertEquals(305, warrior.getWeaponSkillMH());
        assertEquals(305, warrior.getWeaponSkillOH());
    }

    @Test
    void testBloodthirst(){
        assertEquals(632 * 0.45, fight.getBloodthirst().calcBaseDamage());

        AbilityResult critResult = fight.getBloodthirst().calculateResult(fight.getTarget(), AttackTable.RollType.CRIT);

        assertEquals(632 * 0.45 * 2.2 * dmgReduction, critResult.getDamage(), 0.0001);

        AbilityResult hitResult = fight.getBloodthirst().calculateResult(fight.getTarget(), AttackTable.RollType.HIT);

        assertEquals(632 * 0.45 * dmgReduction, hitResult.getDamage(), 0.0001);
    }

    @Test
    void testWhirlwind(){
        double minDamage = warrior.getMainHand().getMinDmg() + 632.0 / 14 * 2.4;
        double maxDamage = warrior.getMainHand().getMaxDmg() + 632.0 / 14 * 2.4;

        for(int i = 0; i < 100000; i++){
            AbilityResult hitResult = fight.getWhirlwind().calculateResult(fight.getTarget(), AttackTable.RollType.HIT);

            assertTrue(hitResult.getDamage() >= minDamage * dmgReduction);
            assertTrue(hitResult.getDamage() <= maxDamage * dmgReduction);
        }
    }

    @Test
    void testHeroicStrike(){
        double minDamage = warrior.getMainHand().getMinDmg() + 632.0 / 14 * 2.7 + 138;
        double maxDamage = warrior.getMainHand().getMaxDmg() + 632.0 / 14 * 2.7 + 138;


        for(int i = 0; i < 100000; i++){
            AbilityResult hitResult = fight.getHeroicStrike().calculateResult(fight.getTarget(), AttackTable.RollType.HIT);

            assertTrue(hitResult.getDamage() >= minDamage * dmgReduction);
            assertTrue(hitResult.getDamage() <= maxDamage * dmgReduction);
        }
    }

    @Test
    void testWhiteMHTable(){
        AttackTableResult attackTableResult = new AttackTableResult();

        for(int i = 0; i < 10000000; i++){
            attackTableResult.addAttack(warrior.getMainHandTable().rollTable(), 0);
        }

        attackTableResult.averageResults(10000000);

        assertEquals(0.19, attackTableResult.getRollTable().get(AttackTable.RollType.MISS), 0.0005);
        assertEquals(0.4, attackTableResult.getRollTable().get(AttackTable.RollType.GLANCING), 0.0005);
        assertEquals(0.056, attackTableResult.getRollTable().get(AttackTable.RollType.CRIT), 0.0005);
        assertEquals(0.06, attackTableResult.getRollTable().get(AttackTable.RollType.DODGE), 0.0005);
        assertEquals(0.294, attackTableResult.getRollTable().get(AttackTable.RollType.HIT), 0.0005);
    }

    @Test
    void testYellowTable(){
        AttackTableResult attackTableResult = new AttackTableResult();

        for(int i = 0; i < 10000000; i++){
            attackTableResult.addAttack(warrior.getYellowTable().rollTable(), 0);
        }

        attackTableResult.averageResults(10000000);

        assertNull(attackTableResult.getRollTable().get(AttackTable.RollType.MISS));
        assertNull(attackTableResult.getRollTable().get(AttackTable.RollType.GLANCING));
        assertEquals(0.056, attackTableResult.getRollTable().get(AttackTable.RollType.CRIT), 0.0005);
        assertEquals(0.06, attackTableResult.getRollTable().get(AttackTable.RollType.DODGE), 0.0005);
        assertEquals(0.884, attackTableResult.getRollTable().get(AttackTable.RollType.HIT), 0.0005);
    }

    @Test
    void testWhiteOHTable(){
        AttackTableResult attackTableResult = new AttackTableResult();

        for(int i = 0; i < 10000000; i++){
            attackTableResult.addAttack(warrior.getOffHandTable().rollTable(), 0);
        }

        attackTableResult.averageResults(10000000);

        assertEquals(0.19, attackTableResult.getRollTable().get(AttackTable.RollType.MISS), 0.0005);
        assertEquals(0.4, attackTableResult.getRollTable().get(AttackTable.RollType.GLANCING), 0.0005);
        assertEquals(0.056, attackTableResult.getRollTable().get(AttackTable.RollType.CRIT), 0.0005);
        assertEquals(0.06, attackTableResult.getRollTable().get(AttackTable.RollType.DODGE), 0.0005);
        assertEquals(0.294, attackTableResult.getRollTable().get(AttackTable.RollType.HIT), 0.0005);
    }

    @Test
    void testGlancingBlow(){
        double sum = 0;
        double min = 1;
        double max = 0;

        for(int i = 0; i < 100000; i++){
            double glancing = warrior.glancingBlow(warrior.getMainHand());

            if(glancing > max){
                max = glancing;
            }

            if(glancing < min){
                min = glancing;
            }

            sum += warrior.glancingBlow(warrior.getMainHand());
        }

        sum /= 100000;

        assertEquals(0.85, sum, 0.001);
        assertEquals(0.8, min, 0.001);
        assertEquals(0.9, max, 0.001);
    }
}
