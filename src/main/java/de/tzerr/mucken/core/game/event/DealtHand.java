package de.tzerr.mucken.core.game.event;

import de.tzerr.mucken.core.game.Hand;
import de.tzerr.mucken.core.game.Player;
import lombok.NonNull;

public record DealtHand(@NonNull Player player, @NonNull Hand hand) implements Event {

  @Override
  @NonNull
  public String toString() {
    return String.format("%s received %s", player, hand);
  }
}
