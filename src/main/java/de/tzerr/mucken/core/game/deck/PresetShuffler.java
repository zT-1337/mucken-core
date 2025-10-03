package de.tzerr.mucken.core.game.deck;

import lombok.NonNull;

public record PresetShuffler(Deck presetDeck) implements DeckShuffler {
  @Override
  public @NonNull Deck shuffle(@NonNull Deck deck) {
    return presetDeck;
  }
}

