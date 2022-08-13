package engineer.engine.gamestate;

import engineer.engine.gamestate.board.Board;
import engineer.engine.gamestate.board.BoardFactory;
import engineer.engine.gamestate.building.Building;
import engineer.engine.gamestate.field.Field;
import engineer.engine.gamestate.mob.Mob;
import engineer.engine.gamestate.mob.MobFactory;
import engineer.engine.gamestate.mob.MobsController;
import engineer.engine.gamestate.turns.Player;
import engineer.engine.gamestate.turns.TurnSystem;
import engineer.utils.Box;
import engineer.utils.Coords;

import java.util.*;

public class GameState {

  public interface SelectionObserver {
    void onFieldSelection(Coords field);
  }
  private final BoardFactory boardFactory;
  private final Camera camera;

  private final Board board;
  private Coords selectedField;
  private final List<SelectionObserver> selectionObservers = new LinkedList<>();

  private final MobsController mobsController;

  @SuppressWarnings({"unused", "FieldCanBeLocal"})
  private final TurnSystem turnSystem;

  public GameState(BoardFactory boardFactory, MobFactory mobFactory, Camera camera) {
    this.boardFactory = boardFactory;
    this.camera = camera;

    board = boardFactory.produceBoard(40, 50);
    mobsController = new MobsController(this::setMob, mobFactory);

    for (int row = 0; row < 40; row++) {
      for (int column = 0; column < 50; column++) {
        Field field = boardFactory.produceField(
            "tile",
            null,
            null,
            true
        );
        board.setField(new Coords(row, column), field);
      }
    }

    // TODO: temporary solution
    List<Player> players = new LinkedList<>();
    players.add(new Player());
    players.add(new Player());
    turnSystem = new TurnSystem(players, mobsController);
    turnSystem.nextTurn();

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

  public void selectField(Coords coords) {
    if (coords.equals(new Coords(0, 0))) {
      turnSystem.nextTurn(); // EXTREMELY TEMP
    }
    Field lastSelectedField = (selectedField == null ? null : getField(selectedField));
    Field actualSelectedField = getField(coords);
    if (actualSelectedField.getMob() == null || turnSystem.getCurrentPlayer().isMobOwner(actualSelectedField.getMob()))
      mobsController.onFieldSelection(coords, selectedField, actualSelectedField, lastSelectedField);
    selectedField = coords;
    selectionObservers.forEach(o -> o.onFieldSelection(selectedField));
  }

  public List<Coords> getAccessibleFields() {
    return mobsController.getAccessibleFields();
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

  public void setMob(Coords coords, String type, int mobsAmount) {
    Mob mob = null;
    if (type != null)
      mob = mobsController.produceMob(type, mobsAmount);
    setMob(coords, mob);
  }

  public void setMob(Coords coords, Mob mob) {
    Field oldField = getField(coords);
    if (mob != null) {
      turnSystem.getCurrentPlayer().addMob(mob);
    } else {
        turnSystem.getCurrentPlayer().removeMob(oldField.getMob());
    }
    Field newField = boardFactory.produceField(
        oldField.getBackground(),
        oldField.getBuilding(),
        mob,
        oldField.isFree()
    );

    board.setField(coords, newField);
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
