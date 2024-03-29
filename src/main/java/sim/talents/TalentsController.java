package sim.talents;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import sim.data.SimDB;
import sim.settings.CharacterSetup;

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

    Map<Integer, TalentButton> talentButtons = new HashMap<>();
    CharacterSetup characterSetup;
    List<TalentTree> talentTrees = new ArrayList<>();

    IntegerProperty totalPoints = new SimpleIntegerProperty(0);

    public TalentsController(CharacterSetup characterSetup){
        this.characterSetup = characterSetup;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initModel();
        initTalentButtons();
        bindTalentPoints();
        initArrows();
        initLabels();
        initResetButtons();
        addListeners();
        loadValues();
    }

    private void initModel(){
        for(int i = 0; i < 3; i++){
            TalentTree talentTree = new TalentTree();
            talentTrees.add(talentTree);
        }

        createTalentTiers();
    }

    private void createTalentTiers(){
        for(int i = 0; i < 3; i++) {
            List<TalentTier> talentTiers = talentTrees.get(i).getTalentTiers();

            for(int j = 0; j < 7; j++){
                talentTiers.add(new TalentTier(talentTrees.get(i), j));
            }

            for (int j = 0; j < 7; j++) {
                if (j < 6) {
                    talentTiers.get(j).setNext(talentTiers.get(j + 1));
                }

                if (j > 0) {
                    talentTiers.get(j).setPrev(talentTiers.get(j - 1));
                }
            }
        }
    }

    private void initTalentButtons(){
        GridPane[] talentGrids = {tree1, tree2, tree3};

        for(Talent talent : SimDB.TALENTS){
            TalentButton talentButton = new TalentButton(talent, talentTrees.get(talent.getTree()).getTalentTiers().get(talent.getRow()), totalPoints);

            talentButtons.put(talent.getId(), talentButton);
            talentTrees.get(talent.getTree()).getTalentTiers().get(talent.getRow()).getTalents().add(talentButton);

            if(talent.getReq() != 0){
                talentButton.setReqTalent(talentTrees.get(talent.getTree()).getTalents().stream()
                        .filter(x -> x.getTalent().getId() == talent.getReq())
                        .findAny()
                        .get());
            }

            talentGrids[talent.getTree()].add(talentButtons.get(talent.getId()), talent.getCol(), talent.getRow());
        }
    }

    private void bindTalentPoints(){
        NumberBinding pointsBinding = null;

        for(TalentTree talentTree : talentTrees){
            if(pointsBinding == null){
                pointsBinding = talentTree.pointsProperty().add(0);
            }else{
                pointsBinding = pointsBinding.add(talentTree.pointsProperty());
            }

            talentTree.bindPoints();

            for(TalentTier talentTier : talentTree.getTalentTiers()){
                talentTier.bindPoints();
                talentTier.bindCumulativePoints();
            }
        }

        totalPoints.bind(pointsBinding);
    }

    private void initArrows(){
        StackPane[] stackPanes = {pane1, pane2, pane3};

        for(int i = 0; i < 3; i++){
            for(TalentButton t : talentTrees.get(i).getTalents()){
                if(t.getReqTalent() != null){
                    stackPanes[i].getChildren().add(new TalentArrow(t.getReqTalent(), t));
                }
            }
        }
    }

    private void initLabels(){
        label1.setGraphic(new ImageView(new Image("images/talent/classes/trees/warrior_1.gif")));
        label2.setGraphic(new ImageView(new Image("images/talent/classes/trees/warrior_2.gif")));
        label3.setGraphic(new ImageView(new Image("images/talent/classes/trees/warrior_3.gif")));

        StringProperty talentBuildString = new SimpleStringProperty();

        talentBuildString.bind(Bindings.concat("Warrior (", talentTrees.get(0).pointsProperty(), "/", talentTrees.get(1).pointsProperty(), "/", talentTrees.get(2).pointsProperty(), ")"));
        talentBuild.textProperty().bind(talentBuildString);

        remainingPoints.textProperty().bind(Bindings.concat("Remaining points: ", Bindings.subtract(51, totalPoints).asString()));
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
        for(TalentButton talent : talentButtons.values()){
            talent.setPoints(0);
        }
    }

    private void resetTree1OnAction(){
        for(TalentButton talent : talentTrees.get(0).getTalents()){
            talent.setPoints(0);
        }
    }

    private void resetTree2OnAction(){
        for(TalentButton talent : talentTrees.get(1).getTalents()){
            talent.setPoints(0);
        }
    }

    private void resetTree3OnAction(){
        for(TalentButton talent : talentTrees.get(2).getTalents()){
            talent.setPoints(0);
        }
    }

    private void addListeners(){
        for(TalentTree talentTree : talentTrees){
            for(TalentTier talentTier : talentTree.getTalentTiers()){
                talentTier.availableProperty().addListener((obs, oldValue, newValue) -> {
                    if(newValue){
                        for(TalentButton talentButton : talentTier.getTalents()){
                            if(talentButton.isPointAddable()){
                                talentButton.setAvailable(true);
                            }
                        }
                    }else{
                        for(TalentButton talentButton : talentTier.getTalents()){
                            talentButton.setAvailable(false);
                        }
                    }
                });
            }
        }
    }

    private void loadValues(){
        for(Talent talent : SimDB.TALENTS){
            if(characterSetup.getActiveTalents().containsKey(talent.getId())){
                talentButtons.get(talent.getId()).setPoints(characterSetup.getActiveTalents().get(talent.getId()));
            }
        }
    }

    public Map<Integer, TalentButton> getTalentButtons(){
        return talentButtons;
    }
}

