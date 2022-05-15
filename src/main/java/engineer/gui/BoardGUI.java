package engineer.gui;

import engineer.engine.board.logic.Field;
import engineer.engine.board.presenter.BoardPresenter;
import javafx.animation.AnimationTimer;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Objects;

public class BoardGUI {
    private final Scene scene;
    private final Canvas canvas;
    private final Pane pane;
    private final TextureManager textureManager;
    private final BoardPresenter boardPresenter;
    private boolean pMovingLeft, pMovingRight, pMovingUp, pMovingDown, pIncreaseFieldsSize, pDecreaseFieldsSize; // player's camera moves
    private final int speed = 1000; // player's camera pixels per sec
    private double paddingWidth, paddingHeight;

    public BoardGUI(Scene scene, TextureManager textureManager, BoardPresenter boardPresenter,
                    double paddingWidth, double paddingHeight) {
        pMovingLeft = pMovingRight = pMovingUp = pMovingDown = pIncreaseFieldsSize = pDecreaseFieldsSize = false;
        this.boardPresenter = boardPresenter;
        this.scene = scene;
        this.textureManager = textureManager;
        this.pane = extractPane(scene);
        this.canvas = extractCanvas(this.pane);
        configFieldsSizes(paddingWidth, paddingHeight);
        configPresenter();
        configClip();
        configFrames();
        scene.setOnKeyPressed(e -> processKey(e.getCode(), true));
        scene.setOnKeyReleased(e -> processKey(e.getCode(), false));
    }

    public void drawField(double x, double y, double width, double height, Field field) {
        String background = field.getBackground();
        canvas.getGraphicsContext2D().setFill(Color.valueOf(background)); // TEMP
        canvas.getGraphicsContext2D().fillRect(x + paddingWidth, y + paddingHeight, width, height); // TEMP
    }

    private void configFrames() {
        int frameNs = (int) (1_000_000_000.0 / 60.03); // TEMP. Should be dynamically calculated later
        //Fix timing to next screen frame
        // Animate
        AnimationTimer timer = new AnimationTimer() {
            long lastFrame = -1;
            double lastCamWidth = -1, lastCamHeight = -1;

            @Override
            public void handle(long time) {
                if (time <= lastFrame)
                    return;
                if (lastCamWidth != scene.getWidth()) {
                    setPresenterCameraFromScene();
                    configClip();
                    lastCamWidth = boardPresenter.getCameraWidth();
                }
                if (lastCamHeight != scene.getHeight()) {
                    setPresenterCameraFromScene();
                    configClip();
                    lastCamHeight = boardPresenter.getCameraHeight();
                }

                long rest = time % frameNs;
                long nextFrame = time;
                if (rest != 0) //Fix timing to next screen frame
                    nextFrame += frameNs - rest;
                // Animate
                double seconds = (nextFrame - lastFrame) / 1_000_000_000.0;
                double deltaX = 0, deltaY = 0;
                if (pMovingDown) deltaY += speed * seconds;
                if (pMovingUp) deltaY -= speed * seconds;
                if (pMovingRight) deltaX += speed * seconds;
                if (pMovingLeft) deltaX -= speed * seconds;
                if (pIncreaseFieldsSize) {
                    boardPresenter.increaseFieldsSize();
                    pIncreaseFieldsSize = false;
                }
                if (pDecreaseFieldsSize) {
                    boardPresenter.decreaseFieldsSize();
                    pDecreaseFieldsSize = false;
                }
                boardPresenter.moveCamera(deltaX, deltaY);
                lastFrame = time;
            }
        };
        timer.start();
    }

    private void configClip() {
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(scene.widthProperty());
        clip.heightProperty().bind(scene.heightProperty());
        clip.xProperty().bind(Bindings.createDoubleBinding(
                () -> checkCamBorders(boardPresenter.getCameraX(), 0, pane.getWidth()), // add padding!
                boardPresenter.getCameraXProperty() , scene.widthProperty()));
        clip.yProperty().bind(Bindings.createDoubleBinding(
                () -> checkCamBorders(boardPresenter.getCameraY(), 0, pane.getHeight()),
                boardPresenter.getCameraYProperty(), scene.heightProperty()));
        canvas.setClip(clip);
        pane.translateXProperty().bind(clip.xProperty().multiply(-1));
        pane.translateYProperty().bind(clip.yProperty().multiply(-1));
    }

    private void configFieldsSizes(double paddingWidth, double paddingHeight) {
        this.paddingWidth = paddingWidth;
        this.paddingHeight = paddingHeight;
    }

    private void configPresenter() {
        boardPresenter.setOnDrawRequest(new BoardPresenter.DrawRequest() {
            @Override
            public void onDrawField(double x, double y, double width, double height, Field field) {
                drawField(x, y, width, height, field);
            }
            @Override
            public void onClearMap() {
                Objects.requireNonNull(canvas).getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            }
        });
        setPresenterCameraFromScene();
    }

    private void setPresenterCameraFromScene() {
        int newCamX, newCamY;
        if (!boardPresenter.isCameraSet()) {
            newCamX = newCamY = 0;
        } else {
            newCamX = (int)boardPresenter.getCameraX();
            newCamY = (int)boardPresenter.getCameraY();
        }
        boardPresenter.setCamera(newCamX, newCamY, scene.getWidth() - paddingWidth * 2, scene.getHeight() - paddingHeight * 2);
    }

    private double checkCamBorders(double val, double min, double max) {
        if (val < min) return min;
        return Math.min(val, max);
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
}
