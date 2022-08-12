package engineer.engine.presenters.game;

import engineer.engine.gamestate.Camera;
import engineer.engine.gamestate.GameState;
import engineer.engine.gamestate.board.Board;
import engineer.engine.gamestate.field.Field;
import engineer.utils.Box;
import engineer.utils.Coords;

public class BoardPresenter {
  public interface View {
    void drawField(Box box, String texture);
    void drawSelection(Box box);
    void enlightenField(Box box);
  }

  private final GameState gameState;
  private final View view;

  public BoardPresenter(GameState gameState, View view) {
    this.gameState = gameState;
    this.view = view;
  }

  public void redrawVisibleFields() {
    for (int row = 0; row < gameState.getRows(); row++) {
      for (int column = 0; column < gameState.getColumns(); column++) {
        Coords coords = new Coords(row, column);

        if (gameState.isFieldVisible(coords)) {
          Field field = gameState.getField(coords);
          Box box = gameState.getFieldBox(coords);

          view.drawField(box, field.getBackground());
          if (field.getMob() != null)
            view.drawField(box, field.getMob().getTexture());
          if (field.getBuilding() != null)
            view.drawField(box, field.getBuilding().getPicture());
        }
      }
    }

    if (gameState.getAccessibleFields() != null) {
      for(Coords i : gameState.getAccessibleFields()){
        Box selectionBox = gameState.getFieldBox(i);
        if (gameState.isFieldVisible(i)) {
          view.enlightenField(selectionBox);
        }
      }
    }

    Coords selectedField = gameState.getSelectedField();
    if (gameState.getSelectedField() != null && gameState.isFieldVisible(selectedField)) {
      Box box = gameState.getFieldBox(selectedField);
      view.drawSelection(box);
    }
  }

  public void selectField(double x, double y) {
    Coords field = gameState.getFieldByPoint(x, y);
    gameState.selectField(field);
  }

  @SuppressWarnings("unused")
  public void unselectField() {
    gameState.unselectField();
  }

  public void moveCamera(double dx, double dy) {
    gameState.moveCamera(dx, dy);
  }

  public void zoomCamera(double delta) {
    gameState.zoomCamera(delta);
  }

  private final Board.Observer boardObserver = (coords) -> redrawVisibleFields();
  private final Camera.Observer cameraObserver = this::redrawVisibleFields;

  public void start() {
    gameState.addBoardObserver(boardObserver);
    gameState.addCameraObserver(cameraObserver);
  }

  public void close() {
    gameState.removeBoardObserver(boardObserver);
    gameState.removeCameraObserver(cameraObserver);
  }
}
