package de.tzerr.mucken.core.game.bet;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Modifier {
  Normal(0, 61, 0), HighWin(3, 90, 2), CleanWin(6, 120, 4);

  private final int priority;
  private final int minWinScore;
  private final int basePoints;

}
