package engineer.engine.gamestate.board;

import engineer.engine.gamestate.building.Building;
import engineer.engine.gamestate.building.BuildingFactory;
import engineer.engine.gamestate.field.Field;
import engineer.engine.gamestate.field.FieldFactory;
import engineer.engine.gamestate.mob.Mob;
import engineer.utils.Coords;

import java.util.*;
import java.util.stream.Stream;

public class BoardFactory {
  private final FieldFactory fieldFactory;
  private final BuildingFactory buildingFactory;

  private class BoardImpl implements Board {
    private final int rows, columns;
    private final Field[][] fields;
    private final Collection<Coords> markedFields = new HashSet<>();
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
      observerList.add(0, observer);
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
      if(fields[coords.row()][coords.column()].getMob() != null){
        onMobRemoved(fields[coords.row()][coords.column()].getMob());
      }

      fields[coords.row()][coords.column()] = field;
      onFieldChanged(coords);

      if(fields[coords.row()][coords.column()].getMob() != null){
        onMobAdded(fields[coords.row()][coords.column()].getMob());
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

    private List<Coords> getNeighbours(Coords v) {
      return Stream.of(
              new Coords(v.row()-1, v.column()),
              new Coords(v.row()+1, v.column()),
              new Coords(v.row(), v.column()-1),
              new Coords(v.row(), v.column()+1)
      ).filter(c ->
              0 <= c.row() && c.row() < rows
              && 0 <= c.column() && c.column() < columns
              && getField(c).isFree()
      ).toList();
    }

    private Integer[][] getDistanceFrom(Coords start) {
      Integer[][] distance = new Integer[rows][columns];

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
        for (Coords u : getNeighbours(v)) {
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

      Set<Coords> result = new HashSet<>();
      for (int row=0; row<rows; row++) {
        for (int column=0; column<columns; column++) {
          if (distanceArray[row][column] <= range) {
            result.add(new Coords(row, column));
          }
        }
      }

      return result;
    }

    @Override
    public List<Coords> findPath(Coords start, Coords finish) {
      Integer[][] distanceArray = getDistanceFrom(start);
      int distance = distanceArray[finish.row()][finish.column()];

      if (distance == Integer.MAX_VALUE) {
        return null;
      }

      List<Coords> result = new ArrayList<>(distance+1);
      result.add(finish);

      while (!start.equals(finish)) {
        for (Coords c : getNeighbours(finish)) {
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
    public void markFields(Collection<Coords> collection) {
      if (collection != null) {
        markedFields.addAll(collection);
      }
    }

    @Override
    public void unmarkAllFields() {
      markedFields.clear();
    }

    @Override
    public Collection<Coords> getMarkedFields() {
      return markedFields;
    }

    @Override
    public Field produceField(String background, Building building, Mob mob, boolean free) {
      return fieldFactory.produce(background, building, mob, free);
    }
  }

  public BoardFactory(FieldFactory fieldFactory, BuildingFactory buildingFactory) {
    this.fieldFactory = fieldFactory;
    this.buildingFactory = buildingFactory;
  }

  public Board produceBoard(int rows, int columns) {
    return new BoardImpl(rows, columns);
  }

  public Field produceField(String background, Building building, Mob mob, boolean free) {
    return fieldFactory.produce(background, building, mob, free);
  }

  public Building produceBuilding(String picture) {
    return buildingFactory.produce(picture);
  }
}
