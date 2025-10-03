package de.tzerr.mucken.core.game;

import lombok.NonNull;

public record Player(@NonNull String name) {

  @Override
  @NonNull
  public String toString() {
    return name;
  }
}
