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

        setTalentTiers();
        setReqTalents();

        for(TalentTree talentTree : talents.getTalentTrees()){
            talentTree.bindPoints();
        }

        talents.bindPoints();
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

