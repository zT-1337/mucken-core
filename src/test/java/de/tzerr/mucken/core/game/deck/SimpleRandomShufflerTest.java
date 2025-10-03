package de.tzerr.mucken.core.game.deck;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleRandomShufflerTest {

  @Test
  void shouldOnlyContainEachCardOnce() {
    var deck = Deck.defaultDeck();
    var shuffler = new SimpleRandomShuffler(5, 0L);
    var shuffledDeck = shuffler.shuffle(deck);

    assertThat(shuffledDeck.cards()).hasSize(deck.cards().size());
    for (var card : deck.cards()) {
      assertThat(shuffledDeck.cards()).contains(card);
    }
  }

  @Test
  void shouldShuffleSameOrderWithSameSeedAndRounds() {
    var deck = Deck.defaultDeck();
    var firstShuffler = new SimpleRandomShuffler(5, 0L);
    var firstShuffledDeck = firstShuffler.shuffle(deck);

    var secondShuffler = new SimpleRandomShuffler(5, 0L);
    var secondShuffledDeck = secondShuffler.shuffle(deck);

    assertThat(firstShuffledDeck.cards()).isEqualTo(secondShuffledDeck.cards());
  }

  @Test
  void shouldNotShuffleIfRoundsIsZero() {
    var deck = Deck.defaultDeck();
    var shuffler = new SimpleRandomShuffler(0, 0L);
    var shuffledDeck = shuffler.shuffle(deck);

    assertThat(shuffledDeck.cards()).isEqualTo(deck.cards());
  }
}