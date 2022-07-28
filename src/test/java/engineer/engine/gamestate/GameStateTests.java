// package engineer.engine.gamestate;
//
// import engineer.engine.gamestate.board.Board;
// import engineer.engine.gamestate.board.BoardFactory;
// import engineer.engine.gamestate.board.BoardFactoryTests;
// import engineer.engine.gamestate.building.Building;
// import engineer.engine.gamestate.building.BuildingFactory;
// import engineer.engine.gamestate.field.Field;
// import engineer.engine.gamestate.field.FieldFactory;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.Mock;
//
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.anyInt;
// import static org.mockito.Mockito.when;
//
// public class GameStateTests {
//  @Mock private Board board;
//  @Mock private BoardFactory boardFactory;
//
//  @BeforeEach
//  public void setup() {
//    when(boardFactory.produceBoard(anyInt(), anyInt())).thenAnswer(invocation -> {
//      return board() argument[0]
//    });
//  }
//  @Test
//  public void testConstructor() {
//    BoardFactory boardFactory = new BoardFactory(null, null);
//
//  }
// }
