package sim.main;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.PopupControl;

public class CustomPopup extends PopupControl {

    public CustomPopup(){
        setAutoHide(true);
    }

    public void show(Node ownerNode, double offsetX, double offsetY){
        super.show(ownerNode,
                ownerNode.localToScene(0, 0).getX() + ownerNode.getScene().getX() + ownerNode.getScene().getWindow().getX() + offsetX,
                ownerNode.localToScene(0, 0).getY() + ownerNode.getScene().getY() + ownerNode.getScene().getWindow().getY() + offsetY);
    }

    public void setContent(Parent node){
        getScene().setRoot(node);
    }
}
