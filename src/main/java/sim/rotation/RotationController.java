package sim.rotation;


import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class RotationController implements Initializable {
    @FXML
    VBox selectionBox;
    @FXML
    HBox bloodthirst;
    @FXML
    Label bloodthirstLabel;
    @FXML
    Label whirlwindLabel;
    @FXML
    Label heroicStrikeLabel;
    @FXML
    Label overpowerLabel;
    @FXML
    Label hamstringLabel;
    @FXML
    Label deathWishLabel;
    @FXML
    Label mightyRageLabel;
    @FXML
    Label recklessnessLabel;

    // TODO Create rotation selection boxes programatically

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bloodthirstLabel.setGraphic(new ImageView(new Image("images/icons/spell_nature_bloodlust.jpg")));
        whirlwindLabel.setGraphic(new ImageView(new Image("images/icons/ability_whirlwind.jpg")));
        heroicStrikeLabel.setGraphic(new ImageView(new Image("images/icons/ability_rogue_ambush.jpg")));
        overpowerLabel.setGraphic(new ImageView(new Image("images/icons/ability_meleedamage.jpg")));
        hamstringLabel.setGraphic(new ImageView(new Image("images/icons/ability_shockwave.jpg")));
        deathWishLabel.setGraphic(new ImageView(new Image("images/icons/spell_shadow_deathpact.jpg")));
        mightyRageLabel.setGraphic(new ImageView(new Image("images/icons/inv_potion_41.jpg")));
        recklessnessLabel.setGraphic(new ImageView(new Image("images/icons/ability_criticalstrike.jpg")));
    }

    public void removeBloodthirst(){
        selectionBox.getChildren().remove(bloodthirst);
    }

    public void addBloodthirst(){
        selectionBox.getChildren().add(0, bloodthirst);
    }
}
