package sim.talents;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class TalentsController implements Initializable {
    @FXML
    Button button1;
    @FXML
    Button button2;
    @FXML
    GridPane armsTree;
    @FXML
    GridPane furyTree;
    @FXML
    GridPane protTree;
    @FXML
    StackPane sp1;
    @FXML
    Canvas canvas;

    TalentTree[] talentTrees;
    HashMap<Talent, TalentButton> talentButtons = new HashMap<>();

    private IntegerProperty armsPoints = new SimpleIntegerProperty(0);
    private IntegerProperty furyPoints = new SimpleIntegerProperty(0);
    private IntegerProperty protPoints = new SimpleIntegerProperty(0);
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addTalents();
        drawArrow(2, 0, 2, 2);

        armsPoints.addListener(((observable, oldValue, newValue) -> {
            if(oldValue.intValue() < newValue.intValue()){
                if(newValue.intValue() % 5 == 0){
                    for (Talent talent : talentTrees[0].getTier(newValue.intValue() / 5)) {
                        talentButtons.get(talent).setAvailable();
                    }
                }
            }else{

                for (Talent talent : talentTrees[0].getTier((int)Math.ceil(oldValue.doubleValue() / 5))) {
                    talentButtons.get(talent).setUnavailable();
                }

            }

        }));
    }

    public void addTalents(){
        Gson gson = new Gson();
        talentTrees = gson.fromJson(new InputStreamReader(getClass().getResourceAsStream("data/talents.json")), TalentTree[].class);

        GridPane[] gridPanes = {armsTree, furyTree, protTree};

        for(int i = 0; i < talentTrees.length; i++){
            Talent[] talents = talentTrees[i].getTalents();

            for(int j = 0; j < talents.length; j++){

                TalentButton tb = new TalentButton(talents[j], this);
                talentButtons.put(talents[j], tb);
                gridPanes[i].add(tb, talents[j].getX(), talents[j].getY());
            }
        }
    }

    private void drawArrow(int x1, int y1, int x2, int y2){
        Image arrow = new Image("images/talent/arrows/down.png");
        PixelReader reader = arrow.getPixelReader();

        int startX = x1 * 60 + 30;
        int startY = y1 * 60 + 49;
        int endY = y2 * 60 + 16;

        WritableImage bottom = new WritableImage(reader, 0, 0, (int)arrow.getWidth(), 10);
        WritableImage middle = new WritableImage(reader, 0, 10, (int)arrow.getWidth(), endY - startY - 20);
        WritableImage top = new WritableImage(reader, 0, (int)arrow.getHeight() - 10, (int)arrow.getWidth(), 10);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.drawImage(bottom, startX-7.5, startY);
        gc.drawImage(middle, startX-7.5, startY + 10);
        gc.drawImage(top, startX-7.5, endY - 10);

    }

    public void armsTreeAddPoint(){
        armsPoints.set(armsPoints.getValue() + 1);
    }
    public void armsTreeRemovePoint(){
        armsPoints.set(armsPoints.getValue() - 1);
    }
}

