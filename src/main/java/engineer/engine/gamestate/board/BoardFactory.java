package engineer.engine.gamestate.board;

import engineer.engine.gamestate.building.Building;
import engineer.engine.gamestate.building.BuildingFactory;
import engineer.engine.gamestate.field.Field;
import engineer.engine.gamestate.field.FieldFactory;
import engineer.engine.gamestate.mob.Mob;
import engineer.utils.Coords;

import java.util.LinkedList;
import java.util.List;

public class BoardFactory {
  private final FieldFactory fieldFactory;
  private final BuildingFactory buildingFactory;

  private static class BoardImpl implements Board {
    private final int rows, columns;
    private final Field[][] fields;

    private Coords selectedField;

    private final List<Observer> observerList = new LinkedList<>();

    public BoardImpl(int rows, int columns) {
      this.rows = rows;
      this.columns = columns;
      fields = new Field[rows][columns];
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
      fields[coords.row()][coords.column()] = field;
      onFieldChanged(coords);
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
