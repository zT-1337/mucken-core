package de.tzerr.mucken.core.game;

import de.tzerr.mucken.core.game.command.Command;
import de.tzerr.mucken.core.game.command.DealHands;
import de.tzerr.mucken.core.game.deck.Deck;
import de.tzerr.mucken.core.game.deck.DeckShuffler;
import de.tzerr.mucken.core.game.event.DealtHand;
import de.tzerr.mucken.core.game.event.Event;
import de.tzerr.mucken.core.game.event.InvalidCommandReceived;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("ClassCanBeRecord")
final class DealingPhase implements GamePhase {

  private static final int HAND_SIZE = 6;
  private static final List<Command> acceptableCommands = List.of(new DealHands());

  private final DeckShuffler deckShuffler;
  private final GameState gameState;

  public DealingPhase(DeckShuffler deckShuffler, GameState gameState) {
    this.deckShuffler = deckShuffler;
    this.gameState = gameState;
  }

  @Override
  public @NonNull List<Command> getAcceptableCommands() {
    return acceptableCommands;
  }

  @Override
  public @NonNull PhaseResult execute(@NonNull Command command) {
    if (command instanceof DealHands) {
      return dealHands();
    }

    return new PhaseResult(
      Optional.empty(),
      List.of(
        new InvalidCommandReceived(String.format("Only %s command is currently accepted", DealHands.class.getSimpleName()))
      )
    );
  }

  private PhaseResult dealHands() {
    var deck = deckShuffler.shuffle(Deck.defaultDeck());
    var hands = divideByHands(gameState, deck);
    var events = new ArrayList<Event>(4);

    int index = 0;
    for (var player : gameState.getPlayers()) {
      var dealtHand = new DealtHand(player, hands.get(index));
      events.add(dealtHand);
      gameState.apply(dealtHand);
      index++;
    }

    return new PhaseResult(Optional.of(BettingPhase.class), List.copyOf(events));
  }

  private List<Hand> divideByHands(GameState gameState, Deck deck) {
    var hands = new ArrayList<Hand>(gameState.getPlayerCount());

    for (int playerIndex = 0; playerIndex < gameState.getPlayerCount(); playerIndex++) {
      hands.add(
        new Hand(
          List.copyOf(deck.cards().subList(playerIndex * HAND_SIZE, playerIndex * HAND_SIZE + HAND_SIZE))
        )
      );
    }

    return hands;
  }
}
