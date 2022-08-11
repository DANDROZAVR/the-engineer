package engineer.gui.javafx.controllers;

import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MouseController implements EventHandler<MouseEvent> {
  private static final List<EventType<MouseEvent>> supportedEvents = Arrays.asList(
          MouseEvent.MOUSE_CLICKED,
          MouseEvent.MOUSE_ENTERED,
          MouseEvent.MOUSE_EXITED,
          MouseEvent.MOUSE_MOVED
  );

  public interface Observer {
    default void onMouseClick(double x, double y) {}
    default void onMouseEnter() {}
    default void onMouseExit() {}
    default void onMouseMove(double x, double y) {}
    default void onMouseScroll(double delta) {}
  }

  private final List<Observer> observerList = new LinkedList<>();

  public void addObserver(Observer observer) {
    observerList.add(observer);
  }

  public void removeObserver(Observer observer) {
    observerList.remove(observer);
  }

  public MouseController(Node node) {
    supportedEvents.forEach(event -> node.addEventHandler(event, this));
    node.setOnScroll(this::handleScroll);
  }

  private void handleScroll(ScrollEvent event) {
    observerList.forEach(o -> o.onMouseScroll(event.getDeltaY()));
  }

  @Override
  public void handle(MouseEvent event) {
    EventType<? extends MouseEvent> type = event.getEventType();

    if (type == MouseEvent.MOUSE_CLICKED) {
      if (event.getButton() == MouseButton.PRIMARY)
        observerList.forEach(o -> o.onMouseClick(event.getX(), event.getY()));
    } else if (type == MouseEvent.MOUSE_ENTERED) {
      observerList.forEach(Observer::onMouseEnter);
    } else if (type == MouseEvent.MOUSE_EXITED) {
      observerList.forEach(Observer::onMouseExit);
    } else if (type == MouseEvent.MOUSE_MOVED) {
      observerList.forEach(o -> o.onMouseMove(event.getX(), event.getY()));
    } else {
      throw new RuntimeException("Unsupported mouse event type " + type);
    }
  }
}
