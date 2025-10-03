package de.tzerr.mucken.core.game;

import de.tzerr.mucken.core.game.deck.Deck;

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

public class DeckTestData {
  public static final Deck ORDER_BY_RANK = new Deck(List.of(
    //Thömels Cards
    BROWN_NINE, RED_NINE, GREEN_NINE, YELLOW_NINE, BROWN_MINOR, RED_MINOR,
    //Leas Cards
    GREEN_MINOR, YELLOW_MINOR, BROWN_MAJOR, RED_MAJOR, GREEN_MAJOR, THE_OLD_ONE,
    //Marcels Cards
    BROWN_KING, RED_KING, GREEN_KING, YELLOW_KING, BROWN_TEN, RED_TEN,
    //Flos Cards
    GREEN_TEN, YELLOW_TEN, BROWN_ACE, RED_ACE, GREEN_ACE, YELLOW_ACE
  ));

  public static final Deck ORDER_BY_COLOR = new Deck(List.of(
    //Thömels Cards
    BROWN_NINE, BROWN_MINOR, BROWN_MAJOR, BROWN_KING, BROWN_TEN, BROWN_ACE,
    //Leas Cards
    RED_NINE, RED_MINOR, RED_MAJOR, RED_KING, RED_TEN, RED_ACE,
    //Marcels Cards
    GREEN_NINE, GREEN_MINOR, GREEN_MAJOR, GREEN_KING, GREEN_TEN, GREEN_ACE,
    //Flos Cards
    YELLOW_NINE, YELLOW_MINOR, THE_OLD_ONE, YELLOW_KING, YELLOW_TEN, YELLOW_ACE
  ));

  public static final Deck REDS_CLEAN_WIN_DECK = new Deck(List.of(
    //Thömels Cards
    THE_OLD_ONE, GREEN_MAJOR, RED_MAJOR, BROWN_MAJOR, YELLOW_MINOR, GREEN_MINOR,
    //Leas Cards
    RED_MINOR, BROWN_MINOR, RED_ACE, RED_TEN, RED_KING, RED_NINE,
    //Marcels Cards
    YELLOW_ACE, YELLOW_TEN, YELLOW_KING, YELLOW_NINE, BROWN_ACE, BROWN_TEN,
    //Flos Cards
    GREEN_ACE, GREEN_TEN, GREEN_KING, GREEN_NINE, BROWN_KING, BROWN_NINE
  ));

  public static final Deck REDS_HIGH_WIN_DECK = new Deck(List.of(
    //Thömels Cards
    THE_OLD_ONE, GREEN_MAJOR, RED_MAJOR, BROWN_MAJOR, RED_MINOR, BROWN_MINOR,
    //Leas Cards
    YELLOW_MINOR, GREEN_MINOR, RED_ACE, RED_TEN, RED_KING, RED_NINE,
    //Marcels Cards
    YELLOW_ACE, YELLOW_TEN, YELLOW_KING, YELLOW_NINE, BROWN_ACE, BROWN_TEN,
    //Flos Cards
    GREEN_ACE, GREEN_TEN, GREEN_KING, GREEN_NINE, BROWN_KING, BROWN_NINE
  ));

  public static final Deck MINORS_OR_MAJORS_CLEAN_WIN_DECK = new Deck(List.of(
    //Thömels Cards
    YELLOW_MINOR, GREEN_MINOR, RED_MINOR, BROWN_MINOR, YELLOW_ACE, YELLOW_TEN,
    //Leas Cards
    THE_OLD_ONE, GREEN_MAJOR, RED_MAJOR, BROWN_MAJOR, GREEN_ACE, GREEN_TEN,
    //Marcels Cards
    YELLOW_KING, YELLOW_NINE, GREEN_KING, GREEN_NINE, BROWN_KING, BROWN_NINE,
    //Flos Cards
    RED_ACE, RED_TEN, RED_KING, RED_NINE, BROWN_ACE, BROWN_TEN
  ));

  public static final Deck MINORS_OR_MAJORS_HIGH_WIN_DECK = new Deck(List.of(
    //Thömels Cards
    YELLOW_MINOR, GREEN_MINOR, RED_MINOR, BROWN_MINOR, YELLOW_NINE, GREEN_NINE,
    //Leas Cards
    THE_OLD_ONE, GREEN_MAJOR, RED_MAJOR, BROWN_MAJOR, YELLOW_KING, GREEN_KING,
    //Marcels Cards
    GREEN_ACE, GREEN_TEN, RED_ACE, RED_TEN, BROWN_KING, BROWN_NINE,
    //Flos Cards
    YELLOW_ACE, YELLOW_TEN, RED_KING, RED_NINE, BROWN_ACE, BROWN_TEN
  ));

  public static final Deck TRASH_BET_DRAW_DECK = new Deck(List.of(
    //Thömels Cards
    THE_OLD_ONE, BROWN_MINOR, BROWN_KING, GREEN_NINE, RED_ACE, YELLOW_ACE,
    //Leas Cards
    GREEN_MAJOR, RED_MAJOR, BROWN_ACE, GREEN_ACE, RED_TEN, YELLOW_TEN,
    //Marcels Cards
    GREEN_MINOR, BROWN_MAJOR, BROWN_TEN, GREEN_TEN, RED_KING, YELLOW_KING,
    //Flos Cards
    YELLOW_MINOR, RED_MINOR, BROWN_NINE, GREEN_KING, RED_NINE, YELLOW_NINE
  ));
}
