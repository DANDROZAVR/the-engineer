package engineer.gui.javafx.game;

import engineer.engine.gamestate.GameState;
import engineer.engine.presenters.game.MinimapPresenter;
import engineer.gui.javafx.TextureManager;
import engineer.utils.Box;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static java.lang.Math.min;

public class MinimapGui implements MinimapPresenter.View {
  private final StackPane window;
  private final TextureManager textureManager;
  private final GameState gameState;
  private final Rectangle cameraRect = new Rectangle();

  private Canvas background;

  private final MinimapPresenter presenter;

  private double fieldSize;
  private double offsetX, offsetY;

  public MinimapGui(StackPane window, TextureManager textureManager, GameState gameState) {
    this.window = window;
    this.textureManager = textureManager;
    this.gameState = gameState;

    presenter = new MinimapPresenter(gameState, this);
  }

  @Override
  public void drawOnBackground(int row, int column, String texture) {
    GraphicsContext gc = background.getGraphicsContext2D();
    gc.drawImage(
            textureManager.getTexture(texture),
            column*fieldSize + offsetX,
            row*fieldSize + offsetY,
            fieldSize,
            fieldSize
    );
  }

  @Override
  public void drawCameraBox(Box box) {
    cameraRect.setX(box.left() * fieldSize + offsetX);
    cameraRect.setY(box.top() * fieldSize + offsetY);
    cameraRect.setWidth(box.width() * fieldSize);
    cameraRect.setHeight(box.height() * fieldSize);
    System.out.println(cameraRect.getX()+" "+cameraRect.getY());
  }

  public void start() {
    background = new Canvas(window.getWidth(), window.getHeight());
    window.getChildren().add(background);

    cameraRect.setFill(Color.TRANSPARENT);
    cameraRect.setStroke(Color.RED);
    cameraRect.setStrokeWidth(2.0);

    AnchorPane pane = new AnchorPane();
    pane.getChildren().add(cameraRect);
    window.getChildren().add(pane);

    fieldSize = min(
            background.getWidth() / (double) gameState.getColumns(),
            background.getHeight() / (double) gameState.getRows()
    );

    offsetX = (background.getWidth() - gameState.getColumns() * fieldSize) / 2.0;
    offsetY = (background.getHeight() - gameState.getRows() * fieldSize) / 2.0;

    presenter.start();
  }

  public void close() {
    presenter.close();
  }
}
