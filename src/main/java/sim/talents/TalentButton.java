package sim.talents;

import javafx.beans.NamedArg;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TalentButton extends Button {
    @FXML
    Label label1;
    @FXML
    Label label2;

    Talent talent;
    TalentTree tree;

    TalentsController talentsController;

    private IntegerProperty points = new SimpleIntegerProperty(0);
    private BooleanProperty available = new SimpleBooleanProperty(false);

    public TalentButton(Talent talent, TalentsController talentsController) {
        this.talent = talent;
        this.talentsController = talentsController;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/TalentButton.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        this.getStyleClass().add("talent-button");

//        Label label1 = new Label();
//        label1.getStyleClass().add("talent-border");
//        label1.setMinWidth(42.0);
//        label1.setMaxWidth(42.0);
//        label1.setMinHeight(42.0);
//        label1.setMaxHeight(42.0);
//
//        Label label2 = new Label();
//        label2.setMinWidth(22.0);
//        label2.setMaxWidth(22.0);
//        label2.setMinHeight(23.0);
//        label2.setMaxHeight(23.0);
//        label2.setText("0");
//        label2.getStyleClass().add("talent-bubble");
//
//
//        this.getChildren().addAll(label1, label2);
        this.setStyle("-fx-background-image: url(/images/icons/" + talent.getImg().toLowerCase()+ ".jpg);");
        ColorAdjust grayscale = new ColorAdjust();
        this.setEffect(grayscale);

        if(talent.getY() > 0 ){
            grayscale.setSaturation(-1);
            label2.setVisible(false);
        }



        this.setOnMouseClicked(this::onMouseClicked);
        label2.textProperty().bind(points.asString());

        points.addListener(((observable, oldValue, newValue) -> {
            if(newValue.intValue() == talent.getMax()){
                label1.setStyle("-fx-background-position: -42px, 0px;");
                label2.setStyle("-fx-text-fill: rgb(231, 186, 0);");
            }else if(newValue.intValue() < talent.getMax()){
                label1.setStyle("-fx-background-position: -84px, 0px;");
                label2.setStyle("-fx-text-fill: rgb(23, 253, 23);");
            }else{
                label1.setStyle("-fx-background-position: 0px, 0px;");
            }
        }));

        available.addListener(((observable, oldValue, newValue) -> {

            if (newValue){
                grayscale.setSaturation(0);
                label2.setVisible(true);
            }else{
                grayscale.setSaturation(-1);
                label2.setVisible(false);
            }
        }));
    }

    private void onMouseClicked(MouseEvent e){
        if(e.getButton() == MouseButton.PRIMARY){
            addPoint();
        }else if (e.getButton() == MouseButton.SECONDARY){
            removePoint();
        }
        System.out.println(talent.getPoints());
    }

    private void addPoint(){
        if(talent.getMax() > points.getValue()){
            points.set(points.getValue() + 1);
            talent.setPoints(points.getValue());
            talentsController.armsTreeAddPoint();
        }
    }

    private void removePoint(){
        if(points.getValue() > 0){
            points.set(points.getValue() - 1);
            talent.setPoints(points.getValue());
            talentsController.armsTreeRemovePoint();

        }
    }

    public void setAvailable(){
        available.set(true);
    }

    public void setUnavailable(){
        available.set(false);
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
}

