package sim.talents;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class TalentButton extends Button {
    @FXML
    Label label1;
    @FXML
    Label label2;

    private Talent talent;
    private Talents talents;

    public TalentButton(Talent talent, Talents talents) {
        this.talent = talent;
        this.talents = talents;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/TalentButton.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        talent.availableProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue){
                setAvailable();
            }else{
                setUnavailable();
            }
        });

        if(talent.isAvailable()){
            setAvailable();
        }else{
            setUnavailable();
        }

        this.setOnMouseClicked(this::onMouseClicked);

        label2.textProperty().bind(talent.pointsProperty().asString());

        talent.pointsProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.intValue() == talent.getMax()){
                setMaxed();
            }else if(newValue.intValue() < talent.getMax()){
                setUnmaxed();
            }
        });

        talents.pointsProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.intValue() == 51){
                if(talent.availableProperty().get() && talent.getPoints() == 0){
                    talent.availableProperty().set(false);
                }
            }else if(oldValue.intValue() == 51){
                if(talent.getTalentTier().isAvailable()){
                    talent.availableProperty().set(true);
                }
            }
        });
    }

    private void setMaxed(){
        label1.setStyle("-fx-background-position: -42px, 0px;");
        label2.setStyle("-fx-text-fill: rgb(231, 186, 0);");
    }

    private void setUnmaxed(){
        if(talent.isAvailable()){
            label1.setStyle("-fx-background-position: -84px, 0px;");
            label2.setStyle("-fx-text-fill: rgb(23, 253, 23);");
        }
    }

    private void onMouseClicked(MouseEvent e){
        if(e.getButton() == MouseButton.PRIMARY){
            if(talents.getPoints() < 51 && talent.isAvailable()){
                talent.addPoint();
            }
        }else if (e.getButton() == MouseButton.SECONDARY){
            if(!talent.getTalentTier().getLocked() && isPointRemovable() && !talent.isLocked()){
                talent.removePoint();
            }
        }
    }

    public void setAvailable(){
        this.setStyle("-fx-background-image: url(/images/icons/" + talent.getImg().toLowerCase()+ ".jpg);");
        label1.setStyle("-fx-background-position: -84px, 0px;");
        label2.setVisible(true);
    }

    public void setUnavailable(){
        label1.setStyle("-fx-background-position: 0px, 0px;");
        this.setStyle("-fx-background-image: url(/images/icons/" + talent.getImg().toLowerCase()+ "_grey.jpg);");
        label2.setVisible(false);
    }

    public Talent getTalent() {
        return talent;
    }

    private boolean isPointRemovable(){
        TalentTier highestActive = talent.getTalentTier().getTalentTree().getHighestActiveTier();
        TalentTier nextTier = talent.getTalentTier().getNext();

        if(talent.getPoints() == 0){
            return false;
        }


        if(highestActive.getRow() == 0){
            return true;
        }

        if(talent.getTalentTier() == highestActive){
            return true;
        }

        if(nextTier.getPoints() > 0 && talent.getTalentTier().getCumulativePoints() == nextTier.getRow() * 5){
            return false;
        }

        if(highestActive.getPrev().getCumulativePoints() == highestActive.getRow() * 5 && highestActive != talent.getTalentTier()){
            return false;
        }

        if(highestActive.getPrev().getCumulativePoints() == highestActive.getRow() * 5 - 1){
            return false;
        }

        return true;
    }

}

