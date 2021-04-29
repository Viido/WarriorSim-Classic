package sim.engine.warrior;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sim.data.SimDB;
import sim.engine.AttackTable;
import sim.engine.Target;
import sim.items.Enchant;
import sim.items.Item;
import sim.items.Spell;
import sim.settings.CharacterSetup;
import sim.settings.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static sim.Main.loggingEnabled;
import static sim.data.Constants.*;


public class Warrior {
    private int rage;
    private int level = 60;
    private CharacterSetup characterSetup;
    private Settings settings;
    private Target target;
    private Weapon mainHand;
    private Weapon offHand;
    private AttackTable mainHandTable;
    private AttackTable offHandTable;
    private AttackTable yellowTable;
    private AttackTable extraTargetTable;
    private boolean dualWielding = false;
    private double rageConversionFactor;
    private boolean isHeroicStrikeQueued = false;
    private boolean heroicStrike9;

    private Stats stats;

    private double damageMod = 1.0;
    private int flurryStacks = 0;
    private double flurryHaste = 1.0;

    private DoubleProperty haste = new SimpleDoubleProperty(1.0);

    private boolean flurryTalented = false;

    private boolean onGlobalCooldown = false;

    private List<Spell> mainHandProcs;
    private List<Spell> offHandProcs;

    private Logger logger;

    public Warrior(Settings settings, Target target, Target extraTarget) {
        if(loggingEnabled) logger = LogManager.getLogger(Warrior.class);
        if(loggingEnabled) logger.debug("Warrior constructor entered.");
        if(loggingEnabled) logger.debug("Warrior level: {}", level);

        this.characterSetup = settings.getCharacterSetup();
        this.settings = settings;
        stats = new Stats(characterSetup.getWarrior().getStats());
        this.target = target;
        this.heroicStrike9 = settings.isHeroicStrike9();
        this.rage = settings.getInitialRage();

        if(characterSetup.getEquippedItems()[MAINHAND] != null){
            mainHand = new Weapon(characterSetup.getEquippedItems()[MAINHAND], this);
            mainHand.setSkill(getWeaponSkillMH());
        }

        if(characterSetup.getEquippedItems()[OFFHAND] != null){
            offHand = new Weapon(characterSetup.getEquippedItems()[OFFHAND], this);

            if(!offHand.getType().equals("shield")){
                offHand.setSkill(getWeaponSkillOH());
                dualWielding = true;
            }
        }

        mainHandTable = new AttackTable(mainHand.getSkill(), target.getLevel(), level, getHit(), getCritMH(), getAgi(), dualWielding, false);

        if(dualWielding){
            offHandTable = new AttackTable(offHand.getSkill(), target.getLevel(), level, getHit(), getCritOH(), getAgi(), dualWielding, false);
        }

        yellowTable = new AttackTable(mainHand.getSkill(), target.getLevel(), level, getHit(), getCritMH(), getAgi(), dualWielding, true);

        if(settings.isMultitarget()){
            extraTargetTable = new AttackTable(mainHand.getSkill(), extraTarget.getLevel(), level, getHit(), getCritMH(), getAgi(), dualWielding, true);
        }

        rageConversionFactor = 0.0091107836 * Math.pow(level, 2) + 3.225598133 * level + 4.2652911;

        if(characterSetup.getActiveTalents().getOrDefault(FLURRY, 0) > 0){
            flurryTalented = true;
            flurryHaste = 1.1 + (characterSetup.getActiveTalents().get(FLURRY) - 1) * 0.05;
        }

        haste.set(stats.getHaste());

        initProcs();
    }

    public void reset(){
        finishGlobalCooldown();
        stats = new Stats(characterSetup.getWarrior().getStats());
        this.rage = settings.getInitialRage();
        haste.set(stats.getHaste());
        flurryStacks = 0;
    }

    private void initProcs(){
        if(loggingEnabled) logger.debug("Initializing proc lists.");
        mainHandProcs = new ArrayList<>();

        if(dualWielding){
            offHandProcs = new ArrayList<>();
        }

        Enchant mainHandEnchant = characterSetup.getEquippedEnchants()[MAINHAND];
        if(mainHandEnchant != null){
            if(mainHandEnchant.isSpellId()){
                mainHandProcs.add(SimDB.SPELLS.get(characterSetup.getEquippedEnchants()[MAINHAND].getId()));
                if(loggingEnabled) logger.debug("Mainhand proc added: {}", mainHandProcs.get(mainHandProcs.size()-1).getId());
            }
        }

        Enchant offHandEnchant = characterSetup.getEquippedEnchants()[OFFHAND];
        if(offHandEnchant != null){
            if(offHandEnchant.isSpellId()){
                offHandProcs.add(SimDB.SPELLS.get(characterSetup.getEquippedEnchants()[OFFHAND].getId()));
                if(loggingEnabled) logger.debug("Offhand proc added: {}", offHandProcs.get(offHandProcs.size()-1).getId());
            }
        }
    }

    public Warrior(CharacterSetup characterSetup){
        this.characterSetup = characterSetup;
        stats = new Stats();
    }

    public int getRage() {
        return rage;
    }

    public void setRage(int rage) {
        this.rage = rage;
    }

    public Weapon getMainHand() {
        return mainHand;
    }

    public Weapon getOffHand() {
        return offHand;
    }

    public boolean isDualWielding() {
        return dualWielding;
    }

    public boolean isFlurryTalented() {
        return flurryTalented;
    }

    public int getLevel() {
        return level;
    }

    public AttackTable getMainHandTable() {
        return mainHandTable;
    }

    public AttackTable getOffHandTable() {
        return offHandTable;
    }

    public AttackTable getYellowTable() {
        return yellowTable;
    }

    public boolean isHeroicStrikeQueued() {
        return isHeroicStrikeQueued;
    }

    public void setHeroicStrikeQueued(boolean heroicStrikeQueued) {
        isHeroicStrikeQueued = heroicStrikeQueued;
    }

    public void generateRage(double damage){
        int rage = (int) (damage / rageConversionFactor * 7.5);

        if(this.rage + rage > 100){
            this.rage = 100;
        }else{
            this.rage += rage;
        }
    }

    public void removeRage(int rageCost){
        if(rage - rageCost < 0){
            setRage(0);
        }else{
            setRage(rage - rageCost);
        }
    }

    public void applyFlurry(){
        if(flurryStacks == 0){
            haste.set(haste.get() * flurryHaste);
        }

        flurryStacks = 3;
    }

    public void removeFlurry(){
        haste.set(haste.get() / flurryHaste);
    }

    public void decrementFlurry(){
        if(flurryStacks > 0){
            flurryStacks--;

            if(flurryStacks == 0){
                removeFlurry();
            }
        }
    }

    public int getFlurryStacks() {
        return flurryStacks;
    }

    public double testGlancing(){
        return glancingBlow(mainHand);
    }

    public double glancingBlow(Weapon weapon){
        double lowEnd = Math.min(1.3 - 0.05 * (target.getDefense() - weapon.getSkill()), 0.91);
        double highEnd = Math.max(Math.min(1.2 - 0.03 * (target.getDefense() - weapon.getSkill()), 0.99), 0.2);

        return (ThreadLocalRandom.current().nextInt(0, 10001) * (highEnd - lowEnd) + lowEnd * 10000.0) / 10000.0;
    }

    public double getFlurryHaste() {
        return flurryHaste;
    }

    public CharacterSetup getCharacterSetup() {
        return characterSetup;
    }

    public double getDamageMod() {
        return damageMod;
    }

    public boolean isHeroicStrike9() {
        return heroicStrike9;
    }

    public Stats getStats() {
        return stats;
    }

    public double getStatMod(){
        double mod = 1;

        if(characterSetup.getActiveAuras().containsKey(SPIRIT_OF_ZANDALAR)){
            mod *= characterSetup.getActiveAuras().get(SPIRIT_OF_ZANDALAR).getStatMod();
        }

        if(characterSetup.getActiveAuras().containsKey(BLESSING_OF_KINGS)){
            mod *= characterSetup.getActiveAuras().get(BLESSING_OF_KINGS).getStatMod();
        }

        return mod;
    }

    public double getStaMod(){
        double mod = 1;

        if(characterSetup.getActiveAuras().containsKey(DMT_STA)){
            mod *= characterSetup.getActiveAuras().get(DMT_STA).getStaMod();
        }

        return mod;
    }

    public int getStr(){
        return (int) Math.floor((characterSetup.getRace().getBaseStats().getStr() + stats.getStr()) * getStatMod());
    }

    public int getAgi(){
        return (int) Math.floor((characterSetup.getRace().getBaseStats().getAgi() + stats.getAgi()) * getStatMod());
    }

    public int getSta(){
        return (int) Math.floor((characterSetup.getRace().getBaseStats().getSta() + stats.getSta()) * getStatMod() * getStaMod());
    }

    public int getAp(){
        return getStr() * 2 + characterSetup.getRace().getBaseStats().getAp() + stats.getAp()
                + (characterSetup.getTempEnchantMH() != null ? characterSetup.getTempEnchantMH().getAp() : 0)
                + (characterSetup.getTempEnchantOH() != null ? characterSetup.getTempEnchantOH().getAp() : 0);
    }

    public int getHit(){
        return stats.getHit();
    }

    public double getCritMH(){
        return stats.getCrit() + getAgi()/20.0
                + (characterSetup.getTempEnchantMH() != null ? characterSetup.getTempEnchantMH().getCrit() : 0)
                + (characterSetup.getTempEnchantOH() != null ? characterSetup.getTempEnchantOH().getCrit() : 0)
                + (getWeaponTypeMH().equals("axe") ? characterSetup.getActiveTalents().getOrDefault(AXE_SPEC, 0) : 0)
                + (getWeaponTypeMH().equals("polearm") ? characterSetup.getActiveTalents().getOrDefault(POLEARM_SPEC, 0) : 0)
                + characterSetup.getActiveTalents().getOrDefault(CRUELTY, 0);
    }

    public double getCritOH(){
        return stats.getCrit() + getAgi()/20.0
                + (characterSetup.getTempEnchantMH() != null ? characterSetup.getTempEnchantMH().getCrit() : 0)
                + (characterSetup.getTempEnchantOH() != null ? characterSetup.getTempEnchantOH().getCrit() : 0)
                + (getWeaponTypeOH().equals("axe") ? characterSetup.getActiveTalents().getOrDefault(AXE_SPEC, 0) : 0)
                + (getWeaponTypeOH().equals("polearm") ? characterSetup.getActiveTalents().getOrDefault(POLEARM_SPEC, 0) : 0)
                + characterSetup.getActiveTalents().getOrDefault(CRUELTY, 0);
    }

    public String getWeaponTypeMH(){
        if(characterSetup.getEquippedItems()[MAINHAND] !=  null){
            return characterSetup.getEquippedItems()[MAINHAND].getType();
        }else{
            return "";
        }
    }

    public String getWeaponTypeOH(){
        if(characterSetup.getEquippedItems()[OFFHAND] !=  null){
            return characterSetup.getEquippedItems()[OFFHAND].getType();
        }else{
            return "";
        }
    }

    public int getHp(){
        return (int) Math.floor((characterSetup.getRace().getBaseStats().getHp() + getSta() * 10  + stats.getHp()) * (characterSetup.getRace().getId() == TAUREN ? 1.05f : 1));
    }

    public int getDefense(){
        return level * 5 + stats.getDefense() + characterSetup.getActiveTalents().getOrDefault(ANTICIPATION, 0) * 2;
    }

    public double getBlock(){
        return  5 + stats.getBlock() + stats.getDefense() * 0.04 + characterSetup.getActiveTalents().getOrDefault(SHIELD_SPEC, 0);
    }

    public int getBlockValue(){
        return (int) Math.floor(getStr()/20.0 + stats.getBlockValue());
    }

    public double getParry(){
        return 5 + stats.getParry() + stats.getDefense() * 0.04 + characterSetup.getActiveTalents().getOrDefault(DEFLECTION, 0);
    }

    public double getDodge(){
        return getAgi()/20.0f + stats.getDodge() + stats.getDefense() * 0.04 + characterSetup.getRace().getBaseStats().getDodge();
    }

    // TODO: Verify how Toughness talent works (only pure armor from items? enchants? agi from items?)
    public int getArmor(){
        int armorFromItems = 0;

        for(Item item : characterSetup.getEquippedItems()){
            if(item != null){
                armorFromItems += item.getArmor();
            }
        }

        return (int) Math.floor(stats.getArmor() + armorFromItems * characterSetup.getActiveTalents().getOrDefault(TOUGHNESS, 0) * 0.02f + getAgi() * 2);
    }

    public int getWeaponSkillMH(){
        return getWeaponSkill(MAINHAND);
    }

    public int getWeaponSkillOH(){
        return getWeaponSkill(OFFHAND);
    }

    private int getWeaponSkill(int slot){
        int weaponSkill = level * 5;

        if(characterSetup.getEquippedItems()[slot] == null){
            return weaponSkill;
        }

        weaponSkill += characterSetup.getEquippedItems()[slot].getWeaponSkill();

        if(characterSetup.getRace().getId() == HUMAN){
            if(characterSetup.getEquippedItems()[slot].getType().equals("mace") || characterSetup.getEquippedItems()[slot].getType().equals("sword")){
                weaponSkill += 5;
            }
        }

        if(characterSetup.getRace().getId() == ORC){
            if(characterSetup.getEquippedItems()[slot].getType().equals("axe")){
                weaponSkill += 5;
            }
        }

        if(characterSetup.getEquippedItems()[slot].getSlot().equals("2h")){
            return weaponSkill;
        }

        for(Item item : characterSetup.getEquippedItems()){
            if(item != null){
                if(item.getWeaponSkillType() != null){
                    if(item.getWeaponSkillType().equals(characterSetup.getEquippedItems()[slot].getType())){
                        weaponSkill += item.getWeaponSkill();
                    }
                    if(item.getWeaponSkillType().equals("varied")){
                        if(characterSetup.getEquippedItems()[slot].getType().equals("axe") || characterSetup.getEquippedItems()[slot].getType().equals("dagger") || characterSetup.getEquippedItems()[slot].getType().equals("sword")){
                            weaponSkill += item.getWeaponSkill();
                        }
                    }
                }
            }
        }

        if(characterSetup.getSetBonuses().containsKey(WARBLADES_SET)){
            if(characterSetup.getSetBonuses().get(WARBLADES_SET).getActiveSetBonuses().size() == 1){
                weaponSkill += 6;
            }
        }

        if(slot == MAINHAND){
            if(characterSetup.getEquippedItems()[OFFHAND] != null){
                weaponSkill += (characterSetup.getEquippedItems()[OFFHAND].getType().equals(characterSetup.getEquippedItems()[MAINHAND].getType()) ? characterSetup.getEquippedItems()[OFFHAND].getWeaponSkill() : 0);
            }
        }else{
            if(characterSetup.getEquippedItems()[MAINHAND] != null){
                weaponSkill += (characterSetup.getEquippedItems()[MAINHAND].getType().equals(characterSetup.getEquippedItems()[OFFHAND].getType()) ? characterSetup.getEquippedItems()[MAINHAND].getWeaponSkill() : 0);
            }
        }

        return weaponSkill;
    }

    public boolean isOnGlobalCooldown() {
        return onGlobalCooldown;
    }

    public void startGlobalCooldown(){
        onGlobalCooldown = true;
    }

    public void finishGlobalCooldown(){
        onGlobalCooldown = false;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

    public double getHaste() {
        return haste.get();
    }

    public DoubleProperty hasteProperty() {
        return haste;
    }

    public void setHaste(double haste) {
        this.haste.set(haste);
    }

    public List<Spell> getMainHandProcs() {
        return mainHandProcs;
    }

    public List<Spell> getOffHandProcs() {
        return offHandProcs;
    }

    public AttackTable getExtraTargetTable() {
        return extraTargetTable;
    }
}
