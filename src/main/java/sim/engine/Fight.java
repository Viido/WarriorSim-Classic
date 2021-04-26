package sim.engine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sim.data.SimDB;
import sim.engine.warrior.Warrior;
import sim.items.Spell;
import sim.settings.Settings;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static sim.data.SimDB.SPELLS;
import static sim.engine.Event.EventType.*;
import sim.engine.Event.EventType;


public class Fight{
    private Logger logger = LogManager.getLogger(Fight.class);

    private Settings settings;
    private Warrior warrior;
    private Target target;
    private FightResult results = new FightResult();
    private Event autoAttackMH = new Event(AUTOATTACK_MH, 0);
    private Event autoAttackOH = new Event(AUTOATTACK_OH, 0);
    private Event heroicStrike = new Event(HEROIC_STRIKE, 0);
    private Event globalCooldown = new Event(GLOBAL_COOLDOWN, 0);
    private Event bloodthirst = new Event(BLOODTHIRST, 0);
    private Event bloodthirstCD = new Event(BLOODTHIRST_CD, 0);
    private Event whirlwind = new Event(WHIRLWIND, 0);
    private Event whirlwindCD = new Event(WHIRLWIND_CD, 0);

    private Map<Integer, Event> spellEvents = new HashMap<>();
    private Map<Integer, Event> spellFadeEvents = new HashMap<>();

    private Queue<Event> eventQueue = new PriorityQueue<>();
    private double currentTime = 0;

    private final int BATCH = 400;

    private int HEROIC_STRIKE_THRESHOLD = 50;

    private double previousSwingMH = 0;
    private double previousSwingOH = 0;

    public Fight(Settings settings) {
        logger.debug("Fight constructor entered.");
        this.settings = settings;

        target = new Target(settings.getTargetLevel(), settings.getTargetArmor(), settings.getTargetResistance());

        warrior = new Warrior(settings.getCharacterSetup(), target, settings.isHeroicStrike9());
        warrior.setRage(settings.getInitialRage());

        logger.debug("New Fight created.");

        initEvents();
    }

    private void initEvents(){
        for(Spell spell : warrior.getMainHandProcs()){
            if(spell.getDuration() > 0){
                spellFadeEvents.put(spell.getId(), new Event(SPELL_FADE, 0, spell));
            }
            spellEvents.put(spell.getId(), new Event(SPELL_PROC, 0, spell));
        }

        if(warrior.isDualWielding()){
            for(Spell spell : warrior.getOffHandProcs()){
                if(spell.getDuration() > 0){
                    spellFadeEvents.put(spell.getId(), new Event(SPELL_FADE, 0, spell));
                }
                spellEvents.put(spell.getId(), new Event(SPELL_PROC, 0, spell));
            }
        }
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

    private void rollMainHandProcs(){
        List<Spell> procs = warrior.getMainHandProcs();

        for(Spell spell : procs){
            double procChance;

            if(spell.getProcChance() != 0){
                procChance = spell.getProcChance();
            }else {
                procChance = spell.getPpm() * warrior.getMainHand().getBaseSpeed() / 60.0;
            }

            double roll = ThreadLocalRandom.current().nextDouble(0, 1);

            if(roll < procChance){
                changeEventTime(spellEvents.get(spell.getId()), currentTime);
            }
        }
    }

    private void rollOffHandProcs(){
        List<Spell> procs = warrior.getOffHandProcs();

        for(Spell spell : procs){
            double procChance;

            if(spell.getProcChance() != 0){
                procChance = spell.getProcChance();
            }else {
                procChance = spell.getPpm() * warrior.getOffHand().getBaseSpeed() / 60.0;
            }

            double roll = ThreadLocalRandom.current().nextDouble(0, 1);

            if(roll < procChance){
                changeEventTime(spellEvents.get(spell.getId()), currentTime);
            }
        }
    }

    private void handleEvent(Event event){
        logger.info("Time: {}, strength: {}, attack power: {}", currentTime, warrior.getStr(), warrior.getAp());
        //logger.info("Time: " + currentTime + " Flurry stacks: " + warrior.getFlurryStacks() + " haste: " + warrior.getHaste() + " rage: " + warrior.getRage());
        if(event.getType() == AUTOATTACK_MH){
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

                results.addAttackResult(AUTOATTACK_MH, roll, damage);
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

            if(damage > 0){
                rollMainHandProcs();
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

            if(damage > 0){
                rollOffHandProcs();
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

            if(damage > 0){
                rollMainHandProcs();
            }
        }

        if(event.getType() == WHIRLWIND){
            AttackTable.RollType roll = warrior.getYellowTable().rollTable();

            double damage = warrior.whirlwind(roll);

            results.addAttackResult(event.getType(), roll, damage);

            logger.info("Whirlwind " + roll + " " + damage + " " + "\n");

            if(warrior.isFlurryTalented()){
                if(roll == AttackTable.RollType.CRIT){
                    warrior.applyFlurry();
                }
            }

            if(damage > 0){
                rollMainHandProcs();
            }
        }

        if(event.getType() == Event.EventType.BLOODTHIRST_CD){
            logger.info("BLOODTHIRST COOLDOWN DONE\n");
        }

        if(event.getType() == Event.EventType.WHIRLWIND_CD){
            logger.info("WHIRLWIND COOLDOWN DONE\n");
        }

        if(event.getType() == Event.EventType.GLOBAL_COOLDOWN){
            logger.info("GLOBAL COOLDOWN DONE\n");
        }

        if(event.getType() == SPELL_PROC){
            Spell spell = event.getSpell();

            switch(spell.getEffect()){
                case STAT_BUFF:
                    Event fadeEvent = spellFadeEvents.get(spell.getId());

                    if(!eventQueue.contains(fadeEvent)){
                        warrior.getStats().addStats(spell.getStats());
                    }

                    changeEventTime(fadeEvent, currentTime + spell.getDuration() * 1000);
            }

            logger.info("{} ({}) procced . Fading at {}\n", event.getSpell().getName(), event.getSpell().getId(), currentTime + spell.getDuration() * 1000);
        }

        if(event.getType() == SPELL_FADE){
            warrior.getStats().removeStats(event.getSpell().getStats());
            logger.info("{} ({}) faded .\n", event.getSpell().getName(), event.getSpell().getId());
        }

        changeEventTime(calculateNextAbility(), currentTime);
    }
}
