package engineer.gui.javafx.game;

import engineer.engine.gamestate.GameState;
import engineer.engine.presenters.game.BoardPresenter;
import engineer.gui.javafx.TextureManager;
import engineer.gui.javafx.controllers.MouseController;
import engineer.utils.Box;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import static java.lang.Math.abs;

public class BoardGui implements BoardPresenter.View, MouseController.Observer {
  private final Canvas canvas;
  private final BoardPresenter presenter;

  private final TextureManager textureManager;
  private final MouseController mouseController;

  public BoardGui(Canvas canvas, TextureManager textureManager, GameState gameState) {
    this.canvas = canvas;
    this.textureManager = textureManager;

    presenter = new BoardPresenter(gameState, this);
    mouseController = new MouseController(canvas);
  }

  private double getWidth() {
    return canvas.getWidth();
  }

  private double getHeight() {
    return canvas.getHeight();
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

  private double cameraSpeedX, cameraSpeedY;

  private final AnimationTimer timer = new AnimationTimer() {
    private static final long NANOS_IN_SEC = 1_000_000_000;
    private long last = -1;

    @Override
    public void handle(long now) {
      if (last != -1) {
        double delta = (double) (now - last) / NANOS_IN_SEC;
        presenter.moveCamera(
                cameraSpeedX * delta,
                cameraSpeedY * delta
        );
      }
      last = now;
    }
  };

  @Override
  public void onMouseExit() {
    cameraSpeedX = 0.0;
    cameraSpeedY = 0.0;
  }

  @Override
  public void onMouseMove(double x, double y) {
    cameraSpeedX = x - getWidth()/2;
    cameraSpeedY = y - getHeight()/2;

    if (abs(cameraSpeedX) < getWidth()*3/8) cameraSpeedX = 0.0;
    if (abs(cameraSpeedY) < getHeight()*3/8) cameraSpeedY = 0.0;
  }

  @Override
  public void onMouseClick(double x, double y) {
    presenter.selectField(x, y);
  }

  public void start() {
    timer.start();
    presenter.start();
    mouseController.addObserver(this);
  }

  public void close() {
    timer.stop();
    presenter.close();
    mouseController.removeObserver(this);
  }
}
