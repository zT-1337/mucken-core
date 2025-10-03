package de.tzerr.mucken.core.game.card;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Card {

  THE_OLD_ONE(Rank.Major, Color.Yellow),
  GREEN_MAJOR(Rank.Major, Color.Green),
  RED_MAJOR(Rank.Major, Color.Red),
  BROWN_MAJOR(Rank.Major, Color.Brown),

  YELLOW_MINOR(Rank.Minor, Color.Yellow),
  GREEN_MINOR(Rank.Minor, Color.Green),
  RED_MINOR(Rank.Minor, Color.Red),
  BROWN_MINOR(Rank.Minor, Color.Brown),

  YELLOW_ACE(Rank.Ace, Color.Yellow),
  GREEN_ACE(Rank.Ace, Color.Green),
  RED_ACE(Rank.Ace, Color.Red),
  BROWN_ACE(Rank.Ace, Color.Brown),

  YELLOW_TEN(Rank.Ten, Color.Yellow),
  GREEN_TEN(Rank.Ten, Color.Green),
  RED_TEN(Rank.Ten, Color.Red),
  BROWN_TEN(Rank.Ten, Color.Brown),

  YELLOW_KING(Rank.King, Color.Yellow),
  GREEN_KING(Rank.King, Color.Green),
  RED_KING(Rank.King, Color.Red),
  BROWN_KING(Rank.King, Color.Brown),

  YELLOW_NINE(Rank.Nine, Color.Yellow),
  GREEN_NINE(Rank.Nine, Color.Green),
  RED_NINE(Rank.Nine, Color.Red),
  BROWN_NINE(Rank.Nine, Color.Brown);

  private final Rank rank;
  private final Color color;

  @Override
  public String toString() {
    return String.format("%s-%s", color, rank);
  }

  public boolean isHigherRank(Card card) {
    return this.rank.getPoints() > card.rank.getPoints();
  }

  public boolean isHigherTrump(Card card) {
    if (this.rank.getTrumpPriority() > card.rank.getTrumpPriority()) {
      return true;
    }

    if (this.rank.getTrumpPriority() < card.rank.getTrumpPriority()) {
      return false;
    }

    if (this.color.getPriority() > card.color.getPriority()) {
      return true;
    }

    if (this.color.getPriority() < card.color.getPriority()) {
      return false;
    }

    return isHigherRank(card);
  }
}
