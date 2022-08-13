package engineer.engine.gamestate.board;

import engineer.engine.gamestate.building.Building;
import engineer.engine.gamestate.building.BuildingFactory;
import engineer.engine.gamestate.field.Field;
import engineer.engine.gamestate.field.FieldFactory;
import engineer.engine.gamestate.mob.Mob;
import engineer.engine.gamestate.mob.MobFactory;

import java.util.LinkedList;
import java.util.List;

public class BoardFactory {
  private final FieldFactory fieldFactory;
  private final BuildingFactory buildingFactory;
  private final MobFactory mobFactory;

  private static class BoardImpl implements Board {
    private final int rows, columns;
    private final Field[][] fields;

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

    private void onFieldChanged(int row, int column) {
      observerList.forEach(o -> o.onFieldChanged(row, column));
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
    public Field getField(int row, int column) {
      return fields[row][column];
    }

    @Override
    public void setField(int row, int column, Field field) {
      fields[row][column] = field;
      onFieldChanged(row, column);
    }
  }

  public BoardFactory(FieldFactory fieldFactory, BuildingFactory buildingFactory, MobFactory mobFactory) {
    this.fieldFactory = fieldFactory;
    this.buildingFactory = buildingFactory;
    this.mobFactory = mobFactory;
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
  public Mob produceMob(String picture, int number) {
    return mobFactory.produce(picture, number);
  }

}
