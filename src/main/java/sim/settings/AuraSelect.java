package sim.settings;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTooltip;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import sim.main.Warrior;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


public class AuraSelect extends HBox {
    @FXML
    Label auraInfo;
    @FXML
    JFXCheckBox checkBox;

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

        if(warrior.getActiveAuras().containsKey(aura.getId())){
            checkBox.setSelected(true);
        }

        auraInfo.setText(aura.getName());


        ImageView imageView = new ImageView(new Image("images/auras/" + aura.getIcon() + ".png", 32, 32, true, true));
        imageView.setViewport(new Rectangle2D(1, 1, 32, 32));
        auraInfo.setGraphic(imageView);

        checkBox.setMinHeight(32);

        JFXTooltip tooltip = new JFXTooltip();
        tooltip.setPos(Pos.TOP_RIGHT);
        tooltip.setMargin(-30);

        tooltip.setShowDelay(Duration.ZERO);

        tooltip.setText(aura.getDescription());

        auraInfo.setOnMouseEntered(e -> {
            tooltip.show(auraInfo, 0, 0);
        });

        auraInfo.setOnMouseExited(e -> {
            tooltip.hide();
        });

        checkBox.selectedProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue){
                warrior.getActiveAuras().put(aura.getId(), aura);
            }else{
                warrior.getActiveAuras().remove(aura.getId());
            }
        });
    }
}
