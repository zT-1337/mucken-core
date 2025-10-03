package de.tzerr.mucken.core.game.event;

import de.tzerr.mucken.core.game.Player;
import de.tzerr.mucken.core.game.Sting;
import lombok.NonNull;

public record WonSting(@NonNull Player winner, @NonNull Sting sting) implements Event {

  @Override
  @NonNull
  public String toString() {
    return String.format("Sting won by %s with %s", winner, sting);
  }
}
