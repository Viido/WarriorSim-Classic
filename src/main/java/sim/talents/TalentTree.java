package sim.talents;

import javafx.beans.binding.NumberBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.ArrayList;
import java.util.List;

public class TalentTree {
    private List<TalentTier> talentTiers = new ArrayList<>();

    private IntegerProperty points = new SimpleIntegerProperty(0);

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

        for(TalentTier t : talentTiers){
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

    public List<TalentButton> getTalents(){
        List<TalentButton> talentButtons = new ArrayList<>();

        for(TalentTier talentTier : talentTiers){
            talentButtons.addAll(talentTier.getTalents());
        }

        return talentButtons;
    }
}

