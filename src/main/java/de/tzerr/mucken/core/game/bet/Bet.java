package de.tzerr.mucken.core.game.bet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor
public enum Bet {
  None(Mode.None, Modifier.Normal),

  NormalReds(Mode.Reds, Modifier.Normal),
  HighReds(Mode.Reds, Modifier.HighWin),
  CleanReds(Mode.Reds, Modifier.CleanWin),

  NormalMinors(Mode.Minors, Modifier.Normal),
  HighMinors(Mode.Minors, Modifier.HighWin),
  CleanMinors(Mode.Minors, Modifier.CleanWin),

  NormalMajors(Mode.Majors, Modifier.Normal),
  HighMajors(Mode.Majors, Modifier.HighWin),
  CleanMajors(Mode.Majors, Modifier.CleanWin);

  private final Mode mode;
  private final Modifier modifier;

  @Override
  public String toString() {
    return this == None ? mode.toString() : String.format("%s %s", modifier, mode);
  }

  public boolean isNoneBet() {
    return mode == Mode.None && modifier == Modifier.Normal;
  }

  public boolean isBigger(@NonNull Bet bet) {
    return this.priority() > bet.priority();
  }

  public boolean isHighest() {
    return this.mode == Mode.Majors && this.modifier == Modifier.CleanWin;
  }

  private int priority() {
    return mode.getPriority() + modifier.getPriority();
  }

  public int calcBetPoints(int winningTeamScore) {
    var basePoints = mode.getBasePoints() + modifier.getBasePoints();

    if (mode == Mode.None) {
      return basePoints;
    }

    if (modifier == Modifier.Normal && winningTeamScore >= Modifier.HighWin.getMinWinScore()) {
      basePoints++;
    }

    if ((modifier == Modifier.Normal || modifier == Modifier.HighWin) && winningTeamScore >= Modifier.CleanWin.getMinWinScore()) {
      basePoints++;
    }

    return basePoints;
  }
}
