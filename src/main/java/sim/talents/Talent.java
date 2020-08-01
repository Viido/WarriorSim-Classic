package sim.talents;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Talent implements Serializable {
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
    @SerializedName(value = "t")
    private int tree;
    @SerializedName(value = "x")
    private int col;
    @SerializedName(value = "y")
    private int row;
    @SerializedName(value = "r")
    private int req;
    @SerializedName(value = "iconname")
    private String img;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getMax() {
        return max;
    }

    public int[] getSpellIds() {
        return spellIds;
    }

    public String[] getDescriptions() {
        return descriptions;
    }

    public int getTree() {
        return tree;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public int getReq() {
        return req;
    }

    public String getImg() {
        return img;
    }
}

