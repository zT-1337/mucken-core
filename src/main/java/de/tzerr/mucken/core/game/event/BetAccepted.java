package de.tzerr.mucken.core.game.event;

import de.tzerr.mucken.core.game.Player;
import de.tzerr.mucken.core.game.bet.Bet;
import lombok.NonNull;

public record BetAccepted(@NonNull Player player, @NonNull Bet bet) implements Event {

  @Override
  @NonNull
  public String toString() {
    return String.format("Accepted %s bet by %s ", bet, player);
  }
}
