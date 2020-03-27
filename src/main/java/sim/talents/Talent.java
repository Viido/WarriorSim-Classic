package sim.talents;

import com.google.gson.annotations.SerializedName;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Talent {
    @SerializedName(value = "i")
    private int id;
    @SerializedName(value = "n")
    private String name;
    @SerializedName(value = "m")
    private int max;
    @SerializedName(value = "s")
    private int[] spellIds;
    @SerializedName(value = "d")
    private String[] descriptions;
    @SerializedName(value = "x")
    private int col;
    @SerializedName(value = "y")
    private int row;
    @SerializedName(value = "r")
    private int[] req;
    @SerializedName(value = "iconname")
    private String img;

    private IntegerProperty points;
    private TalentTier talentTier;
    private Talent reqTalent;
    private BooleanProperty available;
    private BooleanProperty reqMaxed;
    private BooleanProperty locked;

    public Talent(){
        points = new SimpleIntegerProperty(0);
        available = new SimpleBooleanProperty(false);
        reqMaxed = new SimpleBooleanProperty(true);
        locked = new SimpleBooleanProperty(false);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int[] getSpellIds() {
        return spellIds;
    }

    public void setSpellIds(int[] spellIds) {
        this.spellIds = spellIds;
    }

    public String[] getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(String[] descriptions) {
        this.descriptions = descriptions;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
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


    public void addPoint(){
        if(points.get() < max){
            points.set(points.get() + 1);
        }
    }

    public void removePoint(){
        if(points.get() > 0 ){
            points.set(points.get() - 1);
        }
    }

    public TalentTier getTalentTier() {
        return talentTier;
    }

    public void setTalentTier(TalentTier talentTier) {
        this.talentTier = talentTier;

        if(talentTier.getRow() == 0){
            available.set(true);
        }

        talentTier.availableProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue && reqMaxed.get()){
                available.set(true);
            }else{
                available.set(false);
            }
        });
    }

    public Talent getReqTalent() {
        return reqTalent;
    }

    public void setReqTalent(Talent reqTalent) {
        this.reqTalent = reqTalent;
        reqMaxed.set(false);

        reqTalent.pointsProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.intValue() == reqTalent.getMax()){
                reqMaxed.set(true);
            }else{
                reqMaxed.set(false);
            }

            if(newValue.intValue() == reqTalent.getMax() && talentTier.isAvailable()){
                available.set(true);
            }else{
                available.set(false);
            }
        });

        pointsProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.intValue() > 0){
                reqTalent.setLocked(true);
            }else{
                reqTalent.setLocked(false);
            }
        });
    }

    public boolean isReqMaxed() {
        return available.get();
    }

    public BooleanProperty reqMaxedProperty() {
        return available;
    }

    public boolean isAvailable() {
        return available.get();
    }

    public BooleanProperty availableProperty() {
        return available;
    }

    public boolean isLocked() {
        return locked.get();
    }

    public BooleanProperty lockedProperty() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked.set(locked);
    }

    public int[] getReq() {
        return req;
    }


}

