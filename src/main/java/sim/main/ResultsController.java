package sim.main;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import sim.engine.AttackTable;
import sim.engine.FightResult;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ResultsController implements Initializable {
    private FightResult fightResult;

    @FXML
    PieChart attackTable;

    public ResultsController(FightResult fightResult) {
        this.fightResult = fightResult;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        attackTable.setTitle("White Attacks");

        List<PieChart.Data> data = new ArrayList<>();

        /*for(AttackTable.RollType rollType : fightResult.getWhiteMHTable().keySet()){
            data.add(new PieChart.Data(rollType.toString() + " (" + fightResult.getWhiteMHTable().get(rollType)/fightResult.getWhiteMHSwingCount() * 100 + "%)", fightResult.getWhiteMHTable().get(rollType)));
        }*/

        attackTable.setData(FXCollections.observableArrayList(data));
        attackTable.setStyle("-fx-text-fill: white");
        attackTable.setLegendVisible(false);
    }

    public FightResult getFightResult() {
        return fightResult;
    }

    public void setFightResult(FightResult fightResult) {
        this.fightResult = fightResult;
    }
}
