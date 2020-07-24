package sim.settings;

import javafx.fxml.FXML;

import java.util.List;
import java.util.stream.Collectors;

public class Auras {
    private List<Aura> auras;
    private List<Aura> buffs;
    private List<Aura> debuffs;

    public List<Aura> getAuras() {
        return auras;
    }

    public List<Aura> getBuffs() {
        return auras.stream().filter(x -> !x.getType().equals("debuff")).collect(Collectors.toList());
    }

    public List<Aura> getDebuffs() {
        return auras.stream().filter(x -> x.getType().equals("debuff")).collect(Collectors.toList());
    }
}
