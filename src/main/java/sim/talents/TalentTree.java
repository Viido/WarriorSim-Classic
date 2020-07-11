package sim.talents;

import com.google.gson.annotations.SerializedName;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.ArrayList;
import java.util.List;

public class TalentTree {
    @SerializedName(value = "n")
    private String name;
    @SerializedName(value = "t")
    private Talent[] talents;

    private List<TalentTier> talentTiers;
    private Talents allTalents;

    private IntegerProperty points;

    public TalentTree(){
        points = new SimpleIntegerProperty(0);
        talentTiers = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Talent[] getTalents() {
        return talents;
    }

    public void setTalents(Talent[] talents) {
        this.talents = talents;
    }

    public Talent getTalent(int col, int row){
        for (Talent t : talents){
            if(t.getCol() == col && t.getRow() == row){
                return t;
            }
        }

        return null;
    }

    public int getPoints() {
        return points.get();
    }

    public TalentTier getTier(int row){
        return talentTiers.get(row);
    }

    public IntegerProperty pointsProperty() {
        return points;
    }

    public void setPoints(int points) {
        this.points.set(points);
    }

    public void bindPoints(){
        NumberBinding pointsBinding = null;

        for(Talent t : talents){
            if(pointsBinding == null){
                pointsBinding = t.pointsProperty().add(0);
            }else{
                pointsBinding = pointsBinding.add(t.pointsProperty());
            }
        }

        points.bind(pointsBinding);
    }

    public TalentTier getHighestActiveTier(){
        for(int i = 6; i >= 0; i--){
            if(talentTiers.get(i).getPoints() > 0){
                return talentTiers.get(i);
            }
        }

        return talentTiers.get(0);
    }

    public List<TalentTier> getTalentTiers() {
        return talentTiers;
    }

    public void setTalentTiers(List<TalentTier> talentTiers) {
        this.talentTiers = talentTiers;
    }

    public void setAllTalents(Talents talents){
        this.allTalents = talents;
    }

    public Talents getAllTalents(){
        return allTalents;
    }
}

