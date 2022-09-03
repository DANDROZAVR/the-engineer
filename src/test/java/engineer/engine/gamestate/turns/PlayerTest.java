package engineer.engine.gamestate.turns;

import engineer.engine.gamestate.resource.Resource;
import engineer.engine.gamestate.resource.ResourceFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayerTest {
  private AutoCloseable closeable;

  @BeforeEach
  public void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  public void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  void testGetName() {
    Player player = new Player("name");
    assertEquals("name", player.getNickname());
  }
  @Test
  void testResource() {
    Player player = new Player("name");
    Resource woodResource = mock(Resource.class);
    Resource anotherWoodResource = mock(Resource.class);

    doReturn(15).when(woodResource).getResAmount();
    doReturn("wood").when(woodResource).getType();
    doReturn("wood").when(anotherWoodResource).getType();
    doReturn(20).when(anotherWoodResource).getResAmount();
    doReturn(true).when(woodResource).equals(woodResource);
    doReturn(true).when(anotherWoodResource).equals(anotherWoodResource);
    doReturn(true).when(woodResource).equals(anotherWoodResource);
    doReturn(true).when(anotherWoodResource).equals(woodResource);

    player.addResource(woodResource);
    verify(woodResource, never()).addResAmount(anyInt());
    player.addResource(woodResource);
    verify(woodResource).addResAmount(15);

    assertEquals(1, player.getResources().size());
    assertEquals("wood", player.getResources().get(0).getType());
    assertEquals(15, player.getResources().get(0).getResAmount());

    List<Resource> singleWoodResource = Collections.singletonList(anotherWoodResource);
    player.retrieveResourcesFromSchema(singleWoodResource);
    assertEquals(1, player.getResources().size());
    assertEquals("wood", player.getResources().get(0).getType());
    assertEquals(15, player.getResources().get(0).getResAmount());
    // nothing changed because we don't have enough wood for building

    doReturn(10).when(anotherWoodResource).getResAmount();
    anotherWoodResource.addResAmount(-10);
    player.retrieveResourcesFromSchema(singleWoodResource);
    verify(woodResource).addResAmount(-10);
  }

  @Test
  void testConstructorFromJson() {
    assertThrows(RuntimeException.class,
        () -> new Player(null, mock(ResourceFactory.class))
    );
  }
}
