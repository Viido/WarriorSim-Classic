package sim.talents;

import com.google.gson.annotations.SerializedName;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Talents {
    @SerializedName(value = "tree")
    private List<TalentTree> talentTrees;
    private IntegerProperty points;
    private NumberBinding pointsBinding;

    public Talents(){
        points = new SimpleIntegerProperty(0);
    }

    public List<TalentTree> getTalentTrees() {
        return talentTrees;
    }

    public void setTalentTrees(List<TalentTree> talentTrees) {
        this.talentTrees = talentTrees;
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

    public List<Talent> getTalents(){
        List<Talent> talents = new ArrayList<>();

        for(TalentTree tree : talentTrees){
            talents.addAll(Arrays.asList(tree.getTalents()));
        }

        return talents;
    }

    public void setPointsBinding(){
        for(TalentTree t : talentTrees){
            if(pointsBinding == null){
                pointsBinding = t.pointsProperty().add(0);
            }else{
                pointsBinding = pointsBinding.add(t.pointsProperty());
            }
        }

        points.bind(pointsBinding);
    }

    public Talent getTalent(int tree, int col, int row){
        return talentTrees.get(tree).getTalent(col, row);
    }


}
