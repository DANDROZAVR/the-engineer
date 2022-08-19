package engineer.engine.presenters.game;

import engineer.engine.gamestate.GameState;
import engineer.engine.gamestate.board.Board;
import engineer.engine.gamestate.building.Building;
import engineer.engine.gamestate.field.Field;
import engineer.engine.gamestate.mob.FightSystem;
import engineer.engine.gamestate.mob.Mob;
import engineer.utils.Coords;

import java.util.ArrayList;
import java.util.List;

public class ContextMenuPresenter {
  public interface View {
    void showGeneralInfoWindow(String playerName);
    void showBuildingsChosenWindow(String picture, String name);
    void showBuildingInfoWindow(String picture, String name);
    void showBuildingsListWindow(List<Building> buildings);
    void startFight();

    void showFight(List<String> newItems);
  }

  private final GameState gameState;
  private final Board board;
  private final View view;
  private List<Building> tempListOfAllBuildings;
  public ContextMenuPresenter(GameState gameState, View view) {
    this.gameState = gameState;
    board = gameState.getBoard();
    this.view = view;
  }

  private Building chosenBuilding;

  private final Board.Observer boardObserver = new Board.Observer() {
    @Override
    public void onSelectionChanged(Coords coords) {
      onFieldSelection(coords);
    }
  };

  private final FightSystem.Observer fightObserver = new FightSystem.Observer() {
    @Override
    public void onFightStart(Mob attacker, Mob defender){
      view.startFight();
      List<String> list = new ArrayList<>();
      list.add("FightSystem between: ");
      list.add(attacker.getType() + " x " + attacker.getMobsAmount() +
              " and " + defender.getType() + " x " + defender.getMobsAmount());
      list.add("remaining hp after each round:");
      view.showFight(list);
    }
    @Override
    public void onFightTurn(Integer attack, Integer defence){
      List<String> list = new ArrayList<>();
      list.add(attack.toString() + "   " + defence.toString());
      view.showFight(list);
    }
  };

  public void start() {
    gameState.getMobsController().getFight().addObserver(fightObserver);
    board.addObserver(boardObserver);
  }

  public void close() {
    board.removeObserver(boardObserver);
    gameState.getMobsController().getFight().removeObserver(fightObserver);
  }

  public void onFieldSelection(Coords coords) {
    Field field = board.getField(coords);
    if(field.getMob() != null){
      return;
    }
    if (field.getBuilding() == null) {
      // we should somehow understand that field is covered with some resources
      onShowBuildingsList();
    } else {
      view.showBuildingInfoWindow(field.getBuilding().getPicture(), field.getBuilding().getPicture());
    }
  }

  public void onShowGeneralInfo() {
    String nickname = gameState.getTurnSystem().getCurrentPlayer().getNickname();
    view.showGeneralInfoWindow(nickname);
  }

  public void onBuildingChoose(int index){
    Building building = tempListOfAllBuildings.get(index);
    chosenBuilding = null;
    if (building != null) {
      chosenBuilding = building;
      view.showBuildingsChosenWindow(building.getPicture(), building.getPicture());
    }
  }

  public void onShowBuildingsList() {
    tempListOfAllBuildings = gameState.getAllBuildingsList();
    view.showBuildingsListWindow(tempListOfAllBuildings);
  }

  public void onBuild() {
    Coords selectedField = board.getSelectedCoords();
    if (chosenBuilding != null && selectedField != null) {
      gameState.build(selectedField, chosenBuilding.getPicture());
      view.showBuildingInfoWindow(chosenBuilding.getPicture(), chosenBuilding.getPicture());
    }
  }
}
