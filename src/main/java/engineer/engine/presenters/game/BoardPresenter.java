package engineer.engine.presenters.game;

import engineer.engine.gamestate.Camera;
import engineer.engine.gamestate.GameState;
import engineer.engine.gamestate.board.Board;
import engineer.engine.gamestate.field.Field;
import engineer.utils.Box;
import engineer.utils.Pair;

public class BoardPresenter {
  public interface View {
    void drawField(Box box, String texture);
    void drawSelection(Box box);
  }

  private final GameState gameState;
  private final View view;

  public BoardPresenter(GameState gameState, View view) {
    this.gameState = gameState;
    this.view = view;
  }

  public void redrawVisibleFields() {
    for (int row = 0; row < gameState.getRows(); row++)
      for (int column = 0; column < gameState.getColumns(); column++)
        if (gameState.isFieldVisible(row, column)) {
          Field field = gameState.getField(row, column);
          Box box = gameState.getFieldBox(row, column);

          view.drawField(box, field.getBackground());
          if (field.getBuilding() != null)
            view.drawField(box, field.getBuilding().getPicture());
        }

    Pair selectedField = gameState.getSelectedField();
    if (gameState.getSelectedField() != null &&
            gameState.isFieldVisible(selectedField.first(), selectedField.second())
    ) {
      Box box = gameState.getFieldBox(
              selectedField.first(),
              selectedField.second()
      );
      view.drawSelection(box);
    }
  }

  public void selectField(double x, double y) {
    Pair field = gameState.getFieldByPoint(x, y);
    gameState.selectField(field.first(), field.second());
  }

  @SuppressWarnings("unused")
  public void unselectField() {
    gameState.unselectField();
  }

  public void moveCamera(double dx, double dy) {
    gameState.moveCamera(dx, dy);
  }

  private final Board.Observer boardObserver = (row, column) -> redrawVisibleFields();
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
