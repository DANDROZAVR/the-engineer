package engineer.engine.board.presenter;

import engineer.engine.board.exceptions.IndexOutOfBoardException;
import engineer.engine.board.logic.Board;
import engineer.engine.board.logic.Field;
import javafx.beans.property.DoubleProperty;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BoardPresenter {
    public interface Observer {
        void onFieldChange(int row, int column);
    }

    public interface DrawRequest {
        void onDrawField(double x, double y, double width, double height, Field field);
        void onClearMap();
    }

    private final static int maxFieldWidth = 50, maxFieldHeight = 50, minFieldWidth = 5, minFieldHeight = 5;
    private final static int defFieldWidth = 30, defFieldHeight = 30;

    private final Board board;
    private final List<Observer> observerList = new ArrayList<>();
    private BoardDrawableStatus status;
    private DrawRequest drawRequest;
    private double fieldWidth = defFieldWidth, fieldHeight = defFieldHeight;

    private Rectangle cam;

    final static double speedChangingSize = 1.1;

    public BoardPresenter(Board board) {
        this.board = board;
        this.status = new BoardDrawableStatus(defFieldWidth, defFieldHeight);
    }

    public void addObservers(Observer... observerArray) {
        observerList.addAll(Arrays.asList(observerArray));
    }
    public void removeObservers(Observer... observerArray) {
        observerList.removeAll(Arrays.asList(observerArray));
    }

    public void setOnDrawRequest(DrawRequest drawRequest) {
        this.drawRequest = drawRequest;
        if (cam != null) {
            try {
                status.checkVisibleFields(drawRequest);
            } catch (IndexOutOfBoardException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private final Board.Observer boardObserver = this::onFieldChange;

    // Need to be called at the beginning and at the end of lifetime
    public void startObserve() { board.addObserver(boardObserver); }
    public void stopObserve() { board.removeObserver(boardObserver); }

    private void onFieldChange(int row, int column) {
        for (Observer observer : observerList)
            observer.onFieldChange(row, column);
    }

    public double getCameraX() { return cam.getX(); }
    public double getCameraY() { return cam.getY(); }
    public DoubleProperty getCameraXProperty() { return cam.xProperty(); }
    public DoubleProperty getCameraYProperty() { return cam.yProperty(); }
    public double getCameraWidth() { return cam.getWidth(); }
    public double getCameraHeight() { return cam.getHeight(); }

    public void setCamera(int camX, int camY, double camWidth, double camHeight) {
        this.cam = new Rectangle(0, 0, camWidth, camHeight);
        cam.setX(camX);
        cam.setY(camY);
        if (drawRequest != null) {
            try {
                status.checkVisibleFields(drawRequest);
            } catch (IndexOutOfBoardException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean isCameraSet() { return cam != null; }

    public void moveCamera(double deltaX, double deltaY) {
        cam.setX(checkCamBorders(cam.getX() + deltaX, 0, fieldWidth * board.getRows() - cam.getWidth()));
        cam.setY(checkCamBorders(cam.getY() + deltaY, 0, fieldHeight * board.getColumns() - cam.getHeight()));

        if (drawRequest != null) {
            try {
                status.checkVisibleFields(drawRequest);
            } catch (IndexOutOfBoardException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @SuppressWarnings("SameParameterValue")
    private double checkCamBorders(double val, double min, double max) {
        if (val < min) return min;
        return Math.min(val, max);
    }

    public void setFieldsSize(double fieldWidth, double fieldHeight) {
        this.fieldWidth = fieldWidth;
        this.fieldHeight = fieldHeight;
        this.status = new BoardDrawableStatus(fieldWidth, fieldHeight);
        if (drawRequest != null) {
            try {
                drawRequest.onClearMap();
                status.checkVisibleFields(drawRequest);
            } catch (IndexOutOfBoardException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void increaseFieldsSize() {
        if (fieldWidth * speedChangingSize < maxFieldWidth && fieldHeight * speedChangingSize < maxFieldHeight) {
            setFieldsSize(fieldWidth * speedChangingSize, fieldHeight * speedChangingSize);
        }
    }

    public void decreaseFieldsSize() {
        if (fieldWidth / speedChangingSize > minFieldWidth && fieldHeight / speedChangingSize > minFieldHeight) {
            setFieldsSize(fieldWidth / speedChangingSize, fieldHeight / speedChangingSize);
        }
    }

    private class BoardDrawableStatus {
        final double fieldWidth, fieldHeight;
        boolean[][] wasDrawn;

        BoardDrawableStatus(double fieldWidth, double fieldHeight) {
            this.fieldHeight = fieldHeight;
            this.fieldWidth = fieldWidth;
            this.wasDrawn = new boolean[board.getRows()][board.getColumns()];
        }

        public void checkVisibleFields(DrawRequest drawRequest) throws IndexOutOfBoardException {
            int visibleColumnStart = getXByCoordinate(cam.getX());
            int visibleRowStart = getYByCoordinate(cam.getY());
            int camRows = (int)(cam.getHeight() / fieldHeight);
            int camCols = (int)(cam.getWidth() / fieldWidth);
            for (int r = Math.max(0, visibleRowStart - 2); r <= Math.min(visibleRowStart + 2 + camRows, board.getRows() - 1); ++r)
                for (int c = Math.max(0, visibleColumnStart - 2); c <= Math.min(visibleColumnStart + 2 + camCols, board.getColumns() - 1); ++c) {
                    if (!wasDrawn[r][c]) {
                        drawRequest.onDrawField(c * fieldWidth, r * fieldHeight, fieldWidth, fieldHeight, board.getField(r, c));
                        wasDrawn[r][c] = true;
                    }
                }
        }

        public int getXByCoordinate(double x) { return (int)(x / fieldWidth); }
        public int getYByCoordinate(double y) { return (int)(y / fieldHeight); }
    }
}
