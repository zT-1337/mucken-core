package de.tzerr.mucken.core.game.event;

import de.tzerr.mucken.core.game.Player;
import de.tzerr.mucken.core.game.bet.Bet;
import lombok.NonNull;

public record BetShouted(@NonNull Player player, @NonNull Bet bet) implements Event {

  @Override
  @NonNull
  public String toString() {
    return String.format("%s bets %s", player, bet);
  }
}
