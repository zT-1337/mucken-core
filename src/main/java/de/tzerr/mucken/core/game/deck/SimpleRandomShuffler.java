package de.tzerr.mucken.core.game.deck;

import de.tzerr.mucken.core.game.card.Card;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SuppressWarnings("ClassCanBeRecord")
public final class SimpleRandomShuffler implements DeckShuffler {

  private final int rounds;
  private final Random random;

  public SimpleRandomShuffler(int rounds, long seed) {
    this.rounds = rounds;
    this.random = new Random(seed);
  }

  @Override
  public @NonNull Deck shuffle(@NonNull Deck deck) {
    var cardPositions = createCardPositions(deck);
    shuffleCardPositions(cardPositions);
    return new Deck(applyCardPositions(deck, cardPositions));
  }

  private List<Integer> createCardPositions(Deck deck) {
    List<Integer> positions = new ArrayList<>(deck.cards().size());
    for (int i = 0; i < deck.cards().size(); i++) {
      positions.add(i);
    }

    return positions;
  }

  private void shuffleCardPositions(List<Integer> cardPositions) {
    int currentRound = 0;
    while (currentRound < rounds) {
      for (int i = 0; i < cardPositions.size(); i++) {
        swap(cardPositions, i, random.nextInt(cardPositions.size()));
      }
      currentRound++;
    }
  }

  private void swap(List<Integer> cardPositions, int i, int j) {
    var temp = cardPositions.get(i);
    cardPositions.set(i, cardPositions.get(j));
    cardPositions.set(j, temp);
  }

  private List<Card> applyCardPositions(Deck deck, List<Integer> cardPositions) {
    var cards = new ArrayList<Card>(deck.cards().size());

    for (var position : cardPositions) {
      cards.add(deck.cards().get(position));
    }

    return cards;
  }
}
