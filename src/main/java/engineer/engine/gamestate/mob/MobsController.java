package engineer.engine.gamestate.mob;

import com.google.gson.JsonObject;
import engineer.engine.gamestate.board.Board;
import engineer.engine.gamestate.building.Building;
import engineer.engine.gamestate.field.Field;
import engineer.engine.gamestate.turns.Player;
import engineer.engine.gamestate.turns.TurnSystem;
import engineer.utils.Coords;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.lang.Math.max;

public class MobsController implements TurnSystem.Observer{
  private final MobFactory mobFactory;
  private final TurnSystem turnSystem;
  private final FightSystem fightSystem;
  private Coords lastSelectedCoords;
  public boolean attackSelected;
  private final Board board;
  private final List<Mob> mobList = new ArrayList<>();

  public MobsController(Board board, TurnSystem turnSystem, MobFactory mobFactory, FightSystem fightSystem) {
    this.board = board;
    this.turnSystem = turnSystem;
    this.mobFactory = mobFactory;
    this.fightSystem = fightSystem;
    this.attackSelected = false;

    parseMobs(board);
    turnSystem.addObserver(this);
  }
  @Override
  public void onTurnChange(){
    mobList.forEach(Mob::reset);
  }

  public void onSelectionChanged(Coords coords) {
    boolean isReachable = board.getMarkedFieldsToMove().contains(coords);
    boolean isCausingFight = board.getMarkedFieldsToAttack().contains(coords);
    board.unmarkAllFields();

    Player player = turnSystem.getCurrentPlayer();
    Field field = board.getField(coords);
    Mob mob = field.getMob();

    if (isReachable && lastSelectedCoords != null) {
      moveMob(lastSelectedCoords, coords);
    } else if (mob != null && player.equals(mob.getOwner())) {
      Pair<Collection<Coords>, Collection<Coords>> collection = board.getNearestFields(coords, mob.getRemainingSteps());
      board.markFields(collection.getKey(), collection.getValue());
    }

    if (isCausingFight && lastSelectedCoords != null) {
      if (board.getField(coords).getMob() != null) {
        makeFight(lastSelectedCoords, coords);
      } else if (board.getField(coords).getBuilding() != null) {
        attackBuilding(lastSelectedCoords, coords);
      }
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

    if(!mob.getOwner().equals(player)) {
      throw new RuntimeException("Forbidden mob move");
    }

    int stepsUsed = board.findPath(from, to).size() - 1;

    if (fieldTo.getMob() != null) {
      Mob mobTo = fieldTo.getMob();

      if(!player.equals(mobTo.getOwner())){
        mob.reduceRemainingSteps(stepsUsed);
        makeFight(from, to);
        return;
      }

      if (!mob.getType().equals(mobTo.getType())) {
        throw new RuntimeException("Forbidden mob move");
      }

      stepsUsed = max(stepsUsed, mob.getRemainingSteps() - mobTo.getRemainingSteps());
      mob.addMobs(mobTo.getMobsAmount());
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

  private void makeFight(Coords from, Coords to) {
    int stepsUsed = board.findPath(from, to).size() - 1;
    Mob mob = board.getField(from).getMob();
    Mob mobTo = board.getField(to).getMob();
    mob.reduceRemainingSteps(stepsUsed);
    setMob(from, null);
    Pair<Integer, Integer> result = fightSystem.makeFight(mob, mobTo);
    mob.addMobs(result.getKey() - mob.getMobsAmount());
    mobTo.addMobs(result.getValue() - mobTo.getMobsAmount());
    if (result.getKey() > result.getValue()) {
      setMob(to, mob);
    } else {
      if (mobTo.getMobsAmount() <= 0)
        mobTo = null;
      setMob(to, mobTo);
    }
  }

  private void attackBuilding(Coords mobCoords, Coords buildingCoords) {
    Mob mob = board.getField(mobCoords).getMob();
    Building building = board.getField(buildingCoords).getBuilding();

    if (!mob.canAttackInThisTurn()) {
      return;
    }
    mob.makeAttack();
    building.reduceLifeRemaining(mob.getMobsAmount() * mob.getMobsAttack());
    if (building.getLifeRemaining() == 0) {
      Field fieldTo = board.getField(buildingCoords);
      board.setField(buildingCoords, board.produceField(
          fieldTo.getBackground(),
          null,
          fieldTo.getMob(),
          fieldTo.isFree()
      ));
    }
  }

  public Mob produceMob(JsonObject jsonMob, List<Player> players) {
    Mob mob = mobFactory.produce(jsonMob, players);
    addMob(mob);
    return mob;
  }

  public Mob produceMob(String type, int mobsAmount, Player owner) {
    Mob newMob = mobFactory.produce(type, mobsAmount, owner);
    addMob(newMob);
    return newMob;
  }

  public void setMob(Coords coords, Mob mob) {
    Field oldField = board.getField(coords);

    Field newField = board.produceField(
            oldField.getBackground(),
            oldField.getBuilding(),
            mob,
            oldField.isFree()
    );

    board.setField(coords, newField);
  }

  void addMob(Mob mob){
    mobList.add(mob);
  }

  public FightSystem getFightSystem() {
    return fightSystem;
  }

  private void parseMobs(Board board) {
    for (int i = 0; i < board.getRows(); ++i)
      for (int j = 0; j < board.getColumns(); ++j) {
        Field field = board.getField(new Coords(i, j));
        Mob mob = field.getMob();
        if (mob != null)
          addMob(mob);
      }
  }
}
