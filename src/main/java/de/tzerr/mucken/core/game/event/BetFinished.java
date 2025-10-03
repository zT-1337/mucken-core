package de.tzerr.mucken.core.game.event;

import de.tzerr.mucken.core.game.Team;
import lombok.NonNull;

import java.util.Optional;

public record BetFinished(@NonNull Optional<Team> winner,
                          @NonNull Optional<Integer> winningScore,
                          int betPoints) implements Event {

  @Override
  @NonNull
  public String toString() {
    return winner.map(
      team -> String.format("Bet was won by %s and received %d points", team, betPoints)
    ).orElse("Bet was drawn");
  }
}
