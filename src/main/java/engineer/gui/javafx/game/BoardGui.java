package engineer.gui.javafx.game;

import engineer.engine.gamestate.GameState;
import engineer.engine.presenters.BoardPresenter;
import engineer.gui.javafx.KeyHandler;
import engineer.gui.javafx.TextureManager;
import engineer.gui.javafx.controllers.MouseController;
import engineer.utils.Box;
import javafx.animation.AnimationTimer;
import javafx.event.EventType;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;

public class BoardGui implements BoardPresenter.View {
  private static final double cameraSpeed = 500;

  private final GraphicsContext gc;
  private final BoardPresenter presenter;

  private final MouseController mouseController;

  private final TextureManager textureManager;

  public BoardGui(Canvas canvas, TextureManager textureManager, GameState gameState) {
    this.gc = canvas.getGraphicsContext2D();
    this.textureManager = textureManager;

    mouseController = new MouseController();
    mouseController.setObserver(new BoardPresenterObserver());
    presenter = new BoardPresenter(gameState, this);
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

  public void start() {
    mouseController.setObserver(new BoardPresenterObserver());
    timer.start();
  }

  public void close() {
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
