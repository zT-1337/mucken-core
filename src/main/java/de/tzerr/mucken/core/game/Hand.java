package de.tzerr.mucken.core.game;

import de.tzerr.mucken.core.game.bet.Mode;
import de.tzerr.mucken.core.game.card.Card;
import de.tzerr.mucken.core.game.card.Color;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.util.List;

@EqualsAndHashCode
public final class Hand {

  private final List<Card> cards;
  private final boolean[] playedCards;

  public Hand(@NonNull List<Card> cards) {
    this.cards = List.copyOf(cards);
    this.playedCards = new boolean[cards.size()];
  }

  @Override
  public String toString() {
    return getCards().toString();
  }

  public boolean hasColor(@NonNull Color color, @NonNull Mode mode) {
    return cards.stream().anyMatch(
      card -> card.getColor() == color && !mode.isTrumpCard(card) && hasCard(card)
    );
  }

  public boolean hasTrump(@NonNull Mode mode) {
    return cards.stream().anyMatch(card -> mode.isTrumpCard(card) && hasCard(card));
  }

  public boolean hasCard(@NonNull Card card) {
    var cardPosition = cards.indexOf(card);
    return cardPosition != -1 && !playedCards[cardPosition];
  }

  void markCardAsPlayed(@NonNull Card card) {
    var cardPosition = cards.indexOf(card);

    if (cardPosition != -1) {
      playedCards[cardPosition] = true;
    }
  }

  public List<Card> getCards() {
    return cards.stream().filter(card -> !playedCards[cards.indexOf(card)]).toList();
  }
}
