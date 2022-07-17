package engineer.engine.board.presenter;

import engineer.engine.board.logic.Board;
import engineer.engine.board.logic.FieldContentImpl;
import javafx.util.Pair;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class BoardPresenter {
    public interface View {
        double getViewHeight();
        double getViewWidth();
        void drawField(Box box, String texture);
        void drawSelection(Box box);
    }

    private final static double zoomSpeed = 1.1;

    private double fieldWidth = 70, fieldHeight = 70;

    private final Board board;
    private final View view;

    private double cameraX = 0.0, cameraY = 0.0;
    private double cameraSpeedX = 0.0, cameraSpeedY = 0.0;
    private double cameraMoveX, cameraMoveY;
    private String pressedButton;
    private Pair<Integer, Integer> selectedField;

    public BoardPresenter(Board board, View view) {
        this.board = board;
        this.view = view;
    }



    private Box getFieldBox(int i, int j) {
        return new Box(
                i*fieldWidth - cameraX,
                j*fieldHeight - cameraY,
                fieldWidth,
                fieldHeight
        );
    }

    private boolean isVisible(Box box) {
        return (box.right() > 0 && box.left() < view.getViewWidth()) &&
                (box.bottom() > 0 && box.top() < view.getViewHeight());
    }

    private void redrawVisibleFields() {
        for(int i=0;i<board.getRows();i++)
            for(int j=0;j<board.getColumns();j++)
                if(isVisible(getFieldBox(i,j))) {
                    view.drawField(getFieldBox(i, j), board.getField(i, j).getBackground());
                    if(board.getField(i, j).getContent() != null)
                        view.drawField(getFieldBox(i, j), board.getField(i, j).getContent().getPicture());
                }
        if (selectedField != null) {
            Box selectionBox = getFieldBox(selectedField.getKey(), selectedField.getValue());
            if (isVisible(selectionBox)) {
                view.drawSelection(selectionBox);
            }
        }
    }


/*
    // USELESS FOR NOW
    private final Board.Observer boardObserver = (row, column) -> {};

    // Need to be called at the beginning and at the end of lifetime
    public void start() {
        board.addObserver(boardObserver);
        redrawVisibleFields();
    }
    public void close() { board.removeObserver(boardObserver); }
*/


    public void update(double time) {
        cameraX += cameraSpeedX * time + cameraMoveX;
        cameraY += cameraSpeedY * time + cameraMoveY;
        cameraMoveX = 0;
        cameraMoveY = 0;

        cameraX = max(cameraX, 0);
        cameraY = max(cameraY, 0);

        cameraX = min(cameraX, fieldWidth * board.getRows() - view.getViewWidth());
        cameraY = min(cameraY, fieldHeight * board.getColumns() - view.getViewHeight());

        redrawVisibleFields();
    }

    public void setCameraSpeedX(double speedX) { cameraSpeedX = speedX; }
    public void setCameraSpeedY(double speedY) { cameraSpeedY = speedY; }
    public void setCameraMoveX(double speedX) { cameraMoveX = speedX; }
    public void setCameraMoveY(double speedY) { cameraMoveY = speedY; }
    public void zoomIn() {
        fieldWidth *= zoomSpeed;
        fieldHeight *= zoomSpeed;
    }
    public void zoomOut() {
        fieldWidth /= zoomSpeed;
        fieldHeight /= zoomSpeed;
    }
    public void setPressedButton(String button) { pressedButton = button; }

    public void changeContent(double x, double y) {
        int row = (int) ((x + cameraX) / fieldWidth);
        int col = (int) ((y + cameraY) / fieldHeight);
        if(board.getField(row, col).isFree()) {
            board.setFieldContent(
                    row,
                    col,
                    new FieldContentImpl(pressedButton)
            );
            pressedButton = null;
        }
    }
    public void setSelectedField(double x, double y) {
        int row = (int) ((x + cameraX) / fieldWidth);
        int col = (int) ((y + cameraY) / fieldHeight);
        selectedField = new Pair<>(row, col);
    }

    @SuppressWarnings("unused")
    public void cancelSelectedField() { selectedField = null; }
}
