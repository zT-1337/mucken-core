package de.tzerr.mucken.core.game.command;

import de.tzerr.mucken.core.game.Player;
import de.tzerr.mucken.core.game.card.Card;
import lombok.NonNull;

public record PlayCard(@NonNull Player player, @NonNull Card card) implements Command {

  @NonNull
  @Override
  public String toString() {
    return String.format("%s plays %s", player, card);
  }
}
