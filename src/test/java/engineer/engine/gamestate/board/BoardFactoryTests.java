package engineer.engine.gamestate.board;

import engineer.engine.gamestate.building.Building;
import engineer.engine.gamestate.building.BuildingFactory;
import engineer.engine.gamestate.field.Field;
import engineer.engine.gamestate.field.FieldFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class BoardFactoryTests {
  @Mock private FieldFactory fieldFactory;
  @Mock private BuildingFactory buildingFactory;
  @Mock private Field standardField;
  @Mock private Building standardBuilding;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    when(fieldFactory.produce(anyString(), any(), anyBoolean())).thenReturn(standardField);
    when(buildingFactory.produce(any())).thenReturn(standardBuilding);
  }

  @Test
  public void testFieldsProduction() {
    BoardFactory boardFactory = new BoardFactory(fieldFactory, buildingFactory);
    Field field = boardFactory.produceField("background", null, false);

    assertEquals(standardField, field);
    verify(fieldFactory, atLeastOnce()).produce("background", null, false);
  }

  @Test
  public void testBuildingProduction() {
    BoardFactory boardFactory = new BoardFactory(fieldFactory, buildingFactory);
    Building building = boardFactory.produceBuilding("building");

    assertEquals(standardBuilding, building);
    verify(buildingFactory, atLeastOnce()).produce("building");
  }
}
