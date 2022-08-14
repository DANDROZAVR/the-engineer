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
import engineer.utils.Coords;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class GameState {
  private final BoardFactory boardFactory;
  private final Camera camera;

  private final Board board;

  private final MobsController mobsController;

  private final TurnSystem turnSystem;

  // TODO: temporary
  @SuppressWarnings("FieldCanBeLocal")
  private final Board.Observer turnSystemObserver = new Board.Observer() {
    @Override
    public void onSelectionChanged(Coords coords) {
      if (new Coords(0, 0).equals(coords)) {
        turnSystem.nextTurn();
      }
    }
  };

  // TODO: temporary
  @SuppressWarnings("FieldCanBeLocal")
  private final Board.Observer mobsControllerObserver = new Board.Observer() {
    private Coords oldCoords;

    @Override
    public void onSelectionChanged(Coords newCoords) {
      Field lastSelectedField = (oldCoords == null ? null : board.getField(oldCoords));
      Field actualSelectedField = board.getField(newCoords);

      if (actualSelectedField.getMob() == null || turnSystem.getCurrentPlayer().isMobOwner(actualSelectedField.getMob())) {
        mobsController.onFieldSelection(newCoords, oldCoords, actualSelectedField, lastSelectedField);
      }

      oldCoords = newCoords;
    }
  };

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
    // TODO: remove observer
    board.addObserver(turnSystemObserver);
    board.addObserver(mobsControllerObserver);

    setMob(new Coords(3, 5), "wood", 15);
    setMob(new Coords(8, 8), "wood", 5);
    setMob(new Coords(2, 7), "exit", 1);
  }

  public void build(Coords coords, String building) {
    Field field = board.getField(coords);
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
    Field oldField = board.getField(coords);
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

  public List<Coords> getAccessibleFields() {
    return mobsController.getAccessibleFields();
  }

  public Board getBoard() {
    return board;
  }

  public Camera getCamera() {
    return camera;
  }

  public List<Building> getAllBuildingsList() {
    // TODO: TEMP. WE SHOULD IMPLEMENT HOUSES TO MOVE FORWARD
    return Arrays.asList(
        boardFactory.produceBuilding("house"), null, null, null, null,
        boardFactory.produceBuilding("house2"));
  }
}
