package engineer.engine.board.controllers;

import javafx.event.EventType;
import javafx.scene.input.MouseEvent;

public class MouseController {
  public interface Observer {
    void onMouseMoved(double eventX, double eventY);

    void onMouseClick(SimpleMouseButton button, int clicksNumber, double eventX, double eventY);

    void onMousePressed(SimpleMouseButton button, double eventX, double eventY);

    void onMouseReleased(SimpleMouseButton button, double eventX, double eventY);

    void onMouseDragged(SimpleMouseButton button, double eventX, double eventY);
  }

  public enum SimpleMouseButton {
    PRIMARY,
    SECONDARY,
    MIDDLE,
    BACK,
    FORWARD,
    MOVED,
    NONE
  }

  private Observer observer;

  public void onMouseEvent(EventType<MouseEvent> eventType, MouseEvent mouseEvent) {
    if (observer == null) return;
    SimpleMouseButton buttonType = SimpleMouseButton.valueOf(mouseEvent.getButton().toString());
    if (eventType == MouseEvent.MOUSE_MOVED) {
      observer.onMouseMoved(mouseEvent.getX(), mouseEvent.getY());
    } else if (eventType == MouseEvent.MOUSE_CLICKED) {
      observer.onMouseClick(
          buttonType, mouseEvent.getClickCount(), mouseEvent.getX(), mouseEvent.getY());
    } else if (eventType == MouseEvent.MOUSE_PRESSED) {
      observer.onMousePressed(buttonType, mouseEvent.getX(), mouseEvent.getY());
    } else if (eventType == MouseEvent.MOUSE_RELEASED) {
      // can be used for moving choosing trips end-point
      observer.onMouseReleased(buttonType, mouseEvent.getX(), mouseEvent.getY());
    } else if (eventType == MouseEvent.MOUSE_DRAGGED) {
      observer.onMouseDragged(buttonType, mouseEvent.getX(), mouseEvent.getY());
    } else {
      System.err.println("unsupported mouse event" + eventType.toString());
    }
  }

  public void setObserver(Observer observer) {
    this.observer = observer;
  }
}
