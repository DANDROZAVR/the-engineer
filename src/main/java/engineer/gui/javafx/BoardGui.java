package engineer.gui.javafx;

import engineer.engine.board.presenter.BoardPresenter;
import engineer.engine.board.presenter.Box;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

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
        gc.drawImage(new Image("file:src/main/resources/images/"+texture+".png"), box.left(), box.top(), box.width(), box.height());
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
        timer.start();
    }

    public void close() {
        presenter = null;
        timer.stop();
    }



    public KeyHandler getKeyHandler() {
        return (code, pressed) -> {
            switch (code) {
                case LEFT -> presenter.setCameraSpeedX(pressed ? -cameraSpeed : 0.0);
                case RIGHT -> presenter.setCameraSpeedX(pressed ? cameraSpeed : 0.0);
                case UP -> presenter.setCameraSpeedY(pressed ? -cameraSpeed : 0.0);
                case DOWN -> presenter.setCameraSpeedY(pressed ? cameraSpeed : 0.0);
                case I -> { if(pressed) presenter.zoomIn(); }
                case O -> { if(pressed) presenter.zoomOut(); }
            }
        };
    }

    public EventHandler<ActionEvent> getButtonClickedHandler() {
        return event -> presenter.setPressedButton(((Button) event.getTarget()).getId());
    }

    public EventHandler<? super MouseEvent> getOnFieldClickHandler() {
        return event -> presenter.changeContent(event.getSceneX(), event.getSceneY());
    }
}