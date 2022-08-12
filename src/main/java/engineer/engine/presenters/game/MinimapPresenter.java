package engineer.engine.presenters.game;

import engineer.engine.gamestate.Camera;
import engineer.engine.gamestate.GameState;
import engineer.utils.Box;
import engineer.utils.Coords;

public class MinimapPresenter {
  public interface View {
    void drawOnBackground(Coords coords, String texture);
    void drawCameraBox(Box box);
  }

  @SuppressWarnings({"unused", "FieldCanBeLocal"})
  private final GameState gameState;
  @SuppressWarnings({"unused", "FieldCanBeLocal"})
  private final View view;

  public MinimapPresenter(GameState gameState, View view) {
    this.gameState = gameState;
    this.view = view;
  }

  public final Camera.Observer cameraObserver = new Camera.Observer() {
    @Override
    public void onCameraUpdate() {
      view.drawCameraBox(gameState.getCameraBox());
    }
  };

  public void start() {
    for (int row = 0; row<gameState.getRows(); row++) {
      for (int column = 0; column<gameState.getColumns(); column++) {
        Coords coords = new Coords(row, column);
        view.drawOnBackground(
                coords,
                gameState.getField(coords).getBackground()
        );
      }
    }

    gameState.addCameraObserver(cameraObserver);
  }

  public void close() {
    gameState.removeCameraObserver(cameraObserver);
  }
}
