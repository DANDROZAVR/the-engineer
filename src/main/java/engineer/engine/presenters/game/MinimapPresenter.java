package engineer.engine.presenters.game;

import engineer.engine.gamestate.GameState;

public class MinimapPresenter {
  public interface View {
    void drawOnBackground(int row, int column, String texture);
  }

  @SuppressWarnings({"unused", "FieldCanBeLocal"})
  private final GameState gameState;
  @SuppressWarnings({"unused", "FieldCanBeLocal"})
  private final View view;

  public MinimapPresenter(GameState gameState, View view) {
    this.gameState = gameState;
    this.view = view;

    for (int row=0;row<gameState.getBoardRows();row++) {
      for (int column=0;column<gameState.getBoardColumns();column++) {
        view.drawOnBackground(
                row,
                column,
                gameState.getField(row, column).getBackground()
        );
      }
    }
  }
}
