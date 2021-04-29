package sim.rotation;

import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import sim.data.SimDB;
import sim.engine.Event;
import sim.settings.Settings;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class RotationController implements Initializable {
    @FXML
    VBox selectionBox;

    Settings settings;
    Map<Event.EventType, RotationControl> rotationControls = new HashMap<>();

    public RotationController(Settings settings){
        this.settings = settings;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if(settings.getRotationOptions().size() != 0){
            for(RotationOption rotationOption : settings.getRotationOptions().values()){
                RotationControl rotationControl = new RotationControl(rotationOption);

                rotationControls.put(rotationOption.getEvent(), rotationControl);

                if(rotationOption.isEnabled()){
                    addOption(rotationControl);
                }
            }
        }else{
            Gson gson = new Gson();
            RotationOption[] rotationOptions = gson.fromJson(new InputStreamReader(SimDB.class.getResourceAsStream("/sim/data/rotationOptions.json")), RotationOption[].class);

            for(RotationOption rotationOption : rotationOptions){
                RotationControl rotationControl = new RotationControl(rotationOption);

                settings.getRotationOptions().put(rotationOption.getEvent(), rotationOption);
                rotationControls.put(rotationOption.getEvent(), rotationControl);

                if(rotationOption.isEnabled()){
                    addOption(rotationControl);
                }
            }
        }
    }

    public void disableOption(Event.EventType eventType){
        RotationControl rotationControl = rotationControls.get(eventType);
        rotationControl.getRotationOption().setEnabled(false);

        removeOption(rotationControl);
    }

    public void enableOption(Event.EventType eventType){
        RotationControl rotationControl = rotationControls.get(eventType);
        rotationControl.getRotationOption().setEnabled(true);

        addOption(rotationControl);
    }

    private void removeOption(RotationControl rotationControl){
        selectionBox.getChildren().remove(rotationControl);
    }

    // Order of elements is preserved using RotationOption index field
    private void addOption(RotationControl rotationControl){
        if(selectionBox.getChildren().size() == 0){
            selectionBox.getChildren().add(rotationControl);
        }else{
            int i = 0;

            while(true){
                RotationControl current = (RotationControl) selectionBox.getChildren().get(i);

                if(rotationControl.getRotationOption().getIndex() <= current.getRotationOption().getIndex()){
                    selectionBox.getChildren().add(i, rotationControl);
                    break;
                }

                i++;

                if(i == selectionBox.getChildren().size()){
                    selectionBox.getChildren().add(rotationControl);
                    break;
                }
            }
        }
    }

    public void saveRotationOptions(){
        for(RotationControl rotationControl : rotationControls.values()){
            rotationControl.saveOption();
        }
    }
}
