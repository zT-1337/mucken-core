package de.tzerr.mucken.core.game.deck;

import de.tzerr.mucken.core.game.card.Card;
import lombok.NonNull;

import java.util.List;

import static de.tzerr.mucken.core.game.card.Card.BROWN_ACE;
import static de.tzerr.mucken.core.game.card.Card.BROWN_KING;
import static de.tzerr.mucken.core.game.card.Card.BROWN_MAJOR;
import static de.tzerr.mucken.core.game.card.Card.BROWN_MINOR;
import static de.tzerr.mucken.core.game.card.Card.BROWN_NINE;
import static de.tzerr.mucken.core.game.card.Card.BROWN_TEN;
import static de.tzerr.mucken.core.game.card.Card.GREEN_ACE;
import static de.tzerr.mucken.core.game.card.Card.GREEN_KING;
import static de.tzerr.mucken.core.game.card.Card.GREEN_MAJOR;
import static de.tzerr.mucken.core.game.card.Card.GREEN_MINOR;
import static de.tzerr.mucken.core.game.card.Card.GREEN_NINE;
import static de.tzerr.mucken.core.game.card.Card.GREEN_TEN;
import static de.tzerr.mucken.core.game.card.Card.RED_ACE;
import static de.tzerr.mucken.core.game.card.Card.RED_KING;
import static de.tzerr.mucken.core.game.card.Card.RED_MAJOR;
import static de.tzerr.mucken.core.game.card.Card.RED_MINOR;
import static de.tzerr.mucken.core.game.card.Card.RED_NINE;
import static de.tzerr.mucken.core.game.card.Card.RED_TEN;
import static de.tzerr.mucken.core.game.card.Card.THE_OLD_ONE;
import static de.tzerr.mucken.core.game.card.Card.YELLOW_ACE;
import static de.tzerr.mucken.core.game.card.Card.YELLOW_KING;
import static de.tzerr.mucken.core.game.card.Card.YELLOW_MINOR;
import static de.tzerr.mucken.core.game.card.Card.YELLOW_NINE;
import static de.tzerr.mucken.core.game.card.Card.YELLOW_TEN;

public record Deck(@NonNull List<Card> cards) {
  private static final List<Card> DEFAULT_DECK_LIST = List.of(
    BROWN_NINE, RED_NINE, GREEN_NINE, YELLOW_NINE,
    BROWN_MINOR, RED_MINOR, GREEN_MINOR, YELLOW_MINOR,
    BROWN_MAJOR, RED_MAJOR, GREEN_MAJOR, THE_OLD_ONE,
    BROWN_KING, RED_KING, GREEN_KING, YELLOW_KING,
    BROWN_TEN, RED_TEN, GREEN_TEN, YELLOW_TEN,
    BROWN_ACE, RED_ACE, GREEN_ACE, YELLOW_ACE
  );

  public Deck {
    cards = List.copyOf(cards);
  }

  public static @NonNull Deck defaultDeck() {
    return new Deck(List.copyOf(DEFAULT_DECK_LIST));
  }
}
