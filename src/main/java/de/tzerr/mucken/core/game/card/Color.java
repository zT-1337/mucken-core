package de.tzerr.mucken.core.game.card;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Color {
  Brown(0), Red(1), Green(2), Yellow(3);

  private final int priority;
}
