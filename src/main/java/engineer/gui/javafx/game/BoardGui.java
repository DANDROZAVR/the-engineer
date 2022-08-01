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

  @Override
  public double getViewHeight() {
    return canvas.getHeight();
  }

  @Override
  public double getViewWidth() {
    return canvas.getWidth();
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

  private final AnimationTimer timer = new AnimationTimer() {
    private static final long NANOS_IN_SEC = 1_000_000_000;
    private long last = -1;

    @Override
    public void handle(long now) {
      if (last != -1) presenter.update((double) (now - last) / NANOS_IN_SEC);
      last = now;
    }
  };

  @Override
  public void onMouseExit() {
    presenter.setCameraSpeedX(0);
    presenter.setCameraSpeedY(0);
  }

  @Override
  public void onMouseMove(double x, double y) {
    double speedX = x - getViewWidth()/2;
    double speedY = y - getViewHeight()/2;

    if (abs(speedX) < getViewWidth()*3/8) speedX = 0;
    if (abs(speedY) < getViewHeight()*3/8) speedY = 0;

    presenter.setCameraSpeedX(speedX);
    presenter.setCameraSpeedY(speedY);
  }

  @Override
  public void onMouseClick(double x, double y) {
    presenter.selectField(x, y);
  }

  public void start() {
    timer.start();
    mouseController.addObserver(this);
  }

  public void close() {
    timer.stop();
    mouseController.removeObserver(this);
  }
}
