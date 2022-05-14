package engineer.gui;

import engineer.engine.board.exceptions.IndexOutOfBoardException;
import engineer.engine.board.presenter.BoardPresenter;
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

public class BoardGUI {
    private final Scene scene;
    private final Canvas canvas;
    private final Pane pane;
    private final TextureManager textureManager;
    private final BoardPresenter boardPresenter;
    private boolean pMovingLeft, pMovingRight, pMovingUp, pMovingDown, pIncreaseFieldsSize, pDecreaseFieldsSize; // player's camera moves
    private final int speed = 1000; // player's camera pixels per sec
    private double paddingWidth, paddingHeight, actualMapWidth, actualMapHeight, fieldWidth, fieldHeight;
    private final Rectangle cam;
    private Rectangle clip;
    private AnimationTimer timer;
    private FieldSizeNotifier notifier;

    public interface FieldSizeNotifier {
        void increaseRequest();
        void decreaseRequest();
    }

    public BoardGUI(Scene scene, TextureManager textureManager, BoardPresenter boardPresenter, FieldSizeNotifier notifier,
                    double fieldWidth, double fieldHeight, double paddingWidth, double paddingHeight) {
        pMovingLeft = pMovingRight = pMovingUp = pMovingDown = pIncreaseFieldsSize = pDecreaseFieldsSize = false;
        this.notifier = notifier;
        this.boardPresenter = boardPresenter;
        this.scene = scene;
        this.textureManager = textureManager;
        this.pane = extractPane(scene);
        this.canvas = extractCanvas(this.pane);
        this.cam = new Rectangle(0, 0, scene.getWidth(), scene.getHeight());
        configFieldsSizes(fieldWidth, fieldHeight, paddingWidth, paddingHeight);
        configClip();
        configFrames();
    }
    public void onFieldChange(int row, int column) { drawField(canvas.getGraphicsContext2D(), row, column); }
    public void drawField(GraphicsContext gc, int row, int column) {
        String background;
        try {
            background = boardPresenter.getField(row, column).getBackground();
        } catch (IndexOutOfBoardException e) {
            e.printStackTrace();
            return;
        }
        //textureManager.getImageField(background); TODO
        //canvas.drawImage() TODO
        gc.setFill(Color.valueOf(background)); // TEMP
        gc.fillRect(row * fieldWidth + paddingWidth, column * fieldHeight + paddingHeight, fieldWidth, fieldHeight); // TEMP
    }
    private void configFrames() {
        int frameNs = (int) (1_000_000_000.0 / 60.03); // TEMP. Should be dynamically calculated later
        this.timer = new AnimationTimer() {
            long lastFrame = -1;
            @Override
            public void handle(long time) {
                if (time <= lastFrame)
                    return;
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
                    if (notifier != null) {
                        notifier.increaseRequest();
                    }
                    pIncreaseFieldsSize = false;
                }
                if (pDecreaseFieldsSize) {
                    if (notifier != null) {
                        notifier.decreaseRequest();
                    }
                    pDecreaseFieldsSize = false;
                }
                cam.setX(checkCamBorders(cam.getX() + deltaX, 0, actualMapWidth - cam.getWidth()));
                cam.setY(checkCamBorders(cam.getY() + deltaY, 0, actualMapHeight - cam.getHeight()));
                lastFrame = time;
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        timer.start();
        draw();
    }
    private void configClip() {
        this.clip = new Rectangle();
        clip.widthProperty().bind(scene.widthProperty());
        clip.heightProperty().bind(scene.heightProperty());
        clip.xProperty().bind(Bindings.createDoubleBinding(
                () -> checkCamBorders(cam.getX(), 0, pane.getWidth()),
                cam.xProperty(), scene.widthProperty()));
        clip.yProperty().bind(Bindings.createDoubleBinding(
                () -> checkCamBorders(cam.getY(), 0, pane.getHeight()),
                cam.yProperty(), scene.heightProperty()));
        canvas.setClip(clip);
        pane.translateXProperty().bind(clip.xProperty().multiply(-1));
        pane.translateYProperty().bind(clip.yProperty().multiply(-1));
        scene.setOnKeyPressed(e -> processKey(e.getCode(), true));
        scene.setOnKeyReleased(e -> processKey(e.getCode(), false));
    }
    private void configFieldsSizes(double fieldWidth, double fieldHeight, double paddingWidth, double paddingHeight) {
        this.paddingWidth = paddingWidth;
        this.paddingHeight = paddingHeight;
        this.actualMapWidth = paddingWidth * 2 + fieldWidth * boardPresenter.getRows();
        this.actualMapHeight = paddingHeight * 2 + fieldHeight * boardPresenter.getColumns();
        this.fieldWidth = fieldWidth;
        this.fieldHeight = fieldHeight;
    }
    private double checkCamBorders(double val, double min, double max) {
        if (val < min) return min;
        return Math.min(val, max);
    }
    private void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        for (int i = 0; i < boardPresenter.getRows(); ++i)
            for (int j = 0; j < boardPresenter.getColumns(); ++j) {
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
    public void setFieldsSize(double fieldWidth, double fieldHeight) {
        this.fieldHeight = fieldHeight;
        this.fieldWidth = fieldWidth;
        actualMapWidth = (int)(paddingWidth * 2 + fieldWidth * boardPresenter.getRows());
        actualMapHeight = (int)(paddingHeight * 2 + fieldHeight * boardPresenter.getColumns());
        draw();
    }
}
