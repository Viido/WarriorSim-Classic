import org.junit.jupiter.api.Test;
import sim.data.SimDB;
import sim.engine.warrior.Warrior;
import sim.engine.Target;
import sim.items.Item;
import sim.settings.CharacterSetup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static sim.data.Constants.MAINHAND;

public class WarriorTest {

    @Test
    public void testGlancingBlow(){
        Item item = SimDB.ITEMS.getItemById(17068);
        CharacterSetup characterSetup = new CharacterSetup();
        characterSetup.setRace(SimDB.RACES[0]);
        characterSetup.equipItem(MAINHAND, item);

        Target target = new Target(63, 3731, 0);
        Warrior warrior = new Warrior(characterSetup, target, true);

        double sum = 0;
        double min = 1;
        double max = 0;

        for(int i = 0; i < 100000; i++){
            double glancing = warrior.testGlancing();

            if(glancing > max){
                max = glancing;
            }

            if(glancing < min){
                min = glancing;
            }

            sum += warrior.testGlancing();
        }


        sum /= 100000;

        assertEquals((0.55 + 0.75)/2, sum, 0.001);
        assertEquals(0.55, min, 0.001);
        assertEquals(0.75, max, 0.001);
    }


}
