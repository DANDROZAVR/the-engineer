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
  @Mock private Field fieldWithMob, fieldWithoutMob, fieldWithNewMob;
  @Mock private Mob mob, newMob;
  @Mock private FightSystem fightSystem;
  @Mock private Building building;
  @Mock List<Coords> markedFieldsToMove;
  @Mock List<Coords> markedFieldsToAttack;

  @BeforeEach
  public void setUp() {
    closeable = MockitoAnnotations.openMocks(this);

    doReturn(player).when(turnSystem).getCurrentPlayer();

    doReturn("troop").when(mob).getType();
    doReturn(10).when(mob).getRemainingSteps();
    doReturn(4).when(mob).getMobsAmount();
    doReturn(2).when(mob).getMobsAttack();
    doReturn(10).when(newMob).getRemainingSteps();

    doReturn(mob).when(fieldWithMob).getMob();
    doReturn(null).when(fieldWithoutMob).getMob();

    when(board.getNearestFields(any(Coords.class), anyInt())).thenReturn(null);

    when(board.getMarkedFieldsToMove()).thenReturn(markedFieldsToMove);
    when(board.getMarkedFieldsToAttack()).thenReturn(markedFieldsToAttack);

    when(mobFactory.produce(any(), anyInt(), any())).thenReturn(newMob);

    when(building.getLevel()).thenReturn(3);


    when(board.produceField(
            nullable(String.class),
            nullable(Building.class),
            eq(newMob)
    )).thenReturn(fieldWithNewMob);

    when(board.produceField(
            nullable(String.class),
            nullable(Building.class),
            eq(mob)
    )).thenReturn(fieldWithMob);

    when(board.produceField(
            nullable(String.class),
            nullable(Building.class),
            eq(null)
    )).thenReturn(fieldWithoutMob);
  }

  @AfterEach
  public void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  public void testOnSelectionChangedToFieldWithoutMob() {
    MobsController mobsController = new MobsController(board, turnSystem, mobFactory, fightSystem);
    when(board.getField(any())).thenReturn(fieldWithoutMob);

    mobsController.onSelectionChangedMobs(new Coords(1, 1), 1);
    verify(board).unmarkAllFields();
    verify(board, never()).markFields(anyCollection(), anyCollection());
    verify(board, never()).setField(any(Coords.class), any(Field.class));
  }

  @Test
  public void testOnSelectionChangedToFieldWithNotOwnedMob() {
    MobsController mobsController = new MobsController(board, turnSystem, mobFactory, fightSystem);
    when(board.getField(any())).thenReturn(fieldWithMob);
    when(fieldWithMob.getMob().getOwner()).thenReturn(null);

    mobsController.onSelectionChangedMobs(new Coords(1, 1), 1);
    verify(board).unmarkAllFields();
    verify(board, never()).markFields(anyCollection(), anyCollection());
    verify(board, never()).setField(any(Coords.class), any(Field.class));
  }

  @Test
  public void testOnSelectionChangedToFieldWithOwnedMob() {
    MobsController mobsController = new MobsController(board, turnSystem, mobFactory, fightSystem);
    Collection<Coords> collection = new LinkedList<>();
    Collection<Coords> collectionAttack = new LinkedList<>();

    Coords coords1 = new Coords(0, 0);
    Coords coords2 = new Coords(1, 1);

    when(board.getField(any(Coords.class))).thenReturn(fieldWithMob);
    when(board.getField(coords1).getMob()).thenReturn(mob);
    when(board.getField(coords2).getBuilding()).thenReturn(building);
    when(mob.canAttackInThisTurn()).thenReturn(true);
    collectionAttack.add(coords1);
    collectionAttack.add(coords2);

    when(fieldWithMob.getMob().getOwner()).thenReturn(player);
    when(board.getNearestFields(any(Coords.class), anyInt())).thenReturn(collection);
    when(board.getFieldsToAttack(any(Coords.class), any())).thenReturn(collectionAttack);

    mobsController.onSelectionChangedMobs(new Coords(1, 2), 1);
    verify(board).unmarkAllFields();
    verify(board).markFields(collection, collectionAttack);
    verify(board, never()).setField(any(Coords.class), any(Field.class));

    when(mob.canAttackInThisTurn()).thenReturn(false);
    when(mob.getRemainingSteps()).thenReturn(0);

    mobsController.onSelectionChangedMobs(new Coords(1, 2), 1);
    verify(board).markFields(collection, collection);
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

    mobsController.onSelectionChangedMobs(new Coords(0, 0), 1);
    mobsController.onSelectionChangedMobs(new Coords(2, 1), 1);

    verify(mobFactory).produce(mob.getType(), 1, mob.getOwner());
    verify(newMob).reduceRemainingSteps(3);
    verify(mob).reduceMobs(1);
    verify(board).setField(new Coords(2, 1), fieldWithNewMob);
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

    mobsController.onSelectionChangedMobs(new Coords(0, 0), 1);
    mobsController.onSelectionChangedMobs(new Coords(0, 0), 1);

    verify(board, never()).setField(any(), any());
  }

  @Test
  public void testExceptionOnEmptyStartField() {
    MobsController mobsController = new MobsController(board, turnSystem, mobFactory, fightSystem);
    when(board.getField(any())).thenReturn(fieldWithoutMob);
    when(board.getMarkedFieldsToMove()).thenReturn(List.of(new Coords(2, 1)));
    when(board.findPath(any(), any())).thenReturn(Collections.emptyList());

    mobsController.onSelectionChangedMobs(new Coords(0, 0), 1);
    assertThrows(
            RuntimeException.class,
            () -> mobsController.onSelectionChangedMobs(new Coords(2, 1), 1)
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

    mobsController.onSelectionChangedMobs(new Coords(0, 0), 1);
    assertThrows(
            RuntimeException.class,
            () -> mobsController.onSelectionChangedMobs(new Coords(2, 1), 1)
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

    when(newMob.getType()).thenReturn("otherType");

    mobsController.onSelectionChangedMobs(new Coords(0, 0), 1);
    assertThrows(
            RuntimeException.class,
            () -> mobsController.onSelectionChangedMobs(new Coords(2, 1), 1)
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
    when(otherMob.getOwner()).thenReturn(player2);
    when(board.getField(from)).thenReturn(fieldWithMob);
    when(board.getField(to)).thenReturn(targetField);
    when(board.getMarkedFieldsToAttack()).thenReturn(List.of(to));
    when(board.findPath(any(), any())).thenReturn(Collections.emptyList());

    mobsController.onSelectionChangedMobs(from, 1);
    mobsController.onSelectionChangedMobs(to, 1);
    verify(fightSystem).makeFight(newMob, otherMob);
  }

  @Test
  public void testFightAllDie() {
    MobsController mobsController = new MobsController(board, turnSystem, mobFactory, fightSystem);

    Field targetField = mock(Field.class);
    Field fieldWithMob = mock(Field.class);
    Coords from = new Coords(0, 0);
    Coords to = new Coords(0, 1);

    when(fieldWithMob.getMob()).thenReturn(mob);
    when(targetField.getMob()).thenReturn(mob);
    when(board.getField(from)).thenReturn(fieldWithMob);
    when(board.getField(to)).thenReturn(targetField);
    when(board.getMarkedFieldsToAttack()).thenReturn(List.of(to));
    doReturn(new Pair<>(0, 0)).when(fightSystem).makeFight(any(), any());
    doReturn(0).when(mob).getMobsAmount();

    mobsController.onSelectionChangedMobs(from, 1);
    mobsController.onSelectionChangedMobs(to, 1);
    verify(board).setField(eq(from), eq(fieldWithoutMob));
    verify(board).setField(eq(to), eq(fieldWithoutMob));
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
    when(board.produceField(any(), any(), eq(null))).thenReturn(fieldWithNullMob);
    when(board.produceField(any(), any(), eq(newMob))).thenReturn(fieldWithNewMob);
    when(board.produceField(any(), any(), eq(mob))).thenReturn(fieldWithMob);
    when(board.getMarkedFieldsToAttack()).thenReturn(List.of(to));
    doReturn(new Pair<>(2, 0)).when(fightSystem).makeFight(any(), any());


    mobsController.onSelectionChangedMobs(from, 1);
    mobsController.onSelectionChangedMobs(to, 1);

    verify(board).setField(eq(from), eq(fieldWithMob));
    verify(board).setField(eq(to), eq(fieldWithNewMob));
  }

  @Test
  public void testMobBuildingAttackBasic() {
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
    when(board.produceField(any(), notNull(), any())).thenReturn(fieldWithBuilding);
    when(board.produceField(any(), eq(null), eq(null))).thenReturn(emptyField);
    when(board.produceField(any(), any(), notNull())).thenReturn(fieldWithMob);
    when(board.getMarkedFieldsToAttack()).thenReturn(List.of(to));
    when(mob.canAttackInThisTurn()).thenReturn(true);

    mobsController.onSelectionChangedMobs(from, 1);
    mobsController.onSelectionChangedMobs(to, 1);
    verify(mob).makeAttack();
    verify(building).reduceLifeRemaining(6);

    when(mob.canAttackInThisTurn()).thenReturn(false);
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
    when(board.produceField(any(), notNull(), any())).thenReturn(fieldWithBuilding);
    when(board.produceField(any(), eq(null), eq(null))).thenReturn(emptyField);
    when(board.produceField(any(), any(), notNull())).thenReturn(fieldWithMob);
    when(board.getMarkedFieldsToAttack()).thenReturn(List.of(to));
    when(mob.canAttackInThisTurn()).thenReturn(true);

    mobsController.onSelectionChangedMobs(from, 1);
    mobsController.onSelectionChangedMobs(to, 1);
    verify(mob).makeAttack();
    verify(building).reduceLifeRemaining(6);

  }

  @Test
  public void testMergeMobs() {
    MobsController mobsController = new MobsController(board, turnSystem, mobFactory, fightSystem);
    Field targetField = mock(Field.class);
    Mob otherMob = mock(Mob.class);

    when(otherMob.getType()).thenReturn("troop");
    when(otherMob.getRemainingSteps()).thenReturn(1);
    when(otherMob.getMobsAmount()).thenReturn(3);
    when(targetField.getMob()).thenReturn(otherMob);

    when(mob.getOwner()).thenReturn(player);
    when(otherMob.getOwner()).thenReturn(player);
    when(board.getField(new Coords(0, 0))).thenReturn(fieldWithMob);
    when(board.getField(new Coords(2, 1))).thenReturn(targetField);
    when(board.getMarkedFieldsToMove()).thenReturn(List.of(new Coords(2, 1)));
    when(board.findPath(any(), any())).thenReturn(Collections.emptyList());
    when(newMob.getType()).thenReturn("troop");

    mobsController.onSelectionChangedMobs(new Coords(0, 0), 1);
    mobsController.onSelectionChangedMobs(new Coords(2, 1), 1);

    verify(newMob).addMobs(3);
  }

  @Test
  public void testMoveAllMobs() {
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

    mobsController.onSelectionChangedMobs(new Coords(0, 0), 4);
    mobsController.onSelectionChangedMobs(new Coords(2, 1), 4);

    verify(mob).reduceRemainingSteps(3);
    verify(board).setField(new Coords(2, 1), fieldWithMob);
    verify(board).setField(new Coords(0, 0), fieldWithoutMob);
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

    when(board.getField(any(Coords.class))).thenReturn(field);
    when(board.produceField(
            field.getBackground(),
            field.getBuilding(),
            mob
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

    when(board.getField(any(Coords.class))).thenReturn(field);
    when(board.produceField(
            field.getBackground(),
            field.getBuilding(),
            null
    )).thenReturn(fieldWithoutMob);

    mobsController.setMob(new Coords(1, 2), null);

    verify(board).setField(new Coords(1, 2), fieldWithoutMob);
  }

  @Test
  void testOnTurnChange() {
    MobsController mobsController = new MobsController(board, turnSystem, mobFactory, fightSystem);
    mobsController.onMobAdded(mob);
    mobsController.onTurnChange(player);

    verify(mob).reset();

    mobsController.onMobRemoved(mob);
    mobsController.onTurnChange(player);

    verifyNoMoreInteractions(mob);
  }

  @Test
  void testMakeMob() {
    MobsController mobsController = new MobsController(board, turnSystem, mobFactory, fightSystem);
    when(board.getField(any(Coords.class))).thenReturn(fieldWithoutMob);
    mobsController.makeMob(new Coords(0, 0), "troop", 1, player);
    verify(mobFactory).produce("troop", 1, player);
  }

  @Test
  void testParseMob() {
    when(board.getField(any())).thenReturn(fieldWithMob);
    when(board.getRows()).thenReturn(1);
    when(board.getColumns()).thenReturn(1);
    MobsController mobsController = new MobsController(board, turnSystem, mobFactory, fightSystem);

    mobsController.onTurnChange(player);

    verify(mob).reset();
  }
}
