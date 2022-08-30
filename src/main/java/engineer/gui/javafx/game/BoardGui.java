package engineer.gui.javafx.game;

import engineer.engine.gamestate.Camera;
import engineer.engine.gamestate.board.Board;
import engineer.engine.presenters.game.BoardPresenter;
import engineer.gui.javafx.TextureManager;
import engineer.gui.javafx.controllers.MouseController;
import engineer.utils.Box;
import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;

public class BoardGui implements BoardPresenter.View, MouseController.Observer {
  private final Canvas canvas;
  private final BoardPresenter presenter;

  private final TextureManager textureManager;
  private final MouseController mouseController;

  public BoardGui(Canvas canvas, TextureManager textureManager, Board board, Camera camera) {
    this.canvas = canvas;
    this.textureManager = textureManager;

    presenter = new BoardPresenter(board, camera, this);
    mouseController = new MouseController(canvas);
  }

  @Override
  public void drawField(Box box, String texture) {
    GraphicsContext gc = canvas.getGraphicsContext2D();
    gc.drawImage(textureManager.getTexture(texture), box.left(), box.top(), box.width(), box.height());
  }

  @Override
  public void drawSelection(Box box) {
    GraphicsContext gc = canvas.getGraphicsContext2D();
    gc.drawImage(textureManager.getTexture("tileSelection"), box.left(), box.top(), box.width(), box.height());
  }

  @Override
  public void markField(Box box) {
    GraphicsContext gc = canvas.getGraphicsContext2D();
    gc.drawImage(textureManager.getTexture("tileEnlighten"), box.left(), box.top(), box.width(), box.height());
  }

  @Override
  public void attackField(Box box) {
    GraphicsContext gc = canvas.getGraphicsContext2D();
    gc.drawImage(textureManager.getTexture("tileAttack"), box.left(), box.top(), box.width(), box.height());
  }

  private static final double CAMERA_SPEED_SCALE = 1000.0;
  private double cameraUp, cameraDown;
  private double cameraLeft, cameraRight;

  private final AnimationTimer timer = new AnimationTimer() {
    private static final long NANOS_IN_SEC = 1_000_000_000;
    private long last = -1;

    @Override
    public void handle(long now) {
      if (last != -1) {
        double delta = (double) (now - last) / NANOS_IN_SEC;
        presenter.moveCamera(
                (cameraRight - cameraLeft) * delta * CAMERA_SPEED_SCALE,
                (cameraDown - cameraUp) * delta * CAMERA_SPEED_SCALE
        );
      }
      last = now;
    }
  };

  private final EventHandler<KeyEvent> onKeyPressed = event -> {
    switch (event.getCode()) {
      case UP, W -> cameraUp = 1.0;
      case DOWN, S -> cameraDown = 1.0;
      case LEFT, A -> cameraLeft = 1.0;
      case RIGHT, D -> cameraRight = 1.0;
    }

    event.consume();
  };

  private final EventHandler<KeyEvent> onKeyReleased = event -> {
    switch (event.getCode()) {
      case UP, W -> cameraUp = 0.0;
      case DOWN, S -> cameraDown = 0.0;
      case LEFT, A -> cameraLeft = 0.0;
      case RIGHT, D -> cameraRight = 0.0;
    }

    event.consume();
  };

  @Override
  public void onMouseScroll(double delta) {
    presenter.zoomCamera(1.0+delta / 1000.0);
  }

  @Override
  public void onMouseClick(double x, double y) {
    presenter.selectField(x, y);
  }



  public void start() {
    timer.start();
    presenter.start();
    mouseController.addObserver(this);

    canvas.getScene().addEventHandler(KeyEvent.KEY_PRESSED, onKeyPressed);
    canvas.getScene().addEventHandler(KeyEvent.KEY_RELEASED, onKeyReleased);
  }

  public void close() {
    timer.stop();
    presenter.close();
    mouseController.removeObserver(this);

    canvas.getScene().removeEventHandler(KeyEvent.KEY_PRESSED, onKeyPressed);
    canvas.getScene().removeEventHandler(KeyEvent.KEY_RELEASED, onKeyReleased);
  }
}
