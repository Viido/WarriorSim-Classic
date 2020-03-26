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
    HashMap<Talent, TalentButton> talentButtons = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addTalents();
    }

    public void addTalents(){
        Gson gson = new Gson();
        talents = gson.fromJson(new InputStreamReader(getClass().getResourceAsStream("data/talents.json")), Talents.class);

        setReqTalents();

        GridPane[] gridPanes = {armsTree, furyTree, protTree};
        StackPane[] stackPanes = {sp1, sp2, sp3};


        for(int i = 0; i < talents.getTalentTrees().size(); i++){
            talents.getTalentTrees().get(i).setTalentTiers();
            talents.getTalentTrees().get(i).setPointsBinding();

            Talent[] talentList = talents.getTalentTrees().get(i).getTalents();

            for (Talent talent : talentList) {
                talent.setTalentTier(talents.getTalentTrees().get(i).getTier(talent.getRow()));
                TalentButton tb = new TalentButton(talent, talents);

                if (talent.getReqTalent() != null) {
                    stackPanes[i].getChildren().add(new TalentArrow(talentButtons.get(talent.getReqTalent()), tb));
                }
                talentButtons.put(talent, tb);
                gridPanes[i].add(tb, talent.getCol(), talent.getRow());
            }
        }

        talents.setPointsBinding();
    }

    private void setReqTalents(){
        talents.getTalent(0,2,2).setReqTalent(talents.getTalent(0, 2, 0));
        talents.getTalent(0,1,2).setReqTalent(talents.getTalent(0, 1, 1));
        talents.getTalent(0,2,3).setReqTalent(talents.getTalent(0, 2, 2));
        talents.getTalent(0,1,6).setReqTalent(talents.getTalent(0, 1, 4));

        talents.getTalent(1,2,5).setReqTalent(talents.getTalent(1, 2, 3));
        talents.getTalent(1,1,6).setReqTalent(talents.getTalent(1, 1, 4));

        talents.getTalent(2,1,2).setReqTalent(talents.getTalent(2, 1, 0));
        talents.getTalent(2,1,6).setReqTalent(talents.getTalent(2, 1, 4));
    }

}

