package de.tzerr.mucken.core.game.card;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Rank {
  Nine(0, 0),
  Minor(2, 1),
  Major(3, 2),
  King(4, 0),
  Ten(10, 0),
  Ace(11, 0);

  private final int points;
  private final int trumpPriority;
}
