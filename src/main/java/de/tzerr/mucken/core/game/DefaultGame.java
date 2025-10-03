package de.tzerr.mucken.core.game;

import de.tzerr.mucken.core.game.command.Command;
import de.tzerr.mucken.core.game.deck.DeckShuffler;
import de.tzerr.mucken.core.game.event.Event;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class DefaultGame implements Game {

  private final Map<Class<? extends GamePhase>, GamePhase> gamePhases;
  private final GameState gameState;
  private GamePhase currentPhase;

  private DefaultGame(@NonNull Map<Class<? extends GamePhase>, GamePhase> gamePhases,
                      @NonNull GameState gameState) {
    this.gamePhases = gamePhases;
    this.gameState = gameState;
    this.currentPhase = gamePhases.get(DealingPhase.class);
  }

  public @NonNull List<Player> getPlayers() {
    return gameState.getPlayers();
  }

  @Override
  public @NonNull List<Event> execute(@NonNull Command command) {
    var result = this.currentPhase.execute(command);

    result.nextPhase().ifPresent(nextPhase -> {
      if (!gamePhases.containsKey(nextPhase)) {
        throw new IllegalStateException("Unknown game phase " + nextPhase);
      }

      this.currentPhase = gamePhases.get(nextPhase);
    });

    return result.events();
  }

  @Override
  public @NonNull List<Command> getAcceptableCommands() {
    return currentPhase.getAcceptableCommands();
  }

  public static class Builder {
    private final List<Player> players = new ArrayList<>(4);
    private DeckShuffler deckShuffler;

    public @NonNull DefaultGame.Builder withPlayer(@NonNull Player player) {
      if (players.size() > 3) {
        throw new IllegalStateException("Too many players");
      }

      if (players.contains(player)) {
        throw new IllegalStateException("Player already exists");
      }

      players.add(player);
      return this;
    }

    public @NonNull DefaultGame.Builder withDeckShuffler(@NonNull DeckShuffler deckShuffler) {
      this.deckShuffler = deckShuffler;
      return this;
    }

    public @NonNull DefaultGame build() {
      if (players.size() != 4) {
        throw new IllegalStateException("Not enough players");
      }

      if (deckShuffler == null) {
        throw new IllegalStateException("No deck shuffler specified");
      }

      var gameState = new GameState(players);

      return new DefaultGame(
        Map.of(
          DealingPhase.class, new DealingPhase(deckShuffler, gameState),
          BettingPhase.class, new BettingPhase(gameState),
          PlayCardPhase.class, new PlayCardPhase(gameState),
          FinishedPhase.class, new FinishedPhase()
        ),
        gameState
      );
    }
  }
}
