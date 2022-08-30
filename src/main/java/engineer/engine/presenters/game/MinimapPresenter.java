package engineer.engine.presenters.game;

import engineer.engine.gamestate.Camera;
import engineer.engine.gamestate.board.Board;
import engineer.utils.Box;
import engineer.utils.Coords;

public class MinimapPresenter {
  public interface View {
    void drawOnBackground(Coords coords, String texture);
    void drawCameraBox(Box box);
  }

  private final Board board;
  private final Camera camera;
  private final View view;

  public MinimapPresenter(Board board, Camera camera, View view) {
    this.board = board;
    this.camera = camera;
    this.view = view;
  }

  public final Camera.Observer cameraObserver = new Camera.Observer() {
    @Override
    public void onCameraUpdate() {
      view.drawCameraBox(camera.getCameraBox());
    }
  };

  public void start() {
    for (int row = 0; row<board.getRows(); row++) {
      for (int column = 0; column<board.getColumns(); column++) {
        Coords coords = new Coords(row, column);
        view.drawOnBackground(
                coords,
                board.getField(coords).getBackground()
        );
      }
    }

    camera.addObserver(cameraObserver);
  }

  public void close() {
    camera.removeObserver(cameraObserver);
  }
}
