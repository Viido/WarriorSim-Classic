package sim.engine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sim.engine.abilities.*;
import sim.engine.warrior.Warrior;
import sim.items.Spell;
import sim.settings.Settings;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static sim.Main.loggingEnabled;
import static sim.engine.Event.EventType.*;


public class Fight{
    private static Logger logger;

    private Settings settings;
    private Warrior warrior;
    private Target target;
    private Target extraTarget;
    private FightResult results = new FightResult();

    private Event globalCooldown = new Event(GLOBAL_COOLDOWN, 0);

    private Ability bloodthirst;
    private Ability whirlwind;
    private Ability cleave;
    private Ability heroicStrike;
    private Ability autoAttackMH;
    private Ability autoAttackOH;

    private Map<Integer, Event> spellEvents = new HashMap<>();
    private Map<Integer, Event> spellFadeEvents = new HashMap<>();

    private Queue<Event> eventQueue = new PriorityQueue<>();
    private double currentTime = 0;

    DecimalFormat df = new DecimalFormat("0.00");

    public Fight(Settings settings) {
        if(loggingEnabled) logger = LogManager.getLogger(Fight.class);

        this.settings = settings;

        target = new Target(settings.getTargetLevel(), settings.getTargetArmor() - settings.getCharacterSetup().getArmorReduction(), settings.getTargetResistance());

        if(settings.isMultitarget()){
            extraTarget = new Target(settings.getExtraTargetLevel(), settings.getExtraTargetArmor() - settings.getCharacterSetup().getArmorReduction(), 0);
        }

        warrior = new Warrior(settings, target, extraTarget);

        bloodthirst = new Bloodthirst(this, settings.getRotationOptions().get(BLOODTHIRST));
        whirlwind = new Whirlwind(this, settings.getRotationOptions().get(WHIRLWIND));
        cleave = new Cleave(this, settings.getRotationOptions().get(CLEAVE));
        heroicStrike = new HeroicStrike(this, settings.getRotationOptions().get(HEROIC_STRIKE));
        autoAttackMH = new AutoAttackMH(this);

        if(warrior.isDualWielding()){
            autoAttackOH = new AutoAttackOH(this);
        }


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

        initEvents();
    }

    public void reset(){
        warrior.reset();
        results = new FightResult();
        eventQueue = new PriorityQueue<>();
        currentTime = 0;
        autoAttackMH.setTime(0);

        if(warrior.isDualWielding()){
            autoAttackOH.setTime(0);
        }

        bloodthirst.finishCooldown();
        whirlwind.finishCooldown();
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
        if(bloodthirst.isUsable()){
            queueAbility(bloodthirst);
            return bloodthirst;
        }

        if(whirlwind.isUsable()){
            queueAbility(whirlwind);
            return whirlwind;
        }

        return null;
    }

    private void queueAbility(Ability ability){
        if(!eventQueue.contains(ability)){
            changeEventTime(globalCooldown, currentTime + 1500);
            changeEventTime(ability.getCooldownEvent(), currentTime + ability.getCooldown());
        }
    }

    public void rollMainHandProcs(){
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

    public void rollOffHandProcs(){
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
        log("Flurry stacks: " + warrior.getFlurryStacks() + " haste: " + warrior.getHaste() + " rage: " + warrior.getRage() + " HS queue: " + warrior.isHeroicStrikeQueued());

        if(event.getType() == AUTOATTACK_MH){
            if(warrior.isHeroicStrikeQueued()){
                if(settings.isMultitarget()){
                    cleave.useAbility();
                }else{
                    heroicStrike.useAbility();
                }
            }else{
                autoAttackMH.useAbility();
            }

            if(settings.isMultitarget()){
                warrior.setHeroicStrikeQueued(cleave.isUsable());
            }else{
                warrior.setHeroicStrikeQueued(heroicStrike.isUsable());
            }

            changeEventTime(autoAttackMH, currentTime + warrior.getMainHand().getCurrentSpeed() * 1000);
        }

        if(event.getType() == Event.EventType.AUTOATTACK_OH){
            autoAttackOH.useAbility();

            if(settings.isMultitarget()){
                warrior.setHeroicStrikeQueued(cleave.isUsable());
            }else{
                warrior.setHeroicStrikeQueued(heroicStrike.isUsable());
            }

            changeEventTime(autoAttackOH, currentTime + warrior.getOffHand().getCurrentSpeed() * 1000);
        }

        if(event.getType() == Event.EventType.BLOODTHIRST){
            bloodthirst.useAbility();
        }

        if(event.getType() == WHIRLWIND){
            whirlwind.useAbility();
        }

        if(event.getType() == Event.EventType.BLOODTHIRST_CD){
            bloodthirst.finishCooldown();
            log("BLOODTHIRST COOLDOWN DONE");
        }

        if(event.getType() == Event.EventType.WHIRLWIND_CD){
            whirlwind.finishCooldown();
            log("WHIRLWIND COOLDOWN DONE");
        }

        if(event.getType() == Event.EventType.GLOBAL_COOLDOWN){
            warrior.finishGlobalCooldown();
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

    public void log(String message){
        if(loggingEnabled) logger.info(df.format(currentTime) + " " + message);
    }

    public void logError(String message){
        if(loggingEnabled) logger.error(df.format(currentTime) + " " + message);
    }

    public Warrior getWarrior() {
        return warrior;
    }

    public double getCurrentTime() {
        return currentTime;
    }

    public FightResult getResults() {
        return results;
    }

    public Target getTarget() {
        return target;
    }

    public Target getExtraTarget() {
        return extraTarget;
    }

    public Settings getSettings() {
        return settings;
    }
}
