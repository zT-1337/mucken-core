package de.tzerr.mucken.core.game.command;

import de.tzerr.mucken.core.game.Player;
import de.tzerr.mucken.core.game.bet.Bet;
import lombok.NonNull;

public record ShoutBet(@NonNull Player player, @NonNull Bet bet) implements Command {

  @NonNull
  @Override
  public String toString() {
    return String.format("%s bets %s", player, bet);
  }
}
