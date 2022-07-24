package engineer.engine.presenters;

import static java.lang.Math.max;
import static java.lang.Math.min;

import engineer.engine.gamestate.Board;
import engineer.engine.gamestate.FieldContentImpl;
import engineer.utils.Box;
import javafx.util.Pair;

public class BoardPresenter {

  public interface View {
    double getViewHeight();

    double getViewWidth();

    void drawField(Box box, String texture);

    void drawSelection(Box box);
  }

  private static final double zoomSpeed = 1.1;

  private double fieldWidth = 70, fieldHeight = 70;

  private final Board board;
  private final View view;
  private double cameraX, cameraY;
  private double cameraSpeedX, cameraSpeedY;
  private double cameraMoveX, cameraMoveY;
  private String pressedButton;
  private Pair<Integer, Integer> selectedField;

  private double lastCameraTouchX, lastCameraTouchY;

  public BoardPresenter(Board board, View view) {
    this.board = board;
    this.view = view;
  }

  private Box getFieldBox(int i, int j) {
    return new Box(i * fieldWidth - cameraX, j * fieldHeight - cameraY, fieldWidth, fieldHeight);
  }

  private boolean isVisible(Box box) {
    return (box.right() > 0 && box.left() < view.getViewWidth())
        && (box.bottom() > 0 && box.top() < view.getViewHeight());
  }

  private void redrawVisibleFields() {
    for (int i = 0; i < board.getRows(); i++)
      for (int j = 0; j < board.getColumns(); j++)
        if (isVisible(getFieldBox(i, j))) {
          view.drawField(getFieldBox(i, j), board.getField(i, j).getBackground());
          if (board.getField(i, j).getContent() != null)
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

  public void addCameraSpeedX(double speedX) {
    cameraSpeedX += speedX;
  }

  public void addCameraSpeedY(double speedY) {
    cameraSpeedY += speedY;
  }

  public void setCameraSpeedX(double speedX) {
    cameraSpeedX = speedX;
  }

  public void setCameraSpeedY(double speedY) {
    cameraSpeedY = speedY;
  }

  public void setCameraMoveX(double speedX) {
    cameraMoveX = speedX;
  }

  public void setCameraMoveY(double speedY) {
    cameraMoveY = speedY;
  }

  public void zoomIn() {
    fieldWidth *= zoomSpeed;
    fieldHeight *= zoomSpeed;
  }

  public void zoomOut() {
    fieldWidth /= zoomSpeed;
    fieldHeight /= zoomSpeed;
  }

  public void setPressedButton(String button) {
    pressedButton = button;
  }

  public void changeContent(double x, double y) {
    int row = (int) ((x + cameraX) / fieldWidth);
    int col = (int) ((y + cameraY) / fieldHeight);
    if (board.getField(row, col).isFree()) {
      board.setFieldContent(row, col, new FieldContentImpl(pressedButton));
      pressedButton = null;
    }
  }

  public void setSelectedField(double x, double y) {
    int row = (int) ((x + cameraX) / fieldWidth);
    int col = (int) ((y + cameraY) / fieldHeight);
    selectedField = new Pair<>(row, col);
  }

  @SuppressWarnings("unused")
  public void cancelSelectedField() {
    selectedField = null;
  }

  public enum SimpleMouseButton {
    PRIMARY,
    SECONDARY,
    MIDDLE,
    BACK,
    FORWARD,
    MOVED,
    NONE
  }

  private double dx = 0, dy = 0;

  public void onMouseMoved(double eventX, double eventY) {
    addCameraSpeedX(-dx);
    addCameraSpeedY(-dy);
    dx = 0;
    dy = 0;
    final double viewWidth = view.getViewWidth();
    final double viewHeight = view.getViewHeight();
    final int cnst = 5;
    if (eventX <= viewWidth / cnst) dx = -(viewWidth / cnst - eventX);
    if (eventY <= viewHeight / cnst) dy = -(viewHeight / cnst - eventY);
    if (viewWidth - eventX <= viewHeight / cnst) dx = viewWidth / cnst - (viewWidth - eventX);
    if (viewHeight - eventY <= viewHeight / cnst) dy = viewHeight / cnst - (viewHeight - eventY);
    dx = Math.pow(Math.abs(dx), 0.42) * dx;
    dy = Math.pow(Math.abs(dy), 0.42) * dy;
    addCameraSpeedX(dx);
    addCameraSpeedY(dy);
  }

  @SuppressWarnings("StatementWithEmptyBody")
  public void onMouseClick(
      SimpleMouseButton button, int clicksNumber, double eventX, double eventY) {
    if (button.equals(SimpleMouseButton.PRIMARY)) {
      if (clicksNumber == 1) {
        changeContent(eventX, eventY);
        setSelectedField(eventX, eventY);
      } else if (clicksNumber == 2) {
        // we can create functionality later, f.e choosing troops from field
      }
    }
  }

  public void onMousePressed(SimpleMouseButton button, double eventX, double eventY) {
    if (button.equals(SimpleMouseButton.SECONDARY)) {
      lastCameraTouchX = eventX;
      lastCameraTouchY = eventY;
    }
  }

  @SuppressWarnings("unused")
  public void onMouseReleased(SimpleMouseButton button, double eventX, double eventY) {}

  @SuppressWarnings("StatementWithEmptyBody")
  public void onMouseDragged(SimpleMouseButton button, double eventX, double eventY) {
    if (button.equals(SimpleMouseButton.SECONDARY)) {
      setCameraMoveX(lastCameraTouchX - eventX);
      setCameraMoveY(lastCameraTouchY - eventY);
      lastCameraTouchX = eventX;
      lastCameraTouchY = eventY;
    } else if (button.equals(SimpleMouseButton.PRIMARY)) {
      // can be used for setting troops trips
    }
  }
}
