package sim.engine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sim.engine.warrior.Warrior;
import sim.settings.Settings;

import java.io.IOException;
import java.util.*;

public class Fight{
    private Logger logger = LogManager.getLogger(Fight.class);

    private Settings settings;
    private Warrior warrior;
    private Target target;
    private FightResult results = new FightResult();
    private Event autoAttackMH = new Event(Event.EventType.AUTOATTACK_MH, 0);
    private Event autoAttackOH = new Event(Event.EventType.AUTOATTACK_OH, 0);
    private Event heroicStrike = new Event(Event.EventType.HEROIC_STRIKE, 0);
    private Event globalCooldown = new Event(Event.EventType.GLOBAL_COOLDOWN, 0);
    private Event bloodthirst = new Event(Event.EventType.BLOODTHIRST, 0);
    private Event bloodthirstCD = new Event(Event.EventType.BLOODTHIRST_CD, 0);
    private Event whirlwind = new Event(Event.EventType.WHIRLWIND, 0);
    private Event whirlwindCD = new Event(Event.EventType.WHIRLWIND_CD, 0);

    private Queue<Event> eventQueue = new PriorityQueue<>();
    private double currentTime = 0;

    private final int BATCH = 400;

    private int HEROIC_STRIKE_THRESHOLD = 50;

    private double previousSwingMH = 0;
    private double previousSwingOH = 0;

    public Fight(Settings settings) {
        this.settings = settings;

        target = new Target(settings.getTargetLevel(), settings.getTargetArmor(), settings.getTargetResistance());

        warrior = new Warrior(settings.getCharacterSetup(), target, settings.isHeroicStrike9());
        warrior.setRage(settings.getInitialRage());

    }

    public FightResult run() {
        eventQueue.add(autoAttackMH);

        if(warrior.isDualWielding()){
            eventQueue.add(autoAttackOH);
        }

        changeEventTime(calculateNextAbility(), currentTime);

        // recalculate swing timers in progress when player's haste changes
        warrior.hasteProperty().addListener((obs, oldValue, newValue) -> {
            if(!oldValue.equals(newValue)){
                if(autoAttackMH.getTime() != currentTime){
                    double remainingTime = autoAttackMH.getTime() - currentTime;
                    logger.info("MH remaining: " + remainingTime + " oldValue: "+ oldValue + " newValue: " + newValue + "getTime(): " + autoAttackMH.getTime() + " currentTime: " + currentTime);
                    remainingTime = remainingTime * oldValue.doubleValue() / newValue.doubleValue();
                    logger.info(remainingTime + "");

                    changeEventTime(autoAttackMH, currentTime + remainingTime);
                }

                if(warrior.isDualWielding()){
                    if(autoAttackOH.getTime() != currentTime){
                        double remainingTime = autoAttackOH.getTime() - currentTime;
                        logger.info("OH remaining:" + remainingTime + " oldValue: "+ oldValue + " newValue: " + newValue + "getTime(): " + autoAttackOH.getTime() + " currentTime: " + currentTime);
                        remainingTime = remainingTime * oldValue.doubleValue() / newValue.doubleValue();
                        logger.info(remainingTime + "");

                        changeEventTime(autoAttackOH, currentTime + remainingTime);
                    }
                }
            }
        });

        while(true){
            Event event = eventQueue.remove();
            currentTime = event.getTime();

            if(currentTime <= settings.getFightDuration() * 1000){
                handleEvent(event);
            }else{
                break;
            }
        }

        return results;
    }

    private void changeEventTime(Event event, double time){
        if(event != null){
            eventQueue.remove(event);
            event.setTime(time);
            eventQueue.add(event);
        }
    }

    private Event calculateNextAbility(){
        if(warrior.getRage() >= 30 && !eventQueue.contains(bloodthirstCD) && !eventQueue.contains(globalCooldown)){
            if(!eventQueue.contains(bloodthirst)){
                changeEventTime(globalCooldown, currentTime + 1500);
                changeEventTime(bloodthirstCD, currentTime + 6000);
                return bloodthirst;
            }
        }
        if((warrior.getRage() >= 25) && !eventQueue.contains(whirlwindCD) && !eventQueue.contains(globalCooldown)){
            if(!eventQueue.contains(whirlwind)){
                changeEventTime(globalCooldown, currentTime + 1500);
                changeEventTime(whirlwindCD, currentTime + 10000);
                return whirlwind;
            }
        }

        return null;
    }



    private void handleEvent(Event event){
        logger.info("Time: " + currentTime + " Flurry stacks: " + warrior.getFlurryStacks() + " haste: " + warrior.getHaste() + " rage: " + warrior.getRage());
        if(event.getType() == Event.EventType.AUTOATTACK_MH){
            AttackTable.RollType roll;
            double damage;

            if(warrior.isHeroicStrikeQueued()){
                roll = warrior.getYellowTable().rollTable();
                damage = warrior.heroicStrike(roll);

                results.addAttackResult(Event.EventType.HEROIC_STRIKE, roll, damage);

                logger.info("Heroic Strike " + roll + " " + damage + " " + (currentTime - previousSwingMH) + "\n");
            }else{
                roll = warrior.getMainHandTable().rollTable();
                damage = warrior.autoAttackMH(roll);

                results.addAttackResult(Event.EventType.AUTOATTACK_MH, roll, damage);
                logger.info("Auto-attack MH " + roll + " " + damage + " " + (currentTime - previousSwingMH) + "\n");
            }

            previousSwingMH = currentTime;

            if(warrior.isFlurryTalented()){
                if(roll == AttackTable.RollType.CRIT){
                    warrior.applyFlurry();
                }else{
                    warrior.decrementFlurry();
                }
            }

            warrior.setHeroicStrikeQueued(warrior.getRage() >= HEROIC_STRIKE_THRESHOLD);

            changeEventTime(autoAttackMH, currentTime + warrior.getMainHand().getCurrentSpeed() * 1000);
        }

        if(event.getType() == Event.EventType.AUTOATTACK_OH){
            warrior.getOffHandTable().setYellowTable(warrior.isHeroicStrikeQueued());

            AttackTable.RollType roll = warrior.getOffHandTable().rollTable();

            if(roll == AttackTable.RollType.GLANCING && warrior.isHeroicStrikeQueued()){
                logger.error("Glancing while HS queued");
            }

            double damage = warrior.autoAttackOH(roll);

            results.addAttackResult(event.getType(), roll, damage);

            logger.info("Auto-attack OH " + roll + " " + damage + " " + (currentTime - previousSwingOH) + "\n");

            previousSwingOH = currentTime;

            if(warrior.isFlurryTalented()){
                if(roll == AttackTable.RollType.CRIT){
                    warrior.applyFlurry();
                }else{
                    warrior.decrementFlurry();
                }
            }

            warrior.setHeroicStrikeQueued(warrior.getRage() >= HEROIC_STRIKE_THRESHOLD);

            changeEventTime(autoAttackOH, currentTime + warrior.getOffHand().getCurrentSpeed() * 1000);
        }

        if(event.getType() == Event.EventType.BLOODTHIRST){
            AttackTable.RollType roll = warrior.getYellowTable().rollTable();

            double damage = warrior.bloodthirst(roll);

            results.addAttackResult(event.getType(), roll, damage);

            logger.info("Bloodthirst " + roll + " " + damage + " " + "\n");

            if(warrior.isFlurryTalented()){
                if(roll == AttackTable.RollType.CRIT){
                    warrior.applyFlurry();
                }
            }
        }

        if(event.getType() == Event.EventType.WHIRLWIND){
            AttackTable.RollType roll = warrior.getYellowTable().rollTable();

            double damage = warrior.whirlwind(roll);

            results.addAttackResult(event.getType(), roll, damage);

            logger.info("Whirlwind " + roll + " " + damage + " " + "\n");

            if(warrior.isFlurryTalented()){
                if(roll == AttackTable.RollType.CRIT){
                    warrior.applyFlurry();
                }
            }

        }

        if(event.getType() == Event.EventType.BLOODTHIRST_CD){
            logger.info("BLOODTHIRST COOLDOWN DONE");
        }

        if(event.getType() == Event.EventType.WHIRLWIND_CD){
            logger.info("WHIRLWIND COOLDOWN DONE");
        }

        if(event.getType() == Event.EventType.GLOBAL_COOLDOWN){
            logger.info("GLOBAL COOLDOWN DONE");
        }

        changeEventTime(calculateNextAbility(), currentTime);
    }
}
