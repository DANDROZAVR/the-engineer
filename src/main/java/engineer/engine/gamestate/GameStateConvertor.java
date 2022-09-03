package engineer.engine.gamestate;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import engineer.engine.gamestate.board.Board;
import engineer.engine.gamestate.building.Building;
import engineer.engine.gamestate.field.Field;
import engineer.engine.gamestate.mob.Mob;
import engineer.engine.gamestate.resource.Resource;
import engineer.engine.gamestate.turns.Player;
import engineer.utils.Coords;

import java.util.List;

public class GameStateConvertor {
  public static JsonObject produceJsonFromBoard(Board board, List<Player> players) {
    JsonObject jsonResult = new JsonObject();
    jsonResult.addProperty("rows", board.getRows());
    jsonResult.addProperty("columns", board.getColumns());
    JsonArray jsonPlayers = new JsonArray();
    for (Player player : players)
      jsonPlayers.add(produceJsonFromPlayer(player));
    jsonResult.add("players", jsonPlayers);

    JsonArray jsonBoard = new JsonArray();
    for (int row = 0; row < board.getRows(); row++) {
      JsonArray jsonColumn = new JsonArray();
      for (int column = 0; column < board.getColumns(); column++) {
        JsonObject jsonField = new JsonObject();
        Field field = board.getField(new Coords(row, column));
        Building building = field.getBuilding();
        JsonObject jsonBuilding = produceJsonFromBuilding(building);
        JsonObject jsonMob = produceJsonFromMob(field.getMob());

        jsonField.add("background", JsonParser.parseString(field.getBackground()));
        jsonField.add("building", jsonBuilding);
        jsonField.add("mob", jsonMob);
        jsonField.addProperty("free", field.isFree());
        jsonColumn.add(jsonField);
      }
      jsonBoard.add(jsonColumn);
    }
    jsonResult.add("board", jsonBoard);
    return jsonResult;
  }

  private static JsonObject produceJsonFromBuilding(Building building) {
    if (building == null) return new JsonObject();
    JsonObject buildingJson = new JsonObject();
    buildingJson.addProperty("type", building.getType().trim());
    buildingJson.add("owner", JsonParser.parseString(building.getOwner().getNickname()));
    buildingJson.addProperty("life_remaining", building.getLifeRemaining());
    buildingJson.addProperty("level", building.getLevel());
    return buildingJson;
  }

  private static JsonObject produceJsonFromResource(Resource resource) {
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("type", resource.getType());
    jsonObject.addProperty("res_amount", resource.getResAmount());
    return jsonObject;
  }

  private static JsonObject produceJsonFromMob(Mob mob) {
    if (mob == null) return new JsonObject();
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("type", mob.getType());
    jsonObject.addProperty("mobs_amount", mob.getMobsAmount());
    jsonObject.addProperty("player", mob.getOwner().getNickname());
    return jsonObject;
  }

  private static JsonObject produceJsonFromPlayer(Player player) {
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("nickname", player.getNickname());
    JsonArray jsonArray = new JsonArray();
    for (Resource resource : player.getResources())
      jsonArray.add(produceJsonFromResource(resource));
    jsonObject.add("resources", jsonArray);
    return jsonObject;
  }
}
