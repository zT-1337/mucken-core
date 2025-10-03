package de.tzerr.mucken.core.game.bet;

import de.tzerr.mucken.core.game.card.Card;
import de.tzerr.mucken.core.game.card.Color;
import de.tzerr.mucken.core.game.card.Rank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor
public enum Mode {
  None(0, 2), Reds(1, 2), Minors(2, 3), Majors(3, 3);

  private final int priority;
  private final int basePoints;

  public boolean isTrumpCard(@NonNull Card card) {
    return switch (this) {
      case None, Reds ->
        card.getColor().equals(Color.Red) || card.getRank().equals(Rank.Minor) || card.getRank().equals(Rank.Major);
      case Minors -> card.getRank().equals(Rank.Minor);
      case Majors -> card.getRank().equals(Rank.Major);
    };
  }
}
