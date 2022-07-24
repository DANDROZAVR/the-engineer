package engineer.gui.javafx;

import engineer.engine.board.controllers.MouseController;
import engineer.engine.board.presenter.BoardPresenter;
import engineer.engine.board.presenter.Box;
import engineer.gui.TextureManager;
import javafx.animation.AnimationTimer;
import javafx.event.EventType;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;

public class BoardGui implements BoardPresenter.View {
  private static final double cameraSpeed = 500;

  private final GraphicsContext gc;
  private BoardPresenter presenter;

  private MouseController mouseController;

  private final TextureManager textureManager;

  public BoardGui(GraphicsContext gc, TextureManager textureManager) {
    this.gc = gc;
    this.textureManager = textureManager;
  }

  @Override
  public double getViewHeight() {
    return gc.getCanvas().getHeight();
  }

  @Override
  public double getViewWidth() {
    return gc.getCanvas().getWidth();
  }

  @Override
  public void drawField(Box box, String texture) {
    gc.drawImage(
        textureManager.getTexture(texture), box.left(), box.top(), box.width(), box.height());
  }

  @Override
  public void drawSelection(Box box) {
    gc.drawImage(
        textureManager.getTexture("tileSelection"),
        box.left(),
        box.top(),
        box.width(),
        box.height());
  }

  private final AnimationTimer timer =
      new AnimationTimer() {
        private static final long NANOS_IN_SEC = 1_000_000_000;
        private long last = -1;

        @Override
        public void handle(long now) {
          if (last != -1) presenter.update((double) (now - last) / NANOS_IN_SEC);
          last = now;
        }
      };

  public void start(BoardPresenter presenter, MouseController mouseController) {
    this.presenter = presenter;
    this.mouseController = mouseController;
    mouseController.setObserver(new BoardPresenterObserver());
    timer.start();
  }

  public void close() {
    presenter = null;
    timer.stop();
  }

  public KeyHandler getKeyHandler() {
    return (code, pressed) -> {
      switch (code) {
        case LEFT -> presenter.setCameraSpeedX(pressed ? -cameraSpeed : 0);
        case RIGHT -> presenter.setCameraSpeedX(pressed ? cameraSpeed : 0);
        case UP -> presenter.setCameraSpeedY(pressed ? -cameraSpeed : 0);
        case DOWN -> presenter.setCameraSpeedY(pressed ? cameraSpeed : 0);
        case I -> {
          if (pressed) presenter.zoomIn();
        }
        case O -> {
          if (pressed) presenter.zoomOut();
        }
      }
    };
  }

  // Temporary solution
  public void onButtonClicked(String button) {
    presenter.setPressedButton(button);
  }

  public void onMouseEvent(EventType<MouseEvent> eventType, MouseEvent mouseEvent) {
    mouseController.onMouseEvent(eventType, mouseEvent);
    // presenter.changeContent(mouseEvent.getX(), mouseEvent.getY());
  }

  class BoardPresenterObserver implements MouseController.Observer {
    private BoardPresenter.SimpleMouseButton convertButtonType(
        MouseController.SimpleMouseButton button) {
      return BoardPresenter.SimpleMouseButton.valueOf(button.toString());
    }

    @Override
    public void onMouseMoved(double eventX, double eventY) {
      presenter.onMouseMoved(eventX, eventY);
    }

    @Override
    public void onMouseClick(
        MouseController.SimpleMouseButton button, int clicksNumber, double eventX, double eventY) {
      presenter.onMouseClick(convertButtonType(button), clicksNumber, eventX, eventY);
    }

    @Override
    public void onMousePressed(
        MouseController.SimpleMouseButton button, double eventX, double eventY) {
      presenter.onMousePressed(convertButtonType(button), eventX, eventY);
    }

    @Override
    public void onMouseReleased(
        MouseController.SimpleMouseButton button, double eventX, double eventY) {
      presenter.onMouseReleased(convertButtonType(button), eventX, eventY);
    }

    @Override
    public void onMouseDragged(
        MouseController.SimpleMouseButton button, double eventX, double eventY) {
      presenter.onMouseDragged(convertButtonType(button), eventX, eventY);
    }
  }
}
