package de.tzerr.mucken.core.game;

import de.tzerr.mucken.core.game.card.Card;
import lombok.NonNull;

import java.util.List;

public record Sting(@NonNull Team owner, @NonNull List<Card> playedCards) {
  public Sting {
    playedCards = List.copyOf(playedCards);
  }

  @Override
  @NonNull
  public String toString() {
    return String.format("Sting owned by %s with %s", owner, playedCards);
  }
}
