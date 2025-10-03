package de.tzerr.mucken.core.game;

import lombok.NonNull;

public record Team(@NonNull Player first, @NonNull Player second) {

  public boolean containsPlayer(Player player) {
    return first.equals(player) || second.equals(player);
  }

  @Override
  @NonNull
  public String toString() {
    return String.format("%s, %s", first, second);
  }
}
