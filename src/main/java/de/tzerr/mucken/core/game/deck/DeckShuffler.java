package de.tzerr.mucken.core.game.deck;

import lombok.NonNull;

public sealed interface DeckShuffler permits PresetShuffler, SimpleRandomShuffler {

  @NonNull Deck shuffle(@NonNull Deck deck);
}
