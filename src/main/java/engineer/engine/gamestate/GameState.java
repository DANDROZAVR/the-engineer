package engineer.engine.gamestate;

import engineer.engine.gamestate.board.Board;
import engineer.engine.gamestate.board.BoardFactory;
import engineer.engine.gamestate.building.Building;
import engineer.engine.gamestate.field.Field;
import engineer.utils.Box;
import engineer.utils.Pair;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class GameState {

  public interface SelectionObserver {
    void onFieldSelection(Pair field);
  }
  private final BoardFactory boardFactory;
  private final Camera camera;

  private final Board board;
  private Pair selectedField;
  private final List<SelectionObserver> selectionObservers = new LinkedList<>();

  public GameState(BoardFactory boardFactory, Camera camera) {
    this.boardFactory = boardFactory;
    this.camera = camera;

    board = boardFactory.produceBoard(40, 50);

    for (int row = 0; row < 40; row++)
      for (int column = 0; column < 50; column++) {
        Field field = boardFactory.produceField(
                "tile",
                boardFactory.produceBuilding(null),
                true
        );
        board.setField(row, column, field);
      }
  }



  // Board functions



  @SuppressWarnings("unused")
  public void addBoardObserver(Board.Observer observer) {
    board.addObserver(observer);
  }

  @SuppressWarnings("unused")
  public void removeBoardObserver(Board.Observer observer) {
    board.removeObserver(observer);
  }

  public int getRows() {
    return board.getRows();
  }

  public int getColumns() {
    return board.getColumns();
  }

  public Field getField(int row, int column) {
    return board.getField(row, column);
  }

  public Pair getSelectedField() {
    return selectedField;
  }

  public void selectField(int x, int y) {
    selectedField = new Pair(x, y);
    selectionObservers.forEach(o -> o.onFieldSelection(selectedField));

  @SuppressWarnings("unused")
  public void unselectField() {
    selectedField = null;
  }

  @SuppressWarnings("unused")
  public void build(int row, int column, String building) {
    Field field = getField(row, column);
    Field newField = boardFactory.produceField(
            field.getBackground(),
            boardFactory.produceBuilding(building),
            field.isFree()
    );

    board.setField(row, column, newField);
  }



  // Camera functions



  public void addCameraObserver(Camera.Observer observer) {
    camera.addObserver(observer);
  }

  public void removeCameraObserver(Camera.Observer observer) {
    camera.removeObserver(observer);
  }

  public Box getFieldBox(int row, int column) {
    return camera.getFieldBox(row, column);
  }

  public Pair getFieldByPoint(double x, double y) {
    return camera.getFieldByPoint(x, y);
  }

  public boolean isFieldVisible(int row, int column) {
    return camera.isFieldVisible(row, column);
  }

  public void moveCamera(double dx, double dy) {
    camera.moveCamera(dx, dy);
  }

  public Box getCameraBox() {
    return camera.getCameraBox();
  }

  public List<Building> getAllBuildingsList() {
    // TEMP. WE SHOULD IMPLEMENT HOUSES TO MOVE FORWARD
    return Arrays.asList(
        boardFactory.produceBuilding("house"), null, null, null, null,
        boardFactory.produceBuilding("house2"));
  }
}
