package de.tzerr.mucken.core.game;

import de.tzerr.mucken.core.game.bet.Bet;
import de.tzerr.mucken.core.game.bet.Mode;
import de.tzerr.mucken.core.game.command.Command;
import de.tzerr.mucken.core.game.command.ShoutBet;
import de.tzerr.mucken.core.game.event.BetAccepted;
import de.tzerr.mucken.core.game.event.BetShouted;
import de.tzerr.mucken.core.game.event.Event;
import de.tzerr.mucken.core.game.event.InvalidCommandReceived;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("ClassCanBeRecord")
final class BettingPhase implements GamePhase {

  private final GameState gameState;

  public BettingPhase(GameState gameState) {
    this.gameState = gameState;
  }

  @Override
  public @NonNull List<Command> getAcceptableCommands() {
    return Arrays.stream(Bet.values())
      .filter(bet -> bet.isBigger(gameState.getCurrentlyPlayedBet()) || bet == Bet.None)
      .map(bet -> (Command) new ShoutBet(gameState.getCurrentPlayer(), bet))
      .toList();
  }

  @Override
  public @NonNull PhaseResult execute(@NonNull Command command) {
    if (command instanceof ShoutBet shoutedBet) {
      return this.handleShoutedBet(shoutedBet);
    }

    return new PhaseResult(
      Optional.empty(),
      List.of(
        new InvalidCommandReceived(String.format("Only %s command is currently accepted", ShoutBet.class.getSimpleName()))
      )
    );
  }

  private PhaseResult handleShoutedBet(ShoutBet shoutedBet) {
    var result = checkInvalidCommand(shoutedBet);
    if (result != null) {
      return new PhaseResult(Optional.empty(), List.of(result));
    }

    Class<? extends GamePhase> nextPhase = null;
    var resultedEvents = new ArrayList<Event>(2);

    var betShouted = new BetShouted(shoutedBet.player(), shoutedBet.bet());
    resultedEvents.add(betShouted);
    gameState.apply(betShouted);

    if (isCurrentHighestBetAccepted() || isNoneBetAccepted()) {
      var betAccepted = new BetAccepted(gameState.getCurrentlyBettingPlayer(), gameState.getCurrentlyPlayedBet());
      resultedEvents.add(betAccepted);
      gameState.apply(betAccepted);
      nextPhase = PlayCardPhase.class;
    }

    return new PhaseResult(Optional.ofNullable(nextPhase), List.copyOf(resultedEvents));
  }

  private Event checkInvalidCommand(ShoutBet shoutedBet) {
    if (isWrongPlayerTurn(shoutedBet)) {
      return new InvalidCommandReceived(
        String.format("It's the turn of %s and not %s", gameState.getCurrentPlayer().name(), shoutedBet.player().name())
      );
    }

    if (isNoneBet(shoutedBet)) {
      return null;
    }

    if (isEqualBet(shoutedBet)) {
      return new InvalidCommandReceived(
        String.format("Cannot place same bet. Placed again: %s", gameState.getCurrentlyPlayedBet())
      );
    }

    if (isSmallerBet(shoutedBet)) {
      return new InvalidCommandReceived(
        String.format("Cannot place lower bet. Current: %s Placed: %s", gameState.getCurrentlyPlayedBet(), shoutedBet.bet())
      );
    }

    return null;
  }

  private boolean isWrongPlayerTurn(ShoutBet shoutedBet) {
    return !shoutedBet.player().equals(gameState.getCurrentPlayer());
  }

  private boolean isNoneBet(ShoutBet shoutedBet) {
    return shoutedBet.bet().getMode() == Mode.None;
  }

  private boolean isEqualBet(ShoutBet shoutedBet) {
    return shoutedBet.bet().equals(gameState.getCurrentlyPlayedBet());
  }

  private boolean isSmallerBet(ShoutBet shoutedBet) {
    return gameState.getCurrentlyPlayedBet().isBigger(shoutedBet.bet());
  }

  private boolean isCurrentHighestBetAccepted() {
    return gameState.getCurrentlyPlayedBet().isHighest() || isBetAcceptedFromOthers();
  }

  private boolean isBetAcceptedFromOthers() {
    return gameState.getCurrentlyPlayedBet().getMode() != Mode.None && gameState.getConsecutiveNoneBets() == gameState.getPlayerCount() - 1;
  }

  private boolean isNoneBetAccepted() {
    return gameState.getConsecutiveNoneBets() == gameState.getPlayerCount();
  }
}
