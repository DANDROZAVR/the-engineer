package engineer.engine.gamestate.resource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ResourceFactoryTest {
  @Test
  public void testProduceResource() {
    ResourceFactory resourceFactory = new ResourceFactory();
    resourceFactory.addResType("type", "texture");
    resourceFactory.addResType("anotherType", "texture");
    Resource resource = resourceFactory.produce("type");
    Resource nonEqualResource = resourceFactory.produce("anotherType");

    assertEquals(0, resource.getResAmount());
    assertEquals("type", resource.getType());
    assertEquals("texture", resource.getTexture());

    resource.addResAmount(5);
    assertEquals(5, resource.getResAmount());
    //noinspection EqualsWithItself
    assertTrue(resource.equals(resource));
    assertFalse(resource.equals(nonEqualResource));
  }

  @Test
  public void testResourceTypeChecking() {
    ResourceFactory resourceFactory = new ResourceFactory();
    assertThrows(RuntimeException.class,
        () -> resourceFactory.produce("type"));
  }
}
