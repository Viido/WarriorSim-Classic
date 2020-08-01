package sim.items;

import com.google.gson.Gson;
import com.jfoenix.controls.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.util.Callback;
import javafx.util.Duration;
import sim.main.Warrior;

import java.io.*;
import java.net.URL;
import java.util.*;

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
                JFXTooltip tooltip = new JFXTooltip();
                tooltip.setPos(Pos.TOP_RIGHT);
                tooltip.setMargin(-30);
                tooltip.setShowDelay(Duration.ZERO);


                ListCell<Item> cell = new ListCell<>() {
                    @Override
                    protected void updateItem(Item item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item.getName());
                            setTextFill(Paint.valueOf(item.getColor()));

                            tooltip.setText(item.getTooltip());
                        } else {
                            setText("");
                        }
                    }
                };

                cell.setOnMouseEntered(e -> tooltip.show(cell, 0, 0));
                cell.setOnMouseExited(e -> tooltip.hide());

                return cell;
            }
        });

        enchantSelect.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Enchant> call(ListView<Enchant> param) {
                ListCell<Enchant> cell = new ListCell<>(){
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
                return cell;
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
    }

    private void initItems(){
        Gson gson = new Gson();
        items = gson.fromJson(new InputStreamReader(getClass().getResourceAsStream("data/items.json")), Items.class);
        enchants = gson.fromJson(new InputStreamReader(getClass().getResourceAsStream("data/enchants.json")), Enchants.class);
    }

    private void initItemSlots(){

        String[] images = {
                "itemslot_head.jpg", "itemslot_neck.jpg", "itemslot_shoulder.jpg", "itemslot_chest.jpg", "itemslot_chest.jpg", "itemslot_wrists.jpg", "itemslot_mainhand.jpg",
                "itemslot_offhand.jpg", "itemslot_ranged.jpg","itemslot_hands.jpg", "itemslot_waist.jpg", "itemslot_legs.jpg", "itemslot_feet.jpg", "itemslot_finger.jpg",
                "itemslot_finger.jpg", "itemslot_trinket.jpg", "itemslot_trinket.jpg"
                };

        for(int i = 0; i < 17; i++){
            ItemSlot itemSlot = new ItemSlot(items.getItemsBySlot(i), enchants.getEnchantsBySlot(i), itemSelect, enchantSelect, images[i], i, warrior);
            itemSlots[i] = itemSlot;
            itemSlot.refresh();

            if(i < 9){
                leftItems.getChildren().add(itemSlot);
            }else{
                rightItems.getChildren().add(itemSlot);
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


}
