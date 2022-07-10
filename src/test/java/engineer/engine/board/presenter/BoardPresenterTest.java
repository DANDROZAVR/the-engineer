package engineer.engine.board.presenter;

import engineer.engine.board.logic.Board;
import engineer.engine.board.logic.Field;
import engineer.engine.board.logic.FieldContent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class BoardPresenterTest {
    @Mock private Board board;
    @Mock private BoardPresenter.View view;
    @Mock private Field emptyField;
    @Mock private Field nonEmptyField;
    @Mock private FieldContent content;

    private static final double EPS = 0.001;

    private BoardPresenter presenter;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        when(emptyField.getBackground()).thenReturn("Empty");
        when(nonEmptyField.getBackground()).thenReturn("Non-empty");
        when(nonEmptyField.getContent()).thenReturn(content);

        when(board.getColumns()).thenReturn(10);
        when(board.getRows()).thenReturn(8);

        when(board.getField(anyInt(), anyInt())).thenReturn(emptyField);
        when(board.getField(3,4)).thenReturn(nonEmptyField);

        when(view.getViewWidth()).thenReturn(1*70.0);
        when(view.getViewHeight()).thenReturn(1*70.0);

        presenter = new BoardPresenter(board, view);
    }

    @Test
    public void testCameraMove() {
        ArgumentCaptor<Box> captor = ArgumentCaptor.forClass(Box.class);
        List<Box> list;

        presenter.update(0.0);

        verify(board, atLeastOnce()).getField(0, 0);
        verify(board, never()).getField(3, 4);
        verify(emptyField, atLeastOnce()).getContent();
        verify(nonEmptyField, never()).getContent();

        // Check what was drawn
        verify(view).drawField(captor.capture(), anyString());
        list = captor.getAllValues();
        assertFalse(list.stream().anyMatch(box ->
                (box.top() > 70.0+EPS || box.bottom() < 0.0-EPS) ||
                (box.left() > 70.0+EPS || box.right() < 0.0-EPS)));
        assertFalse(list.isEmpty());

        clearInvocations(board, emptyField, nonEmptyField, content);

        presenter.setCameraSpeedX(30.0);
        presenter.setCameraSpeedY(40.0);
        presenter.update(7.0);

        verify(board, never()).getField(0, 0);
        verify(board, atLeastOnce()).getField(3, 4);
        verify(emptyField, never()).getContent();
        verify(nonEmptyField, atLeastOnce()).getContent();
        verify(content, atLeastOnce()).getPicture();

        // Check what was drawn
        verify(view, atLeastOnce()).drawField(captor.capture(), anyString());
        list = captor.getAllValues();
        assertFalse(list.stream().anyMatch(box ->
                (box.top() > 70.0+EPS || box.bottom() < 0.0-EPS) ||
                (box.left() > 70.0+EPS || box.right() < 0.0-EPS)));
        assertFalse(list.isEmpty());
    }

    @Test
    public void testCameraZoomInAndOut() {
        for(int i=0;i<100;i++)
            presenter.zoomOut();
        presenter.update(0.0);

        verify(board, atLeastOnce()).getField(3, 4);

        clearInvocations(board);

        for(int i=0;i<100;i++)
            presenter.zoomIn();
        presenter.update(0.0);

        verify(board, atMost(99)).getField(3, 4);
    }

    @Test
    public void testButtons() {
        presenter.setCameraSpeedX(3*70.0);
        presenter.setCameraSpeedY(4*70.0);
        presenter.update(1.0);

        ArgumentCaptor<FieldContent> captor = ArgumentCaptor.forClass(FieldContent.class);

        presenter.setPressedButton("Button X");
        presenter.changeContent(10.0, 10.0);

        verify(board).setFieldContent(eq(3), eq(4), captor.capture());
        assertEquals("Button X", captor.getValue().getPicture());

        clearInvocations(board);
        presenter.changeContent(20.0, 20.0);

        verify(board).setFieldContent(eq(3), eq(4), captor.capture());
        assertNull(captor.getValue().getPicture());
    }
}