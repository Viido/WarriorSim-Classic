package sim.items;

import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PopupControl;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.*;
import javafx.stage.PopupWindow;

import java.util.List;

import static sim.warrior.Constants.COLOR_UNCOMMON;

public class ItemTooltip extends PopupControl {
    private VBox container;
    private SimpleObjectProperty<Item> item = new SimpleObjectProperty<>();

    public ItemTooltip() {
        container = new VBox();

        Border border = new Border(new BorderImage(new Image(getClass().getResource("/images/tooltip.png").toExternalForm()),
                new BorderWidths(6),
                new Insets(0),
                new BorderWidths(6, 6, 6, 6),
                true,
                BorderRepeat.STRETCH,
                BorderRepeat.STRETCH));

        container.setBorder(border);
        container.setMaxWidth(320);
        container.setPadding(new Insets(0, 3, 3, 3));

        this.setAnchorLocation(PopupWindow.AnchorLocation.CONTENT_BOTTOM_LEFT);
        this.getScene().setRoot(container);

        itemProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue != oldValue && newValue != null){
                generateTooltip();
            }
        });
    }

    private void generateTooltip(){
        container.getChildren().clear();

        List<String> strings = item.get().getTooltip();

        for (String string : strings) {
            if (string.contains("\t")) {
                HBox hBox = new HBox();
                Region region = new Region();
                HBox.setHgrow(region, Priority.ALWAYS);

                String[] labels = string.split("\t");

                Label label1 = createText();
                label1.setText(labels[0]);

                Label label2 = createText();
                label2.setText(labels[1]);
                label2.setPadding(new Insets(0, 0, 0, 48));

                hBox.getChildren().addAll(label1, region, label2);

                container.getChildren().add(hBox);
            } else {
                Label label = createText();

                if (string.startsWith("Equip:")) {
                    label.setTextFill(Paint.valueOf(COLOR_UNCOMMON));
                }

                label.setText(string);

                container.getChildren().add(label);
            }
        }

        Label label = (Label) container.getChildren().get(0);
        label.setTextFill(Paint.valueOf(item.get().getColor()));
    }

    private Label createText(){
        Label text = new Label();
        text.setTextFill(Color.WHITE);
        text.setFont(new Font("Verdana", 12));
        text.autosize();
        text.setWrapText(true);

        return text;
    }

    public void show(Node ownerNode, double offsetX, double offsetY){
        super.show(ownerNode,
                ownerNode.localToScene(0, 0).getX() + ownerNode.getScene().getX() + ownerNode.getScene().getWindow().getX() + offsetX,
                ownerNode.localToScene(0, 0).getY() + ownerNode.getScene().getY() + ownerNode.getScene().getWindow().getY() + offsetY);
    }

    public Item getItem() {
        return item.get();
    }

    public SimpleObjectProperty<Item> itemProperty() {
        return item;
    }

    public void setItem(Item item) {
        this.item.set(item);
    }
}

