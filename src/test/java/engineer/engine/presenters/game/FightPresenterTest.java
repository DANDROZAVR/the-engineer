package engineer.engine.presenters.game;

import engineer.engine.gamestate.mob.FightSystem;
import engineer.engine.gamestate.mob.Mob;
import engineer.engine.gamestate.turns.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

class FightPresenterTest {
  private AutoCloseable closeable;

  @Mock FightSystem fightSystem;
  @Mock FightPresenter.View callbackView;
  @Mock Mob mob1, mob2;
  @Mock Player player;

  @BeforeEach
  public void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  public void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  public void testObserver() {
    FightPresenter presenter = new FightPresenter(fightSystem, callbackView);
    ArgumentCaptor<FightSystem.Observer> observerCaptor = ArgumentCaptor.forClass(FightSystem.Observer.class);

    when(mob1.getOwner()).thenReturn(player);
    when(mob2.getOwner()).thenReturn(player);

    verify(fightSystem, never()).addObserver(observerCaptor.capture());
    presenter.start();
    verify(fightSystem).addObserver(observerCaptor.capture());

    FightSystem.Observer observer = observerCaptor.getValue();
    observer.onShowFight(mob1, mob2, 12, 0);

    verify(callbackView).showFight(any(), any(), anyInt(), anyInt(), anyInt(), anyInt(), any(), any());
    presenter.close();

    verify(fightSystem).removeObserver(observer);
  }

}
