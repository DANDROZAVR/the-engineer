package engineer.gui.javafx;

import engineer.engine.board.presenter.BoardPresenter;
import engineer.engine.board.presenter.Box;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class BoardGui implements BoardPresenter.View {
    private static final double cameraSpeed = 300;

    private final GraphicsContext gc;
    private BoardPresenter presenter;

    public BoardGui(GraphicsContext gc) {
        this.gc = gc;
    }

    @Override
    public double getViewHeight() { return gc.getCanvas().getHeight(); }
    @Override
    public double getViewWidth() { return gc.getCanvas().getWidth(); }

    @Override
    public void drawField(Box box, String texture) {
        gc.setFill(Color.web(texture));
        gc.fillRect(box.left(), box.top(), box.width(), box.height());
    }



    private final AnimationTimer timer = new AnimationTimer() {
        private static final long NANOS_IN_SEC = 1_000_000_000;
        private long last = -1;

        @Override
        public void handle(long now) {
            if (last != -1)
                presenter.update((double) (now-last) / NANOS_IN_SEC);
            last = now;
        }
    };

    public void start(BoardPresenter presenter) {
        this.presenter = presenter;
        presenter.start();
        timer.start();
    }

    public void close() {
        presenter.close();
        timer.stop();
    }



    public KeyHandler getKeyHandler() {
        return (code, pressed) -> {
            switch (code) {
                case LEFT -> presenter.setCameraSpeedX(pressed ? -cameraSpeed : 0.0);
                case RIGHT -> presenter.setCameraSpeedX(pressed ? cameraSpeed : 0.0);
                case UP -> presenter.setCameraSpeedY(pressed ? -cameraSpeed : 0.0);
                case DOWN -> presenter.setCameraSpeedY(pressed ? cameraSpeed : 0.0);
            }
        };
    }
}