package sim.talents;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

public class TalentArrow extends Canvas {
    private Talent talent1;
    private Talent talent2;

    Image arrow = new Image("images/talent/arrows/down.png");
    Image arrowGrey = new Image("images/talent/arrows/down_grey.png");

    public TalentArrow(Talent talent1, Talent talent2){
        setDisable(true);

        this.talent1 = talent1;
        this.talent2 = talent2;

        drawArrow(arrowGrey);

        talent2.availableProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue && talent1.getPoints() == talent1.getMax()){
                drawArrow(arrow);
            }else{
                drawArrow(arrowGrey);
            }
        });
    }

    private void drawArrow(Image image){
        clearCanvas();

        int startX = talent1.getCol() * 60 + 30;
        int startY = talent1.getRow() * 60 + 49;
        int endY = talent2.getRow() * 60 + 16;

        PixelReader reader = image.getPixelReader();

        this.setHeight(endY - startY);
        this.setWidth(15);
        this.setTranslateX(startX - 120);
        this.setTranslateY(startY - 210 + this.getHeight()/2);

        WritableImage bottom = new WritableImage(reader, 0, 0, (int)image.getWidth(), 10);
        WritableImage middle = new WritableImage(reader, 0, 10, (int)image.getWidth(), endY - startY - 20);
        WritableImage top = new WritableImage(reader, 0, (int)image.getHeight() - 10, (int)image.getWidth(), 10);

        GraphicsContext gc = this.getGraphicsContext2D();
        gc.drawImage(bottom, 0, 0);
        gc.drawImage(middle, 0, 10);
        gc.drawImage(top, 0, endY - startY - 10);
    }

    private void clearCanvas(){
        this.getGraphicsContext2D().clearRect(0, 0, this.getWidth(), this.getHeight());
    }
}
