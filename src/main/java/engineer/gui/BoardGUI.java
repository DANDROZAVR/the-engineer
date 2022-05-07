package engineer.gui;

import engineer.engine.board.exceptions.IndexOutOfBoardException;
import engineer.engine.board.logic.Board;
import javafx.animation.AnimationTimer;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static javafx.scene.input.KeyCode.PLUS;

public class BoardGUI {
    private final Scene scene;
    private final Canvas canvas;
    private final Pane pane;
    private final Board board;
    private final TextureManager textureManager;
    private double fieldWidth, fieldHeight;
    boolean pMovingLeft, pMovingRight, pMovingUp, pMovingDown; // player's camera moves
    final int speed = 1000; // player's camera pixels per sec
    final int paddingWidth, paddingHeight;
    int actualMapWidth;
    int actualMapHeight;
    Rectangle cam;
    boolean pIncreaseFieldsSize;
    boolean pDecreaseFieldsSize;
    public BoardGUI(Scene scene, Board board, TextureManager textureManager, int fieldWidth, int fieldHeight,
             int paddingWidth, int paddingHeight) {
        pMovingLeft = pMovingRight = pMovingUp = pMovingDown = pIncreaseFieldsSize = pDecreaseFieldsSize = false;
        this.paddingWidth = paddingWidth;
        this.paddingHeight = paddingHeight;
        this.actualMapWidth = paddingWidth * 2 + fieldWidth * board.getWidth();
        this.actualMapHeight = paddingHeight * 2 + fieldHeight * board.getHeight();
        this.scene = scene;
        this.board = board;
        this.textureManager = textureManager;
        this.pane = extractPane(scene);
        this.canvas = extractCanvas(this.pane);
        assert canvas != null;
        this.fieldWidth = fieldWidth;
        this.fieldHeight = fieldHeight;
        this.cam = new Rectangle(0, 0, scene.getWidth(), scene.getHeight());
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(scene.widthProperty());
        clip.heightProperty().bind(scene.heightProperty());
        clip.xProperty().bind(Bindings.createDoubleBinding(
                () -> fixedCamView(cam.getX(), 0, pane.getWidth()),
                cam.xProperty(), scene.widthProperty()));
        clip.yProperty().bind(Bindings.createDoubleBinding(
                () -> fixedCamView(cam.getY(), 0, pane.getHeight()),
                cam.yProperty(), scene.heightProperty()));
        canvas.setClip(clip);
        pane.translateXProperty().bind(clip.xProperty().multiply(-1));
        pane.translateYProperty().bind(clip.yProperty().multiply(-1));

        scene.setOnKeyPressed(e -> processKey(e.getCode(), true));
        scene.setOnKeyReleased(e -> processKey(e.getCode(), false));
        AnimationTimer timer = new AnimationTimer() {
            private long lastFrame = -666;
            @Override
            public void handle(long time) {
                double seconds = (time - lastFrame) / 1_000_000_000.0;
                double deltaX = 0, deltaY = 0;
                if (pMovingDown) deltaY += speed * seconds;
                if (pMovingUp) deltaY -= speed * seconds;
                if (pMovingRight) deltaX += speed * seconds;
                if (pMovingLeft) deltaX -= speed * seconds;
                if (pIncreaseFieldsSize) {
                    increaseFieldsSize();
                    pIncreaseFieldsSize = false;
                }
                if (pDecreaseFieldsSize) {
                    decreaseFieldsSize();
                    pDecreaseFieldsSize = false;
                }
                cam.setX(fixedCamView(cam.getX() + deltaX, 0, actualMapWidth - cam.getWidth()));
                cam.setY(fixedCamView(cam.getY() + deltaY, 0, actualMapHeight - cam.getHeight()));
                lastFrame = time;

            }
        };
        draw();
        timer.start();

    }
    private double fixedCamView(double val, double min, double max) {
        if (val < min) return min;
        return Math.min(val, max);
    }
    public void onFieldChange(int row, int column) {
        drawField(canvas.getGraphicsContext2D(), row, column);
    }
    public void drawField(GraphicsContext gc, int row, int column) {
        String background;
        try {
            background = board.getField(row, column).getBackground();
        } catch (IndexOutOfBoardException e) {
            e.printStackTrace();
            return;
        }
        //textureManager.getImageField(background); TODO
        //canvas.drawImage() TODO
        gc.setFill(Color.valueOf(background)); // TEMP
        gc.fillRect(row * fieldWidth + paddingWidth, column * fieldHeight + paddingHeight, fieldWidth, fieldHeight); // TEMP
    }
    private void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        for (int i = 0; i < board.getWidth(); ++i)
            for (int j = 0; j < board.getHeight(); ++j) {
                drawField(gc, i, j);
            }
    }
    private Canvas extractCanvas(Pane pane) {
        ObservableList <?> lst = pane.getChildren();
        for (Object obj: lst)
            if (obj instanceof Canvas) {
                return (Canvas) obj;
            }
        return null;
    }
    private Pane extractPane(Scene scene) {
        return ((Pane) ((BorderPane)scene.getRoot()).getChildren().get(0));
    }
    private void processKey(KeyCode code, boolean onMove) {
        System.out.println(code);
        System.out.println(code == PLUS);
        switch (code) {
            case LEFT -> pMovingLeft = onMove;
            case RIGHT -> pMovingRight = onMove;
            case UP -> pMovingUp = onMove;
            case DOWN -> pMovingDown = onMove;
            case PLUS,ADD -> pIncreaseFieldsSize = onMove;
            case MINUS,SUBTRACT -> pDecreaseFieldsSize = onMove;
            default -> {
            }
        }
    }
    private void increaseFieldsSize() {
        fieldWidth *= 1.1;
        fieldHeight *= 1.1;
        actualMapWidth = (int)(paddingWidth * 2 + fieldWidth * board.getWidth());
        actualMapHeight = (int)(paddingHeight * 2 + fieldHeight * board.getHeight());
        try {
            draw();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void decreaseFieldsSize() {
        fieldWidth /= 1.1;
        fieldHeight /= 1.1;
        actualMapWidth = (int)(paddingWidth * 2 + fieldWidth * board.getWidth());
        actualMapHeight = (int)(paddingHeight * 2 + fieldHeight * board.getHeight());
        try {
            draw();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
