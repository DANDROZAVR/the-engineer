package engineer;

import engineer.engine.board.logic.Board;
import engineer.engine.board.logic.BoardDescription;
import engineer.engine.board.logic.FieldFactoryImpl;
import engineer.engine.board.presenter.BoardPresenter;
import engineer.gui.javafx.Gui;
import engineer.gui.javafx.TextureManager;

public class App {
    public static void main(String[] args) {
        // Sample board
        String[] colors = new String[]{ "#F00", "#0F0", "#00F" };
        Board board = new Board(new FieldFactoryImpl(), new BoardDescription() {
            @Override
            public int getRows() { return 40; }
            @Override
            public int getColumns() { return 50; }
            @Override
            public String getBackground(int row, int column) {
                return "tile";
            }
        });

        // Sample
        Gui gui = new Gui();
        gui.start(() -> {
            BoardPresenter boardPresenter = new BoardPresenter(board, gui.getBoardGui());
            TextureManager textureManager = new TextureManager();
            gui.getBoardGui().start(boardPresenter, textureManager);
        });
    }
}
