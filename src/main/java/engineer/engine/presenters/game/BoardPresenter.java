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
    void markField(Box box);
  }

  private final Board board;
  private final Camera camera;
  private final View view;

  public BoardPresenter(GameState gameState, View view) {
    board = gameState.getBoard();
    camera = gameState.getCamera();
    this.view = view;
  }

  public void redrawVisibleFields() {
    for (int row = 0; row < board.getRows(); row++) {
      for (int column = 0; column < board.getColumns(); column++) {
        Coords coords = new Coords(row, column);

        if (camera.isFieldVisible(coords)) {
          Field field = board.getField(coords);
          Box box = camera.getFieldBox(coords);

          view.drawField(box, field.getBackground());
          if (field.getMob() != null)
            view.drawField(box, field.getMob().getTexture());
          if (field.getBuilding() != null)
            view.drawField(box, field.getBuilding().getPicture());
        }
      }
    }

    for(Coords i : board.getMarkedFields()){
      Box selectionBox = camera.getFieldBox(i);
      if (camera.isFieldVisible(i)) {
        view.markField(selectionBox);
      }
    }

    Coords selectedField = board.getSelectedCoords();
    if (board.getSelectedCoords() != null && camera.isFieldVisible(selectedField)) {
      Box box = camera.getFieldBox(selectedField);
      view.drawSelection(box);
    }
  }

  public void selectField(double x, double y) {
    Coords field = camera.getFieldByPoint(x, y);
    board.selectField(field);
  }

  @SuppressWarnings("unused")
  public void unselectField() {
    board.selectField(null);
  }

  public void moveCamera(double dx, double dy) {
    camera.moveCamera(dx, dy);
  }

  public void zoomCamera(double delta) {
    camera.zoom(delta);
  }

  private final Board.Observer boardObserver = new Board.Observer() {
    @Override
    public void onFieldChanged(Coords coords) {
      redrawVisibleFields();
    }
  };

  private final Camera.Observer cameraObserver = this::redrawVisibleFields;

  public void start() {
    board.addObserver(boardObserver);
    camera.addObserver(cameraObserver);
  }

  public void close() {
    board.removeObserver(boardObserver);
    camera.removeObserver(cameraObserver);
  }
}
