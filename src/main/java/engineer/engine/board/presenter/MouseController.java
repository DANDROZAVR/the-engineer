package engineer.engine.board.presenter;

import javafx.event.EventType;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class MouseController {
    BoardPresenter presenter;
    public MouseController(BoardPresenter presenter) {
        this.presenter = presenter;
    }
    double x, y;
    @SuppressWarnings("StatementWithEmptyBody")
    public void onMouseEvent(EventType<MouseEvent> eventType, MouseEvent mouseEvent) {
        if (eventType == MouseEvent.MOUSE_CLICKED) {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                if (mouseEvent.getClickCount() == 1) {
                    presenter.changeContent(mouseEvent.getX(), mouseEvent.getY());
                    presenter.setSelectedField(mouseEvent.getX(), mouseEvent.getY());
                } else if (mouseEvent.getClickCount() == 2) {
                    // we can create functionality later, f.e choosing troops from field
                }
            }
        } else
        if (eventType == MouseEvent.MOUSE_PRESSED) {
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                x = mouseEvent.getX();
                y = mouseEvent.getY();
            }
        } else
        if (eventType == MouseEvent.MOUSE_RELEASED) {
            // can be used for moving choosing trips end-point
        } else
        if (eventType == MouseEvent.MOUSE_DRAGGED) {
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                presenter.setCameraMoveX(x - mouseEvent.getX());
                presenter.setCameraMoveY(y - mouseEvent.getY());
                x = mouseEvent.getX();
                y = mouseEvent.getY();
            } else
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                // can be used for setting troops trips
            }
        } else {
            System.err.println("unsupported mouse event" + eventType.toString());
        }
    }
}
