package sim.talents;

import com.google.gson.Gson;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import java.io.*;
import java.net.URL;
import java.util.*;

public class TalentsController implements Initializable {
    @FXML
    GridPane tree1;
    @FXML
    GridPane tree2;
    @FXML
    GridPane tree3;
    @FXML
    StackPane pane1;
    @FXML
    StackPane pane2;
    @FXML
    StackPane pane3;
    @FXML
    Label label1;
    @FXML
    Label label2;
    @FXML
    Label label3;
    @FXML
    Label talentBuild;
    @FXML
    Label remainingPoints;
    @FXML
    Button resetButton;
    @FXML
    Button resetTree1;
    @FXML
    Button resetTree2;
    @FXML
    Button resetTree3;
    @FXML
    StackPane main;


    Talents talents;
    Map<Talent, TalentButton> talentButtons = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initModel();
        initArrows();
        initLabels();
        initResetButtons();
        initTalentButtons();
    }

    private void initResetButtons(){
        resetButton.setOnAction(e -> resetAllTalents());
        resetButton.setGraphic(new ImageView(new Image("images/talent/clear.gif")));
        resetTree1.setOnAction(e -> resetTree1OnAction());
        resetTree1.setGraphic(new ImageView(new Image("images/talent/clear.gif")));
        resetTree2.setOnAction(e -> resetTree2OnAction());
        resetTree2.setGraphic(new ImageView(new Image("images/talent/clear.gif")));
        resetTree3.setOnAction(e -> resetTree3OnAction());
        resetTree3.setGraphic(new ImageView(new Image("images/talent/clear.gif")));
    }

    private void resetAllTalents(){
        for(Talent talent : talents.getTalents()){
            talent.setPoints(0);
        }
    }

    private void resetTree1OnAction(){
        for(Talent talent : talents.getTalentTrees().get(0).getTalents()){
            talent.setPoints(0);
        }
    }

    private void resetTree2OnAction(){
        for(Talent talent : talents.getTalentTrees().get(1).getTalents()){
            talent.setPoints(0);
        }
    }

    private void resetTree3OnAction(){
        for(Talent talent : talents.getTalentTrees().get(2).getTalents()){
            talent.setPoints(0);
        }
    }

    private void initTalentButtons(){
        GridPane[] talentGrids = {tree1, tree2, tree3};

        for(int i = 0; i < 3; i++){
            for (Talent talent : talents.getTalentTrees().get(i).getTalents()){
                TalentButton talentButton = new TalentButton(talent, talents);
                talentButtons.put(talent, talentButton);
                talentGrids[i].add(talentButton, talent.getCol(), talent.getRow());
            }
        }
    }

    private void initLabels(){
        label1.setGraphic(new ImageView(new Image("images/talent/classes/trees/warrior_1.gif")));
        label2.setGraphic(new ImageView(new Image("images/talent/classes/trees/warrior_2.gif")));
        label3.setGraphic(new ImageView(new Image("images/talent/classes/trees/warrior_3.gif")));

        StringProperty talentBuildString = new SimpleStringProperty();

        List<TalentTree> talentTrees = talents.getTalentTrees();

        talentBuildString.bind(Bindings.concat("Warrior (", talentTrees.get(0).pointsProperty(), "/", talentTrees.get(1).pointsProperty(), "/", talentTrees.get(2).pointsProperty(), ")"));
        talentBuild.textProperty().bind(talentBuildString);

        remainingPoints.textProperty().bind(Bindings.concat("Remaining points: ", Bindings.subtract(51, talents.pointsProperty()).asString()));
    }

    private void initModel(){
        Gson gson = new Gson();
        talents = gson.fromJson(new InputStreamReader(getClass().getResourceAsStream("data/talents.json")), Talents.class);

        setTalentTiers();
        setReqTalents();

        for(TalentTree talentTree : talents.getTalentTrees()){
            talentTree.bindPoints();
        }

        talents.bindPoints();
    }

    private void initArrows(){
        StackPane[] stackPanes = {pane1, pane2, pane3};

        for(int i = 0; i < 3; i++){
            for(Talent t : talents.getTalentTrees().get(i).getTalents()){
                if(t.getReqTalent() != null){
                    stackPanes[i].getChildren().add(new TalentArrow(t.getReqTalent(), t));
                }
            }
        }
    }

    private void setTalentTiers(){
        for(TalentTree talentTree : talents.getTalentTrees()) {
            List<TalentTier> talentTiers = new ArrayList<>();

            for(int i = 0; i < 7; i++){
                talentTiers.add(new TalentTier(talentTree, i));
            }

            for(Talent talent : talentTree.getTalents()){
                talentTiers.get(talent.getRow()).getTalents().add(talent);
                talent.setTalentTier(talentTiers.get(talent.getRow()));
            }

            for (int i = 0; i < 7; i++) {
                if (i < 6) {
                    talentTiers.get(i).setNext(talentTiers.get(i + 1));
                }

                if (i > 0) {
                    talentTiers.get(i).setPrev(talentTiers.get(i - 1));
                }
            }

            for (TalentTier talentTier : talentTiers) {
                talentTier.bindPoints();
                talentTier.bindCumulativePoints();
            }

            talentTree.setTalentTiers(talentTiers);
            talentTree.setAllTalents(talents);
        }
    }

    private void setReqTalents(){
        List<Talent> dependencies = new ArrayList<>();

        for(Talent t : talents.getTalents()){
            if(t.getReq() != null){
                dependencies.add(t);
            }
        }

        for(Talent t : dependencies){
            t.setReqTalent(Objects.requireNonNull(findReq(0, t.getReq()[0], t.getTalentTier().getTalentTree().getTier(0))));
        }
    }

    private Talent findReq(int counter, int dependency, TalentTier talentTier){
        for(Talent t : talentTier.getTalents()){
            if(counter == dependency){
                return t;
            }
            counter++;

            if(counter > dependency){
                return null;
            }
        }

        if(talentTier.getNext() == null){
            return null;
        }

        return findReq(counter, dependency, talentTier.getNext());
    }
}

