package sim.talents;

import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.*;

import java.io.*;
import java.net.URL;
import java.util.*;

public class TalentsController implements Initializable {
    @FXML
    GridPane armsTree;
    @FXML
    GridPane furyTree;
    @FXML
    GridPane protTree;
    @FXML
    StackPane sp1;
    @FXML
    StackPane sp2;
    @FXML
    StackPane sp3;


    Talents talents;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initModel();
        initTalentButtons();
        initArrows();
    }

    private void initTalentButtons(){
        GridPane[] gridPanes = {armsTree, furyTree, protTree};

        for(int i = 0; i < 3; i++){
            for (Talent talent : talents.getTalentTrees().get(i).getTalents()){
                TalentButton talentButton = new TalentButton(talent, talents);

                gridPanes[i].add(talentButton, talent.getCol(), talent.getRow());
            }
        }
    }

    private void initModel(){
        Gson gson = new Gson();
        talents = gson.fromJson(new InputStreamReader(getClass().getResourceAsStream("data/talents.json")), Talents.class);

        for(TalentTree talentTree : talents.getTalentTrees()){
            talentTree.setTalentTiers();
            talentTree.setPointsBinding();

            for(Talent talent : talentTree.getTalents()){
                talent.setTalentTier(talentTree.getTier(talent.getRow()));
            }
        }

        setReqTalents();

        talents.setPointsBinding();
    }

    private void initArrows(){
        StackPane[] stackPanes = {sp1, sp2, sp3};

        for(int i = 0; i < 3; i++){
            for(Talent t : talents.getTalentTrees().get(i).getTalents()){
                if(t.getReqTalent() != null){
                    stackPanes[i].getChildren().add(new TalentArrow(t.getReqTalent(), t));
                }
            }
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
            t.setReqTalent(findReq(0, t.getReq()[0], t.getTalentTier().getTalentTree().getTier(0)));
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

