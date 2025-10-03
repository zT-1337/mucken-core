package de.tzerr.cli.demo.example;

import de.tzerr.mucken.core.game.Game;
import de.tzerr.mucken.core.game.GameState;
import de.tzerr.mucken.core.game.command.DealHands;
import de.tzerr.mucken.core.game.event.DealtHand;

public class DealingView implements View {

  private final Game game;
  private final GameState gameState;

  public DealingView(Game game, GameState gameState) {
    this.game = game;
    this.gameState = gameState;
  }

  @Override
  public Class<? extends View> render() {
    var dealtHands = game.execute(new DealHands());

    for (var event : dealtHands) {
      var dealtHand = (DealtHand) event;
      gameState.apply(dealtHand);
    }

    return BettingView.class;
  }
}
