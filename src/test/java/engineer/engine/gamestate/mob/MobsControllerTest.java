package engineer.engine.gamestate.mob;

import engineer.engine.gamestate.board.Board;
import engineer.engine.gamestate.building.Building;
import engineer.engine.gamestate.field.Field;
import engineer.engine.gamestate.turns.Player;
import engineer.engine.gamestate.turns.TurnSystem;
import engineer.utils.Coords;
import javafx.util.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class MobsControllerTest {
  private AutoCloseable closeable;

  @Mock private Board board;
  @Mock private TurnSystem turnSystem;
  @Mock private Player player;
  @Mock private Player player2;

  @Mock private MobFactory mobFactory;
  @Mock private Field fieldWithMob;
  @Mock private Field fieldWithoutMob;
  @Mock private Mob mob;
  @Mock private FightSystem fightSystem;

  @BeforeEach
  public void setUp() {
    closeable = MockitoAnnotations.openMocks(this);

    doReturn(player).when(turnSystem).getCurrentPlayer();

    doReturn("troop").when(mob).getType();
    doReturn(10).when(mob).getRemainingSteps();
    doReturn(4).when(mob).getMobsAmount();

    doReturn(mob).when(fieldWithMob).getMob();
    doReturn(null).when(fieldWithoutMob).getMob();

    doReturn(new Pair<>(1, 0)).when(fightSystem).makeFight(any(), any());
  }

  @AfterEach
  public void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  public void testOnSelectionChangedToFieldWithoutMob() {
    MobsController mobsController = new MobsController(board, turnSystem, mobFactory, fightSystem);
    when(board.getField(any())).thenReturn(fieldWithoutMob);

    mobsController.onSelectionChanged(new Coords(1, 1));
    verify(board).unmarkAllFields();
    verify(board, never()).markFields(anyCollection());
    verify(board, never()).setField(any(Coords.class), any(Field.class));
  }

  @Test
  public void testOnSelectionChangedToFieldWithNotOwnedMob() {
    MobsController mobsController = new MobsController(board, turnSystem, mobFactory, fightSystem);
    when(board.getField(any())).thenReturn(fieldWithMob);
    when(fieldWithMob.getMob().getOwner()).thenReturn(null);

    mobsController.onSelectionChanged(new Coords(1, 1));
    verify(board).unmarkAllFields();
    verify(board, never()).markFields(anyCollection());
    verify(board, never()).setField(any(Coords.class), any(Field.class));
  }

  @Test
  public void testOnSelectionChangedToFieldWithOwnedMob() {
    MobsController mobsController = new MobsController(board, turnSystem, mobFactory, fightSystem);
    Collection<Coords> collection = new LinkedList<>();

    when(board.getField(any(Coords.class))).thenReturn(fieldWithMob);
    when(fieldWithMob.getMob().getOwner()).thenReturn(player);
    when(board.getNearestFields(any(Coords.class), anyInt())).thenReturn(collection);

    mobsController.onSelectionChanged(new Coords(1, 1));
    verify(board).unmarkAllFields();
    verify(board).markFields(collection);
    verify(board, never()).setField(any(Coords.class), any(Field.class));
  }

  @Test
  public void testBasicMobMove() {
    MobsController mobsController = new MobsController(board, turnSystem, mobFactory, fightSystem);

    when(mob.getOwner()).thenReturn(player);

    when(board.getField(any(Coords.class))).thenReturn(fieldWithoutMob);
    when(board.getField(new Coords(0, 0))).thenReturn(fieldWithMob);
    when(board.getMarkedFields()).thenReturn(List.of(new Coords(2, 1)));
    when(board.findPath(new Coords(0, 0), new Coords(2, 1))).thenReturn(List.of(
            new Coords(0, 0),
            new Coords(1, 0),
            new Coords(2, 0),
            new Coords(2, 1)
    ));

    when(board.produceField(
            nullable(String.class),
            nullable(Building.class),
            isNull(),
            anyBoolean()
    )).thenReturn(fieldWithoutMob);
    when(board.produceField(
            nullable(String.class),
            nullable(Building.class),
            eq(mob),
            anyBoolean()
    )).thenReturn(fieldWithMob);

    mobsController.onSelectionChanged(new Coords(0, 0));
    mobsController.onSelectionChanged(new Coords(2, 1));

    verify(mob).reduceRemainingSteps(3);
    verify(board).setField(new Coords(0, 0), fieldWithoutMob);
    verify(board).setField(new Coords(2, 1), fieldWithMob);
  }

  @Test
  public void testNoMove() {
    MobsController mobsController = new MobsController(board, turnSystem, mobFactory, fightSystem);

    when(mob.getOwner()).thenReturn(player);

    when(board.getField(any(Coords.class))).thenReturn(fieldWithoutMob);
    when(board.getField(new Coords(0, 0))).thenReturn(fieldWithMob);
    when(board.getMarkedFields()).thenReturn(List.of(new Coords(0, 0)));
    when(board.findPath(new Coords(0, 0), new Coords(0, 0))).thenReturn(List.of(
            new Coords(0, 0)
    ));

    when(board.produceField(
            nullable(String.class),
            nullable(Building.class),
            isNull(),
            anyBoolean()
    )).thenReturn(fieldWithoutMob);
    when(board.produceField(
            nullable(String.class),
            nullable(Building.class),
            eq(mob),
            anyBoolean()
    )).thenReturn(fieldWithMob);

    mobsController.onSelectionChanged(new Coords(0, 0));
    mobsController.onSelectionChanged(new Coords(0, 0));
  }

  @Test
  public void testExceptionOnEmptyStartField() {
    MobsController mobsController = new MobsController(board, turnSystem, mobFactory, fightSystem);
    when(board.getField(any())).thenReturn(fieldWithoutMob);
    when(board.getMarkedFields()).thenReturn(List.of(new Coords(2, 1)));
    when(board.findPath(any(), any())).thenReturn(Collections.emptyList());

    mobsController.onSelectionChanged(new Coords(0, 0));
    assertThrows(
            RuntimeException.class,
            () -> mobsController.onSelectionChanged(new Coords(2, 1))
    );
  }

  @Test
  public void testExceptionOnNotMobOwner() {
    MobsController mobsController = new MobsController(board, turnSystem, mobFactory, fightSystem);
    when(mob.getOwner()).thenReturn(player2);
    when(board.getField(any())).thenReturn(fieldWithoutMob);
    when(board.getField(new Coords(0, 0))).thenReturn(fieldWithMob);
    when(board.getMarkedFields()).thenReturn(List.of(new Coords(2, 1)));
    when(board.findPath(any(), any())).thenReturn(Collections.emptyList());

    mobsController.onSelectionChanged(new Coords(0, 0));
    assertThrows(
            RuntimeException.class,
            () -> mobsController.onSelectionChanged(new Coords(2, 1))
    );
  }

  @Test
  public void testExceptionOnDifferentMobType() {
    MobsController mobsController = new MobsController(board, turnSystem, mobFactory, fightSystem);
    Field targetField = mock(Field.class);
    Mob otherMob = mock(Mob.class);

    when(otherMob.getType()).thenReturn("mob");
    when(targetField.getMob()).thenReturn(otherMob);

    when(mob.getOwner()).thenReturn(player);
    when(otherMob.getOwner()).thenReturn(player);
    when(board.getField(new Coords(0, 0))).thenReturn(fieldWithMob);
    when(board.getField(new Coords(2, 1))).thenReturn(targetField);
    when(board.getMarkedFields()).thenReturn(List.of(new Coords(2, 1)));
    when(board.findPath(any(), any())).thenReturn(Collections.emptyList());

    mobsController.onSelectionChanged(new Coords(0, 0));
    assertThrows(
            RuntimeException.class,
            () -> mobsController.onSelectionChanged(new Coords(2, 1))
    );
  }

  @Test
  public void testExceptionOnNotTargetMobOwner() {
    MobsController mobsController = new MobsController(board, turnSystem, mobFactory, fightSystem);
    Field targetField = mock(Field.class);
    Mob otherMob = mock(Mob.class);

    when(otherMob.getType()).thenReturn("troop");
    when(targetField.getMob()).thenReturn(otherMob);

    when(mob.getOwner()).thenReturn(player);
    when(otherMob.getOwner()).thenReturn(null);
    when(board.getField(new Coords(0, 0))).thenReturn(fieldWithMob);
    when(board.getField(new Coords(2, 1))).thenReturn(targetField);
    when(board.getMarkedFields()).thenReturn(List.of(new Coords(2, 1)));
    when(board.findPath(any(), any())).thenReturn(Collections.emptyList());

    mobsController.onSelectionChanged(new Coords(0, 0));
    mobsController.onSelectionChanged(new Coords(2, 1));
    verify(fightSystem).makeFight(mob, otherMob);
  }

  @Test
  public void testMergeMobs() {
    MobsController mobsController = new MobsController(board, turnSystem, mobFactory, fightSystem);
    Field targetField = mock(Field.class);
    Mob otherMob = mock(Mob.class);

    when(otherMob.getType()).thenReturn("troop");
    when(otherMob.getRemainingSteps()).thenReturn(1);
    when(targetField.getMob()).thenReturn(otherMob);

    when(mob.getOwner()).thenReturn(player);
    when(otherMob.getOwner()).thenReturn(player);
    when(board.getField(new Coords(0, 0))).thenReturn(fieldWithMob);
    when(board.getField(new Coords(2, 1))).thenReturn(targetField);
    when(board.getMarkedFields()).thenReturn(List.of(new Coords(2, 1)));
    when(board.findPath(any(), any())).thenReturn(Collections.emptyList());

    mobsController.onSelectionChanged(new Coords(0, 0));
    mobsController.onSelectionChanged(new Coords(2, 1));

    verify(mob).reduceRemainingSteps(9);
  }

  @Test
  public void testProduceMob() {
    MobsController mobsController = new MobsController(board, turnSystem, mobFactory, fightSystem);
    mobsController.produceMob("str", 1, null);
    verify(mobFactory).produce(eq("str"), eq(1), eq(null));
  }

  @Test
  public void testSetMob() {
    MobsController mobsController = new MobsController(board, turnSystem, mobFactory, fightSystem);

    Field field = mock(Field.class);
    when(field.getBackground()).thenReturn("background");
    when(field.getBuilding()).thenReturn(mock(Building.class));
    when(field.getMob()).thenReturn(null);
    when(field.isFree()).thenReturn(true);

    when(board.getField(any(Coords.class))).thenReturn(field);
    when(board.produceField(
            field.getBackground(),
            field.getBuilding(),
            mob,
            field.isFree()
    )).thenReturn(fieldWithMob);

    mobsController.setMob(new Coords(1, 2), mob);

    verify(board).setField(new Coords(1, 2), fieldWithMob);
  }

  @Test
  public void testRemoveMob() {
    MobsController mobsController = new MobsController(board, turnSystem, mobFactory, fightSystem);

    Field field = mock(Field.class);
    when(field.getBackground()).thenReturn("background");
    when(field.getBuilding()).thenReturn(mock(Building.class));
    when(field.getMob()).thenReturn(mob);
    when(field.isFree()).thenReturn(true);

    when(board.getField(any(Coords.class))).thenReturn(field);
    when(board.produceField(
            field.getBackground(),
            field.getBuilding(),
            null,
            field.isFree()
    )).thenReturn(fieldWithoutMob);

    mobsController.setMob(new Coords(1, 2), null);

    verify(board).setField(new Coords(1, 2), fieldWithoutMob);
  }
}
