package de.tzerr.mucken.core.game.event;

import de.tzerr.mucken.core.game.Team;
import lombok.NonNull;

public record GameFinished(@NonNull Team winningTeam, int winningTeamScore,
                           @NonNull Team otherTeam, int otherTeamScore) implements Event {

  @Override
  @NonNull
  public String toString() {
    return String.format(
      "Game was won by %s with %d points.%nGame was lost by %s with %d points.",
      winningTeam, winningTeamScore, otherTeam, otherTeamScore
    );
  }
}
