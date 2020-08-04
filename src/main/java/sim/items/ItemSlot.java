package sim.items;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTooltip;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import sim.warrior.Warrior;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ItemSlot extends HBox {
    @FXML
    JFXListView<Item> itemView;
    @FXML
    JFXListView<Enchant> enchantView;
    List<Item> itemList;
    List<Enchant> enchantList;
    @FXML
    Button itemSlot;
    private int id;
    private Item selectedItem;
    private Enchant selectedEnchant;
    private Warrior warrior;

    Label itemName = new Label();
    Label enchantName = new Label();
    JFXTooltip tooltip = new JFXTooltip();

    public ItemSlot(List<Item> itemList, List<Enchant> enchantList, JFXListView<Item> itemView, JFXListView<Enchant> enchantView, String defaultImage, int id, Warrior warrior){
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
        this.id = id;
        this.warrior = warrior;
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
        
        if(id < 9){
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


        itemSlot.setOnMouseClicked(e -> {
            itemView.getSelectionModel().clearSelection();
            itemView.setId(String.valueOf(id));
            itemView.setItems(FXCollections.observableArrayList(itemList));

            enchantView.setId(String.valueOf(id));
            setEnchantList();
        });

        tooltip.setPos(Pos.TOP_RIGHT);
        tooltip.setMargin(-30);

        tooltip.setShowDelay(Duration.ZERO);

        itemSlot.setStyle("-fx-background-image: url(/images/itemslots/" + defaultImage + ")");
        itemView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != oldValue && newValue != null){
                if(itemView.getId().equals(String.valueOf(id))){
                    setSelectedItem(newValue);
                    warrior.equipItem(id, selectedItem);

                    setEnchantList();
                }
            }

        });

        enchantView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if(enchantView.getId().equals(String.valueOf(id)) && newValue != null){
                setSelectedEnchant(newValue);
                warrior.equipEnchant(id, selectedEnchant);
            }
        }));
    }

    public Item getSelectedItem(){
        return selectedItem;
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

    private void setEnchantList(){
        enchantView.getSelectionModel().clearSelection();
        enchantView.getItems().clear();

        if(id == 6 && selectedItem != null){
            if(!selectedItem.getSlot().equals("2h")){
                enchantView.setItems(FXCollections.observableArrayList(get1hEnchants()));
            }else{
                enchantView.setItems(FXCollections.observableArrayList(enchantList));
            }
        }else if(id == 7 && selectedItem != null){
            if(selectedItem.getType().equals("shield")){
                enchantView.setItems(FXCollections.observableArrayList(getShieldEnchants()));
            }else{
                enchantView.setItems(FXCollections.observableArrayList(get1hEnchants()));
            }
        }else if(enchantList != null){
            enchantView.setItems(FXCollections.observableArrayList(enchantList));
        }
    }

    public void setSelectedItem(Item item){
        selectedItem = item;
        itemSlot.setStyle("-fx-background-image: url(/images/items/" + item.getIcon() + ".png)");
        itemName.setText(selectedItem.getName());
        itemName.setTextFill(Paint.valueOf(selectedItem.getColor()));

        itemSlot.setOnMouseEntered(e -> tooltip.show(this, 0, 0));
        itemSlot.setOnMouseExited(e -> tooltip.hide());
        tooltip.setText(selectedItem.getTooltip());

        if(enchantName.getText().equals("")){
            enchantName.setText("No enchant");
        }
    }

    public void setSelectedEnchant(Enchant enchant){
        selectedEnchant = enchant;
        enchantName.setText(selectedEnchant.getDescription());
    }

    public void refresh(){
        if(warrior.getEquippedItems()[id] != null){
            setSelectedItem(warrior.getEquippedItems()[id]);

        }
        if(warrior.getEquippedEnchants()[id] != null){
            setSelectedEnchant(warrior.getEquippedEnchants()[id]);
        }
    }
}
