package sim.items;

import com.jfoenix.controls.JFXTooltip;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.*;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import static sim.warrior.Constants.COLOR_UNCOMMON;

public class ItemTooltipSkin implements Skin<JFXTooltip> {
    private VBox container;
    private JFXTooltip tooltip;
    private Item item;



    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a new TooltipSkin instance for the given {@link Tooltip}.
     * @param t the tooltip
     */
    public ItemTooltipSkin(JFXTooltip t, Item item) {
        this.tooltip = t;
        this.item = item;

        container = new VBox();

        Border border = new Border(new BorderImage(new Image(getClass().getResource("/images/tooltip.png").toExternalForm()),
                new BorderWidths(6),
                new Insets(0),
                new BorderWidths(6, 6, 6, 6),
                true,
                BorderRepeat.STRETCH,
                BorderRepeat.STRETCH));

        container.setBorder(border);

        container.setMaxWidth(320);
        container.setPadding(new Insets(0, 3, 3, 3));

        generateTooltip();
    }

    private void generateTooltip(){
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(dfs);

        Label nameText = createText(item.getName(), item.getColor());
        nameText.setStyle("-fx-font-size: 14");

        container.getChildren().add(nameText);

        if(item.getType() != null){
            HBox typeInfo = new HBox();


            String slot = "";

            Region region = new Region();
            HBox.setHgrow(region, Priority.ALWAYS);

            if(item.getSlot().equals("main")){
                slot = "Main Hand";
            }else if(item.getSlot().equals("off") || item.getType().equals("shield")){
                slot = "Off Hand";
            }else if(item.getSlot().equals("2h")){
                slot = "Two-Hand";
            }else if(item.getType().equals("bow") || item.getType().equals("crossbow") || item.getType().equals("gun")){
                slot = "Ranged";
            }else{
                slot = "One-Hand";
            }

            Label typeLabel = createText(item.getType().substring(0, 1).toUpperCase() + item.getType().substring(1));
            typeLabel.setPadding(new Insets(0, 0, 0, 48));

            typeInfo.getChildren().addAll(createText(slot), region, typeLabel);
            container.getChildren().add(typeInfo);
        }

        if(item.getSpeed() != 0){
            HBox weaponInfo = new HBox();

            Region region = new Region();
            HBox.setHgrow(region, Priority.ALWAYS);

            Label speedLabel = createText("Speed " + df.format(item.getSpeed()));
            speedLabel.setPadding(new Insets(0, 0, 0, 48));

            weaponInfo.getChildren().addAll(
                    createText(item.getMinDmg() + " - " + item.getMaxDmg() + " Damage"),
                    region,
                    speedLabel);

            container.getChildren().add(weaponInfo);
        }

        if(item.getEleDmgType() != null){
            container.getChildren().add(createText("+ " + item.getEleDmgMin() + " - " + item.getEleDmgMax() + " " + item.getEleDmgType().substring(0, 1).toUpperCase() + item.getEleDmgType().substring(1) + " Damage"));
        }

        if(item.getSpeed() != 0){
            container.getChildren().add(createText("(" + df.format((item.getMinDmg() + item.getMaxDmg() + item.getEleDmgMin() + item.getEleDmgMax())/2.0/item.getSpeed()) + " damage per second)"));
        }

        if(item.getShieldBlockValue() != 0){
            container.getChildren().add(createText( "+" + item.getShieldBlockValue() + " Block"));
        }

        if(item.getArmor() != 0){
            container.getChildren().add(createText(item.getArmor() + " Armor"));
        }

        if(item.getStr() != 0){
            container.getChildren().add(createText("+" + item.getStr() + " Strength"));
        }

        if(item.getAgi() != 0){
            container.getChildren().add(createText("+" + item.getAgi() + " Agility"));
        }

        if(item.getSta() != 0){
            container.getChildren().add(createText("+" + item.getSta() + " Stamina"));
        }

        if(item.getIntellect() != 0){
            container.getChildren().add(createText("+" + item.getIntellect() + " Intellect"));
        }

        if(item.getSpirit() != 0){
            container.getChildren().add(createText("+" + item.getSpirit() + " Spirit"));
        }

        if(item.getArcaneRes() != 0){
            container.getChildren().add(createText("+" + item.getArcaneRes() + " Arcane Resistance"));
        }

        if(item.getFireRes() != 0){
            container.getChildren().add(createText("+" + item.getFireRes() + " Fire Resistance"));
        }

        if(item.getFrostRes() != 0){
            container.getChildren().add(createText("+" + item.getFrostRes() + " Frost Resistance"));
        }

        if(item.getNatureRes() != 0){
            container.getChildren().add(createText("+" + item.getNatureRes() + " Nature Resistance"));
        }

        if(item.getShadowRes() != 0){
            container.getChildren().add(createText("+" + item.getShadowRes() + " Shadow Resistance"));
        }

        if(item.getCrit() != 0){
            container.getChildren().add(createText("Equip: Improves your chance to get a critical strike by " + item.getCrit() + "%.", COLOR_UNCOMMON));
        }

        if(item.getHit() != 0){
            container.getChildren().add(createText("Equip: Improves your chance to hit by " + item.getHit() + "%.", COLOR_UNCOMMON));
        }

        if(item.getAp() != 0){
            container.getChildren().add(createText("Equip: +" + item.getAp() + " Attack Power.", COLOR_UNCOMMON));
        }

        if(item.getWeaponSkill() != 0){
            String string = "";

            if(item.getWeaponSkill() > 0 && item.getWeaponSkillType() == null){
                string = "Equip: Increased " + item.getType().substring(0, 1).toUpperCase() + item.getType().substring(1) + "s +" + item.getWeaponSkill() + ".";
            }else if(item.getWeaponSkillType().equals("varied")){
                string = "Equip: Increased Axes +7.\nEquip: Increased Daggers +7.\nEquip: Increased Swords +7.";
            }else{
                string = "Equip: Increased " + item.getWeaponSkillType().substring(0, 1).toUpperCase() + item.getWeaponSkillType().substring(1) + "s +" + item.getWeaponSkill() + ".";
            }

            container.getChildren().add(createText(string, COLOR_UNCOMMON));
        }

        if(item.getBlock() != 0){
            container.getChildren().add(createText("Equip: Increases your chance to block attacks with a shield by " + item.getBlock() + "%.", COLOR_UNCOMMON));
        }

        if(item.getBlockValue() != 0){
            container.getChildren().add(createText("Equip: Increases the block value of your shield by " + item.getBlockValue() + ".", COLOR_UNCOMMON));
        }

        if(item.getParry() != 0){
            container.getChildren().add(createText("Equip: Increases your chance to parry an attack by " + item.getParry() + "%.", COLOR_UNCOMMON));
        }

        if(item.getDodge() != 0){
            container.getChildren().add(createText("Equip: Increases your chance to dodge an attack by " + item.getDodge() + "%.", COLOR_UNCOMMON));
        }

        if(item.getDefense() != 0){
            container.getChildren().add(createText("Equip: Increased Defense +" + item.getDefense() + ".", COLOR_UNCOMMON));
        }
    }

    private Label createText(String string){
        Label text = new Label();
        text.setTextFill(Color.WHITE);
        text.setFont(new Font("Verdana", 12));
        text.setText(string);
        text.autosize();

        text.setWrapText(true);


        return text;
    }

    private Label createText(String string, String color){
        Label text = new Label();
        text.setTextFill(Paint.valueOf(color));
        text.setFont(new Font("Verdana", 12));
        text.setText(string);
        text.autosize();
        text.setWrapText(true);


        return text;
    }

    @Override
    public JFXTooltip getSkinnable() {
        return tooltip;
    }

    @Override
    public Node getNode() {
        return container;
    }

    @Override
    public void dispose() {
        tooltip = null;
        container = null;
        item = null;
    }
}
