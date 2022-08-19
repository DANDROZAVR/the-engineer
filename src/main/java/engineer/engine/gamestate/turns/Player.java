package engineer.engine.gamestate.turns;

import engineer.engine.gamestate.mob.Mob;

import java.util.LinkedList;
import java.util.List;

public class Player {
  private final String nickname;

  public Player(String nickname) {
    this.nickname = nickname;
  }

  public String getNickname() {
    return nickname;
  }
}
