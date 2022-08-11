package engineer.engine.gamestate;

import engineer.utils.Box;
import engineer.utils.Pair;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CameraTest {
  @Test
  public void testObserver() {
    Camera.Observer observer = mock(Camera.Observer.class);
    Camera camera = new Camera(10, 12, 100, 200);

    camera.addObserver(observer);

    camera.moveCamera(0, 0);
    verify(observer).onCameraUpdate();

    camera.zoom(1.0);
    verify(observer, times(2)).onCameraUpdate();

    camera.removeObserver(observer);

    camera.moveCamera(0, 0);
    verifyNoMoreInteractions(observer);
  }

  @Test
  public void testZoom() {
    Camera camera = new Camera(10, 10, 100, 100);
    Box initBox = camera.getCameraBox();
    camera.zoom(2.0);
    Box newBox = camera.getCameraBox();

    assertEquals(2.0, initBox.width() / newBox.width(), 0.001);
    assertEquals(2.0, initBox.height() / newBox.height(), 0.001);
  }

  @Test
  public void testFieldInteractionsWithCamera() {
    Camera camera = new Camera(10, 20, 300, 400);
    Box box;
    camera.zoom(2.0);

    camera.moveCamera(-1e9, -1e9);

    box = camera.getFieldBox(0, 0);
    assertEquals(0, box.left(), 0.001);
    assertEquals(0, box.top(), 0.001);
    assertTrue(camera.isFieldVisible(0, 0));

    double fieldSize = box.width();

    camera.moveCamera(1e9, 1e9);
    box = camera.getFieldBox(0, 0);

    assertEquals(-20*fieldSize + 300, box.left(), 0.001);
    assertEquals(-10*fieldSize + 400, box.top(), 0.001);
    assertFalse(camera.isFieldVisible(0, 0));
  }

  @Test
  public void testFieldByPoint() {
    Camera camera = new Camera(10, 10, 10, 10);
    camera.zoom(1e-9);

    assertEquals(new Pair(0, 0), camera.getFieldByPoint(0.001, 0.001));
    assertEquals(new Pair(5, 3), camera.getFieldByPoint(3.5, 5.001));
    assertEquals(new Pair(9, 8), camera.getFieldByPoint(8.999, 9.1));
    assertEquals(new Pair(4, 4), camera.getFieldByPoint(4.21, 4.95));
  }
}
