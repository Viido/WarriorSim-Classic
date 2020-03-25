package sim.talents;

import com.google.gson.annotations.SerializedName;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.ArrayList;
import java.util.List;

public class TalentTree {
    @SerializedName(value = "n")
    private String name;
    @SerializedName(value = "t")
    private Talent[] talents;

    private int points = 0;

    public TalentTree(String name, Talent[] talents){
        this.name = name;
        this.talents = talents;
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

    public Talent getTalent(int x, int y){
        for (Talent t : talents){
            if(t.getX() == x && t.getY() == y){
                return t;
            }
        }

        return null;
    }

    public List<Talent> getTier(int y){
        List<Talent> res = new ArrayList<>();

        for(Talent t : talents){
            if(t.getY() == y){
                res.add(t);
            }
        }

        return res;
    }

    public void calcPoints(){
        points = 0;

        for(Talent t : talents){
            points += t.getPoints();
        }
    }

    public void addPoint(){
        points += 1;
    }
    public void removePoint(){
        points -= 1;
    }

}

