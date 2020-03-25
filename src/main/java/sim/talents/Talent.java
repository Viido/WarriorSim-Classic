package sim.talents;

import com.google.gson.annotations.SerializedName;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.ArrayList;

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
    private int x;
    private int y;
    @SerializedName(value = "iconname")
    private String img;

    private int points = 0;
    private int reqPoints;

    public Talent(int id, String name, int max, int[] spellIds, String[] descriptions, int x, int y, String img) {
        this.id = id;
        this.name = name;
        this.max = max;
        this.spellIds = spellIds;
        this.descriptions = descriptions;
        this.x = x;
        this.y = y;
        this.img = img;

        reqPoints = y * 5;
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

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getReqPoints() {
        return reqPoints;
    }

    public void setReqPoints(int reqPoints) {
        this.reqPoints = reqPoints;
    }

}

