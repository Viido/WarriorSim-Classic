package sim.items;

import com.jfoenix.controls.JFXListView;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;
import sim.warrior.Warrior;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ItemSlot extends HBox {
    @FXML
    private JFXListView<Item> itemView;
    @FXML
    private ListView<Enchant> enchantView;
    private List<Item> itemList;
    private List<Enchant> enchantList;
    @FXML
    private Button itemButton;

    private Label itemName = new Label();
    private Label enchantName = new Label();
    private ItemTooltip tooltip = new ItemTooltip();

    private Warrior warrior;
    private String defaultImage;
    private int slot;

    private SimpleObjectProperty<Item> selectedItem = new SimpleObjectProperty<>();
    private SimpleObjectProperty<Enchant> selectedEnchant = new SimpleObjectProperty<>();

    public ItemSlot(List<Item> itemList, List<Enchant> enchantList, JFXListView<Item> itemView, ListView<Enchant> enchantView, String defaultImage, int slot, Warrior warrior){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/ItemSlot.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        this.itemList = itemList;
        this.enchantList = enchantList;
        this.itemView = itemView;
        this.enchantView = enchantView;
        this.slot = slot;
        this.warrior = warrior;
        this.defaultImage = defaultImage;
        this.setMaxSize(300, 42);
        this.setMinSize(300, 42);
        VBox itemInfo = new VBox();

        itemName.getStyleClass().clear();
        enchantName.getStyleClass().clear();

        enchantName.setStyle("");
        enchantName.setTextFill(Paint.valueOf("#1eff00"));

        if(enchantList != null){
            itemInfo.getChildren().addAll(itemName, enchantName);
        }else{
            itemInfo.getChildren().add(itemName);
        }
        
        if(slot < 9){
            this.getChildren().add(0, itemInfo);
            this.setAlignment(Pos.TOP_RIGHT);
            itemInfo.setAlignment(Pos.TOP_RIGHT);
            itemName.setStyle("-fx-padding: 0 5 0 0");
            itemName.setTextAlignment(TextAlignment.RIGHT);
            enchantName.setStyle("-fx-padding: 0 5 0 0");
            enchantName.setTextAlignment(TextAlignment.RIGHT);
        }else{
            this.getChildren().add(itemInfo);
            itemName.setStyle("-fx-padding: 0 0 0 5");
            enchantName.setStyle("-fx-padding: 0 0 0 5");
        }

        itemButton.setStyle("-fx-background-image: url(/images/itemslots/" + defaultImage + ")");
        itemView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != oldValue && newValue != null){
                if(itemView.getId().equals(String.valueOf(slot))){
                    setSelectedItem(newValue);
                }
            }

        });

        enchantView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if(enchantView.getId().equals(String.valueOf(slot)) && newValue != null){
                setSelectedEnchant(newValue);
            }
        }));
    }

    private List<Enchant> get1hEnchants(){
        List<Enchant> result = new ArrayList<>();

        for(Enchant enchant : enchantList){
            for(String slot : enchant.getSlot()){
                if(slot.equals("weapon")){
                    result.add(enchant);
                }
            }
        }

        return result;
    }

    private List<Enchant> getShieldEnchants(){
        List<Enchant> result = new ArrayList<>();

        for(Enchant enchant : enchantList){
            for(String slot : enchant.getSlot()){
                if(slot.equals("shield")){
                    result.add(enchant);
                }
            }
        }

        return result;
    }

    public void setEnchantList(){
        if(slot == 6 && getSelectedItem() != null){
            if(!getSelectedItem().getSlot().equals("2h")){
                enchantView.setItems(FXCollections.observableArrayList(get1hEnchants()));
            }else{
                enchantView.setItems(FXCollections.observableArrayList(enchantList));
            }
        }else if(slot == 7 && getSelectedItem() != null){
            if(getSelectedItem().getType().equals("shield")){
                enchantView.setItems(FXCollections.observableArrayList(getShieldEnchants()));
            }else{
                enchantView.setItems(FXCollections.observableArrayList(get1hEnchants()));
            }
        }else if(enchantList != null){
            enchantView.setItems(FXCollections.observableArrayList(enchantList));
        }
    }

    public void setSelectedItem(Item item){
        warrior.equipItem(slot, item);
        tooltip.setItem(item);
        selectedItem.set(item);
        itemButton.setStyle("-fx-background-image: url(/images/items/" + item.getIcon() + ".png)");
        itemName.setText(getSelectedItem().getName());
        itemName.setTextFill(Paint.valueOf(getSelectedItem().getColor()));


        itemButton.setOnMouseEntered(e -> tooltip.show(itemButton, 40, 0));
        itemButton.setOnMouseExited(e -> tooltip.hide());

        if(enchantName.getText().equals("")){
            enchantName.setText("No enchant");
        }
    }

    public void clearSelection(){
        if(getSelectedItem() != null){
            itemView.getSelectionModel().clearSelection();
            warrior.unequipItem(slot);
            removeTooltip();
            selectedItem.set(null);
            itemButton.setStyle("-fx-background-image: url(/images/itemslots/" + defaultImage + ")");
            itemName.setText("");
        }

        if(getSelectedEnchant() != null){
            enchantView.getSelectionModel().clearSelection();
            warrior.unequipEnchant(slot);
            selectedEnchant.set(null);
        }

        enchantName.setText("");
    }

    private void removeTooltip(){
        if(tooltip.isShowing()){
            tooltip.hide();
        }
        itemButton.setOnMouseEntered(null);
        itemButton.setOnMouseExited(null);
    }

    public void setSelectedEnchant(Enchant enchant){
        warrior.equipEnchant(slot, enchant);
        selectedEnchant.set(enchant);
        enchantName.setText(getSelectedEnchant().getDescription());
    }

    public void refresh(){
        if(warrior.getEquippedItems()[slot] != null){
            setSelectedItem(warrior.getEquippedItems()[slot]);
        }
        if(warrior.getEquippedEnchants()[slot] != null){
            setSelectedEnchant(warrior.getEquippedEnchants()[slot]);
        }
    }

    public Item getSelectedItem(){
        return selectedItem.get();
    }

    public SimpleObjectProperty<Item> selectedItemProperty() {
        return selectedItem;
    }

    public Enchant getSelectedEnchant() {
        return selectedEnchant.get();
    }

    public SimpleObjectProperty<Enchant> selectedEnchantProperty() {
        return selectedEnchant;
    }

    public ItemTooltip getTooltip(){
        return tooltip;
    }

    public Label getEnchantName() {
        return enchantName;
    }

    public int getSlot(){
        return slot;
    }

    public Button getItemButton() {
        return itemButton;
    }

    public List<Item> getItemList() {
        return itemList;
    }
}
