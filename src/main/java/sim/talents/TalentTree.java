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

    private IntegerProperty points;
    private NumberBinding pointsBinding;

    private List<TalentTier> talentTiers;

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

    public void setPointsBinding(){
        for(Talent t : talents){
            if(pointsBinding == null){
                pointsBinding = t.pointsProperty().add(0);
            }else{
                pointsBinding = pointsBinding.add(t.pointsProperty());
            }
        }

        points.bind(pointsBinding);
    }

    public void setTalentTiers(){
        for(int i = 0; i < 7; i++){
            talentTiers.add(new TalentTier(this));
            talentTiers.get(i).setTier(i);
        }

        for(Talent t : talents){
            talentTiers.get(t.getRow()).getTalents().add(t);
        }

        for(int i = 0; i < 7; i++){
            if(i < 6){
                talentTiers.get(i).setNext(talentTiers.get(i + 1));
            }

            if(i > 0){
                talentTiers.get(i).setPrev(talentTiers.get(i - 1));
            }
        }

        for(TalentTier t : talentTiers){
            t.setPointsBinding();
            if(t.getPrev() != null){
                t.bindCumulativePoints();
            }



            if(t.getTier() != 0){
                t.cumulativePointsProperty().addListener((observable, oldValue, newValue) -> {
                    if(newValue.intValue() >= t.getTier() * 5){
                        t.setAvailable(true);
                    }else if(newValue.intValue() < t.getTier() * 5){
                        t.setAvailable(false);
                    }
                });
            }
        }
    }

    public TalentTier getHighestActiveTier(){
        for(int i = 6; i >= 0; i--){
            if(talentTiers.get(i).getPoints() > 0){
                return talentTiers.get(i);
            }
        }

        return talentTiers.get(0);
    }
}

