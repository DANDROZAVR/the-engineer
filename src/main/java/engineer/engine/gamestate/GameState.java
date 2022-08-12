package engineer.engine.gamestate;

import engineer.engine.gamestate.board.Board;
import engineer.engine.gamestate.board.BoardFactory;
import engineer.engine.gamestate.building.Building;
import engineer.engine.gamestate.field.Field;
import engineer.engine.gamestate.mob.Mob;
import engineer.engine.gamestate.turns.Player;
import engineer.engine.gamestate.turns.TurnSystem;
import engineer.utils.Box;
import engineer.utils.Coords;

import java.util.*;

import static java.lang.Math.min;

public class GameState {

  public interface SelectionObserver {
    void onFieldSelection(Coords field);
  }
  private final BoardFactory boardFactory;
  private final Camera camera;

  private final Board board;
  private Coords selectedField;
  private final List<Coords> accessibleFields = new LinkedList<>();
  private final List<SelectionObserver> selectionObservers = new LinkedList<>();

  @SuppressWarnings({"FieldCanBeLocal", "unused"})
  private final TurnSystem turnSystem;

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
        board.setField(new Coords(row, column), field);
      }

    // TODO: temporary solution
    List<Player> players = new LinkedList<>();
    players.add(new Player());
    players.add(new Player());
    turnSystem = new TurnSystem(players);

    setMob(new Coords(3, 5), "wood", 15);
    setMob(new Coords(8, 8), "wood", 5);
    setMob(new Coords(2, 7), "exit", 1);
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

  public Field getField(Coords coords) {
    return board.getField(coords);
  }

  public Coords getSelectedField() {
    return selectedField;
  }

  public List<Coords> getAccessibleFields() {
    return accessibleFields;
  }

  private List<Coords> getNeighbours(Coords field) {
    List<Coords> neighbours = new ArrayList<>();
    neighbours.add(new Coords(field.row()-1, field.column()));
    neighbours.add(new Coords(field.row()+1, field.column()));
    neighbours.add(new Coords(field.row(), field.column()+1));
    neighbours.add(new Coords(field.row(), field.column()-1));
    return neighbours;
  }

  public void selectField(Coords coords) {
    if(accessibleFields.contains(coords)){
      moveMob(selectedField, coords, 1);
    }
    accessibleFields.clear();

    if(getField(coords).getMob() != null){
      setAccessibleFieldsFrom(coords, getField(coords).getMob().getRange());
    }

    selectedField = coords;
    selectionObservers.forEach(o -> o.onFieldSelection(selectedField));
  }

  private void setAccessibleFieldsFrom(Coords coords, int range) {
    accessibleFields.clear();
    List<Coords> tempList = new LinkedList<>();

    accessibleFields.add(coords);
    for (int i = 0; i < range; i++) {
      for (Coords j : accessibleFields) {
        for (Coords k : getNeighbours(j)) {
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
  public void build(Coords coords, String building) {
    Field field = getField(coords);
    Field newField = boardFactory.produceField(
            field.getBackground(),
            boardFactory.produceBuilding(building),
            field.getMob(),
            field.isFree()
    );

    board.setField(coords, newField);
  }

  public void setMob(Coords coords, String type, int number) {
    Field field = getField(coords);

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

    board.setField(coords, newField);
  }

  public void moveMob(Coords from, Coords to, int number) {
    if(from.equals(to)){
      return;
    }
    Field fieldFrom = getField(from);
    Field fieldTo = getField(to);
    if(fieldTo.getMob() != null && !Objects.equals(fieldTo.getMob().getType(), fieldFrom.getMob().getType())){
      return;
    }
    number = min(number, fieldFrom.getMob().getNumber());

    int fieldToNumber = 0;
    if (fieldTo.getMob() != null) {
      fieldToNumber = fieldTo.getMob().getNumber();
    }

    setMob(to, fieldFrom.getMob().getType(), fieldToNumber + number);
    setMob(from, null, 0);
  }



  // Camera functions



  public void addCameraObserver(Camera.Observer observer) {
    camera.addObserver(observer);
  }

  public void removeCameraObserver(Camera.Observer observer) {
    camera.removeObserver(observer);
  }

  public Box getFieldBox(Coords coords) {
    return camera.getFieldBox(coords);
  }

  public Coords getFieldByPoint(double x, double y) {
    return camera.getFieldByPoint(x, y);
  }

  public boolean isFieldVisible(Coords coords) {
    return camera.isFieldVisible(coords);
  }

  public void moveCamera(double dx, double dy) {
    camera.moveCamera(dx, dy);
  }

  public void zoomCamera(double delta) {
    camera.zoom(delta);
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
