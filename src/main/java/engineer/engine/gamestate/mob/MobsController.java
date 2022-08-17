package engineer.engine.gamestate.mob;

import engineer.engine.gamestate.board.Board;
import engineer.engine.gamestate.field.Field;
import engineer.engine.gamestate.turns.Player;
import engineer.engine.gamestate.turns.TurnSystem;
import engineer.utils.Coords;

import java.util.Collection;

import static java.lang.Math.max;

public class MobsController implements Board.Observer{
  private final MobFactory mobFactory;
  private final TurnSystem turnSystem;

  private Coords lastSelectedCoords;
  private final Board board;

  public MobsController(Board board, TurnSystem turnSystem, MobFactory mobFactory) {
    this.board = board;
    this.turnSystem = turnSystem;
    this.mobFactory = mobFactory;

    // TODO: remove observer
    board.addObserver(this);
  }

  @Override
  public void onSelectionChanged(Coords coords) {
    boolean isReachable = board.getMarkedFields().contains(coords);
    board.unmarkAllFields();

    Player player = turnSystem.getCurrentPlayer();
    Field field = board.getField(coords);
    Mob mob = field.getMob();

    if (isReachable && lastSelectedCoords != null) {
      moveMob(lastSelectedCoords, coords);
    } else if (mob != null && player.isMobOwner(mob)) {
      Collection<Coords> collection = board.getNearestFields(coords, mob.getRemainingSteps());
      board.markFields(collection);
    }

    lastSelectedCoords = coords;
  }

  private void moveMob(Coords from, Coords to) {
    if (from.equals(to)) {
      return;
    }

    Field fieldFrom = board.getField(from);
    Field fieldTo = board.getField(to);

    Player player = turnSystem.getCurrentPlayer();

    Mob mob = fieldFrom.getMob();
    int stepsUsed = board.findPath(from, to).size() - 1;
    if (mob == null || !player.isMobOwner(mob)) {
      throw new RuntimeException("Forbidden mob move");
    }

    if (fieldTo.getMob() != null) {
      Mob mobTo = fieldTo.getMob();
      if (!mob.getType().equals(mobTo.getType()) || !player.isMobOwner(mobTo)) {
        throw new RuntimeException("Forbidden mob move");
      }

      stepsUsed = max(stepsUsed, mob.getRemainingSteps() - mobTo.getRemainingSteps());
      mob.addMobs(mobTo.getMobsAmount());
      player.removeMob(mobTo);
    }

    mob.reduceRemainingSteps(stepsUsed);

    board.setField(from, board.produceField(
            fieldFrom.getBackground(),
            fieldFrom.getBuilding(),
            null,
            fieldFrom.isFree()
    ));
    board.setField(to, board.produceField(
            fieldTo.getBackground(),
            fieldTo.getBuilding(),
            mob,
            fieldTo.isFree()
    ));
  }

  public Mob produceMob(String type, int mobsAmount) {
    return mobFactory.produce(type, mobsAmount);
  }

  public void setMob(Coords coords, Mob mob) {
    Field oldField = board.getField(coords);
    if (mob != null) {
      turnSystem.getCurrentPlayer().addMob(mob);
    } else {
      turnSystem.getCurrentPlayer().removeMob(oldField.getMob());
    }

    Field newField = board.produceField(
            oldField.getBackground(),
            oldField.getBuilding(),
            mob,
            oldField.isFree()
    );

    board.setField(coords, newField);
  }
}
