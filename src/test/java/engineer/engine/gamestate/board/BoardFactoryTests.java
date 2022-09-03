package engineer.engine.gamestate.board;

import engineer.engine.gamestate.building.Building;
import engineer.engine.gamestate.building.BuildingFactory;
import engineer.engine.gamestate.field.Field;
import engineer.engine.gamestate.field.FieldFactory;
import engineer.engine.gamestate.mob.Mob;
import engineer.engine.gamestate.mob.MobFactory;
import engineer.engine.gamestate.turns.Player;
import engineer.utils.Coords;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class BoardFactoryTests {
  private AutoCloseable closeable;

  @Mock private FieldFactory fieldFactory;
  @Mock private BuildingFactory buildingFactory;
  @Mock private MobFactory mobFactory;
  @Mock private Field standardField;
  @Mock private Building standardBuilding;
  @Mock private Mob standardMob;
  @Mock private Board board;

  @BeforeEach
  public void setUp() {
    closeable = MockitoAnnotations.openMocks(this);

    when(fieldFactory.produce(anyString(), any(), any(), anyBoolean())).thenReturn(standardField);
    when(buildingFactory.produce(any(String.class), any(Player.class))).thenReturn(standardBuilding);
    when(mobFactory.produce(any(), anyInt(), any())).thenReturn(standardMob);
    when(board.getField(any())).thenReturn(standardField);
    when(fieldFactory.produce(any(), any(), any(), anyBoolean())).thenReturn(standardField);
  }

  @AfterEach
  public void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  public void testFieldsProduction() {
    BoardFactory boardFactory = new BoardFactory(fieldFactory);
    Field field = boardFactory.produceField("background", null, null,false);

    assertEquals(standardField, field);
    verify(fieldFactory, atLeastOnce()).produce("background", null, null,false);
  }

  @Test
  public void testBuildBuilding() {
    BoardFactory boardFactory = new BoardFactory(fieldFactory);
    Coords coords = new Coords(3, 5);

    boardFactory.build(board, coords, standardBuilding);

    verify(board).setField(coords, standardField);
    verify(fieldFactory).produce(standardField.getBackground(), standardBuilding, standardField.getMob(), standardField.isFree());
  }

  @Test
  public void testDestroyBuilding() {
    BoardFactory boardFactory = new BoardFactory(fieldFactory);
    Coords coords = new Coords(3, 5);

    boardFactory.destroyBuilding(board, coords);

    verify(fieldFactory).produce(any(), isNull(), any(), anyBoolean());
    verify(board).setField(coords, standardField);
  }
}
