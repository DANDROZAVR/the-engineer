package engineer.engine.gamestate.mob;

import engineer.engine.gamestate.field.Field;
import engineer.utils.Coords;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.*;

class MobsControllerTest {
  private AutoCloseable closeable;

  @Mock private MobsController.GameStateCallback callback;
  @Mock private MobFactory mobFactory;
  @Mock private Field fieldWithMob;
  @Mock private Field fieldWithoutMob;
  @Mock private Mob mob;

  @BeforeEach
  public void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
    doReturn("troop").when(mob).getType();
    doReturn(mob).when(fieldWithMob).getMob();
    doReturn(null).when(fieldWithoutMob).getMob();
    doReturn(10).when(mob).getRemainingSteps();
    doReturn(4).when(mob).getMobsAmount();
  }

  @AfterEach
  public void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  public void testOnFieldSelection() {
    MobsController mobsController = new MobsController(callback, mobFactory);
    ArgumentCaptor<Integer> argumentCaptor = ArgumentCaptor.forClass(Integer.class);
    doReturn(mob).when(mobFactory).produce(eq("troop"), argumentCaptor.capture());

    mobsController.onFieldSelection(new Coords(1, 1), new Coords(1, 2), fieldWithMob, fieldWithMob);
    mobsController.onFieldSelection(new Coords(1, 3), new Coords(1, 1), fieldWithoutMob, fieldWithMob);
    verify(callback).setMob(new Coords(1, 3), mob);
    verify(callback).setMob(new Coords(1, 1), null);


    mobsController.onFieldSelection(new Coords(0, 0), null, fieldWithMob, fieldWithMob);
    mobsController.onFieldSelection(new Coords(0, 1), new Coords(0, 0), fieldWithMob, fieldWithMob);
    verify(callback).setMob(eq(new Coords(0, 1)), any());
    assertEquals(8, argumentCaptor.getValue());
  }

  @Test
  public void testProduceMob() {
    MobsController mobsController = new MobsController(callback, mobFactory);
    mobsController.produceMob("str", 1);
    verify(mobFactory).produce(eq("str"), eq(1));
  }

  @Test
  public void testGetAccessibleFields() {
    MobsController mobsController = new MobsController(callback, mobFactory);

    mobsController.onFieldSelection(new Coords(0, 0), null, fieldWithoutMob, null);
    assertEquals(0, mobsController.getAccessibleFields().size());
    mobsController.onFieldSelection(new Coords(0, 0), null, fieldWithMob, null);
    assertNotEquals(0, mobsController.getAccessibleFields().size());

    mobsController.onTurnStart();
    assertEquals(0, mobsController.getAccessibleFields().size());
  }

  @Test
  public void testOnTurnStart() {
    MobsController mobsController = new MobsController(callback, mobFactory);
    mobsController.onFieldSelection(new Coords(0, 0), null, fieldWithMob, null);
    mobsController.onTurnStart();
    assertEquals(0, mobsController.getAccessibleFields().size());
  }
}
