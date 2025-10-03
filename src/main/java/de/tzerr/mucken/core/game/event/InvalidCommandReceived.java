package de.tzerr.mucken.core.game.event;

import lombok.NonNull;

public record InvalidCommandReceived(@NonNull String reason) implements Event {

  @Override
  @NonNull
  public String toString() {
    return reason;
  }
}
