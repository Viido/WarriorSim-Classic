package sim.talents;

import javafx.beans.binding.NumberBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.ArrayList;
import java.util.List;

public class TalentTier {
    private List<Talent> talents;
    private IntegerProperty points;
    private NumberBinding pointsBinding;
    private BooleanProperty locked;
    private BooleanProperty available;

    private int tier;

    private TalentTier prev;
    private TalentTier next;
    private TalentTree talentTree;

    private IntegerProperty cumulativePoints;

    public TalentTier(TalentTree talentTree){
        this.talentTree = talentTree;
        talents = new ArrayList<>();
        points = new SimpleIntegerProperty(0);
        locked = new SimpleBooleanProperty(false);
        available = new SimpleBooleanProperty(false);
        cumulativePoints = new SimpleIntegerProperty(0);

        cumulativePoints.bind(pointsProperty());
    }

    public List<Talent> getTalents() {
        return talents;
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

    public void bindCumulativePoints(){
        cumulativePoints.bind(prev.cumulativePoints.add(points));
    }


    public int getPoints() {
        return points.get();
    }

    public IntegerProperty pointsProperty() {
        return points;
    }

    public void setLocked(boolean locked) {
        this.locked.set(locked);
    }

    public boolean getLocked(){
        return locked.get();
    }

    public void setPrev(TalentTier prev) {
        this.prev = prev;
    }

    public void setNext(TalentTier next) {
        this.next = next;
    }

    public TalentTier getPrev() {
        return prev;
    }

    public TalentTier getNext() {
        return next;
    }

    public int getCumulativePoints() {
        return cumulativePoints.get();
    }

    public IntegerProperty cumulativePointsProperty() {
        return cumulativePoints;
    }

    public void setTier(int tier) {
        if(tier == 0){
            availableProperty().set(true);
        }
        this.tier = tier;
    }

    public int getTier() {
        return tier;
    }

    public void setAvailable(boolean available) {
        this.available.set(available);
    }

    public boolean isAvailable() {
        return available.get();
    }

    public BooleanProperty availableProperty() {
        return available;
    }

    public TalentTree getTalentTree() {
        return talentTree;
    }
}

