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

import static org.junit.jupiter.api.Assertions.*;
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
  @Mock private Building building;

  @BeforeEach
  public void setUp() {
    closeable = MockitoAnnotations.openMocks(this);

    doReturn(player).when(turnSystem).getCurrentPlayer();

    doReturn("troop").when(mob).getType();
    doReturn(10).when(mob).getRemainingSteps();
    doReturn(4).when(mob).getMobsAmount();

    doReturn(mob).when(fieldWithMob).getMob();
    doReturn(null).when(fieldWithoutMob).getMob();

    when(board.getNearestFields(any(Coords.class), anyInt())).thenReturn(null);
  }

  @AfterEach
  public void tearDown() throws Exception {
    closeable.close();
  }
/*
  @Test
  public void testOnSelectionChangedToFieldWithoutMob() {
    MobsController mobsController = new MobsController(board, turnSystem, mobFactory, fightSystem);
    when(board.getField(any())).thenReturn(fieldWithoutMob);

    mobsController.onSelectionChanged(new Coords(1, 1));
    verify(board).unmarkAllFields();
    verify(board, never()).markFields(anyCollection(), anyCollection());
    verify(board, never()).setField(any(Coords.class), any(Field.class));
  }

  @Test
  public void testOnSelectionChangedToFieldWithNotOwnedMob() {
    MobsController mobsController = new MobsController(board, turnSystem, mobFactory, fightSystem);
    when(board.getField(any())).thenReturn(fieldWithMob);
    when(fieldWithMob.getMob().getOwner()).thenReturn(null);

    mobsController.onSelectionChanged(new Coords(1, 1));
    verify(board).unmarkAllFields();
    verify(board, never()).markFields(anyCollection(), anyCollection());
    verify(board, never()).setField(any(Coords.class), any(Field.class));
  }

  @Test
  public void testOnSelectionChangedToFieldWithOwnedMob() {
    MobsController mobsController = new MobsController(board, turnSystem, mobFactory, fightSystem);
    Collection<Coords> collection = new LinkedList<>();

    when(board.getField(any(Coords.class))).thenReturn(fieldWithMob);
    when(fieldWithMob.getMob().getOwner()).thenReturn(player);
    when(board.getNearestFields(any(Coords.class), anyInt())).thenReturn(new Pair<>(collection, null));

    mobsController.onSelectionChanged(new Coords(1, 1));
    verify(board).unmarkAllFields();
    verify(board).markFields(collection, null);
    verify(board, never()).setField(any(Coords.class), any(Field.class));
  }

  @Test
  public void testBasicMobMove() {
    MobsController mobsController = new MobsController(board, turnSystem, mobFactory, fightSystem);

    when(mob.getOwner()).thenReturn(player);

    when(board.getField(any(Coords.class))).thenReturn(fieldWithoutMob);
    when(board.getField(new Coords(0, 0))).thenReturn(fieldWithMob);
    when(board.getMarkedFieldsToMove()).thenReturn(List.of(new Coords(2, 1)));
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
    when(board.getMarkedFieldsToMove()).thenReturn(List.of(new Coords(0, 0)));
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

    verify(board, never()).setField(any(), any());
  }

  @Test
  public void testExceptionOnEmptyStartField() {
    MobsController mobsController = new MobsController(board, turnSystem, mobFactory, fightSystem);
    when(board.getField(any())).thenReturn(fieldWithoutMob);
    when(board.getMarkedFieldsToMove()).thenReturn(List.of(new Coords(2, 1)));
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
    when(board.getMarkedFieldsToMove()).thenReturn(List.of(new Coords(2, 1)));
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
    when(board.getMarkedFieldsToMove()).thenReturn(List.of(new Coords(2, 1)));
    when(board.findPath(any(), any())).thenReturn(Collections.emptyList());

    mobsController.onSelectionChanged(new Coords(0, 0));
    assertThrows(
            RuntimeException.class,
            () -> mobsController.onSelectionChanged(new Coords(2, 1))
    );
  }

  @Test
  public void testFightOnTargetNotOwnedMob() {
    MobsController mobsController = new MobsController(board, turnSystem, mobFactory, fightSystem);
    Field targetField = mock(Field.class);
    Mob otherMob = mock(Mob.class);
    Coords from = new Coords(0, 0);
    Coords to = new Coords(0, 1);

    when(otherMob.getType()).thenReturn("troop");
    when(targetField.getMob()).thenReturn(otherMob);
    doReturn(new Pair<>(1, 0)).when(fightSystem).makeFight(any(), any());

    when(mob.getOwner()).thenReturn(player);
    when(otherMob.getOwner()).thenReturn(null);
    when(board.getField(from)).thenReturn(fieldWithMob);
    when(board.getField(to)).thenReturn(targetField);
    when(board.getMarkedFieldsToAttack()).thenReturn(List.of(to));
    when(board.findPath(any(), any())).thenReturn(Collections.emptyList());

<<<<<<< HEAD
    mobsController.onSelectionChanged(from);
    mobsController.onSelectionChanged(to);
    verify(fightSystem).makeFight(mob, otherMob);
  }

  @Test
  public void testFightOnTargetNotOwnedMobWhileMoving() {
    MobsController mobsController = new MobsController(board, turnSystem, mobFactory, fightSystem);
    Field targetField = mock(Field.class);
    Mob otherMob = mock(Mob.class);
    Coords from = new Coords(0, 0);
    Coords to = new Coords(0, 1);

    when(otherMob.getType()).thenReturn("troop");
    when(targetField.getMob()).thenReturn(otherMob);
    doReturn(new Pair<>(1, 0)).when(fightSystem).makeFight(any(), any());

    when(mob.getOwner()).thenReturn(player);
    when(otherMob.getOwner()).thenReturn(null);
    when(board.getField(from)).thenReturn(fieldWithMob);
    when(board.getField(to)).thenReturn(targetField);
    when(board.getMarkedFieldsToMove()).thenReturn(List.of(to));
    when(board.findPath(any(), any())).thenReturn(Collections.emptyList());

    mobsController.onSelectionChanged(from);
    mobsController.onSelectionChanged(to);
    verify(fightSystem).makeFight(mob, otherMob);
=======
    mobsController.onSelectionChanged(new Coords(0, 0));
    //mobsController.onSelectionChanged(new Coords(2, 1));
    //verify(fightSystem).makeFight(mob, otherMob);
>>>>>>> 6c9b2d0... Interactions between resources mobs and buildings
  }

  @Test
  public void testFightAllDie() {
    MobsController mobsController = new MobsController(board, turnSystem, mobFactory, fightSystem);

    Field targetField = mock(Field.class);
    Field fieldWithMob = mock(Field.class);
    Field fieldWithNullMob = mock(Field.class);
    Coords from = new Coords(0, 0);
    Coords to = new Coords(0, 1);

    when(fieldWithMob.getMob()).thenReturn(mob);
    when(targetField.getMob()).thenReturn(mob);
    when(board.getField(from)).thenReturn(fieldWithMob);
    when(board.getField(to)).thenReturn(targetField);
    when(board.produceField(any(), any(), eq(null), anyBoolean())).thenReturn(fieldWithNullMob);
    when(board.produceField(any(), any(), notNull(), anyBoolean())).thenReturn(fieldWithMob);
    when(board.getMarkedFieldsToAttack()).thenReturn(List.of(to));
    doReturn(new Pair<>(0, 0)).when(fightSystem).makeFight(any(), any());
    doReturn(0).when(mob).getMobsAmount();

<<<<<<< HEAD
    mobsController.onSelectionChanged(from);
    mobsController.onSelectionChanged(to);
    verify(board).setField(eq(from), eq(fieldWithNullMob));
    verify(board).setField(eq(to), eq(fieldWithNullMob));
=======
    int numberOfMobsToMove = 1;
    mobsController.makeFight(from, to, numberOfMobsToMove);
    //verify(board).setField(eq(from), eq(fieldWithNullMob));
    //verify(board).setField(eq(to), eq(fieldWithNullMob));
>>>>>>> 6c9b2d0... Interactions between resources mobs and buildings
  }

  @Test
  public void testFightWhenNotAllDie() {
    MobsController mobsController = new MobsController(board, turnSystem, mobFactory, fightSystem);

    Field targetField = mock(Field.class);
    Field fieldWithNullMob = mock(Field.class);
    Field fieldWithMob = mock(Field.class);
    Coords from = new Coords(0, 0);
    Coords to = new Coords(0, 1);

    when(fieldWithMob.getMob()).thenReturn(mob);
    when(targetField.getMob()).thenReturn(mob);
    when(board.getField(from)).thenReturn(fieldWithMob);
    when(board.getField(to)).thenReturn(targetField);
    when(board.produceField(any(), any(), eq(null), anyBoolean())).thenReturn(fieldWithNullMob);
    when(board.produceField(any(), any(), notNull(), anyBoolean())).thenReturn(fieldWithMob);
    when(board.getMarkedFieldsToAttack()).thenReturn(List.of(to));
    doReturn(new Pair<>(2, 0)).when(fightSystem).makeFight(any(), any());

<<<<<<< HEAD
    mobsController.onSelectionChanged(from);
    mobsController.onSelectionChanged(to);
=======
    int numberOfMobsToMove = 1;
    mobsController.makeFight(from, to, numberOfMobsToMove);
>>>>>>> 6c9b2d0... Interactions between resources mobs and buildings
    verify(board).setField(eq(from), eq(fieldWithNullMob));
    verify(board).setField(eq(to), eq(fieldWithMob));
  }

  @Test
  public void testMobBuildingAttack() {
    MobsController mobsController = new MobsController(board, turnSystem, mobFactory, fightSystem);

    Field fieldWithBuilding = mock(Field.class);
    Field emptyField = mock(Field.class);
    Field fieldWithMob = mock(Field.class);
    Coords from = new Coords(0, 0);
    Coords to = new Coords(0, 1);

    when(fieldWithMob.getMob()).thenReturn(mob);
    when(fieldWithBuilding.getBuilding()).thenReturn(building);
    when(board.getField(from)).thenReturn(fieldWithMob);
    when(board.getField(to)).thenReturn(fieldWithBuilding);
    when(board.produceField(any(), notNull(), any(), anyBoolean())).thenReturn(fieldWithBuilding);
    when(board.produceField(any(), eq(null), eq(null), anyBoolean())).thenReturn(emptyField);
    when(board.produceField(any(), any(), notNull(), anyBoolean())).thenReturn(fieldWithMob);
    when(board.getMarkedFieldsToAttack()).thenReturn(List.of(to));
    when(mob.canAttackInThisTurn()).thenReturn(true);
    doReturn(new Pair<>(2, 0)).when(fightSystem).makeFight(any(), any()); /* change to when()..thenReturn()

    mobsController.onSelectionChanged(from);
    mobsController.onSelectionChanged(to);
    verify(board).setField(eq(to), eq(emptyField));
  }*/

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
    when(board.getMarkedFieldsToMove()).thenReturn(List.of(new Coords(2, 1)));
    when(board.findPath(any(), any())).thenReturn(Collections.emptyList());

    mobsController.onSelectionChanged(new Coords(0, 0));
    mobsController.onSelectionChanged(new Coords(2, 1));

    //verify(mob).reduceRemainingSteps(9);
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

  @Test
  void testOnTurnChange() {
    MobsController mobsController = new MobsController(board, turnSystem, mobFactory, fightSystem);
    mobsController.onTurnChange(null);

    //verify(mob).reset();
  }

  @Test
  void testGetFightSystem() {
    MobsController mobsController = new MobsController(board, turnSystem, mobFactory, fightSystem);
    assertEquals(mobsController.getFightSystem(), fightSystem);
  }
}
