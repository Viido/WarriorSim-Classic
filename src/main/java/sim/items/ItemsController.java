package sim.items;

import com.google.gson.Gson;
import com.jfoenix.controls.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.util.Callback;
import sim.warrior.Constants;
import sim.warrior.Warrior;

import java.io.*;
import java.net.URL;
import java.util.*;
import static sim.warrior.Constants.MAINHAND;

public class ItemsController implements Initializable {
    Items items;
    Enchants enchants;
    @FXML
    VBox leftItems;
    @FXML
    VBox rightItems;
    @FXML
    JFXListView<Item> itemSelect;
    @FXML
    JFXListView<Enchant> enchantSelect;
    @FXML
    JFXTabPane tabPane;

    ItemSlot[] itemSlots = new ItemSlot[17];

    Warrior warrior;

    // TODO faction restricted items, item filters and sorting options, set bonuses
    public ItemsController(Warrior warrior){
        this.warrior = warrior;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initItems();
        initItemSlots();

        itemSelect.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Item> call(ListView<Item> param) {
                ItemTooltip itemTooltip = new ItemTooltip();

                return new ListCell<>() {
                    @Override
                    protected void updateItem(Item item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty || item == null) {
                            setText(null);
                            setTextFill(null);
                        } else {
                            setText(item.getName());
                            setTextFill(Paint.valueOf(item.getColor()));
                            itemTooltip.setItem(item);
                        }

                        this.setOnMouseEntered(e -> {
                            itemTooltip.show(this, 260, 0);
                        });

                        this.setOnMouseExited(e -> {
                            itemTooltip.hide();
                        });
                    }
                };
            }
        });

        itemSelect.setItems(FXCollections.observableArrayList(items.getHead()));
        itemSelect.setId("0");

        enchantSelect.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Enchant> call(ListView<Enchant> param) {
                return new ListCell<>(){
                    @Override
                    protected void updateItem(Enchant enchant, boolean empty){
                        super.updateItem(enchant, empty);
                        if(enchant != null){
                            setText(enchant.getName());
                            setTextFill(Paint.valueOf(enchant.getColor()));
                        }else{
                            setText("");
                        }
                    }
                };
            }
        });

        enchantSelect.setItems(FXCollections.observableArrayList(enchants.getHeadEnchants()));
        enchantSelect.setId("0");

        enchantSelect.idProperty().addListener((obs, oldValue, newValue) -> {
            if(tabPane.getSelectionModel().getSelectedIndex() == 1){
                tabPane.getSelectionModel().select(0);
            }
        });

        itemSelect.idProperty().addListener(((obs, oldValue, newValue) -> {
            tabPane.getTabs().get(0).setText(getSlotName(newValue));

            if(enchants.getEnchantsBySlot(Integer.parseInt(newValue)) == null){
                if(tabPane.getTabs().size() > 1){
                    tabPane.getTabs().remove(1);
                }
            }else{
                if(tabPane.getTabs().size() < 2){
                    Tab tab = new Tab("Enchants");
                    tab.setContent(enchantSelect);
                    tabPane.getTabs().add(tab);
                }
            }
        }));

        itemSlots[MAINHAND].selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue != null){
                if(newValue.getSlot().equals("2h")){
                    if(itemSlots[Constants.OFFHAND].getSelectedItem() != null){
                        Platform.runLater(() -> itemSlots[Constants.OFFHAND].clearSelection());
                    }
                }
            }
        });

        itemSlots[Constants.OFFHAND].selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue != null){
                if(itemSlots[MAINHAND].getSelectedItem() != null){
                    if(itemSlots[MAINHAND].getSelectedItem().getSlot().equals("2h")){
                        Platform.runLater(() -> itemSlots[MAINHAND].clearSelection());
                    }
                }
            }
        });

        refreshSetBonuses();
    }

    private void refreshSetBonuses(){
        for(ItemSlot itemSlot : itemSlots){
            if(itemSlot.getSelectedItem() != null){
                updateSetBonusTooltip(itemSlot.getSelectedItem());
            }
        }
    }

    private void initItems(){
        Gson gson = new Gson();
        items = gson.fromJson(new InputStreamReader(getClass().getResourceAsStream("data/items.json")), Items.class);
        enchants = gson.fromJson(new InputStreamReader(getClass().getResourceAsStream("data/enchants.json")), Enchants.class);
    }

    private void initItemSlots() {

        String[] images = {
                "itemslot_head.jpg", "itemslot_neck.jpg", "itemslot_shoulder.jpg", "itemslot_chest.jpg", "itemslot_chest.jpg", "itemslot_wrists.jpg", "itemslot_mainhand.jpg",
                "itemslot_offhand.jpg", "itemslot_ranged.jpg", "itemslot_hands.jpg", "itemslot_waist.jpg", "itemslot_legs.jpg", "itemslot_feet.jpg", "itemslot_finger.jpg",
                "itemslot_finger.jpg", "itemslot_trinket.jpg", "itemslot_trinket.jpg"
        };

        for (int i = 0; i < 17; i++) {
            ItemSlot itemSlot = new ItemSlot(items.getItemsBySlot(i), enchants.getEnchantsBySlot(i), itemSelect, enchantSelect, images[i], i, warrior);
            itemSlots[i] = itemSlot;

            itemSlot.refresh();

            if (i < 9) {
                leftItems.getChildren().add(itemSlot);
            } else {
                rightItems.getChildren().add(itemSlot);
            }

            itemSlot.selectedItemProperty().addListener((obs, oldValue, newValue) -> {
                if(newValue != null){
                    updateSetBonusTooltip(newValue);
                }

                if(oldValue != null){
                    updateSetBonusTooltip(oldValue);
                }
            });
        }
    }

    private void updateSetBonusTooltip(Item item){
        if(item.getItemSetId() != 0){
            for(ItemSlot itemSlot1 : itemSlots){
                if(itemSlot1 != null){
                    if(itemSlot1.getSelectedItem() != null){
                        if(itemSlot1.getSelectedItem().getItemSetId() == item.getItemSetId()){
                            itemSlot1.getTooltip().setItemSetIds(FXCollections.observableArrayList(warrior.getSetBonusControl(item.getItemSetId()).getActiveItems()));
                        }
                    }
                }
            }
        }
    }

    private String getSlotName(String id){
        switch(id){
            case "0":
                return "Head";
            case "1":
                return "Neck";
            case "2":
                return "Shoulder";
            case "3":
                return "Back";
            case "4":
                return "Chest";
            case "5":
                return "Wrist";
            case "6":
                return "Main Hand";
            case "7":
                return "Off Hand";
            case "8":
                return "Ranged";
            case "9":
                return "Hands";
            case "10":
                return "Waist";
            case "11":
                return "Legs";
            case "12":
                return "Feet";
            case "13":
            case "14":
                return "Rings";
            case "15":
            case "16":
                return "Trinkets";
        }

        return "";
    }

    public ItemSlot[] getItemSlots(){
        return itemSlots;
    }
}
