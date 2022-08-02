package engineer.engine.gamestate;

import engineer.utils.Box;
import engineer.utils.Pair;

import java.util.LinkedList;
import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Camera {
  public interface Observer {
    void onCameraMove();
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
  @SuppressWarnings("FieldMayBeFinal")
  private double width, height;
  @SuppressWarnings("FieldMayBeFinal")
  private double fieldSize;

  private void onCameraMove() {
    observerList.forEach(Observer::onCameraMove);
  }

  public Camera(int rows, int columns, double viewWidth, double viewHeight) {
    this.rows = rows;
    this.columns = columns;

    fieldSize = max(viewWidth / columns, viewHeight / rows);

    width = viewWidth;
    height = viewHeight;

    offsetX = (columns * fieldSize - width) / 2;
    offsetY = (rows * fieldSize - height) / 2;
  }

  public Box getFieldBox(int row, int column) {
    return new Box(
            column*fieldSize - offsetX,
            row*fieldSize- offsetY,
            fieldSize,
            fieldSize
    );
  }

  public Pair getFieldByPoint(double x, double y) {
    int row = (int) ((x + offsetX) / fieldSize);
    int column = (int) ((y + offsetY) / fieldSize);
    return new Pair(row, column);
  }

  public boolean isFieldVisible(int row, int column) {
    Box box = getFieldBox(row, column);
    return (box.right() > 0 && box.left() < width)
            && (box.bottom() > 0 && box.top() < height);
  }

  @SuppressWarnings("unused")
  public Box getCameraBox() {
    return new Box(offsetX, offsetY, width, height);
  }

  public void moveCamera(double dx, double dy) {
    offsetX = max(0.0, min(columns*fieldSize - width, offsetX + dx));
    offsetY = max(0.0, min(rows*fieldSize - height, offsetY + dy));
    onCameraMove();
  }
}
