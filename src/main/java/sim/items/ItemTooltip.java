package sim.items;

import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.*;
import javafx.stage.PopupWindow;
import sim.data.SimDB;
import sim.main.CustomPopup;

import java.util.List;

import static sim.warrior.Constants.COLOR_UNCOMMON;

// TODO change item class and stats to be able to be ordered, add missing fields to items.json for more blizzlike tooltips

public class ItemTooltip extends CustomPopup {
    private VBox container;
    private VBox itemSetContainer;
    private SimpleObjectProperty<Item> item = new SimpleObjectProperty<>();
    private SimpleListProperty<Integer> itemSetIds = new SimpleListProperty<>(FXCollections.observableArrayList());

    public ItemTooltip() {
        container = new VBox();
        itemSetContainer = new VBox();

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

        container.getChildren().add(itemSetContainer);

        itemProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue != oldValue && newValue != null){
                generateTooltip();
                if(newValue.getItemSetId() != 0){
                    addItemSetTooltip();
                }else{
                    itemSetContainer.getChildren().clear();
                }
            }
        });

        itemSetIds.addListener((obs, oldValue, newValue) -> {
            addItemSetTooltip();
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

                if (string.startsWith("Equip:") || string.startsWith("Use:") || string.startsWith("Chance on hit:")) {
                    label.setTextFill(Paint.valueOf(COLOR_UNCOMMON));
                }

                label.setText(string);

                container.getChildren().add(label);
            }
        }

        Label label = (Label) container.getChildren().get(0);
        label.setTextFill(Paint.valueOf(item.get().getColor()));

        container.getChildren().add(itemSetContainer);
    }

    private void addItemSetTooltip(){
        itemSetContainer.getChildren().clear();

        ItemSet itemSet = SimDB.ITEM_SETS.get(item.get().getItemSetId());

        Label setName = createText();
        setName.setText("\n" + itemSet.getName() + " (" + itemSetIds.size() + "/" + itemSet.getItemIds().size() + ")");
        setName.setTextFill(Paint.valueOf("#ffd100"));

        itemSetContainer.getChildren().add(setName);

        for(Integer i : itemSet.getItemIds()){
            Label itemName = createText();
            itemName.setText(SimDB.ITEMS.getItemById(i).getName());
            if(itemSetIds.contains(i)){
                itemName.setTextFill(Paint.valueOf("#e4e78f"));
            }else{
                itemName.setTextFill(Paint.valueOf("#9d9d9d"));
            }

            itemName.setPadding(new Insets(0, 0, 0, 5));

            itemSetContainer.getChildren().add(itemName);
        }

        for(int i = 0; i < itemSet.getSetBonuses().size(); i++){
            ItemSet.ItemSetBonus setBonus = itemSet.getSetBonuses().get(i);

            Label setBonusText = createText();
            if(itemSetIds.size() >= setBonus.getCount()){
                setBonusText.setTextFill(Paint.valueOf(COLOR_UNCOMMON));
            }else{
                setBonusText.setTextFill(Paint.valueOf("#9d9d9d"));
            }

            setBonusText.setText("(" + setBonus.getCount() + ") Set : " + setBonus.getDescription());
            if(i == 0){
                setBonusText.setText("\n" + setBonusText.getText());
            }

            itemSetContainer.getChildren().add(setBonusText);
        }
    }

    private Label createText(){
        Label text = new Label();
        text.setTextFill(Color.WHITE);
        text.setFont(new Font("Verdana", 12));
        text.autosize();
        text.setWrapText(true);

        return text;
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

    public void setItemSetIds(ObservableList<Integer> itemSetIds) {
        this.itemSetIds.set(itemSetIds);
    }
}

