package engineer.engine.gamestate.board;

import engineer.engine.gamestate.building.Building;
import engineer.engine.gamestate.building.BuildingFactory;
import engineer.engine.gamestate.field.Field;
import engineer.engine.gamestate.field.FieldFactory;
import engineer.engine.gamestate.mob.Mob;
import engineer.engine.gamestate.mob.MobFactory;
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

  @BeforeEach
  public void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
    when(fieldFactory.produce(anyString(), any(), any(), anyBoolean())).thenReturn(standardField);
    when(buildingFactory.produce(any())).thenReturn(standardBuilding);
    when(mobFactory.produce(any(), anyInt())).thenReturn(standardMob);
  }

  @AfterEach
  public void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  public void testFieldsProduction() {
    BoardFactory boardFactory = new BoardFactory(fieldFactory, buildingFactory);
    Field field = boardFactory.produceField("background", null, null,false);

    assertEquals(standardField, field);
    verify(fieldFactory, atLeastOnce()).produce("background", null, null,false);
  }

  @Test
  public void testBuildingProduction() {
    BoardFactory boardFactory = new BoardFactory(fieldFactory, buildingFactory);
    Building building = boardFactory.produceBuilding("type");

    assertEquals(standardBuilding, building);
    verify(buildingFactory, atLeastOnce()).produce("type");
  }
}
