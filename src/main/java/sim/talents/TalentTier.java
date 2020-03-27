package sim.talents;

import javafx.beans.binding.NumberBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.ArrayList;
import java.util.List;

public class TalentTier {
    private List<Talent> talents = new ArrayList<>();
    private TalentTier prev;
    private TalentTier next;
    private TalentTree talentTree;
    private int row;

    private IntegerProperty points = new SimpleIntegerProperty(0);
    private IntegerProperty cumulativePoints = new SimpleIntegerProperty(0);
    private BooleanProperty locked = new SimpleBooleanProperty(false);
    private BooleanProperty available = new SimpleBooleanProperty(false);

    public TalentTier(TalentTree talentTree, int row){
        this.talentTree = talentTree;
        this.row = row;

        if(row == 0){
            available.set(true);
        }
    }

    public List<Talent> getTalents() {
        return talents;
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

    public void bindCumulativePoints(){
        if(row == 0){
            cumulativePoints.bind(points);
        }else{
            cumulativePoints.bind(prev.cumulativePoints.add(points));

            cumulativePoints.addListener((observable, oldValue, newValue) -> {
                if (newValue.intValue() >= row * 5) {
                    available.set(true);
                } else if (newValue.intValue() < row * 5) {
                    available.set(false);
                }
            });
        }
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

    public int getRow() {
        return row;
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

