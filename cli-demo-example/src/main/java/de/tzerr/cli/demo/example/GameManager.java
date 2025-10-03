package de.tzerr.cli.demo.example;

import de.tzerr.mucken.core.game.DefaultGame;
import de.tzerr.mucken.core.game.GameState;
import de.tzerr.mucken.core.game.Player;
import de.tzerr.mucken.core.game.deck.SimpleRandomShuffler;

import java.util.ArrayList;
import java.util.Map;

public class GameManager {

  private final Map<Class<? extends View>, View> views;
  private View currentView;

  public GameManager(String[] names) {
    if (names.length != 4) {
      throw new IllegalArgumentException("You need to provide exactly 4 names");
    }

    var players = new ArrayList<Player>(4);
    for (var name : names) {
      players.add(new Player(name));
    }

    var builder = new DefaultGame.Builder()
      .withDeckShuffler(new SimpleRandomShuffler(5, System.currentTimeMillis()));
    for (var player : players) {
      builder.withPlayer(player);
    }

    var game = builder.build();
    var gameState = new GameState(players);

    views = Map.of(
      DealingView.class, new DealingView(game, gameState),
      BettingView.class, new BettingView(game, gameState),
      PlayCardView.class, new PlayCardView(game, gameState),
      FinishedView.class, new FinishedView()
    );
    currentView = views.get(DealingView.class);
  }

  public void render() {
    var nextViewClass = currentView.render();

    if (!views.containsKey(nextViewClass)) {
      throw new IllegalStateException(nextViewClass + " not found");
    }

    currentView = views.get(nextViewClass);
  }
}
