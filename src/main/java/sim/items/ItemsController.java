package sim.items;

import com.jfoenix.controls.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.util.Callback;
import sim.main.CustomPopup;
import sim.data.SimDB;
import sim.warrior.Constants;
import sim.warrior.Warrior;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static sim.warrior.Constants.MAINHAND;

public class ItemsController implements Initializable {
    @FXML
    VBox leftItems;
    @FXML
    VBox rightItems;
    @FXML
    JFXListView<Item> itemSelect;
    JFXListView<Enchant> enchantSelect = new JFXListView<>();
    @FXML
    Label selectionName;
    HBox weaponFilters;
    @FXML
    HBox filtersMenu;

    ItemSlot[] itemSlots = new ItemSlot[17];

    Warrior warrior;
    JFXComboBox<String> xHandedWeapon = new JFXComboBox<>();
    JFXComboBox<String> weaponType = new JFXComboBox<>();
    Map<String, String> weaponTypes = new HashMap<>();

    public ItemsController(Warrior warrior){
        this.warrior = warrior;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initItemSlots();

        enchantSelect.getStylesheets().add(this.getClass().getResource("/sim/items/css/Items.css").toExternalForm());
        enchantSelect.getStyleClass().add("item-select");
        enchantSelect.setMinWidth(225);
        enchantSelect.setMaxWidth(225);

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
                            this.setOnMouseEntered(null);
                            this.setOnMouseExited(null);
                        } else {
                            setText(item.getName());
                            setTextFill(Paint.valueOf(item.getColor()));
                            itemTooltip.setItem(item);

                            if(item.getItemSetId() != 0){
                                if(warrior.getSetBonusControl(item.getItemSetId()) != null){
                                    itemTooltip.setItemSetIds(FXCollections.observableArrayList(warrior.getSetBonusControl(item.getItemSetId()).getActiveItems()));
                                }else{
                                    itemTooltip.setItemSetIds(null);
                                }
                            }

                            this.setOnMouseEntered(e -> {
                                itemTooltip.show(this, 260, 0);
                            });

                            this.setOnMouseExited(e -> {
                                itemTooltip.hide();
                            });
                        }
                    }
                };
            }
        });

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

        itemSelect.idProperty().addListener(((obs, oldValue, newValue) -> {
            selectionName.setText(getSlotName(newValue));
        }));

        itemSelect.setId("0");
        refreshItemSelect();

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

        weaponSlotFilters();
    }

    private void refreshSetBonuses(){
        for(ItemSlot itemSlot : itemSlots){
            if(itemSlot.getSelectedItem() != null){
                updateSetBonusTooltip(itemSlot.getSelectedItem());
            }
        }
    }

    private void initItemSlots() {
        String[] images = {
                "itemslot_head.jpg", "itemslot_neck.jpg", "itemslot_shoulder.jpg", "itemslot_chest.jpg", "itemslot_chest.jpg", "itemslot_wrists.jpg", "itemslot_mainhand.jpg",
                "itemslot_offhand.jpg", "itemslot_ranged.jpg", "itemslot_hands.jpg", "itemslot_waist.jpg", "itemslot_legs.jpg", "itemslot_feet.jpg", "itemslot_finger.jpg",
                "itemslot_finger.jpg", "itemslot_trinket.jpg", "itemslot_trinket.jpg"
        };

        CustomPopup enchantPopup = new CustomPopup();

        enchantPopup.getScene().setRoot(enchantSelect);

        for (int i = 0; i < 17; i++) {
            ItemSlot itemSlot = new ItemSlot(SimDB.ITEMS.getItemsBySlot(i), SimDB.ENCHANTS.getEnchantsBySlot(i), itemSelect, enchantSelect, images[i], i, warrior);
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

            enchantSelect.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
                enchantPopup.hide();
            });

            itemSlot.getItemButton().setOnMouseClicked(e -> {
                if(e.getButton().equals(MouseButton.PRIMARY)){
                    itemSelect.getSelectionModel().clearSelection();
                    itemSelect.setId(String.valueOf(itemSlot.getSlot()));
                    refreshItemSelect();
                }

                if(e.getButton().equals(MouseButton.SECONDARY)){
                    itemSlot.clearSelection();
                    itemSelect.refresh();
                }
            });

            itemSlot.getEnchantName().setOnMouseClicked(e -> {
                enchantSelect.setId(String.valueOf(itemSlot.getSlot()));
                itemSlot.setEnchantList();

                enchantPopup.show(itemSlot.getEnchantName(), 0, itemSlot.getEnchantName().getHeight());
            });
        }
    }

    public void refreshItemSelect(){
        List<Item> displayedItems;

        if(itemSelect.getId().equals("6")){
            if(xHandedWeapon.getSelectionModel().getSelectedIndex() == 0){
                displayedItems = SimDB.ITEMS.getMHOneHandedWeaponsByType(weaponTypes.get(weaponType.getSelectionModel().getSelectedItem()));
            }else{
                displayedItems = SimDB.ITEMS.getTwoHandedWeaponsByType(weaponTypes.get(weaponType.getSelectionModel().getSelectedItem()));
            }
        }else if(itemSelect.getId().equals("7")){
            displayedItems = SimDB.ITEMS.getOHItemsByType(weaponTypes.get(weaponType.getSelectionModel().getSelectedItem()));
        }else{
            displayedItems = itemSlots[Integer.parseInt(itemSelect.getId())].getItemList();
        }

        itemSelect.setItems(FXCollections.observableArrayList(displayedItems.stream()
                .filter(i -> i.getFaction() == null || i.getFaction().equals(warrior.getRace().getFaction()))
                .collect(Collectors.toList())));
    }

    private void weaponSlotFilters(){
        weaponFilters = new HBox();

        weaponTypes.put("Axes", "axe");
        weaponTypes.put("Daggers", "dagger");
        weaponTypes.put("Fist Weapons", "fist");
        weaponTypes.put("Maces", "mace");
        weaponTypes.put("Swords", "sword");
        weaponTypes.put("Shields", "shield");
        weaponTypes.put("Polearms", "polearm");
        weaponTypes.put("axe", "Axes");
        weaponTypes.put("dagger", "Daggers");
        weaponTypes.put("fist", "Fist Weapons");
        weaponTypes.put("mace", "Maces");
        weaponTypes.put("sword", "Swords");
        weaponTypes.put("shield", "Shields");
        weaponTypes.put("polearm", "Polearms");

        String[] mainHandOptions = {"One-Handed", "Two-Handed"};

        xHandedWeapon.setItems(FXCollections.observableArrayList(mainHandOptions));

        String[] oneHandTypeOptions = {"Axes", "Daggers", "Fist Weapons", "Maces", "Swords"};

        String[] twoHandTypeOptions = {"Axes", "Maces", "Polearms", "Swords"};

        String[] offHandOptions = {"Axes", "Daggers", "Fist Weapons", "Maces", "Swords", "Shields"};

        xHandedWeapon.getSelectionModel().selectedIndexProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue.intValue() == 0){
                weaponType.setItems(FXCollections.observableArrayList(oneHandTypeOptions));
                weaponType.getSelectionModel().select(0);
            }

            if(newValue.intValue() == 1){
                weaponType.setItems(FXCollections.observableArrayList(twoHandTypeOptions));
                weaponType.getSelectionModel().select(0);
            }
        });

        itemSelect.idProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue.equals("6") && !oldValue.equals(newValue)){
                filtersMenu.getChildren().clear();

                if(itemSlots[6].getSelectedItem() != null){
                    if(itemSlots[6].getSelectedItem().getSlot().equals("2h")){
                        xHandedWeapon.getSelectionModel().select(1);
                    }else{
                        xHandedWeapon.getSelectionModel().select(0);
                    }

                    weaponType.getSelectionModel().select(weaponTypes.get(itemSlots[6].getSelectedItem().getType()));
                }else{
                    weaponType.setItems(FXCollections.observableArrayList(oneHandTypeOptions));
                    xHandedWeapon.getSelectionModel().select(0);
                    weaponType.getSelectionModel().select(0);
                    Platform.runLater(() -> itemSelect.setItems(FXCollections.observableArrayList(SimDB.ITEMS.getMHOneHandedWeaponsByType("axe"))));
                }

                filtersMenu.getChildren().addAll(xHandedWeapon, weaponType);
            }

            if(newValue.equals("7") && !oldValue.equals(newValue)){
                filtersMenu.getChildren().clear();
                weaponType.setItems(FXCollections.observableArrayList(offHandOptions));

                if(itemSlots[7].getSelectedItem() != null){
                    weaponType.getSelectionModel().select(weaponTypes.get(itemSlots[7].getSelectedItem().getType()));
                }else{
                    weaponType.getSelectionModel().select(0);
                }

                filtersMenu.getChildren().add(weaponType);
            }

            if(!(newValue.equals("6") || newValue.equals("7"))){
                filtersMenu.getChildren().clear();
                filtersMenu.getChildren().add(selectionName);
            }
        });

        weaponType.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue != null){
                if(itemSelect.getId().equals("6")){
                    if(xHandedWeapon.getSelectionModel().getSelectedIndex() == 0){
                        itemSelect.setItems(FXCollections.observableArrayList(SimDB.ITEMS.getMHOneHandedWeaponsByType(weaponTypes.get(newValue))));

                    }

                    if(xHandedWeapon.getSelectionModel().getSelectedIndex() == 1){
                        itemSelect.setItems(FXCollections.observableArrayList(SimDB.ITEMS.getTwoHandedWeaponsByType(weaponTypes.get(newValue))));
                    }
                }

                if(itemSelect.getId().equals("7")){
                    itemSelect.setItems(FXCollections.observableArrayList(SimDB.ITEMS.getOHItemsByType(weaponTypes.get(newValue))));
                }
            }
        });

        filtersMenu.getChildren().add(weaponFilters);
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
