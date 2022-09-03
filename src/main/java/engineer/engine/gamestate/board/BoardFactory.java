package engineer.engine.gamestate.board;

import com.google.gson.JsonObject;
import engineer.engine.gamestate.building.Building;
import engineer.engine.gamestate.building.BuildingFactory;
import engineer.engine.gamestate.field.Field;
import engineer.engine.gamestate.field.FieldFactory;
import engineer.engine.gamestate.mob.Mob;
import engineer.engine.gamestate.mob.MobFactory;
import engineer.engine.gamestate.turns.Player;
import engineer.utils.Coords;

import java.util.*;
import java.util.stream.Stream;

public class BoardFactory {
  private final FieldFactory fieldFactory;

  private class BoardImpl implements Board {
    private final int rows, columns;
    private final Field[][] fields;
    private final Collection<Coords> markedFieldsToMove = new HashSet<>();
    private final Collection<Coords> markedFieldsToAttack = new HashSet<>();
    private Coords selectedField;

    private final List<Observer> observerList = new LinkedList<>();


    public BoardImpl(int rows, int columns) {
      this.rows = rows;
      this.columns = columns;
      fields = new Field[rows][columns];

      for (int row=0; row<rows; row++) {
        for (int column=0; column<columns; column++) {
          fields[row][column] = produceField(null, null, null, true);
        }
      }
    }

    @Override
    public void addObserver(Observer observer) {
      observerList.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
      observerList.remove(observer);
    }

    private void onFieldChanged(Coords coords) {
      observerList.forEach(o -> o.onFieldChanged(coords));
    }

    private void onMobRemoved(Mob mob) {
      observerList.forEach(o -> o.onMobRemoved(mob));
    }

    private void onMobAdded(Mob mob) {
      observerList.forEach(o -> o.onMobAdded(mob));
    }

    private void onBuildingRemoved(Building building) {
      observerList.forEach(o -> o.onBuildingRemoved(building));
    }

    private void onBuildingAdded(Building building) {
      observerList.forEach(o -> o.onBuildingAdded(building));
    }

    private void onSelectionChanged() {
      observerList.forEach(o -> o.onSelectionChanged(selectedField));
    }

    @Override
    public int getRows() {
      return rows;
    }

    @Override
    public int getColumns() {
      return columns;
    }

    @Override
    public Field getField(Coords coords) {
      return fields[coords.row()][coords.column()];
    }

    @Override
    public void setField(Coords coords, Field field) {
      if (fields[coords.row()][coords.column()].getMob() != null) {
        onMobRemoved(fields[coords.row()][coords.column()].getMob());
      }
      if (fields[coords.row()][coords.column()].getBuilding() != null) {
        onBuildingRemoved(fields[coords.row()][coords.column()].getBuilding());
      }

      fields[coords.row()][coords.column()] = field;
      onFieldChanged(coords);

      if (fields[coords.row()][coords.column()].getMob() != null) {
        onMobAdded(fields[coords.row()][coords.column()].getMob());
      }
      if (fields[coords.row()][coords.column()].getBuilding() != null) {
        onBuildingAdded(fields[coords.row()][coords.column()].getBuilding());
      }
    }

    @Override
    public void selectField(Coords coords) {
      selectedField = coords;
      onSelectionChanged();
    }

    @Override
    public Coords getSelectedCoords() {
      return selectedField;
    }

    private List<Coords> getNeighbours(Coords v, Player player) {
      return Stream.of(
              new Coords(v.row()-1, v.column()),
              new Coords(v.row()+1, v.column()),
              new Coords(v.row(), v.column()-1),
              new Coords(v.row(), v.column()+1)
      ).filter(c ->
              0 <= c.row() && c.row() < rows
              && 0 <= c.column() && c.column() < columns
              && getField(c).isFree()
              && (getField(c).getOwner() == null || getField(c).getOwner().equals(player))
      ).toList();
    }

    private Integer[][] getDistanceFrom(Coords start) {
      Integer[][] distance = new Integer[rows][columns];
      Player player = getField(start).getOwner();

      for (int row=0; row<rows; row++) {
        for (int column=0; column<columns; column++) {
          distance[row][column] = Integer.MAX_VALUE;
        }
      }

      Queue<Coords> queue = new LinkedList<>();
      queue.add(start);
      distance[start.row()][start.column()] = 0;

      Coords v;
      while ((v = queue.poll()) != null) {
        for (Coords u : getNeighbours(v, player)) {
          if (distance[u.row()][u.column()] == Integer.MAX_VALUE) {
            queue.add(u);
            distance[u.row()][u.column()] = distance[v.row()][v.column()] + 1;
          }
        }
      }

      return distance;
    }

    @Override
    public Collection<Coords> getNearestFields(Coords coords, int range) {
      Integer[][] distanceArray = getDistanceFrom(coords);

      if (!getField(coords).isFree()) {
        return null;
      }

      Set<Coords> resultOwner = new HashSet<>();
      for (int row=0; row<rows; row++) {
        for (int column=0; column<columns; column++) {
          if (distanceArray[row][column] <= range && fields[row][column].getBuilding() == null) {
            resultOwner.add(new Coords(row, column));
          }
        }
      }

      return resultOwner;
    }

    @Override
    public Collection<Coords> getFieldsToAttack(Coords v, Player player) {
      return Stream.of(
              new Coords(v.row()-1, v.column()),
              new Coords(v.row()+1, v.column()),
              new Coords(v.row(), v.column()-1),
              new Coords(v.row(), v.column()+1)
      ).filter(c ->
              0 <= c.row() && c.row() < rows
                      && 0 <= c.column() && c.column() < columns
                      && getField(c).getOwner() != null
                      && !player.equals(getField(c).getOwner())
      ).toList();
    }

    @Override
    public List<Coords> findPath(Coords start, Coords finish) {
      Integer[][] distanceArray = getDistanceFrom(start);
      int distance = distanceArray[finish.row()][finish.column()];
      Player player = getField(start).getOwner();

      if (distance == Integer.MAX_VALUE) {
        return null;
      }

      List<Coords> result = new ArrayList<>(distance+1);
      result.add(finish);

      while (!start.equals(finish)) {
        for (Coords c : getNeighbours(finish, player)) {
          if (distanceArray[c.row()][c.column()] == distance-1) {
            finish = c;
            break;
          }
        }

        distance--;
        result.add(finish);
      }

      Collections.reverse(result);
      return result;
    }

    @Override
    public void markFields(Collection<Coords> toMove, Collection<Coords> toAttack) {
      if (toMove != null) {
        markedFieldsToMove.addAll(toMove);
      }
      if (toAttack != null) {
        markedFieldsToAttack.addAll(toAttack);
      }
    }

    @Override
    public void unmarkAllFields() {
      markedFieldsToMove.clear();
      markedFieldsToAttack.clear();
    }

    @Override
    public Collection<Coords> getMarkedFieldsToMove() {
      return markedFieldsToMove;
    }

    @Override
    public Collection<Coords> getMarkedFieldsToAttack() {
      return markedFieldsToAttack;
    }

    @Override
    public Field produceField(String background, Building building, Mob mob, boolean free) {
      return fieldFactory.produce(background, building, mob, free);
    }
  }

  public BoardFactory(FieldFactory fieldFactory) {
    this.fieldFactory = fieldFactory;
  }

  public Board produceBoard(JsonObject json, BuildingFactory buildingFactory, MobFactory mobFactory, List<Player> players) {
    int rows = json.get("rows").getAsInt();
    int columns = json.get("columns").getAsInt();
    Board board = new BoardFactory.BoardImpl(rows, columns);

    for (int row = 0; row < board.getRows(); row++) {
      for (int column = 0; column < board.getColumns(); column++) {
        JsonObject fieldJson = json.get("board")
            .getAsJsonArray()
            .get(row)
            .getAsJsonArray()
            .get(column)
            .getAsJsonObject();

        Field field = produceField(
            fieldJson.get("background").getAsString(),
            buildingFactory.produce(fieldJson.get("building").getAsJsonObject(), players),
            mobFactory.produce(fieldJson.get("mob").getAsJsonObject(), players),
            fieldJson.get("free").getAsBoolean()
        );
        board.setField(new Coords(row, column), field);
      }
    }
    return board;
  }

  public Field produceField(String background, Building building, Mob mob, boolean free) {
    return fieldFactory.produce(background, building, mob, free);
  }

  public void build(Board board, Coords coords, Building building) {
    Field field = board.getField(coords);
    Field newField = produceField(
        field.getBackground(),
        building,
        field.getMob(),
        field.isFree()
    );

    board.setField(coords, newField);
  }

  public void destroyBuilding(Board board, Coords coords) {
    Field field = board.getField(coords);
    Field newField = produceField(
        field.getBackground(),
        null,
        field.getMob(),
        field.isFree()
    );

    board.setField(coords, newField);
  }
}
