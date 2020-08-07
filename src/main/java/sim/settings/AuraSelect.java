package sim.settings;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTooltip;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import sim.warrior.Warrior;

import java.io.IOException;


public class AuraSelect extends HBox {
    @FXML
    Label auraInfo;
    @FXML
    JFXCheckBox checkBox;
    JFXCheckBox checkBoxOH;

    Aura aura;
    Warrior warrior;

    public AuraSelect(Aura aura, Warrior warrior){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/AuraSelect.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        this.aura = aura;
        this.warrior = warrior;

        if(aura.getGroup() != null){
            if(aura.getGroup().equals("weapon")){
                addOHCheckBox();
            }
        }

        loadData();

        auraInfo.setText(aura.getName());

        ImageView imageView = new ImageView(new Image("images/auras/" + aura.getIcon() + ".png", 32, 32, true, true));
        imageView.setViewport(new Rectangle2D(1, 1, 32, 32));
        auraInfo.setGraphic(imageView);

        checkBox.setMinHeight(32);
        setSpacing(5);


        JFXTooltip tooltip = new JFXTooltip();
        tooltip.setPos(Pos.TOP_RIGHT);

        tooltip.setShowDelay(Duration.ZERO);

        tooltip.setText(aura.getDescription());

        auraInfo.setOnMouseEntered(e -> {
            tooltip.showOnAnchors(auraInfo, 33, 0);
        });

        auraInfo.setOnMouseExited(e -> {
            tooltip.hide();
        });
    }

    private void addOHCheckBox(){
        checkBoxOH = new JFXCheckBox();
        checkBoxOH.setMinHeight(32);

        getChildren().add(checkBoxOH);
    }

    private void loadData(){
        if(warrior.getActiveAuras().containsKey(aura.getId())){
            checkBox.setSelected(true);

        }else if (checkBoxOH != null){
            if(warrior.getTempEnchantMH() != null){
                if(warrior.getTempEnchantMH().getId() == aura.getId()){
                    checkBox.setSelected(true);
                }
            }
            if(warrior.getTempEnchantOH() != null){
                if(warrior.getTempEnchantOH().getId() == aura.getId()){
                    checkBoxOH.setSelected(true);
                }
            }
        }
    }

    public JFXCheckBox getCheckBox(){
        return checkBox;
    }

    public JFXCheckBox getCheckBoxOH(){
        return checkBoxOH;
    }
}
