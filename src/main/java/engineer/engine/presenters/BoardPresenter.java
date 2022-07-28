package engineer.engine.presenters;

import engineer.engine.gamestate.GameState;
import engineer.utils.Box;
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

  private static final double ZOOM_SPEED = 1.1;

  private double fieldWidth = 70, fieldHeight = 70;

  private final GameState gameState;
  private final View view;

  private double cameraX, cameraY;
  private double cameraSpeedX, cameraSpeedY;

  public BoardPresenter(GameState gameState, View view) {
    this.gameState = gameState;
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
    for (int i = 0; i < gameState.getBoardRows(); i++)
      for (int j = 0; j < gameState.getBoardColumns(); j++)
        if (isVisible(getFieldBox(i, j))) {
          view.drawField(getFieldBox(i, j), gameState.getField(i, j).getBackground());
          if (gameState.getField(i, j).getBuilding() != null)
            view.drawField(getFieldBox(i, j), gameState.getField(i, j).getBuilding().getPicture());
        }

    Pair<Integer, Integer> selectedField = gameState.getSelectedField();
    if (gameState.getSelectedField() != null) {
      Box selectionBox = getFieldBox(selectedField.getKey(), selectedField.getValue());
      if (isVisible(selectionBox)) {
        view.drawSelection(selectionBox);
      }
    }
  }

  public void update(double time) {
    cameraX += cameraSpeedX * time;
    cameraY += cameraSpeedY * time;

    cameraX = max(cameraX, 0);
    cameraY = max(cameraY, 0);

    cameraX = min(cameraX, fieldWidth * gameState.getBoardRows() - view.getViewWidth());
    cameraY = min(cameraY, fieldHeight * gameState.getBoardColumns() - view.getViewHeight());
    redrawVisibleFields();
  }

  public void setCameraSpeedX(double speedX) {
    cameraSpeedX = speedX;
  }

  public void setCameraSpeedY(double speedY) {
    cameraSpeedY = speedY;
  }

  // TODO: adjust to mouse
  @SuppressWarnings("unused")
  public void zoomIn() {
    fieldWidth *= ZOOM_SPEED;
    fieldHeight *= ZOOM_SPEED;
  }

  // TODO: adjust to mouse
  @SuppressWarnings("unused")
  public void zoomOut() {
    fieldWidth /= ZOOM_SPEED;
    fieldHeight /= ZOOM_SPEED;
  }

  @SuppressWarnings("unused")
  public void changeContent(double x, double y, String building) {
    int row = (int) ((x + cameraX) / fieldWidth);
    int col = (int) ((y + cameraY) / fieldHeight);
    if (gameState.getField(row, col).isFree())
      gameState.build(row, col, building);
  }

  public void selectField(double x, double y) {
    int row = (int) ((x + cameraX) / fieldWidth);
    int col = (int) ((y + cameraY) / fieldHeight);
    gameState.selectField(row, col);
  }

  @SuppressWarnings("unused")
  public void unselectField() {
    gameState.unselectField();
  }
}
