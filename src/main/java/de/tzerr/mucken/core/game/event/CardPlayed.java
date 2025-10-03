package de.tzerr.mucken.core.game.event;

import de.tzerr.mucken.core.game.Player;
import de.tzerr.mucken.core.game.card.Card;
import lombok.NonNull;

public record CardPlayed(@NonNull Player player, @NonNull Card card) implements Event {

  @Override
  @NonNull
  public String toString() {
    return String.format("%s played %s", player, card);
  }
}
