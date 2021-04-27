package sim.engine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sim.engine.warrior.Warrior;
import sim.items.Spell;
import sim.settings.Settings;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static sim.Main.loggingEnabled;
import static sim.data.Constants.IMP_HEROIC_STRIKE;
import static sim.engine.Event.EventType.*;


public class Fight{
    private Logger logger;

    private Settings settings;
    private Warrior warrior;
    private Target target;
    private Target extraTarget;
    private FightResult results = new FightResult();
    private Event autoAttackMH = new Event(AUTOATTACK_MH, 0);
    private Event autoAttackOH = new Event(AUTOATTACK_OH, 0);
    private Event globalCooldown = new Event(GLOBAL_COOLDOWN, 0);
    private Event bloodthirst = new Event(BLOODTHIRST, 0);
    private Event bloodthirstCD = new Event(BLOODTHIRST_CD, 0);
    private Event whirlwind = new Event(WHIRLWIND, 0);
    private Event whirlwindCD = new Event(WHIRLWIND_CD, 0);
    private Event execute = new Event(EXECUTE, 0);

    private Map<Integer, Event> spellEvents = new HashMap<>();
    private Map<Integer, Event> spellFadeEvents = new HashMap<>();

    private Queue<Event> eventQueue = new PriorityQueue<>();
    private double currentTime = 0;

    private int HEROIC_STRIKE_THRESHOLD = 50;

    private double previousSwingMH = 0;
    private double previousSwingOH = 0;

    public Fight(Settings settings) {
        if(loggingEnabled) logger = LogManager.getLogger(Fight.class);

        this.settings = settings;

        target = new Target(settings.getTargetLevel(), settings.getTargetArmor(), settings.getTargetResistance());

        if(settings.isMultitarget()){
            extraTarget = new Target(settings.getExtraTargetLevel(), settings.getExtraTargetArmor(), 0);
        }


        warrior = new Warrior(settings, target, extraTarget);

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
                    remainingTime = remainingTime * oldValue.doubleValue() / newValue.doubleValue();

                    changeEventTime(autoAttackMH, currentTime + remainingTime);
                }

                if(warrior.isDualWielding()){
                    if(autoAttackOH.getTime() != currentTime){
                        double remainingTime = autoAttackOH.getTime() - currentTime;
                        remainingTime = remainingTime * oldValue.doubleValue() / newValue.doubleValue();

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

    public double applyArmorMitigation(double damage, Target target) {
        double armorMitigation = target.getArmor() / (target.getArmor() + 400 + 85.0 * warrior.getLevel());

        return damage * (1 - armorMitigation);
    }

    private void handleEvent(Event event){
        log("Strength: " + warrior.getStr() + " rage: " + warrior.getRage() + " HS queue: " + warrior.isHeroicStrikeQueued());

        if(event.getType() == AUTOATTACK_MH){
            AttackTable.RollType roll;
            double damage;

            if(warrior.isHeroicStrikeQueued()){
                if(settings.isMultitarget()){
                    roll = warrior.getYellowTable().rollTable();
                    damage = warrior.cleave(roll);

                    damage = applyArmorMitigation(damage, target);

                    results.addAttackResult(Event.EventType.CLEAVE, roll, damage);

                    log("Cleave " + roll + " " + damage + " " + (currentTime - previousSwingMH));

                    roll = warrior.getExtraTargetTable().rollTable();
                    damage = warrior.cleave(roll);

                    damage = applyArmorMitigation(damage, extraTarget);

                    results.addAttackResult(Event.EventType.CLEAVE, roll, damage);

                    warrior.removeRage(20);

                    log("Cleave offtarget " + roll + " " + damage + " " + (currentTime - previousSwingMH));
                }else{
                    roll = warrior.getYellowTable().rollTable();
                    damage = warrior.heroicStrike(roll);
                    damage = applyArmorMitigation(damage, target);

                    results.addAttackResult(Event.EventType.HEROIC_STRIKE, roll, damage);

                    warrior.removeRage(15 - settings.getCharacterSetup().getActiveTalents().getOrDefault(IMP_HEROIC_STRIKE, 0));

                    log("Heroic Strike " + roll + " " + damage + " " + (currentTime - previousSwingMH));
                }
            }else{
                roll = warrior.getMainHandTable().rollTable();
                damage = warrior.autoAttackMH(roll);
                damage = applyArmorMitigation(damage, target);
                warrior.generateRage(damage);

                results.addAttackResult(AUTOATTACK_MH, roll, damage);
                log("Auto-attack MH " + roll + " " + damage + " " + (currentTime - previousSwingMH));
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

            if(roll == AttackTable.RollType.MISS && warrior.isHeroicStrikeQueued()){
                logError("Attack missed while HS queued");
            }

            double damage = warrior.autoAttackOH(roll);
            damage = applyArmorMitigation(damage, target);
            warrior.generateRage(damage);

            results.addAttackResult(event.getType(), roll, damage);

            log("Auto-attack OH " + roll + " " + damage + " " + (currentTime - previousSwingOH));

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
            damage = applyArmorMitigation(damage, target);
            warrior.removeRage(30);

            results.addAttackResult(event.getType(), roll, damage);

            log("Bloodthirst " + roll + " " + damage);

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
            damage = applyArmorMitigation(damage, target);

            results.addAttackResult(event.getType(), roll, damage);

            log("Whirlwind " + roll + " " + damage);

            if(warrior.isFlurryTalented()){
                if(roll == AttackTable.RollType.CRIT){
                    warrior.applyFlurry();
                }
            }

            if(damage > 0){
                rollMainHandProcs();
            }

            if(settings.isMultitarget()){
                for(int i = 0; i < settings.getExtraTargets(); i++){
                    roll = warrior.getExtraTargetTable().rollTable();

                    damage = warrior.whirlwind(roll);

                    damage = applyArmorMitigation(damage, extraTarget);

                    results.addAttackResult(event.getType(), roll, damage);

                    log("Whirlwind offtarget " + roll + " " + damage);

                    if(warrior.isFlurryTalented()){
                        if(roll == AttackTable.RollType.CRIT){
                            warrior.applyFlurry();
                        }
                    }
                }
            }

            warrior.removeRage(25);
        }

        if(event.getType() == Event.EventType.BLOODTHIRST_CD){
            log("BLOODTHIRST COOLDOWN DONE");
        }

        if(event.getType() == Event.EventType.WHIRLWIND_CD){
            log("WHIRLWIND COOLDOWN DONE");
        }

        if(event.getType() == Event.EventType.GLOBAL_COOLDOWN){
            log("GLOBAL COOLDOWN DONE");
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

            log(event.getSpell().getName() + " " + event.getSpell().getId() + " procced. Fading at " + currentTime + spell.getDuration() * 1000);
        }

        if(event.getType() == SPELL_FADE){
            warrior.getStats().removeStats(event.getSpell().getStats());
            log(event.getSpell().getName() + " " + event.getSpell().getId() + " faded");
        }

        changeEventTime(calculateNextAbility(), currentTime);
    }

    private void log(String message){
        DecimalFormat df = new DecimalFormat("0.00");
        if(loggingEnabled) logger.info(df.format(currentTime) + " " + message);
    }

    private void logError(String message){
        DecimalFormat df = new DecimalFormat("0.00");
        if(loggingEnabled) logger.error(df.format(currentTime) + " " + message);
    }
}
