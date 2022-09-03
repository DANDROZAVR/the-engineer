package engineer.engine.gamestate.mob;

import com.google.gson.JsonObject;
import engineer.engine.gamestate.board.Board;
import engineer.engine.gamestate.building.Building;
import engineer.engine.gamestate.field.Field;
import engineer.engine.gamestate.turns.Player;
import engineer.engine.gamestate.turns.TurnSystem;
import engineer.utils.Coords;
import javafx.util.Pair;

import java.util.*;

import static java.lang.Math.max;

public class MobsController implements TurnSystem.Observer, Board.Observer{
  private final MobFactory mobFactory;
  private final TurnSystem turnSystem;
  private final FightSystem fightSystem;
  private Coords lastSelectedCoords;
  private final Board board;
  private final List<Mob> mobList = new ArrayList<>();

  public MobsController(Board board, TurnSystem turnSystem, MobFactory mobFactory, FightSystem fightSystem) {
    this.board = board;
    this.turnSystem = turnSystem;
    this.mobFactory = mobFactory;
    this.fightSystem = fightSystem;

    parseMobs(board);
    turnSystem.addObserver(this);
    board.addObserver(this);
  }
  @Override
  public void onTurnChange(Player currentPlayer){
    mobList.forEach(Mob::reset);
  }

  public void onSelectionChangedMobs(Coords coords, int numberOfMobsToMove) {
    boolean isReachable = board.getMarkedFieldsToMove().contains(coords);
    boolean isCauseingFight = board.getMarkedFieldsToAttack().contains(coords);
    board.unmarkAllFields();

    Player player = turnSystem.getCurrentPlayer();
    Field field = board.getField(coords);
    Mob mob = field.getMob();

    if (isReachable && lastSelectedCoords != null) {
      moveMob(lastSelectedCoords, coords, numberOfMobsToMove);
    } else if (mob != null && player.equals(mob.getOwner())) {
      Collection<Coords> fieldsToAttack = board.getFieldsToAttack(coords, mob.getOwner()).stream().filter(
                                          c -> (board.getField(c).getMob() != null && mob.getRemainingSteps() > 0)
                                                  || (board.getField(c).getBuilding() != null && mob.canAttackInThisTurn())).toList();
      Collection<Coords> nearestFields = board.getNearestFields(coords, mob.getRemainingSteps());
      board.markFields(nearestFields, fieldsToAttack);
    }

    if (isCauseingFight && lastSelectedCoords != null) {
      if(board.getField(coords).getMob() != null)
        makeFight(lastSelectedCoords, coords, numberOfMobsToMove);
      else if(board.getField(coords).getBuilding() != null)
        attackBuilding(lastSelectedCoords, coords);
    }

    lastSelectedCoords = coords;
  }

  private void moveMob(Coords from, Coords to, int numberOfMobsToMove) {
    if (from.equals(to)) {
      return;
    }

    Field fieldFrom = board.getField(from);
    Field fieldTo = board.getField(to);

    Player player = turnSystem.getCurrentPlayer();

    Mob mob = fieldFrom.getMob();

    if (!mob.getOwner().equals(player)) {
      throw new RuntimeException("Forbidden mob move");
    }

    Mob moving = produceMob(mob.getType(), Math.min(mob.getMobsAmount(), numberOfMobsToMove), mob.getOwner());
    moving.reduceRemainingSteps(moving.getRemainingSteps() - mob.getRemainingSteps());
    mob.reduceMobs(numberOfMobsToMove);
    if (mob.getMobsAmount() <= 0) {
      mob = null;
    }
    int stepsUsed = board.findPath(from, to).size() - 1;

    if (fieldTo.getMob() != null) {
      Mob mobTo = fieldTo.getMob();

      if (!moving.getType().equals(mobTo.getType()) || !player.equals(mobTo.getOwner())) {
        throw new RuntimeException("Forbidden mob move");
      }

      stepsUsed = max(stepsUsed, moving.getRemainingSteps() - mobTo.getRemainingSteps());
      moving.addMobs(mobTo.getMobsAmount());
    }
    moving.reduceRemainingSteps(stepsUsed);

    board.setField(from, board.produceField(
            fieldFrom.getBackground(),
            fieldFrom.getBuilding(),
            mob,
            fieldFrom.isFree()
    ));
    board.setField(to, board.produceField(
            fieldTo.getBackground(),
            fieldTo.getBuilding(),
            moving,
            fieldTo.isFree()
    ));
  }


  void makeFight(Coords from, Coords to, int numberOfMobsToMove) {
    int stepsUsed = 1;
    Mob mob = board.getField(from).getMob();
    Mob attacking = produceMob(mob.getType(), Math.min(mob.getMobsAmount(), numberOfMobsToMove), mob.getOwner());
    Mob mobTo = board.getField(to).getMob();
    attacking.reduceRemainingSteps(attacking.getRemainingSteps() - mob.getRemainingSteps() + stepsUsed);
    mob.reduceMobs(numberOfMobsToMove);
    if (mob.getMobsAmount() <= 0) {
      mob = null;
    }
    setMob(from, mob);
    Pair<Integer, Integer> result = fightSystem.makeFight(attacking, mobTo);
    attacking.addMobs(result.getKey() - attacking.getMobsAmount());
    mobTo.addMobs(result.getValue() - mobTo.getMobsAmount());
    if (result.getKey() > result.getValue()) {
      setMob(to, attacking);
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

  private void addMob(Mob mob) {
    mobList.add(mob);
  }

  public Mob produceMob(JsonObject jsonMob, List<Player> players) {
    Mob mob = mobFactory.produce(jsonMob, players);
    addMob(mob);
    return mob;
  }

  public Mob produceMob(String type, int mobsAmount, Player owner) {
    return mobFactory.produce(type, mobsAmount, owner);
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

  public FightSystem getFightSystem() {
    return fightSystem;
  }

  @Override
  public void onMobAdded(Mob mob){
    mobList.add(mob);
  }

  @Override
  public void onMobRemoved(Mob mob){
    mobList.remove(mob);
  }

  public void makeMob(Coords selectedField, String mobRequestedType, int mobRequestedNumber, Player currentPlayer) {
    setMob(selectedField, produceMob(mobRequestedType, mobRequestedNumber, currentPlayer));
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
