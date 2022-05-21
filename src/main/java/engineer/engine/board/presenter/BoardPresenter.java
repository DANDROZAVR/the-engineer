package engineer.engine.board.presenter;

import engineer.engine.board.logic.Board;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class BoardPresenter {
    public interface View {
        double getViewHeight();
        double getViewWidth();

        void drawField(Box box, String texture);
    }

    @SuppressWarnings("FieldCanBeLocal")
    private final double fieldWidth = 70, fieldHeight = 70;

    private final Board board;
    private final View view;

    private double cameraX = 0.0, cameraY = 0.0;
    private double cameraSpeedX = 0.0, cameraSpeedY = 0.0;

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
        return (box.right() > 0 || box.left() < view.getViewWidth()) &&
                (box.bottom() > 0 || box.top() < view.getViewHeight());
    }

    private void redrawVisibleFields() {
        for(int i=0;i<board.getRows();i++)
            for(int j=0;j<board.getColumns();j++)
                if(isVisible(getFieldBox(i,j)))
                    view.drawField(getFieldBox(i,j), board.getField(i,j).getBackground());
    }



    // USELESS FOR NOW
    private final Board.Observer boardObserver = (row, column) -> {};

    // Need to be called at the beginning and at the end of lifetime
    public void start() {
        board.addObserver(boardObserver);
        redrawVisibleFields();
    }
    public void close() { board.removeObserver(boardObserver); }



    public void update(double time) {
        cameraX += cameraSpeedX * time;
        cameraY += cameraSpeedY * time;

        cameraX = max(cameraX, 0);
        cameraY = max(cameraY, 0);

        cameraX = min(cameraX, fieldWidth * board.getRows() - view.getViewWidth());
        cameraY = min(cameraY, fieldHeight * board.getColumns() - view.getViewHeight());

        redrawVisibleFields();
    }

    public void setCameraSpeedX(double speedX) { cameraSpeedX = speedX; }
    public void setCameraSpeedY(double speedY) { cameraSpeedY = speedY; }
}
