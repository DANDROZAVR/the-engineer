package engineer.engine.gamestate;

import engineer.utils.Box;
import engineer.utils.Coords;

import java.util.LinkedList;
import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Camera {
  public interface Observer {
    void onCameraUpdate();
  }

  List<Observer> observerList = new LinkedList<>();

  public void addObserver(Observer observer) {
    observerList.add(observer);
  }

  public void removeObserver(Observer observer) {
    observerList.remove(observer);
  }



  private final int rows, columns;

  private double offsetX, offsetY;
  private final double width, height;

  private double fieldSize;
  @SuppressWarnings("FieldCanBeLocal")
  private final double minFieldSize;

  private void onCameraUpdate() {
    observerList.forEach(Observer::onCameraUpdate);
  }

  public Camera(int rows, int columns, double viewWidth, double viewHeight) {
    this.rows = rows;
    this.columns = columns;

    fieldSize = max(viewWidth / columns, viewHeight / rows);
    minFieldSize = fieldSize;

    width = viewWidth;
    height = viewHeight;

    offsetX = (columns * fieldSize - viewWidth) / 2;
    offsetY = (rows * fieldSize - viewHeight) / 2;
  }

  public void zoom(double scale) {
    fieldSize = max(minFieldSize, fieldSize * scale);
    onCameraUpdate();
  }

  public Box getFieldBox(Coords coords) {
    return new Box(
            coords.column()*fieldSize - offsetX,
            coords.row()*fieldSize - offsetY,
            fieldSize,
            fieldSize
    );
  }

  public Coords getFieldByPoint(double x, double y) {
    int column = (int) ((x + offsetX) / fieldSize);
    int row = (int) ((y + offsetY) / fieldSize);
    return new Coords(row, column);
  }

  public boolean isFieldVisible(Coords coords) {
    Box box = getFieldBox(coords);
    return (box.right() > 0 && box.left() < width)
            && (box.bottom() > 0 && box.top() < height);
  }

  public Box getCameraBox() {
    return new Box(
            offsetX / fieldSize,
            offsetY / fieldSize,
            width / fieldSize,
            height / fieldSize
    );
  }

  public void moveCamera(double dx, double dy) {
    offsetX = max(0.0, min(columns*fieldSize - width, offsetX + dx));
    offsetY = max(0.0, min(rows*fieldSize - height, offsetY + dy));
    onCameraUpdate();
  }
}
