package engineer.gui.javafx.game;

import engineer.engine.gamestate.GameState;
import engineer.engine.presenters.game.MinimapPresenter;
import engineer.gui.javafx.TextureManager;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;

import static java.lang.Math.min;

public class MinimapGui implements MinimapPresenter.View {
  private final StackPane window;
  private final TextureManager textureManager;
  private final GameState gameState;

  private Canvas background;
  @SuppressWarnings("unused")
  private MinimapPresenter presenter;

  private double fieldSize;
  private double offsetX, offsetY;

  public MinimapGui(StackPane window, TextureManager textureManager, GameState gameState) {
    this.window = window;
    this.textureManager = textureManager;
    this.gameState = gameState;
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

  public void start() {
    background = new Canvas(window.getWidth(), window.getHeight());
    window.getChildren().add(background);

    fieldSize = min(
            background.getWidth() / (double) gameState.getColumns(),
            background.getHeight() / (double) gameState.getRows()
    );

    offsetX = (background.getWidth() - gameState.getColumns() * fieldSize) / 2.0;
    offsetY = (background.getHeight() - gameState.getRows() * fieldSize) / 2.0;

    new MinimapPresenter(gameState, this);
  }
}
