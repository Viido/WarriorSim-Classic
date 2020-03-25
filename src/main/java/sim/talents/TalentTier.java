package sim.talents;

public class TalentTier {
    private int tier;
    private int points = 0;
    private TalentTier next = null;
    private TalentTier prev = null;

    public int getTier() {
        return tier;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public TalentTier getNext() {
        return next;
    }

    public void setNext(TalentTier next) {
        this.next = next;
    }

    public TalentTier getPrev() {
        return prev;
    }

    public void setPrev(TalentTier prev) {
        this.prev = prev;
    }

    public TalentTier(int tier){
        this.tier = tier;
    }
}

