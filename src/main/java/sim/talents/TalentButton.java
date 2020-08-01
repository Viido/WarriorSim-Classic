package sim.talents;

import com.jfoenix.controls.JFXTooltip;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.io.IOException;

public class TalentButton extends Button {
    @FXML
    Label label1;
    @FXML
    Label label2;

    private Talent talent;
    private StringProperty description = new SimpleStringProperty();

    private IntegerProperty points = new SimpleIntegerProperty(0);
    private TalentTier talentTier;
    private TalentButton reqTalent;
    private BooleanProperty available = new SimpleBooleanProperty(false);
    private BooleanProperty locked = new SimpleBooleanProperty(false);
    private IntegerProperty totalPoints;

    public TalentButton(Talent talent, TalentTier talentTier, IntegerProperty totalPoints) {
        this.talent = talent;
        this.talentTier = talentTier;
        this.totalPoints = totalPoints;

        description.set(talent.getDescriptions()[0]);

        if(talent.getRow() == 0){
            available.set(true);
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/TalentButton.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        availableProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue){
                setAvailable();
            }else{
                setUnavailable();
            }
        });

        if(isAvailable()){
            setAvailable();
        }else{
            setUnavailable();
        }

        this.setOnMouseClicked(this::onMouseClicked);

        label2.textProperty().bind(pointsProperty().asString());

        pointsProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.intValue() == talent.getMax()){
                setMaxed();
            }else if(newValue.intValue() < talent.getMax()){
                setUnmaxed();
            }
        });

        totalPoints.addListener((obs, oldValue, newValue) -> {
            if(newValue.intValue() == 51 && points.intValue() == 0){
                setUnavailable();
            }
            if(oldValue.intValue() == 51 && isPointAddable()){
                setAvailable();
            }
        });

        setDescription();
    }

    private void setDescription(){
        pointsProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.intValue() > 0){
                description.set(talent.getDescriptions()[newValue.intValue() - 1]);
            }
        });

        // TODO: custom tooltip class
        JFXTooltip tooltip = new JFXTooltip();
        tooltip.setPos(Pos.TOP_RIGHT);
        tooltip.setMargin(-30);

        tooltip.setShowDelay(Duration.ZERO);

        StringProperty descriptionProperty = new SimpleStringProperty();

        descriptionProperty.bind(Bindings.concat(talent.getName(), "\nRank ", pointsProperty(), "/", talent.getMax(), "\n", description));

        tooltip.textProperty().bind(descriptionProperty);

        this.setOnMouseEntered(e -> tooltip.show(this, 0, 0));
        this.setOnMouseExited(e -> tooltip.hide());
    }


    private void setMaxed(){
        label1.setStyle("-fx-background-position: -42px, 0px;");
        label2.setStyle("-fx-text-fill: rgb(231, 186, 0);");
    }

    private void setUnmaxed(){
        if (isAvailable()) {
            label1.setStyle("-fx-background-position: -84px, 0px;");
        }
        label2.setStyle("-fx-text-fill: rgb(23, 253, 23);");
    }

    private void onMouseClicked(MouseEvent e){
        if(e.getButton() == MouseButton.PRIMARY){
            if(isAvailable() && totalPoints.intValue() < 51){
                addPoint();
            }
        }else if (e.getButton() == MouseButton.SECONDARY){
            if(!isLocked() && isPointRemovable()){
                removePoint();
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

    public int getPoints() {
        return points.get();
    }

    public IntegerProperty pointsProperty() {
        return points;
    }

    public void setPoints(int points) {
        this.points.set(points);
    }


    public void addPoint(){
        if(points.get() < talent.getMax()){
            points.set(points.get() + 1);
        }
    }

    public void removePoint(){
        if(points.get() > 0){
            points.set(points.get() - 1);
        }
    }

    public TalentTier getTalentTier() {
        return talentTier;
    }

    public TalentButton getReqTalent() {
        return reqTalent;
    }

    public void setReqTalent(TalentButton reqTalent) {
        this.reqTalent = reqTalent;

        reqTalent.pointsProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.intValue() == reqTalent.getTalent().getMax()){
                if(isPointAddable()){
                    available.set(true);
                }
            }else{
                available.set(false);
            }
        });

        pointsProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.intValue() > 0){
                reqTalent.setLocked(true);
            }else{
                reqTalent.setLocked(false);
            }
        });
    }

    public boolean isReqMaxed() {
        if(reqTalent != null){
            return reqTalent.getPoints() == reqTalent.getTalent().getMax();
        }

        return true;
    }

    public boolean isAvailable() {
        return available.get();
    }

    public BooleanProperty availableProperty() {
        return available;
    }

    public boolean isLocked() {
        return locked.get();
    }

    public BooleanProperty lockedProperty() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked.set(locked);
    }

    public void setAvailable(boolean available) {
        this.available.set(available);
    }


    private boolean isPointRemovable() {
        TalentTier highestActive = getTalentTier().getTalentTree().getHighestActiveTier();
        TalentTier nextTier = getTalentTier().getNext();

        if (getPoints() == 0) {
            return false;
        }


        if (highestActive.getRow() == 0) {
            return true;
        }

        if (getTalentTier() == highestActive) {
            return true;
        }

        if (nextTier.getPoints() > 0 && getTalentTier().getCumulativePoints() == nextTier.getRow() * 5) {
            return false;
        }

        if (highestActive.getPrev().getCumulativePoints() == highestActive.getRow() * 5 && highestActive != getTalentTier()) {
            return false;
        }

        if (highestActive.getPrev().getCumulativePoints() == highestActive.getRow() * 5 - 1) {
            return false;
        }

        return true;
    }

    public boolean isPointAddable(){
        if(getPoints() == talent.getMax()){

            return false;
        }

        if(!isReqMaxed()){

            return false;
        }


        if(!talentTier.isAvailable()){

            return false;
        }

        if(totalPoints.intValue() == 51){

            return false;
        }

        return true;
    }
}

