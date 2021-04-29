package sim.rotation;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class RotationControl extends HBox {
    JFXCheckBox checkBox = new JFXCheckBox();
    JFXTextField input = new JFXTextField();
    RotationOption rotationOption;

    public RotationControl(RotationOption rotationOption){
        this.rotationOption = rotationOption;

        this.getStylesheets().add(getClass().getResource("css/Rotation.css").toExternalForm());
        this.setMaxWidth(500);
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(5.0);

        Label label = new Label();
        label.setGraphic(new ImageView(new Image("images/icons/" + rotationOption.getImage() + ".jpg")));
        label.setText(rotationOption.getName());
        label.setMinWidth(150);

        this.getChildren().addAll(checkBox, label);

        checkBox.setSelected(rotationOption.isSelected());

        if(rotationOption.getType() == RotationOption.ThresholdType.RAGE_ABOVE){
            this.getChildren().add(new Label("Use when above"));
            this.getChildren().add(input);
            input.setText(rotationOption.getRageThreshold() + "");
            this.getChildren().add(new Label("rage."));
        }

        if(rotationOption.getType() == RotationOption.ThresholdType.RAGE_BELOW){
            this.getChildren().add(new Label("Use when below"));
            this.getChildren().add(input);
            input.setText(rotationOption.getRageThreshold() + "");
            this.getChildren().add(new Label("rage."));
        }

        if(rotationOption.getType() == RotationOption.ThresholdType.TIME_REMAINING){
            this.getChildren().add(new Label("Use when fight has"));
            this.getChildren().add(input);
            input.setText(rotationOption.getTimeThreshold() + "");
            this.getChildren().add(new Label("seconds remaining."));
        }

        if(rotationOption.getType() == RotationOption.ThresholdType.EXECUTE_PHASE){
            this.getChildren().add(new Label("Use during execute phase."));
        }
    }

    public RotationOption getRotationOption() {
        return rotationOption;
    }

    public void saveOption(){
        rotationOption.setSelected(checkBox.isSelected());

        if(rotationOption.getType() == RotationOption.ThresholdType.RAGE_ABOVE || rotationOption.getType() == RotationOption.ThresholdType.RAGE_BELOW){
            rotationOption.setRageThreshold(Integer.parseInt(input.getText()));
        }

        if(rotationOption.getType() == RotationOption.ThresholdType.TIME_REMAINING){
            rotationOption.setTimeThreshold(Integer.parseInt(input.getText()));
        }
    }

    public String toString(){
        return rotationOption.getName() + " " + super.toString();
    }
}
