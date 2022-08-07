package engineer.engine.gamestate;

import engineer.engine.gamestate.board.Board;
import engineer.engine.gamestate.board.BoardFactory;
import engineer.engine.gamestate.building.Building;
import engineer.engine.gamestate.field.Field;
import engineer.engine.gamestate.mob.Mob;
import engineer.utils.Box;
import engineer.utils.Pair;

import java.util.*;

import static java.lang.Math.min;

public class GameState {

  public interface SelectionObserver {
    void onFieldSelection(Pair field);
  }
  private final BoardFactory boardFactory;
  private final Camera camera;

  private final Board board;
  private Pair selectedField;
  private final List<Pair> accessibleFields = new LinkedList<>();
  private final List<SelectionObserver> selectionObservers = new LinkedList<>();

  public GameState(BoardFactory boardFactory, Camera camera) {
    this.boardFactory = boardFactory;
    this.camera = camera;

    board = boardFactory.produceBoard(40, 50);

    for (int row = 0; row < 40; row++)
      for (int column = 0; column < 50; column++) {
        Field field = boardFactory.produceField(
                "tile",
                null,
                null,
                true
        );
        board.setField(row, column, field);
      }

    setMob(3, 5, "wood", 15);
    setMob(8, 8, "wood", 5);
    setMob(2, 7, "exit", 1);
  }



  // Board functions



  public void addBoardObserver(Board.Observer observer) {
    board.addObserver(observer);
  }

  public void removeBoardObserver(Board.Observer observer) {
    board.removeObserver(observer);
  }

  public void addSelectionObserver(SelectionObserver observer) {
    selectionObservers.add(observer);
  }

  public void removeSelectionObserver(SelectionObserver observer) {
    selectionObservers.remove(observer);
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

  public List<Pair> getAccessibleFields() {
    return accessibleFields;
  }

  private List<Pair> getNeighbours(Pair field){
    List<Pair> neighbours = new ArrayList<>();
    neighbours.add(new Pair(field.first()-1, field.second()));
    neighbours.add(new Pair(field.first()+1, field.second()));
    neighbours.add(new Pair(field.first(), field.second()+1));
    neighbours.add(new Pair(field.first(), field.second()-1));
    return neighbours;
  }

  public void selectField(int x, int y) {
    if(accessibleFields.contains(new Pair(x, y))){
      moveMob(selectedField.first(), selectedField.second(), x, y, 1);
    }
    accessibleFields.clear();

    if(getField(x, y).getMob() != null){
      setAccessibleFieldsFrom(x, y, getField(x, y).getMob().getRange());
    }

    selectedField = new Pair(x, y);
    selectionObservers.forEach(o -> o.onFieldSelection(selectedField));
  }

  private void setAccessibleFieldsFrom(int x, int y, int range) {
    accessibleFields.clear();
    List<Pair> tempList = new LinkedList<>();

    accessibleFields.add(new Pair(x, y));
    for (int i = 0; i < range; i++) {
      for (Pair j : accessibleFields) {
        for (Pair k : getNeighbours(j)) {
          if (!accessibleFields.contains(k)) {
            tempList.add(k);
          }
        }
      }
      accessibleFields.addAll(tempList);
      tempList.clear();
    }
  }

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
            field.getMob(),
            field.isFree()
    );

    board.setField(row, column, newField);
  }

  public void setMob(int row, int column, String type, int number) {
    Field field = getField(row, column);

    Mob mob = null;
    if (type != null) {
      mob = boardFactory.produceMob(type, number);
    }

    Field newField = boardFactory.produceField(
            field.getBackground(),
            field.getBuilding(),
            mob,
            field.isFree()
    );

    board.setField(row, column, newField);
  }

  public void moveMob(int xFrom, int yFrom, int xTo, int yTo, int number) {
    if(xFrom == xTo && yFrom == yTo){
      return;
    }
    Field fieldFrom = getField(xFrom, yFrom);
    Field fieldTo = getField(xTo, yTo);
    if(fieldTo.getMob() != null && !Objects.equals(fieldTo.getMob().getType(), fieldFrom.getMob().getType())){
      return;
    }
    number = min(number, fieldFrom.getMob().getNumber());

    int fieldToNumber = 0;
    if (fieldTo.getMob() != null) {
      fieldToNumber = fieldTo.getMob().getNumber();
    }

    setMob(xTo, yTo, fieldFrom.getMob().getType(), fieldToNumber + number);
    setMob(xFrom, yFrom, null, 0);
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
