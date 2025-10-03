package de.tzerr.mucken.core.game;

import de.tzerr.mucken.core.game.bet.Bet;
import de.tzerr.mucken.core.game.card.Color;
import de.tzerr.mucken.core.game.command.DealHands;
import de.tzerr.mucken.core.game.command.PlayCard;
import de.tzerr.mucken.core.game.command.ShoutBet;
import de.tzerr.mucken.core.game.deck.Deck;
import de.tzerr.mucken.core.game.deck.DeckShuffler;
import de.tzerr.mucken.core.game.deck.PresetShuffler;
import de.tzerr.mucken.core.game.event.BetAccepted;
import de.tzerr.mucken.core.game.event.BetFinished;
import de.tzerr.mucken.core.game.event.BetShouted;
import de.tzerr.mucken.core.game.event.CardPlayed;
import de.tzerr.mucken.core.game.event.DealtHand;
import de.tzerr.mucken.core.game.event.Event;
import de.tzerr.mucken.core.game.event.GameFinished;
import de.tzerr.mucken.core.game.event.InvalidCommandReceived;
import de.tzerr.mucken.core.game.event.WonSting;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

import static de.tzerr.mucken.core.game.DeckTestData.MINORS_OR_MAJORS_CLEAN_WIN_DECK;
import static de.tzerr.mucken.core.game.DeckTestData.MINORS_OR_MAJORS_HIGH_WIN_DECK;
import static de.tzerr.mucken.core.game.DeckTestData.ORDER_BY_COLOR;
import static de.tzerr.mucken.core.game.DeckTestData.ORDER_BY_RANK;
import static de.tzerr.mucken.core.game.DeckTestData.REDS_CLEAN_WIN_DECK;
import static de.tzerr.mucken.core.game.DeckTestData.REDS_HIGH_WIN_DECK;
import static de.tzerr.mucken.core.game.DeckTestData.TRASH_BET_DRAW_DECK;
import static de.tzerr.mucken.core.game.bet.Bet.CleanMajors;
import static de.tzerr.mucken.core.game.bet.Bet.CleanMinors;
import static de.tzerr.mucken.core.game.bet.Bet.CleanReds;
import static de.tzerr.mucken.core.game.bet.Bet.HighMajors;
import static de.tzerr.mucken.core.game.bet.Bet.HighMinors;
import static de.tzerr.mucken.core.game.bet.Bet.HighReds;
import static de.tzerr.mucken.core.game.bet.Bet.None;
import static de.tzerr.mucken.core.game.bet.Bet.NormalMajors;
import static de.tzerr.mucken.core.game.bet.Bet.NormalMinors;
import static de.tzerr.mucken.core.game.bet.Bet.NormalReds;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DefaultGameTest {

  private static final Player THOMAS = new Player("Thömel");
  private static final Player LEA = new Player("Lea");
  private static final Player MARCEL = new Player("Marcel");
  private static final Player FLO = new Player("Flo");

  private static final Team TEAM_OLD = new Team(THOMAS, MARCEL);
  private static final Team TEAM_YOUNG = new Team(LEA, FLO);

  private static final Player[] defaultOrder = new Player[]{THOMAS, LEA, MARCEL, FLO};

  private DefaultGame buildDefaultGame(DeckShuffler deckShuffler) {
    return new DefaultGame.Builder()
      .withPlayer(THOMAS)
      .withPlayer(LEA)
      .withPlayer(MARCEL)
      .withPlayer(FLO)
      .withDeckShuffler(deckShuffler)
      .build();
  }

  @Nested
  class BuilderTest {

    @Test
    void shouldFailBecauseTooManyPlayersWereAdded() {
      var builder = new DefaultGame.Builder()
        .withPlayer(THOMAS)
        .withPlayer(LEA)
        .withPlayer(MARCEL)
        .withPlayer(FLO);

      assertThatThrownBy(
        () -> builder.withPlayer(new Player("Gabi"))
      ).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldFailBecauseNotEnoughPlayersWereAdded() {
      var builder = new DefaultGame.Builder()
        .withPlayer(THOMAS)
        .withPlayer(LEA)
        .withPlayer(MARCEL)
        .withDeckShuffler(new PresetShuffler(ORDER_BY_RANK));

      assertThatThrownBy(builder::build).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldFailBecausePlayerIsAddedTwice() {
      var builder = new DefaultGame.Builder()
        .withPlayer(THOMAS);

      assertThatThrownBy(
        () -> builder.withPlayer(THOMAS)
      ).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldFailBecauseShufflerIsNotSpecifiedBuildGame() {
      var builder = new DefaultGame.Builder()
        .withPlayer(THOMAS)
        .withPlayer(LEA)
        .withPlayer(MARCEL)
        .withPlayer(FLO);

      assertThatThrownBy(builder::build).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldSuccessfullyBuildGame() {
      var game = new DefaultGame.Builder()
        .withPlayer(THOMAS)
        .withPlayer(LEA)
        .withPlayer(MARCEL)
        .withPlayer(FLO)
        .withDeckShuffler(new PresetShuffler(ORDER_BY_RANK))
        .build();

      assertThat(game.getPlayers()).containsExactly(
        THOMAS,
        LEA,
        MARCEL,
        FLO
      );
    }
  }

  @Nested
  class DealHandsTest {

    @Test
    void shouldDealHands() {
      var game = buildDefaultGame(new PresetShuffler(ORDER_BY_RANK));
      var result = game.execute(new DealHands());

      assertThat(result).containsExactly(
        new DealtHand(
          THOMAS,
          new Hand(List.of(BROWN_NINE, RED_NINE, GREEN_NINE, YELLOW_NINE, BROWN_MINOR, RED_MINOR))
        ),
        new DealtHand(
          LEA,
          new Hand(List.of(GREEN_MINOR, YELLOW_MINOR, BROWN_MAJOR, RED_MAJOR, GREEN_MAJOR, THE_OLD_ONE))
        ),
        new DealtHand(
          MARCEL,
          new Hand(List.of(BROWN_KING, RED_KING, GREEN_KING, YELLOW_KING, BROWN_TEN, RED_TEN))
        ),
        new DealtHand(
          FLO,
          new Hand(List.of(GREEN_TEN, YELLOW_TEN, BROWN_ACE, RED_ACE, GREEN_ACE, YELLOW_ACE))
        )
      );
    }

    @Test
    void shouldOnlyAcceptDealHandsCommand() {
      var game = buildDefaultGame(new PresetShuffler(ORDER_BY_RANK));
      var otherCommands = List.of(new ShoutBet(THOMAS, None), new PlayCard(THOMAS, THE_OLD_ONE));

      for (var command : otherCommands) {
        assertThat(game.execute(command))
          .containsExactly(new InvalidCommandReceived("Only DealHands command is currently accepted"));
      }
    }

    @Test
    void shouldReturnDealHandsAsOnlyAcceptableCommand() {
      var game = buildDefaultGame(new PresetShuffler(ORDER_BY_RANK));
      assertThat(game.getAcceptableCommands()).containsExactlyInAnyOrder(
        new DealHands()
      );
    }
  }

  @Nested
  class BetTest {

    @Test
    void shouldOnlyAcceptShoutBetCommand() {
      var game = buildDefaultGame(new PresetShuffler(ORDER_BY_RANK));
      game.execute(new DealHands());
      var otherCommands = List.of(new DealHands(), new PlayCard(THOMAS, THE_OLD_ONE));

      for (var command : otherCommands) {
        assertThat(game.execute(command))
          .containsExactly(new InvalidCommandReceived("Only ShoutBet command is currently accepted"));
      }
    }

    @Test
    void shouldNotAcceptBetBecauseItsNotPlayersTurn() {
      var game = buildDefaultGame(new PresetShuffler(ORDER_BY_RANK));
      game.execute(new DealHands());
      var result = game.execute(new ShoutBet(LEA, NormalMajors));
      assertThat(result).containsExactly(
        new InvalidCommandReceived("It's the turn of Thömel and not Lea")
      );
    }

    @Test
    void shouldNotAcceptBetBecausePlayerIsNotParticipating() {
      var game = buildDefaultGame(new PresetShuffler(ORDER_BY_RANK));
      game.execute(new DealHands());
      var result = game.execute(new ShoutBet(new Player("Gabi"), NormalMajors));
      assertThat(result).containsExactly(
        new InvalidCommandReceived("It's the turn of Thömel and not Gabi")
      );
    }

    @Test
    void shouldNotAcceptSmallerBet() {
      var game = buildDefaultGame(new PresetShuffler(ORDER_BY_RANK));
      game.execute(new DealHands());
      game.execute(new ShoutBet(THOMAS, HighMinors));
      var result = game.execute(new ShoutBet(LEA, NormalMajors));

      assertThat(result).containsExactly(
        new InvalidCommandReceived(
          String.format(
            "Cannot place lower bet. Current: %s Placed: %s",
            HighMinors,
            NormalMajors
          )
        )
      );
    }

    @Test
    void shouldNotAcceptEqualBet() {
      var game = buildDefaultGame(new PresetShuffler(ORDER_BY_RANK));
      game.execute(new DealHands());
      game.execute(new ShoutBet(THOMAS, NormalMajors));
      var result = game.execute(new ShoutBet(LEA, NormalMajors));

      assertThat(result).containsExactly(
        new InvalidCommandReceived(
          String.format(
            "Cannot place same bet. Placed again: %s",
            NormalMajors
          )
        )
      );
    }

    @Test
    void shouldAcceptAllPossibleBetsInRisingOrderAndStartGameWithHighestBetPossible() {
      var game = buildDefaultGame(new PresetShuffler(ORDER_BY_RANK));
      game.execute(new DealHands());

      assertThat(
        game.execute(new ShoutBet(THOMAS, None))
      ).containsExactly(
        new BetShouted(THOMAS, None)
      );

      assertThat(
        game.execute(new ShoutBet(LEA, NormalReds))
      ).containsExactly(
        new BetShouted(LEA, NormalReds)
      );

      assertThat(
        game.execute(new ShoutBet(MARCEL, NormalMinors))
      ).containsExactly(
        new BetShouted(MARCEL, NormalMinors)
      );

      assertThat(
        game.execute(new ShoutBet(FLO, NormalMajors))
      ).containsExactly(
        new BetShouted(FLO, NormalMajors)
      );

      assertThat(
        game.execute(new ShoutBet(THOMAS, HighReds))
      ).containsExactly(
        new BetShouted(THOMAS, HighReds)
      );

      assertThat(
        game.execute(new ShoutBet(LEA, HighMinors))
      ).containsExactly(
        new BetShouted(LEA, HighMinors)
      );

      assertThat(
        game.execute(new ShoutBet(MARCEL, HighMajors))
      ).containsExactly(
        new BetShouted(MARCEL, HighMajors)
      );

      assertThat(
        game.execute(new ShoutBet(FLO, CleanReds))
      ).containsExactly(
        new BetShouted(FLO, CleanReds)
      );

      assertThat(
        game.execute(new ShoutBet(THOMAS, CleanMinors))
      ).containsExactly(
        new BetShouted(THOMAS, CleanMinors)
      );

      assertThat(
        game.execute(new ShoutBet(LEA, CleanMajors))
      ).containsExactly(
        new BetShouted(LEA, CleanMajors),
        new BetAccepted(LEA, CleanMajors)
      );
    }

    @Test
    void shouldAcceptCurrentHighestBetAfterNoneBets() {
      var game = buildDefaultGame(new PresetShuffler(ORDER_BY_RANK));
      game.execute(new DealHands());
      game.execute(new ShoutBet(THOMAS, NormalMajors));

      assertThat(
        game.execute(new ShoutBet(LEA, None))
      ).containsExactly(
        new BetShouted(LEA, None)
      );

      assertThat(
        game.execute(new ShoutBet(MARCEL, None))
      ).containsExactly(
        new BetShouted(MARCEL, None)
      );

      assertThat(
        game.execute(new ShoutBet(FLO, None))
      ).containsExactly(
        new BetShouted(FLO, None),
        new BetAccepted(THOMAS, NormalMajors)
      );
    }

    @Test
    void shouldStartTrashGameBecauseAllPlayersShoutedNoneBet() {
      var game = buildDefaultGame(new PresetShuffler(ORDER_BY_RANK));
      game.execute(new DealHands());
      game.execute(new ShoutBet(THOMAS, None));
      game.execute(new ShoutBet(LEA, None));
      game.execute(new ShoutBet(MARCEL, None));

      assertThat(
        game.execute(new ShoutBet(FLO, None))
      ).containsExactly(
        new BetShouted(FLO, None),
        new BetAccepted(THOMAS, None)
      );
    }

    @Test
    void shouldReturnAllPossibleBetCommandsFromCurrentPlayer() {
      var game = buildDefaultGame(new PresetShuffler(ORDER_BY_RANK));

      game.execute(new DealHands());
      assertThat(game.getAcceptableCommands()
      ).containsExactlyInAnyOrder(
        new ShoutBet(THOMAS, None),
        new ShoutBet(THOMAS, NormalReds),
        new ShoutBet(THOMAS, NormalMinors),
        new ShoutBet(THOMAS, NormalMajors),
        new ShoutBet(THOMAS, HighReds),
        new ShoutBet(THOMAS, HighMinors),
        new ShoutBet(THOMAS, HighMajors),
        new ShoutBet(THOMAS, CleanReds),
        new ShoutBet(THOMAS, CleanMinors),
        new ShoutBet(THOMAS, CleanMajors)
      );

      game.execute(new ShoutBet(THOMAS, NormalMajors));
      assertThat(game.getAcceptableCommands()
      ).containsExactlyInAnyOrder(
        new ShoutBet(LEA, None),
        new ShoutBet(LEA, HighReds),
        new ShoutBet(LEA, HighMinors),
        new ShoutBet(LEA, HighMajors),
        new ShoutBet(LEA, CleanReds),
        new ShoutBet(LEA, CleanMinors),
        new ShoutBet(LEA, CleanMajors)
      );
    }
  }

  @Nested
  class PlayCardTest {

    void setupGame(Game game, Bet bet, Player[] players) {
      game.execute(new DealHands());
      game.execute(new ShoutBet(players[0], bet));
      game.execute(new ShoutBet(players[1], None));
      game.execute(new ShoutBet(players[2], None));
      game.execute(new ShoutBet(players[3], None));
    }

    void setupGameForTeamOld(Game game, Bet bet) {
      setupGame(game, bet, defaultOrder);
    }

    void setupGameForTeamYoung(Game game, Bet bet) {
      game.execute(new DealHands());
      game.execute(new ShoutBet(THOMAS, None));
      game.execute(new ShoutBet(LEA, bet));
      game.execute(new ShoutBet(MARCEL, None));
      game.execute(new ShoutBet(FLO, None));
      game.execute(new ShoutBet(THOMAS, None));
    }

    void setupTrashGame(Game game) {
      game.execute(new DealHands());
      game.execute(new ShoutBet(THOMAS, None));
      game.execute(new ShoutBet(LEA, None));
      game.execute(new ShoutBet(MARCEL, None));
      game.execute(new ShoutBet(FLO, None));
    }

    List<Event> playRedsGameAsCleanWin(Game game, Player[] players) {
      game.execute(new PlayCard(players[0], THE_OLD_ONE));
      game.execute(new PlayCard(players[1], RED_MINOR));
      game.execute(new PlayCard(players[2], YELLOW_ACE));
      game.execute(new PlayCard(players[3], GREEN_ACE));

      game.execute(new PlayCard(players[0], GREEN_MAJOR));
      game.execute(new PlayCard(players[1], BROWN_MINOR));
      game.execute(new PlayCard(players[2], YELLOW_TEN));
      game.execute(new PlayCard(players[3], GREEN_TEN));

      game.execute(new PlayCard(players[0], RED_MAJOR));
      game.execute(new PlayCard(players[1], RED_ACE));
      game.execute(new PlayCard(players[2], YELLOW_KING));
      game.execute(new PlayCard(players[3], GREEN_KING));

      game.execute(new PlayCard(players[0], BROWN_MAJOR));
      game.execute(new PlayCard(players[1], RED_TEN));
      game.execute(new PlayCard(players[2], YELLOW_NINE));
      game.execute(new PlayCard(players[3], GREEN_NINE));

      game.execute(new PlayCard(players[0], YELLOW_MINOR));
      game.execute(new PlayCard(players[1], RED_KING));
      game.execute(new PlayCard(players[2], BROWN_ACE));
      game.execute(new PlayCard(players[3], BROWN_KING));

      game.execute(new PlayCard(players[0], GREEN_MINOR));
      game.execute(new PlayCard(players[1], RED_NINE));
      game.execute(new PlayCard(players[2], BROWN_TEN));

      return game.execute(new PlayCard(players[3], BROWN_NINE));
    }

    List<Event> playRedsGameAsCleanWinForTeamOld(Game game) {
      return playRedsGameAsCleanWin(game, defaultOrder);
    }

    List<Event> playRedsGameAsHighWin(Game game, Player[] players) {
      game.execute(new PlayCard(players[0], THE_OLD_ONE));
      game.execute(new PlayCard(players[1], RED_NINE));
      game.execute(new PlayCard(players[2], YELLOW_ACE));
      game.execute(new PlayCard(players[3], GREEN_TEN));

      game.execute(new PlayCard(players[0], GREEN_MAJOR));
      game.execute(new PlayCard(players[1], RED_KING));
      game.execute(new PlayCard(players[2], YELLOW_TEN));
      game.execute(new PlayCard(players[3], BROWN_NINE));

      game.execute(new PlayCard(players[0], RED_MAJOR));
      game.execute(new PlayCard(players[1], RED_TEN));
      game.execute(new PlayCard(players[2], BROWN_ACE));
      game.execute(new PlayCard(players[3], GREEN_KING));

      game.execute(new PlayCard(players[0], BROWN_MAJOR));
      game.execute(new PlayCard(players[1], RED_ACE));
      game.execute(new PlayCard(players[2], BROWN_TEN));
      game.execute(new PlayCard(players[3], BROWN_KING));

      game.execute(new PlayCard(players[0], RED_MINOR));
      game.execute(new PlayCard(players[1], YELLOW_MINOR));
      game.execute(new PlayCard(players[2], YELLOW_NINE));
      game.execute(new PlayCard(players[3], GREEN_ACE));

      game.execute(new PlayCard(players[1], GREEN_MINOR));
      game.execute(new PlayCard(players[2], YELLOW_KING));
      game.execute(new PlayCard(players[3], GREEN_NINE));

      return game.execute(new PlayCard(players[0], BROWN_MINOR));
    }

    List<Event> playRedsGameAsHighWinForTeamOld(Game game) {
      return playRedsGameAsHighWin(game, defaultOrder);
    }

    List<Event> playRedsGameAsNormalWin(Game game, Player[] players) {
      game.execute(new PlayCard(players[0], THE_OLD_ONE));
      game.execute(new PlayCard(players[1], RED_NINE));
      game.execute(new PlayCard(players[2], YELLOW_ACE));
      game.execute(new PlayCard(players[3], GREEN_NINE));

      game.execute(new PlayCard(players[0], GREEN_MAJOR));
      game.execute(new PlayCard(players[1], RED_KING));
      game.execute(new PlayCard(players[2], YELLOW_TEN));
      game.execute(new PlayCard(players[3], BROWN_NINE));

      game.execute(new PlayCard(players[0], RED_MAJOR));
      game.execute(new PlayCard(players[1], RED_TEN));
      game.execute(new PlayCard(players[2], BROWN_ACE));
      game.execute(new PlayCard(players[3], GREEN_KING));

      game.execute(new PlayCard(players[0], BROWN_MAJOR));
      game.execute(new PlayCard(players[1], RED_ACE));
      game.execute(new PlayCard(players[2], BROWN_TEN));
      game.execute(new PlayCard(players[3], BROWN_KING));

      game.execute(new PlayCard(players[0], RED_MINOR));
      game.execute(new PlayCard(players[1], YELLOW_MINOR));
      game.execute(new PlayCard(players[2], YELLOW_NINE));
      game.execute(new PlayCard(players[3], GREEN_ACE));

      game.execute(new PlayCard(players[1], GREEN_MINOR));
      game.execute(new PlayCard(players[2], YELLOW_KING));
      game.execute(new PlayCard(players[3], GREEN_TEN));

      return game.execute(new PlayCard(players[0], BROWN_MINOR));
    }

    List<Event> playRedsGameAsNormalWinForTeamOld(Game game) {
      return playRedsGameAsNormalWin(game, defaultOrder);
    }

    List<Event> playMinorsGameAsCleanWin(Game game, Player[] players) {
      game.execute(new PlayCard(players[0], YELLOW_MINOR));
      game.execute(new PlayCard(players[1], THE_OLD_ONE));
      game.execute(new PlayCard(players[2], YELLOW_KING));
      game.execute(new PlayCard(players[3], RED_NINE));

      game.execute(new PlayCard(players[0], GREEN_MINOR));
      game.execute(new PlayCard(players[1], GREEN_MAJOR));
      game.execute(new PlayCard(players[2], YELLOW_NINE));
      game.execute(new PlayCard(players[3], RED_KING));

      game.execute(new PlayCard(players[0], RED_MINOR));
      game.execute(new PlayCard(players[1], RED_MAJOR));
      game.execute(new PlayCard(players[2], BROWN_KING));
      game.execute(new PlayCard(players[3], RED_TEN));

      game.execute(new PlayCard(players[0], BROWN_MINOR));
      game.execute(new PlayCard(players[1], BROWN_MAJOR));
      game.execute(new PlayCard(players[2], BROWN_NINE));
      game.execute(new PlayCard(players[3], RED_ACE));

      game.execute(new PlayCard(players[0], YELLOW_ACE));
      game.execute(new PlayCard(players[1], GREEN_ACE));
      game.execute(new PlayCard(players[2], GREEN_KING));
      game.execute(new PlayCard(players[3], BROWN_ACE));

      game.execute(new PlayCard(players[0], YELLOW_TEN));
      game.execute(new PlayCard(players[1], GREEN_TEN));
      game.execute(new PlayCard(players[2], GREEN_NINE));

      return game.execute(new PlayCard(players[3], BROWN_TEN));
    }

    List<Event> playMinorsGameAsCleanWinForTeamOld(Game game) {
      return playMinorsGameAsCleanWin(game, defaultOrder);
    }

    List<Event> playMinorsGameAsHighWin(Game game, Player[] players) {
      game.execute(new PlayCard(players[0], YELLOW_MINOR));
      game.execute(new PlayCard(players[1], THE_OLD_ONE));
      game.execute(new PlayCard(players[2], GREEN_TEN));
      game.execute(new PlayCard(players[3], YELLOW_TEN));

      game.execute(new PlayCard(players[0], GREEN_MINOR));
      game.execute(new PlayCard(players[1], GREEN_MAJOR));
      game.execute(new PlayCard(players[2], RED_TEN));
      game.execute(new PlayCard(players[3], YELLOW_ACE));

      game.execute(new PlayCard(players[0], RED_MINOR));
      game.execute(new PlayCard(players[1], RED_MAJOR));
      game.execute(new PlayCard(players[2], RED_ACE));
      game.execute(new PlayCard(players[3], RED_KING));

      game.execute(new PlayCard(players[0], BROWN_MINOR));
      game.execute(new PlayCard(players[1], BROWN_MAJOR));
      game.execute(new PlayCard(players[2], BROWN_KING));
      game.execute(new PlayCard(players[3], RED_NINE));

      game.execute(new PlayCard(players[0], GREEN_NINE));
      game.execute(new PlayCard(players[1], GREEN_KING));
      game.execute(new PlayCard(players[2], GREEN_ACE));
      game.execute(new PlayCard(players[3], BROWN_ACE));

      game.execute(new PlayCard(players[2], BROWN_NINE));
      game.execute(new PlayCard(players[3], BROWN_TEN));
      game.execute(new PlayCard(players[0], YELLOW_NINE));

      return game.execute(new PlayCard(players[1], YELLOW_KING));
    }

    List<Event> playMinorsGameAsHighWinForTeamOld(Game game) {
      return playMinorsGameAsHighWin(game, defaultOrder);
    }

    List<Event> playMinorsGameAsNormalWin(Game game, Player[] players) {
      game.execute(new PlayCard(players[0], YELLOW_MINOR));
      game.execute(new PlayCard(players[1], THE_OLD_ONE));
      game.execute(new PlayCard(players[2], GREEN_TEN));
      game.execute(new PlayCard(players[3], YELLOW_TEN));

      game.execute(new PlayCard(players[0], GREEN_MINOR));
      game.execute(new PlayCard(players[1], GREEN_MAJOR));
      game.execute(new PlayCard(players[2], RED_TEN));
      game.execute(new PlayCard(players[3], BROWN_ACE));

      game.execute(new PlayCard(players[0], RED_MINOR));
      game.execute(new PlayCard(players[1], RED_MAJOR));
      game.execute(new PlayCard(players[2], RED_ACE));
      game.execute(new PlayCard(players[3], RED_KING));

      game.execute(new PlayCard(players[0], BROWN_MINOR));
      game.execute(new PlayCard(players[1], BROWN_MAJOR));
      game.execute(new PlayCard(players[2], BROWN_KING));
      game.execute(new PlayCard(players[3], RED_NINE));

      game.execute(new PlayCard(players[0], YELLOW_NINE));
      game.execute(new PlayCard(players[1], YELLOW_KING));
      game.execute(new PlayCard(players[2], GREEN_ACE));
      game.execute(new PlayCard(players[3], YELLOW_ACE));

      game.execute(new PlayCard(players[3], BROWN_TEN));
      game.execute(new PlayCard(players[0], GREEN_NINE));
      game.execute(new PlayCard(players[1], GREEN_KING));

      return game.execute(new PlayCard(players[2], BROWN_NINE));
    }

    List<Event> playMinorsGameAsNormalWinForTeamOld(Game game) {
      return playMinorsGameAsNormalWin(game, defaultOrder);
    }

    List<Event> playMajorsGameAsCleanWin(Game game, Player[] players) {
      game.execute(new PlayCard(players[0], YELLOW_MINOR));
      game.execute(new PlayCard(players[1], THE_OLD_ONE));
      game.execute(new PlayCard(players[2], YELLOW_KING));
      game.execute(new PlayCard(players[3], RED_ACE));

      game.execute(new PlayCard(players[1], GREEN_MAJOR));
      game.execute(new PlayCard(players[2], YELLOW_NINE));
      game.execute(new PlayCard(players[3], RED_TEN));
      game.execute(new PlayCard(players[0], GREEN_MINOR));

      game.execute(new PlayCard(players[1], RED_MAJOR));
      game.execute(new PlayCard(players[2], GREEN_KING));
      game.execute(new PlayCard(players[3], RED_KING));
      game.execute(new PlayCard(players[0], RED_MINOR));

      game.execute(new PlayCard(players[1], BROWN_MAJOR));
      game.execute(new PlayCard(players[2], GREEN_NINE));
      game.execute(new PlayCard(players[3], RED_NINE));
      game.execute(new PlayCard(players[0], BROWN_MINOR));

      game.execute(new PlayCard(players[1], GREEN_ACE));
      game.execute(new PlayCard(players[2], BROWN_KING));
      game.execute(new PlayCard(players[3], BROWN_ACE));
      game.execute(new PlayCard(players[0], YELLOW_ACE));

      game.execute(new PlayCard(players[1], GREEN_TEN));
      game.execute(new PlayCard(players[2], BROWN_NINE));
      game.execute(new PlayCard(players[3], BROWN_TEN));

      return game.execute(new PlayCard(players[0], YELLOW_TEN));
    }

    List<Event> playMajorsGameAsCleanWinForTeamYoung(Game game) {
      return playMajorsGameAsCleanWin(game, new Player[]{THOMAS, LEA, MARCEL, FLO});
    }

    List<Event> playMajorsGameAsHighWin(Game game, Player[] players) {
      game.execute(new PlayCard(players[0], GREEN_NINE));
      game.execute(new PlayCard(players[1], GREEN_KING));
      game.execute(new PlayCard(players[2], GREEN_ACE));
      game.execute(new PlayCard(players[3], RED_NINE));

      game.execute(new PlayCard(players[2], GREEN_TEN));
      game.execute(new PlayCard(players[3], RED_KING));
      game.execute(new PlayCard(players[0], GREEN_MINOR));
      game.execute(new PlayCard(players[1], BROWN_MAJOR));

      game.execute(new PlayCard(players[1], THE_OLD_ONE));
      game.execute(new PlayCard(players[2], BROWN_NINE));
      game.execute(new PlayCard(players[3], BROWN_TEN));
      game.execute(new PlayCard(players[0], YELLOW_NINE));

      game.execute(new PlayCard(players[1], GREEN_MAJOR));
      game.execute(new PlayCard(players[2], BROWN_KING));
      game.execute(new PlayCard(players[3], YELLOW_TEN));
      game.execute(new PlayCard(players[0], BROWN_MINOR));

      game.execute(new PlayCard(players[1], RED_MAJOR));
      game.execute(new PlayCard(players[2], RED_TEN));
      game.execute(new PlayCard(players[3], BROWN_ACE));
      game.execute(new PlayCard(players[0], RED_MINOR));

      game.execute(new PlayCard(players[1], YELLOW_KING));
      game.execute(new PlayCard(players[2], RED_ACE));
      game.execute(new PlayCard(players[3], YELLOW_ACE));

      return game.execute(new PlayCard(players[0], YELLOW_MINOR));
    }

    List<Event> playMajorsGameAsHighWinForTeamYoung(Game game) {
      return playMajorsGameAsHighWin(game, defaultOrder);
    }

    List<Event> playMajorsGameAsNormalWin(Game game, Player[] players) {
      game.execute(new PlayCard(players[0], RED_MINOR));
      game.execute(new PlayCard(players[1], YELLOW_KING));
      game.execute(new PlayCard(players[2], RED_ACE));
      game.execute(new PlayCard(players[3], RED_KING));

      game.execute(new PlayCard(players[2], GREEN_ACE));
      game.execute(new PlayCard(players[3], BROWN_TEN));
      game.execute(new PlayCard(players[0], GREEN_MINOR));
      game.execute(new PlayCard(players[1], GREEN_KING));

      game.execute(new PlayCard(players[2], GREEN_TEN));
      game.execute(new PlayCard(players[3], YELLOW_TEN));
      game.execute(new PlayCard(players[0], GREEN_NINE));
      game.execute(new PlayCard(players[1], BROWN_MAJOR));

      game.execute(new PlayCard(players[1], THE_OLD_ONE));
      game.execute(new PlayCard(players[2], RED_TEN));
      game.execute(new PlayCard(players[3], RED_NINE));
      game.execute(new PlayCard(players[0], YELLOW_MINOR));

      game.execute(new PlayCard(players[1], RED_MAJOR));
      game.execute(new PlayCard(players[2], BROWN_KING));
      game.execute(new PlayCard(players[3], BROWN_ACE));
      game.execute(new PlayCard(players[0], BROWN_MINOR));

      game.execute(new PlayCard(players[1], GREEN_MAJOR));
      game.execute(new PlayCard(players[2], BROWN_NINE));
      game.execute(new PlayCard(players[3], YELLOW_ACE));

      return game.execute(new PlayCard(players[0], YELLOW_NINE));
    }

    List<Event> playMajorsGameAsNormalWinForTeamYoung(Game game) {
      return playMajorsGameAsNormalWin(game, defaultOrder);
    }

    @Test
    void shouldReturnAllPossiblePlayCardCommandsFromCurrentPlayer() {
      var game = buildDefaultGame(new PresetShuffler(MINORS_OR_MAJORS_CLEAN_WIN_DECK));
      setupGameForTeamOld(game, NormalMinors);

      assertThat(game.getAcceptableCommands()
      ).containsExactlyInAnyOrder(
        new PlayCard(THOMAS, YELLOW_MINOR),
        new PlayCard(THOMAS, GREEN_MINOR),
        new PlayCard(THOMAS, RED_MINOR),
        new PlayCard(THOMAS, BROWN_MINOR),
        new PlayCard(THOMAS, YELLOW_ACE),
        new PlayCard(THOMAS, YELLOW_TEN)
      );

      game.execute(new PlayCard(THOMAS, YELLOW_ACE));
      assertThat(game.getAcceptableCommands()
      ).containsExactlyInAnyOrder(
        new PlayCard(LEA, THE_OLD_ONE)
      );
    }

    @Test
    void shouldNotAcceptPlayCardBecausePlayerIsNotParticipating() {
      var game = buildDefaultGame(new PresetShuffler(ORDER_BY_RANK));
      setupTrashGame(game);
      var result = game.execute(new PlayCard(new Player("Gabi"), THE_OLD_ONE));
      assertThat(result).containsExactly(
        new InvalidCommandReceived("It's the turn of Thömel and not Gabi")
      );
    }

    @Test
    void shouldOnlyAcceptShoutBetCommand() {
      var game = buildDefaultGame(new PresetShuffler(ORDER_BY_RANK));
      setupTrashGame(game);
      var otherCommands = List.of(new DealHands(), new ShoutBet(THOMAS, None));

      for (var command : otherCommands) {
        assertThat(game.execute(command))
          .containsExactly(new InvalidCommandReceived("Only PlayCard command is currently accepted"));
      }
    }

    @Test
    void shouldNotAcceptWrongPlayersTurn() {
      var game = buildDefaultGame(new PresetShuffler(ORDER_BY_RANK));
      setupGameForTeamOld(game, NormalMajors);

      var result = game.execute(new PlayCard(LEA, THE_OLD_ONE));

      assertThat(result).containsExactly(
        new InvalidCommandReceived("It's the turn of Thömel and not Lea")
      );
    }

    @Test
    void shouldNotAcceptNotOwnedCard() {
      var game = buildDefaultGame(new PresetShuffler(ORDER_BY_RANK));
      setupGameForTeamOld(game, NormalMajors);

      var result = game.execute(new PlayCard(THOMAS, THE_OLD_ONE));

      assertThat(result).containsExactly(
        new InvalidCommandReceived("Thömel does not have this card " + THE_OLD_ONE)
      );
    }

    @Test
    void shouldNotAcceptColorDenialWithOtherColor() {
      var game = buildDefaultGame(new PresetShuffler(ORDER_BY_RANK));
      setupGameForTeamOld(game, NormalMajors);

      game.execute(new PlayCard(THOMAS, GREEN_NINE));

      assertThat(
        game.execute(new PlayCard(LEA, YELLOW_MINOR))
      ).containsExactly(
        new InvalidCommandReceived("Lea has to admit color " + Color.Green.name())
      );
    }

    @Test
    void shouldNotAcceptColorDenialWithTrumpCard() {
      var game = buildDefaultGame(new PresetShuffler(ORDER_BY_RANK));
      setupGameForTeamOld(game, NormalMajors);
      game.execute(new PlayCard(THOMAS, GREEN_NINE));

      assertThat(
        game.execute(new PlayCard(LEA, THE_OLD_ONE))
      ).containsExactly(
        new InvalidCommandReceived("Lea has to admit color " + Color.Green.name())
      );
    }

    @Test
    void shouldNotAcceptColorDenialWithTrumpCardOfSameColor() {
      var game = buildDefaultGame(new PresetShuffler(ORDER_BY_RANK));
      setupGameForTeamOld(game, NormalMajors);
      game.execute(new PlayCard(THOMAS, GREEN_NINE));

      assertThat(
        game.execute(new PlayCard(LEA, GREEN_MAJOR))
      ).containsExactly(
        new InvalidCommandReceived("Lea has to admit color " + Color.Green.name())
      );
    }

    @Test
    void shouldNotAcceptTrumpDenialWithRedsGame() {
      var game = buildDefaultGame(new PresetShuffler(ORDER_BY_RANK));
      setupGameForTeamOld(game, NormalReds);
      game.execute(new PlayCard(THOMAS, RED_NINE));
      game.execute(new PlayCard(LEA, THE_OLD_ONE));

      assertThat(
        game.execute(new PlayCard(MARCEL, BROWN_KING))
      ).containsExactly(
        new InvalidCommandReceived("Marcel has to admit trump")
      );
    }

    @Test
    void shouldNotAcceptTrumpDenialWithMinorsGame() {
      var game = buildDefaultGame(new PresetShuffler(ORDER_BY_RANK));
      setupGameForTeamOld(game, NormalMinors);
      game.execute(new PlayCard(THOMAS, BROWN_MINOR));

      assertThat(
        game.execute(new PlayCard(LEA, THE_OLD_ONE))
      ).containsExactly(
        new InvalidCommandReceived("Lea has to admit trump")
      );
    }

    @Test
    void shouldNotAcceptTrumpDenialWithMajorsGame() {
      var game = buildDefaultGame(new PresetShuffler(ORDER_BY_COLOR));
      setupGameForTeamOld(game, NormalMajors);
      game.execute(new PlayCard(THOMAS, BROWN_MAJOR));

      assertThat(
        game.execute(new PlayCard(LEA, RED_NINE))
      ).containsExactly(
        new InvalidCommandReceived("Lea has to admit trump")
      );
    }

    @Test
    void shouldNotAcceptAlreadyPlayedCard() {
      var game = buildDefaultGame(new PresetShuffler(ORDER_BY_COLOR));
      setupGameForTeamOld(game, NormalMajors);
      game.execute(new PlayCard(THOMAS, BROWN_NINE));
      game.execute(new PlayCard(LEA, RED_NINE));
      game.execute(new PlayCard(MARCEL, GREEN_NINE));
      game.execute(new PlayCard(FLO, YELLOW_NINE));

      assertThat(
        game.execute(new PlayCard(THOMAS, BROWN_NINE))
      ).containsExactly(
        new InvalidCommandReceived("Thömel does not have this card " + BROWN_NINE)
      );
    }

    @Test
    void shouldAcceptAnyFirstCard() {
      var game = buildDefaultGame(new PresetShuffler(ORDER_BY_RANK));
      setupGameForTeamOld(game, NormalMajors);

      assertThat(
        game.execute(new PlayCard(THOMAS, GREEN_NINE))
      ).containsExactly(
        new CardPlayed(THOMAS, GREEN_NINE)
      );
    }

    @Test
    void shouldAcceptOtherColorBecausePlayerDoesNotHaveColor() {
      var game = buildDefaultGame(new PresetShuffler(ORDER_BY_RANK));
      setupGameForTeamOld(game, NormalMajors);
      game.execute(new PlayCard(THOMAS, BROWN_NINE));

      assertThat(
        game.execute(new PlayCard(LEA, GREEN_MINOR))
      ).containsExactly(
        new CardPlayed(LEA, GREEN_MINOR)
      );
    }

    @Test
    void shouldAcceptOtherColorBecausePlayerHasAlreadyPlayedAllCardsOfColor() {
      var game = buildDefaultGame(new PresetShuffler(ORDER_BY_RANK));
      setupGameForTeamOld(game, NormalMajors);
      game.execute(new PlayCard(THOMAS, GREEN_NINE));
      game.execute(new PlayCard(LEA, GREEN_MINOR));
      game.execute(new PlayCard(MARCEL, GREEN_KING));
      game.execute(new PlayCard(FLO, GREEN_ACE));
      game.execute(new PlayCard(FLO, GREEN_TEN));

      assertThat(
        game.execute(new PlayCard(THOMAS, BROWN_NINE))
      ).containsExactly(
        new CardPlayed(THOMAS, BROWN_NINE)
      );
    }

    @Test
    void shouldAcceptOtherColorBecauseOnlyTrumpCardIsOfSameColor() {
      var game = buildDefaultGame(new PresetShuffler(ORDER_BY_RANK));
      setupGameForTeamOld(game, NormalMajors);
      game.execute(new PlayCard(THOMAS, BROWN_NINE));

      assertThat(
        game.execute(new PlayCard(LEA, GREEN_MINOR))
      ).containsExactly(
        new CardPlayed(LEA, GREEN_MINOR)
      );
    }

    @Test
    void shouldAcceptOtherColorBecauseTrumpIsPlayedAndPlayerHasNoTrump() {
      var game = buildDefaultGame(new PresetShuffler(ORDER_BY_RANK));
      setupGameForTeamOld(game, NormalMinors);
      game.execute(new PlayCard(THOMAS, BROWN_MINOR));
      game.execute(new PlayCard(LEA, GREEN_MINOR));

      assertThat(
        game.execute(new PlayCard(MARCEL, GREEN_KING))
      ).containsExactly(
        new CardPlayed(MARCEL, GREEN_KING)
      );
    }

    @Test
    void shouldAcceptOtherColorBecauseTrumpIsPlayedAndPlayerHasAlreadyPlayedAllTrumps() {
      var game = buildDefaultGame(new PresetShuffler(ORDER_BY_RANK));
      setupGameForTeamOld(game, NormalReds);
      game.execute(new PlayCard(THOMAS, RED_NINE));
      game.execute(new PlayCard(LEA, GREEN_MINOR));
      game.execute(new PlayCard(MARCEL, RED_KING));
      game.execute(new PlayCard(FLO, RED_ACE));
      game.execute(new PlayCard(LEA, THE_OLD_ONE));
      game.execute(new PlayCard(MARCEL, RED_TEN));

      assertThat(
        game.execute(new PlayCard(FLO, YELLOW_TEN))
      ).containsExactly(
        new CardPlayed(FLO, YELLOW_TEN)
      );
    }

    @Test
    void shouldStartNewRoundWithWinnerOfLastSting() {
      var game = buildDefaultGame(new PresetShuffler(ORDER_BY_RANK));
      setupGameForTeamOld(game, NormalReds);
      game.execute(new PlayCard(THOMAS, RED_NINE));
      game.execute(new PlayCard(LEA, GREEN_MINOR));
      game.execute(new PlayCard(MARCEL, RED_KING));

      assertThat(
        game.execute(new PlayCard(FLO, RED_ACE))
      ).containsExactly(
        new CardPlayed(FLO, RED_ACE),
        new WonSting(LEA, new Sting(TEAM_YOUNG, List.of(RED_NINE, GREEN_MINOR, RED_KING, RED_ACE)))
      );

      assertThat(
        game.execute(new PlayCard(LEA, THE_OLD_ONE))
      ).containsExactly(
        new CardPlayed(LEA, THE_OLD_ONE)
      );
    }

    @Test
    void shouldScoreNormalRedsBetAsCleanWin() {
      var game = buildDefaultGame(new PresetShuffler(REDS_CLEAN_WIN_DECK));
      setupGameForTeamOld(game, NormalReds);

      assertThat(
        playRedsGameAsCleanWinForTeamOld(game)
      ).containsExactly(
        new CardPlayed(FLO, BROWN_NINE),
        new WonSting(THOMAS, new Sting(TEAM_OLD, List.of(GREEN_MINOR, RED_NINE, BROWN_TEN, BROWN_NINE))),
        new BetFinished(Optional.of(TEAM_OLD), Optional.of(120), 4)
      );
    }

    @Test
    void shouldScoreNormalRedsBetAsHighWin() {
      var game = buildDefaultGame(new PresetShuffler(REDS_HIGH_WIN_DECK));
      setupGameForTeamOld(game, NormalReds);

      assertThat(
        playRedsGameAsHighWinForTeamOld(game)
      ).containsExactly(
        new CardPlayed(THOMAS, BROWN_MINOR),
        new WonSting(LEA, new Sting(TEAM_YOUNG, List.of(GREEN_MINOR, YELLOW_KING, GREEN_NINE, BROWN_MINOR))),
        new BetFinished(Optional.of(TEAM_OLD), Optional.of(97), 3)
      );
    }

    @Test
    void shouldScoreNormalRedsBetAsNormalWin() {
      var game = buildDefaultGame(new PresetShuffler(REDS_HIGH_WIN_DECK));
      setupGameForTeamOld(game, NormalReds);

      assertThat(
        playRedsGameAsNormalWinForTeamOld(game)
      ).containsExactly(
        new CardPlayed(THOMAS, BROWN_MINOR),
        new WonSting(LEA, new Sting(TEAM_YOUNG, List.of(GREEN_MINOR, YELLOW_KING, GREEN_TEN, BROWN_MINOR))),
        new BetFinished(Optional.of(TEAM_OLD), Optional.of(87), 2)
      );
    }

    @Test
    void shouldScoreNormalRedsBetAsCleanLoss() {
      var game = buildDefaultGame(new PresetShuffler(REDS_CLEAN_WIN_DECK));
      setupGameForTeamYoung(game, NormalReds);

      assertThat(
        playRedsGameAsCleanWinForTeamOld(game)
      ).containsExactly(
        new CardPlayed(FLO, BROWN_NINE),
        new WonSting(THOMAS, new Sting(TEAM_OLD, List.of(GREEN_MINOR, RED_NINE, BROWN_TEN, BROWN_NINE))),
        new BetFinished(Optional.of(TEAM_OLD), Optional.of(120), 4)
      );
    }

    @Test
    void shouldScoreNormalRedsBetAsHighLoss() {
      var game = buildDefaultGame(new PresetShuffler(REDS_HIGH_WIN_DECK));
      setupGameForTeamYoung(game, NormalReds);

      assertThat(
        playRedsGameAsHighWinForTeamOld(game)
      ).containsExactly(
        new CardPlayed(THOMAS, BROWN_MINOR),
        new WonSting(LEA, new Sting(TEAM_YOUNG, List.of(GREEN_MINOR, YELLOW_KING, GREEN_NINE, BROWN_MINOR))),
        new BetFinished(Optional.of(TEAM_OLD), Optional.of(97), 3)
      );
    }

    @Test
    void shouldScoreNormalRedsBetAsNormalLoss() {
      var game = buildDefaultGame(new PresetShuffler(REDS_HIGH_WIN_DECK));
      setupGameForTeamYoung(game, NormalReds);

      assertThat(
        playRedsGameAsNormalWinForTeamOld(game)
      ).containsExactly(
        new CardPlayed(THOMAS, BROWN_MINOR),
        new WonSting(LEA, new Sting(TEAM_YOUNG, List.of(GREEN_MINOR, YELLOW_KING, GREEN_TEN, BROWN_MINOR))),
        new BetFinished(Optional.of(TEAM_OLD), Optional.of(87), 2)
      );
    }

    @Test
    void shouldScoreNormalMinorsBetAsCleanWin() {
      var game = buildDefaultGame(new PresetShuffler(MINORS_OR_MAJORS_CLEAN_WIN_DECK));
      setupGameForTeamOld(game, NormalMinors);

      assertThat(
        playMinorsGameAsCleanWinForTeamOld(game)
      ).containsExactly(
        new CardPlayed(FLO, BROWN_TEN),
        new WonSting(THOMAS, new Sting(TEAM_OLD, List.of(YELLOW_TEN, GREEN_TEN, GREEN_NINE, BROWN_TEN))),
        new BetFinished(Optional.of(TEAM_OLD), Optional.of(120), 5)
      );
    }

    @Test
    void shouldScoreNormalMinorsBetAsHighWin() {
      var game = buildDefaultGame(new PresetShuffler(MINORS_OR_MAJORS_HIGH_WIN_DECK));
      setupGameForTeamOld(game, NormalMinors);

      assertThat(
        playMinorsGameAsHighWinForTeamOld(game)
      ).containsExactly(
        new CardPlayed(LEA, YELLOW_KING),
        new WonSting(FLO, new Sting(TEAM_YOUNG, List.of(BROWN_NINE, BROWN_TEN, YELLOW_NINE, YELLOW_KING))),
        new BetFinished(Optional.of(TEAM_OLD), Optional.of(106), 4)
      );
    }

    @Test
    void shouldScoreNormalMinorsBetAsNormalWin() {
      var game = buildDefaultGame(new PresetShuffler(MINORS_OR_MAJORS_HIGH_WIN_DECK));
      setupGameForTeamOld(game, NormalMinors);

      assertThat(
        playMinorsGameAsNormalWinForTeamOld(game)
      ).containsExactly(
        new CardPlayed(MARCEL, BROWN_NINE),
        new WonSting(FLO, new Sting(TEAM_YOUNG, List.of(BROWN_TEN, GREEN_NINE, GREEN_KING, BROWN_NINE))),
        new BetFinished(Optional.of(TEAM_OLD), Optional.of(80), 3)
      );
    }

    @Test
    void shouldScoreNormalMinorsBetAsCleanLoss() {
      var game = buildDefaultGame(new PresetShuffler(MINORS_OR_MAJORS_CLEAN_WIN_DECK));
      setupGameForTeamYoung(game, NormalMinors);

      assertThat(
        playMinorsGameAsCleanWinForTeamOld(game)
      ).containsExactly(
        new CardPlayed(FLO, BROWN_TEN),
        new WonSting(THOMAS, new Sting(TEAM_OLD, List.of(YELLOW_TEN, GREEN_TEN, GREEN_NINE, BROWN_TEN))),
        new BetFinished(Optional.of(TEAM_OLD), Optional.of(120), 5)
      );
    }

    @Test
    void shouldScoreNormalMinorsBetAsHighLoss() {
      var game = buildDefaultGame(new PresetShuffler(MINORS_OR_MAJORS_HIGH_WIN_DECK));
      setupGameForTeamYoung(game, NormalMinors);

      assertThat(
        playMinorsGameAsHighWinForTeamOld(game)
      ).containsExactly(
        new CardPlayed(LEA, YELLOW_KING),
        new WonSting(FLO, new Sting(TEAM_YOUNG, List.of(BROWN_NINE, BROWN_TEN, YELLOW_NINE, YELLOW_KING))),
        new BetFinished(Optional.of(TEAM_OLD), Optional.of(106), 4)
      );
    }

    @Test
    void shouldScoreNormalMinorsBetAsNormalLoss() {
      var game = buildDefaultGame(new PresetShuffler(MINORS_OR_MAJORS_HIGH_WIN_DECK));
      setupGameForTeamYoung(game, NormalMinors);

      assertThat(
        playMinorsGameAsNormalWinForTeamOld(game)
      ).containsExactly(
        new CardPlayed(MARCEL, BROWN_NINE),
        new WonSting(FLO, new Sting(TEAM_YOUNG, List.of(BROWN_TEN, GREEN_NINE, GREEN_KING, BROWN_NINE))),
        new BetFinished(Optional.of(TEAM_OLD), Optional.of(80), 3)
      );
    }

    @Test
    void shouldScoreNormalMajorsBetAsCleanWin() {
      var game = buildDefaultGame(new PresetShuffler(MINORS_OR_MAJORS_CLEAN_WIN_DECK));
      setupGameForTeamYoung(game, NormalMajors);

      assertThat(
        playMajorsGameAsCleanWinForTeamYoung(game)
      ).containsExactly(
        new CardPlayed(THOMAS, YELLOW_TEN),
        new WonSting(LEA, new Sting(TEAM_YOUNG, List.of(GREEN_TEN, BROWN_NINE, BROWN_TEN, YELLOW_TEN))),
        new BetFinished(Optional.of(TEAM_YOUNG), Optional.of(120), 5)
      );
    }

    @Test
    void shouldScoreNormalMajorsBetAsHighWin() {
      var game = buildDefaultGame(new PresetShuffler(MINORS_OR_MAJORS_HIGH_WIN_DECK));
      setupGameForTeamYoung(game, NormalMajors);

      assertThat(
        playMajorsGameAsHighWinForTeamYoung(game)
      ).containsExactly(
        new CardPlayed(THOMAS, YELLOW_MINOR),
        new WonSting(FLO, new Sting(TEAM_YOUNG, List.of(YELLOW_KING, RED_ACE, YELLOW_ACE, YELLOW_MINOR))),
        new BetFinished(Optional.of(TEAM_YOUNG), Optional.of(105), 4)
      );
    }

    @Test
    void shouldScoreNormalMajorsBetAsNormalWin() {
      var game = buildDefaultGame(new PresetShuffler(MINORS_OR_MAJORS_HIGH_WIN_DECK));
      setupGameForTeamYoung(game, NormalMajors);

      assertThat(
        playMajorsGameAsNormalWinForTeamYoung(game)
      ).containsExactly(
        new CardPlayed(THOMAS, YELLOW_NINE),
        new WonSting(LEA, new Sting(TEAM_YOUNG, List.of(GREEN_MAJOR, BROWN_NINE, YELLOW_ACE, YELLOW_NINE))),
        new BetFinished(Optional.of(TEAM_YOUNG), Optional.of(72), 3)
      );
    }

    @Test
    void shouldScoreNormalMajorsBetAsCleanLoss() {
      var game = buildDefaultGame(new PresetShuffler(MINORS_OR_MAJORS_CLEAN_WIN_DECK));
      setupGameForTeamOld(game, NormalMajors);

      assertThat(
        playMajorsGameAsCleanWinForTeamYoung(game)
      ).containsExactly(
        new CardPlayed(THOMAS, YELLOW_TEN),
        new WonSting(LEA, new Sting(TEAM_YOUNG, List.of(GREEN_TEN, BROWN_NINE, BROWN_TEN, YELLOW_TEN))),
        new BetFinished(Optional.of(TEAM_YOUNG), Optional.of(120), 5)
      );
    }

    @Test
    void shouldScoreNormalMajorsBetAsHighLoss() {
      var game = buildDefaultGame(new PresetShuffler(MINORS_OR_MAJORS_HIGH_WIN_DECK));
      setupGameForTeamOld(game, NormalMajors);

      assertThat(
        playMajorsGameAsHighWinForTeamYoung(game)
      ).containsExactly(
        new CardPlayed(THOMAS, YELLOW_MINOR),
        new WonSting(FLO, new Sting(TEAM_YOUNG, List.of(YELLOW_KING, RED_ACE, YELLOW_ACE, YELLOW_MINOR))),
        new BetFinished(Optional.of(TEAM_YOUNG), Optional.of(105), 4)
      );
    }

    @Test
    void shouldScoreNormalMajorsBetAsNormalLoss() {
      var game = buildDefaultGame(new PresetShuffler(MINORS_OR_MAJORS_HIGH_WIN_DECK));
      setupGameForTeamOld(game, NormalMajors);

      assertThat(
        playMajorsGameAsNormalWinForTeamYoung(game)
      ).containsExactly(
        new CardPlayed(THOMAS, YELLOW_NINE),
        new WonSting(LEA, new Sting(TEAM_YOUNG, List.of(GREEN_MAJOR, BROWN_NINE, YELLOW_ACE, YELLOW_NINE))),
        new BetFinished(Optional.of(TEAM_YOUNG), Optional.of(72), 3)
      );
    }

    @Test
    void shouldScoreHighWinRedsGameAsCleanWin() {
      var game = buildDefaultGame(new PresetShuffler(REDS_CLEAN_WIN_DECK));
      setupGameForTeamOld(game, HighReds);

      assertThat(
        playRedsGameAsCleanWinForTeamOld(game)
      ).containsExactly(
        new CardPlayed(FLO, BROWN_NINE),
        new WonSting(THOMAS, new Sting(TEAM_OLD, List.of(GREEN_MINOR, RED_NINE, BROWN_TEN, BROWN_NINE))),
        new BetFinished(Optional.of(TEAM_OLD), Optional.of(120), 5)
      );
    }

    @Test
    void shouldScoreHighWinRedsGameAsHighWin() {
      var game = buildDefaultGame(new PresetShuffler(REDS_HIGH_WIN_DECK));
      setupGameForTeamOld(game, HighReds);

      assertThat(
        playRedsGameAsHighWinForTeamOld(game)
      ).containsExactly(
        new CardPlayed(THOMAS, BROWN_MINOR),
        new WonSting(LEA, new Sting(TEAM_YOUNG, List.of(GREEN_MINOR, YELLOW_KING, GREEN_NINE, BROWN_MINOR))),
        new BetFinished(Optional.of(TEAM_OLD), Optional.of(97), 4)
      );
    }

    @Test
    void shouldScoreHighWinRedsGameAsNormalWin() {
      var game = buildDefaultGame(new PresetShuffler(REDS_HIGH_WIN_DECK));
      setupGameForTeamOld(game, HighReds);

      assertThat(
        playRedsGameAsNormalWinForTeamOld(game)
      ).containsExactly(
        new CardPlayed(THOMAS, BROWN_MINOR),
        new WonSting(LEA, new Sting(TEAM_YOUNG, List.of(GREEN_MINOR, YELLOW_KING, GREEN_TEN, BROWN_MINOR))),
        new BetFinished(Optional.of(TEAM_YOUNG), Optional.of(33), 4)
      );
    }

    @Test
    void shouldScoreHighWinRedsGameAsCleanLoss() {
      var game = buildDefaultGame(new PresetShuffler(REDS_CLEAN_WIN_DECK));
      setupGameForTeamOld(game, HighReds);

      assertThat(
        playRedsGameAsCleanWinForTeamOld(game)
      ).containsExactly(
        new CardPlayed(FLO, BROWN_NINE),
        new WonSting(THOMAS, new Sting(TEAM_OLD, List.of(GREEN_MINOR, RED_NINE, BROWN_TEN, BROWN_NINE))),
        new BetFinished(Optional.of(TEAM_OLD), Optional.of(120), 5)
      );
    }

    @Test
    void shouldScoreHighWinRedsGameAsHighLoss() {
      var game = buildDefaultGame(new PresetShuffler(REDS_HIGH_WIN_DECK));
      setupGameForTeamYoung(game, HighReds);

      assertThat(
        playRedsGameAsHighWinForTeamOld(game)
      ).containsExactly(
        new CardPlayed(THOMAS, BROWN_MINOR),
        new WonSting(LEA, new Sting(TEAM_YOUNG, List.of(GREEN_MINOR, YELLOW_KING, GREEN_NINE, BROWN_MINOR))),
        new BetFinished(Optional.of(TEAM_OLD), Optional.of(97), 4)
      );
    }

    @Test
    void shouldScoreHighWinRedsGameAsNormalLoss() {
      var game = buildDefaultGame(new PresetShuffler(REDS_HIGH_WIN_DECK));
      setupGameForTeamYoung(game, HighReds);

      assertThat(
        playRedsGameAsNormalWinForTeamOld(game)
      ).containsExactly(
        new CardPlayed(THOMAS, BROWN_MINOR),
        new WonSting(LEA, new Sting(TEAM_YOUNG, List.of(GREEN_MINOR, YELLOW_KING, GREEN_TEN, BROWN_MINOR))),
        new BetFinished(Optional.of(TEAM_OLD), Optional.of(87), 4)
      );
    }

    @Test
    void shouldScoreHighWinMinorsGameAsCleanWin() {
      var game = buildDefaultGame(new PresetShuffler(MINORS_OR_MAJORS_CLEAN_WIN_DECK));
      setupGameForTeamOld(game, HighMinors);

      assertThat(
        playMinorsGameAsCleanWinForTeamOld(game)
      ).containsExactly(
        new CardPlayed(FLO, BROWN_TEN),
        new WonSting(THOMAS, new Sting(TEAM_OLD, List.of(YELLOW_TEN, GREEN_TEN, GREEN_NINE, BROWN_TEN))),
        new BetFinished(Optional.of(TEAM_OLD), Optional.of(120), 6)
      );
    }

    @Test
    void shouldScoreHighWinMinorsGameAsHighWin() {
      var game = buildDefaultGame(new PresetShuffler(MINORS_OR_MAJORS_HIGH_WIN_DECK));
      setupGameForTeamOld(game, HighMinors);

      assertThat(
        playMinorsGameAsHighWinForTeamOld(game)
      ).containsExactly(
        new CardPlayed(LEA, YELLOW_KING),
        new WonSting(FLO, new Sting(TEAM_YOUNG, List.of(BROWN_NINE, BROWN_TEN, YELLOW_NINE, YELLOW_KING))),
        new BetFinished(Optional.of(TEAM_OLD), Optional.of(106), 5)
      );
    }

    @Test
    void shouldScoreHighWinMinorsGameAsNormalWin() {
      var game = buildDefaultGame(new PresetShuffler(MINORS_OR_MAJORS_HIGH_WIN_DECK));
      setupGameForTeamOld(game, HighMinors);

      assertThat(
        playMinorsGameAsNormalWinForTeamOld(game)
      ).containsExactly(
        new CardPlayed(MARCEL, BROWN_NINE),
        new WonSting(FLO, new Sting(TEAM_YOUNG, List.of(BROWN_TEN, GREEN_NINE, GREEN_KING, BROWN_NINE))),
        new BetFinished(Optional.of(TEAM_YOUNG), Optional.of(40), 5)
      );
    }

    @Test
    void shouldScoreHighWinMinorsGameAsCleanLoss() {
      var game = buildDefaultGame(new PresetShuffler(MINORS_OR_MAJORS_CLEAN_WIN_DECK));
      setupGameForTeamYoung(game, HighMinors);

      assertThat(
        playMinorsGameAsCleanWinForTeamOld(game)
      ).containsExactly(
        new CardPlayed(FLO, BROWN_TEN),
        new WonSting(THOMAS, new Sting(TEAM_OLD, List.of(YELLOW_TEN, GREEN_TEN, GREEN_NINE, BROWN_TEN))),
        new BetFinished(Optional.of(TEAM_OLD), Optional.of(120), 6)
      );
    }

    @Test
    void shouldScoreHighWinMinorsGameAsHighLoss() {
      var game = buildDefaultGame(new PresetShuffler(MINORS_OR_MAJORS_HIGH_WIN_DECK));
      setupGameForTeamYoung(game, HighMinors);

      assertThat(
        playMinorsGameAsHighWinForTeamOld(game)
      ).containsExactly(
        new CardPlayed(LEA, YELLOW_KING),
        new WonSting(FLO, new Sting(TEAM_YOUNG, List.of(BROWN_NINE, BROWN_TEN, YELLOW_NINE, YELLOW_KING))),
        new BetFinished(Optional.of(TEAM_OLD), Optional.of(106), 5)
      );
    }

    @Test
    void shouldScoreHighWinMinorsGameAsNormalLoss() {
      var game = buildDefaultGame(new PresetShuffler(MINORS_OR_MAJORS_HIGH_WIN_DECK));
      setupGameForTeamYoung(game, HighMinors);

      assertThat(
        playMinorsGameAsNormalWinForTeamOld(game)
      ).containsExactly(
        new CardPlayed(MARCEL, BROWN_NINE),
        new WonSting(FLO, new Sting(TEAM_YOUNG, List.of(BROWN_TEN, GREEN_NINE, GREEN_KING, BROWN_NINE))),
        new BetFinished(Optional.of(TEAM_OLD), Optional.of(80), 5)
      );
    }

    @Test
    void shouldScoreHighWinMajorsGameAsCleanWin() {
      var game = buildDefaultGame(new PresetShuffler(MINORS_OR_MAJORS_CLEAN_WIN_DECK));
      setupGameForTeamYoung(game, HighMajors);

      assertThat(
        playMajorsGameAsCleanWinForTeamYoung(game)
      ).containsExactly(
        new CardPlayed(THOMAS, YELLOW_TEN),
        new WonSting(LEA, new Sting(TEAM_YOUNG, List.of(GREEN_TEN, BROWN_NINE, BROWN_TEN, YELLOW_TEN))),
        new BetFinished(Optional.of(TEAM_YOUNG), Optional.of(120), 6)
      );
    }

    @Test
    void shouldScoreHighWinMajorsGameAsHighWin() {
      var game = buildDefaultGame(new PresetShuffler(MINORS_OR_MAJORS_HIGH_WIN_DECK));
      setupGameForTeamYoung(game, HighMajors);

      assertThat(
        playMajorsGameAsHighWinForTeamYoung(game)
      ).containsExactly(
        new CardPlayed(THOMAS, YELLOW_MINOR),
        new WonSting(FLO, new Sting(TEAM_YOUNG, List.of(YELLOW_KING, RED_ACE, YELLOW_ACE, YELLOW_MINOR))),
        new BetFinished(Optional.of(TEAM_YOUNG), Optional.of(105), 5)
      );
    }

    @Test
    void shouldScoreHighWinMajorsGameAsNormalWin() {
      var game = buildDefaultGame(new PresetShuffler(MINORS_OR_MAJORS_HIGH_WIN_DECK));
      setupGameForTeamYoung(game, HighMajors);

      assertThat(
        playMajorsGameAsNormalWinForTeamYoung(game)
      ).containsExactly(
        new CardPlayed(THOMAS, YELLOW_NINE),
        new WonSting(LEA, new Sting(TEAM_YOUNG, List.of(GREEN_MAJOR, BROWN_NINE, YELLOW_ACE, YELLOW_NINE))),
        new BetFinished(Optional.of(TEAM_OLD), Optional.of(48), 5)
      );
    }

    @Test
    void shouldScoreHighWinMajorsGameAsCleanLoss() {
      var game = buildDefaultGame(new PresetShuffler(MINORS_OR_MAJORS_CLEAN_WIN_DECK));
      setupGameForTeamOld(game, HighMajors);

      assertThat(
        playMajorsGameAsCleanWinForTeamYoung(game)
      ).containsExactly(
        new CardPlayed(THOMAS, YELLOW_TEN),
        new WonSting(LEA, new Sting(TEAM_YOUNG, List.of(GREEN_TEN, BROWN_NINE, BROWN_TEN, YELLOW_TEN))),
        new BetFinished(Optional.of(TEAM_YOUNG), Optional.of(120), 6)
      );
    }

    @Test
    void shouldScoreHighWinMajorsGameAsHighLoss() {
      var game = buildDefaultGame(new PresetShuffler(MINORS_OR_MAJORS_HIGH_WIN_DECK));
      setupGameForTeamOld(game, HighMajors);

      assertThat(
        playMajorsGameAsHighWinForTeamYoung(game)
      ).containsExactly(
        new CardPlayed(THOMAS, YELLOW_MINOR),
        new WonSting(FLO, new Sting(TEAM_YOUNG, List.of(YELLOW_KING, RED_ACE, YELLOW_ACE, YELLOW_MINOR))),
        new BetFinished(Optional.of(TEAM_YOUNG), Optional.of(105), 5)
      );
    }

    @Test
    void shouldScoreHighWinMajorsGameAsNormalLoss() {
      var game = buildDefaultGame(new PresetShuffler(MINORS_OR_MAJORS_HIGH_WIN_DECK));
      setupGameForTeamOld(game, HighMajors);

      assertThat(
        playMajorsGameAsNormalWinForTeamYoung(game)
      ).containsExactly(
        new CardPlayed(THOMAS, YELLOW_NINE),
        new WonSting(LEA, new Sting(TEAM_YOUNG, List.of(GREEN_MAJOR, BROWN_NINE, YELLOW_ACE, YELLOW_NINE))),
        new BetFinished(Optional.of(TEAM_YOUNG), Optional.of(72), 5)
      );
    }

    @Test
    void shouldScoreCleanWinRedsGameAsCleanWin() {
      var game = buildDefaultGame(new PresetShuffler(REDS_CLEAN_WIN_DECK));
      setupGameForTeamOld(game, CleanReds);

      assertThat(
        playRedsGameAsCleanWinForTeamOld(game)
      ).containsExactly(
        new CardPlayed(FLO, BROWN_NINE),
        new WonSting(THOMAS, new Sting(TEAM_OLD, List.of(GREEN_MINOR, RED_NINE, BROWN_TEN, BROWN_NINE))),
        new BetFinished(Optional.of(TEAM_OLD), Optional.of(120), 6)
      );
    }

    @Test
    void shouldScoreCleanWinRedsGameAsHighWin() {
      var game = buildDefaultGame(new PresetShuffler(REDS_HIGH_WIN_DECK));
      setupGameForTeamOld(game, CleanReds);

      assertThat(
        playRedsGameAsHighWinForTeamOld(game)
      ).containsExactly(
        new CardPlayed(THOMAS, BROWN_MINOR),
        new WonSting(LEA, new Sting(TEAM_YOUNG, List.of(GREEN_MINOR, YELLOW_KING, GREEN_NINE, BROWN_MINOR))),
        new BetFinished(Optional.of(TEAM_YOUNG), Optional.of(23), 6)
      );
    }

    @Test
    void shouldScoreCleanWinRedsGameAsNormalWin() {
      var game = buildDefaultGame(new PresetShuffler(REDS_HIGH_WIN_DECK));
      setupGameForTeamOld(game, CleanReds);

      assertThat(
        playRedsGameAsNormalWinForTeamOld(game)
      ).containsExactly(
        new CardPlayed(THOMAS, BROWN_MINOR),
        new WonSting(LEA, new Sting(TEAM_YOUNG, List.of(GREEN_MINOR, YELLOW_KING, GREEN_TEN, BROWN_MINOR))),
        new BetFinished(Optional.of(TEAM_YOUNG), Optional.of(33), 6)
      );
    }

    @Test
    void shouldScoreCleanWinRedsGameAsCleanLoss() {
      var game = buildDefaultGame(new PresetShuffler(REDS_CLEAN_WIN_DECK));
      setupGameForTeamYoung(game, CleanReds);

      assertThat(
        playRedsGameAsCleanWinForTeamOld(game)
      ).containsExactly(
        new CardPlayed(FLO, BROWN_NINE),
        new WonSting(THOMAS, new Sting(TEAM_OLD, List.of(GREEN_MINOR, RED_NINE, BROWN_TEN, BROWN_NINE))),
        new BetFinished(Optional.of(TEAM_OLD), Optional.of(120), 6)
      );
    }

    @Test
    void shouldScoreCleanWinRedsGameAsHighLoss() {
      var game = buildDefaultGame(new PresetShuffler(REDS_HIGH_WIN_DECK));
      setupGameForTeamYoung(game, CleanReds);

      assertThat(
        playRedsGameAsHighWinForTeamOld(game)
      ).containsExactly(
        new CardPlayed(THOMAS, BROWN_MINOR),
        new WonSting(LEA, new Sting(TEAM_YOUNG, List.of(GREEN_MINOR, YELLOW_KING, GREEN_NINE, BROWN_MINOR))),
        new BetFinished(Optional.of(TEAM_OLD), Optional.of(97), 6)
      );
    }

    @Test
    void shouldScoreCleanWinRedsGameAsNormalLoss() {
      var game = buildDefaultGame(new PresetShuffler(REDS_HIGH_WIN_DECK));
      setupGameForTeamYoung(game, CleanReds);

      assertThat(
        playRedsGameAsNormalWinForTeamOld(game)
      ).containsExactly(
        new CardPlayed(THOMAS, BROWN_MINOR),
        new WonSting(LEA, new Sting(TEAM_YOUNG, List.of(GREEN_MINOR, YELLOW_KING, GREEN_TEN, BROWN_MINOR))),
        new BetFinished(Optional.of(TEAM_OLD), Optional.of(87), 6)
      );
    }

    @Test
    void shouldScoreCleanWinMinorsGameAsCleanWin() {
      var game = buildDefaultGame(new PresetShuffler(MINORS_OR_MAJORS_CLEAN_WIN_DECK));
      setupGameForTeamOld(game, CleanMinors);

      assertThat(
        playMinorsGameAsCleanWinForTeamOld(game)
      ).containsExactly(
        new CardPlayed(FLO, BROWN_TEN),
        new WonSting(THOMAS, new Sting(TEAM_OLD, List.of(YELLOW_TEN, GREEN_TEN, GREEN_NINE, BROWN_TEN))),
        new BetFinished(Optional.of(TEAM_OLD), Optional.of(120), 7)
      );
    }

    @Test
    void shouldScoreCleanWinMinorsGameAsHighWin() {
      var game = buildDefaultGame(new PresetShuffler(MINORS_OR_MAJORS_HIGH_WIN_DECK));
      setupGameForTeamOld(game, CleanMinors);

      assertThat(
        playMinorsGameAsHighWinForTeamOld(game)
      ).containsExactly(
        new CardPlayed(LEA, YELLOW_KING),
        new WonSting(FLO, new Sting(TEAM_YOUNG, List.of(BROWN_NINE, BROWN_TEN, YELLOW_NINE, YELLOW_KING))),
        new BetFinished(Optional.of(TEAM_YOUNG), Optional.of(14), 7)
      );
    }

    @Test
    void shouldScoreCleanWinMinorsGameAsNormalWin() {
      var game = buildDefaultGame(new PresetShuffler(MINORS_OR_MAJORS_HIGH_WIN_DECK));
      setupGameForTeamOld(game, CleanMinors);

      assertThat(
        playMinorsGameAsNormalWinForTeamOld(game)
      ).containsExactly(
        new CardPlayed(MARCEL, BROWN_NINE),
        new WonSting(FLO, new Sting(TEAM_YOUNG, List.of(BROWN_TEN, GREEN_NINE, GREEN_KING, BROWN_NINE))),
        new BetFinished(Optional.of(TEAM_YOUNG), Optional.of(40), 7)
      );
    }

    @Test
    void shouldScoreCleanWinMinorsGameAsCleanLoss() {
      var game = buildDefaultGame(new PresetShuffler(MINORS_OR_MAJORS_CLEAN_WIN_DECK));
      setupGameForTeamYoung(game, CleanMinors);

      assertThat(
        playMinorsGameAsCleanWinForTeamOld(game)
      ).containsExactly(
        new CardPlayed(FLO, BROWN_TEN),
        new WonSting(THOMAS, new Sting(TEAM_OLD, List.of(YELLOW_TEN, GREEN_TEN, GREEN_NINE, BROWN_TEN))),
        new BetFinished(Optional.of(TEAM_OLD), Optional.of(120), 7)
      );
    }

    @Test
    void shouldScoreCleanWinMinorsGameAsHighLoss() {
      var game = buildDefaultGame(new PresetShuffler(MINORS_OR_MAJORS_HIGH_WIN_DECK));
      setupGameForTeamYoung(game, CleanMinors);

      assertThat(
        playMinorsGameAsHighWinForTeamOld(game)
      ).containsExactly(
        new CardPlayed(LEA, YELLOW_KING),
        new WonSting(FLO, new Sting(TEAM_YOUNG, List.of(BROWN_NINE, BROWN_TEN, YELLOW_NINE, YELLOW_KING))),
        new BetFinished(Optional.of(TEAM_OLD), Optional.of(106), 7)
      );
    }

    @Test
    void shouldScoreCleanWinMinorsGameAsNormalLoss() {
      var game = buildDefaultGame(new PresetShuffler(MINORS_OR_MAJORS_HIGH_WIN_DECK));
      setupGameForTeamYoung(game, CleanMinors);

      assertThat(
        playMinorsGameAsNormalWinForTeamOld(game)
      ).containsExactly(
        new CardPlayed(MARCEL, BROWN_NINE),
        new WonSting(FLO, new Sting(TEAM_YOUNG, List.of(BROWN_TEN, GREEN_NINE, GREEN_KING, BROWN_NINE))),
        new BetFinished(Optional.of(TEAM_OLD), Optional.of(80), 7)
      );
    }

    @Test
    void shouldScoreCleanWinMajorsGameAsCleanWin() {
      var game = buildDefaultGame(new PresetShuffler(MINORS_OR_MAJORS_CLEAN_WIN_DECK));
      setupGameForTeamYoung(game, CleanMajors);

      assertThat(
        playMajorsGameAsCleanWinForTeamYoung(game)
      ).containsExactly(
        new CardPlayed(THOMAS, YELLOW_TEN),
        new WonSting(LEA, new Sting(TEAM_YOUNG, List.of(GREEN_TEN, BROWN_NINE, BROWN_TEN, YELLOW_TEN))),
        new BetFinished(Optional.of(TEAM_YOUNG), Optional.of(120), 7)
      );
    }

    @Test
    void shouldScoreCleanWinMajorsGameAsHighWin() {
      var game = buildDefaultGame(new PresetShuffler(MINORS_OR_MAJORS_HIGH_WIN_DECK));
      setupGameForTeamYoung(game, CleanMajors);

      assertThat(
        playMajorsGameAsHighWinForTeamYoung(game)
      ).containsExactly(
        new CardPlayed(THOMAS, YELLOW_MINOR),
        new WonSting(FLO, new Sting(TEAM_YOUNG, List.of(YELLOW_KING, RED_ACE, YELLOW_ACE, YELLOW_MINOR))),
        new BetFinished(Optional.of(TEAM_OLD), Optional.of(15), 7)
      );
    }

    @Test
    void shouldScoreCleanWinMajorsGameAsNormalWin() {
      var game = buildDefaultGame(new PresetShuffler(MINORS_OR_MAJORS_HIGH_WIN_DECK));
      setupGameForTeamYoung(game, CleanMajors);

      assertThat(
        playMajorsGameAsNormalWinForTeamYoung(game)
      ).containsExactly(
        new CardPlayed(THOMAS, YELLOW_NINE),
        new WonSting(LEA, new Sting(TEAM_YOUNG, List.of(GREEN_MAJOR, BROWN_NINE, YELLOW_ACE, YELLOW_NINE))),
        new BetFinished(Optional.of(TEAM_OLD), Optional.of(48), 7)
      );
    }

    @Test
    void shouldScoreCleanWinMajorsGameAsCleanLoss() {
      var game = buildDefaultGame(new PresetShuffler(MINORS_OR_MAJORS_CLEAN_WIN_DECK));
      setupGameForTeamOld(game, CleanMajors);

      assertThat(
        playMajorsGameAsCleanWinForTeamYoung(game)
      ).containsExactly(
        new CardPlayed(THOMAS, YELLOW_TEN),
        new WonSting(LEA, new Sting(TEAM_YOUNG, List.of(GREEN_TEN, BROWN_NINE, BROWN_TEN, YELLOW_TEN))),
        new BetFinished(Optional.of(TEAM_YOUNG), Optional.of(120), 7)
      );
    }

    @Test
    void shouldScoreCleanWinMajorsGameAsHighLoss() {
      var game = buildDefaultGame(new PresetShuffler(MINORS_OR_MAJORS_HIGH_WIN_DECK));
      setupGameForTeamOld(game, CleanMajors);

      assertThat(
        playMajorsGameAsHighWinForTeamYoung(game)
      ).containsExactly(
        new CardPlayed(THOMAS, YELLOW_MINOR),
        new WonSting(FLO, new Sting(TEAM_YOUNG, List.of(YELLOW_KING, RED_ACE, YELLOW_ACE, YELLOW_MINOR))),
        new BetFinished(Optional.of(TEAM_YOUNG), Optional.of(105), 7)
      );
    }

    @Test
    void shouldScoreCleanWinMajorsGameAsNormalLoss() {
      var game = buildDefaultGame(new PresetShuffler(MINORS_OR_MAJORS_HIGH_WIN_DECK));
      setupGameForTeamOld(game, CleanMajors);

      assertThat(
        playMajorsGameAsNormalWinForTeamYoung(game)
      ).containsExactly(
        new CardPlayed(THOMAS, YELLOW_NINE),
        new WonSting(LEA, new Sting(TEAM_YOUNG, List.of(GREEN_MAJOR, BROWN_NINE, YELLOW_ACE, YELLOW_NINE))),
        new BetFinished(Optional.of(TEAM_YOUNG), Optional.of(72), 7)
      );
    }

    @Test
    void shouldScoreTrashBetNormalWin() {
      var game = buildDefaultGame(new PresetShuffler(REDS_HIGH_WIN_DECK));
      setupTrashGame(game);

      assertThat(
        playRedsGameAsNormalWinForTeamOld(game)
      ).containsExactly(
        new CardPlayed(THOMAS, BROWN_MINOR),
        new WonSting(LEA, new Sting(TEAM_YOUNG, List.of(GREEN_MINOR, YELLOW_KING, GREEN_TEN, BROWN_MINOR))),
        new BetFinished(Optional.of(TEAM_YOUNG), Optional.of(33), 2)
      );
    }

    @Test
    void shouldScoreTrashBetHighWin() {
      var game = buildDefaultGame(new PresetShuffler(REDS_HIGH_WIN_DECK));
      setupTrashGame(game);

      assertThat(
        playRedsGameAsHighWinForTeamOld(game)
      ).containsExactly(
        new CardPlayed(THOMAS, BROWN_MINOR),
        new WonSting(LEA, new Sting(TEAM_YOUNG, List.of(GREEN_MINOR, YELLOW_KING, GREEN_NINE, BROWN_MINOR))),
        new BetFinished(Optional.of(TEAM_YOUNG), Optional.of(23), 2)
      );
    }

    @Test
    void shouldScoreTrashBetCleanWin() {
      var game = buildDefaultGame(new PresetShuffler(REDS_CLEAN_WIN_DECK));
      setupTrashGame(game);

      assertThat(
        playRedsGameAsCleanWinForTeamOld(game)
      ).containsExactly(
        new CardPlayed(FLO, BROWN_NINE),
        new WonSting(THOMAS, new Sting(TEAM_OLD, List.of(GREEN_MINOR, RED_NINE, BROWN_TEN, BROWN_NINE))),
        new BetFinished(Optional.of(TEAM_YOUNG), Optional.of(0), 2)
      );
    }

    @Test
    void shouldScoreTrashBetDraw() {
      var game = buildDefaultGame(new PresetShuffler(TRASH_BET_DRAW_DECK));
      setupTrashGame(game);

      game.execute(new PlayCard(THOMAS, THE_OLD_ONE));
      game.execute(new PlayCard(LEA, GREEN_MAJOR));
      game.execute(new PlayCard(MARCEL, GREEN_MINOR));
      game.execute(new PlayCard(FLO, YELLOW_MINOR));

      game.execute(new PlayCard(THOMAS, BROWN_MINOR));
      game.execute(new PlayCard(LEA, RED_MAJOR));
      game.execute(new PlayCard(MARCEL, BROWN_MAJOR));
      game.execute(new PlayCard(FLO, RED_MINOR));

      game.execute(new PlayCard(LEA, BROWN_ACE));
      game.execute(new PlayCard(MARCEL, BROWN_TEN));
      game.execute(new PlayCard(FLO, BROWN_NINE));
      game.execute(new PlayCard(THOMAS, BROWN_KING));

      game.execute(new PlayCard(LEA, GREEN_ACE));
      game.execute(new PlayCard(MARCEL, GREEN_TEN));
      game.execute(new PlayCard(FLO, GREEN_KING));
      game.execute(new PlayCard(THOMAS, GREEN_NINE));

      game.execute(new PlayCard(LEA, RED_TEN));
      game.execute(new PlayCard(MARCEL, RED_KING));
      game.execute(new PlayCard(FLO, RED_NINE));
      game.execute(new PlayCard(THOMAS, RED_ACE));

      game.execute(new PlayCard(THOMAS, YELLOW_ACE));
      game.execute(new PlayCard(LEA, YELLOW_TEN));
      game.execute(new PlayCard(MARCEL, YELLOW_KING));

      assertThat(
        game.execute(new PlayCard(FLO, YELLOW_NINE))
      ).containsExactly(
        new CardPlayed(FLO, YELLOW_NINE),
        new WonSting(THOMAS, new Sting(TEAM_OLD, List.of(YELLOW_ACE, YELLOW_TEN, YELLOW_KING, YELLOW_NINE))),
        new BetFinished(Optional.empty(), Optional.empty(), 0)
      );
    }

    void shouldScoreCompleteGame(Deck deck,
                                 int iterations,
                                 Bet bet, BiFunction<Game, Player[], List<Event>> playGame,
                                 Event... expectedResults) {
      var game = buildDefaultGame(new PresetShuffler(deck));

      var currentRoundOrder = new Player[]{THOMAS, LEA, MARCEL, FLO};
      for (int i = 0; i < iterations; ++i) {
        setupGame(game, bet, currentRoundOrder);
        playGame.apply(game, currentRoundOrder);
        Collections.rotate(Arrays.asList(currentRoundOrder), -1);
      }


      setupGame(game, bet, currentRoundOrder);
      assertThat(playGame.apply(game, currentRoundOrder)).containsExactly(expectedResults);

      assertThat(
        game.execute(new DealHands())
      ).containsExactly(
        new InvalidCommandReceived("Game is already finished. No more commands are accepted")
      );
    }

    @Test
    void shouldCompleteGameWithCleanMinorWins() {
      shouldScoreCompleteGame(
        MINORS_OR_MAJORS_CLEAN_WIN_DECK,
        8,
        CleanMinors,
        this::playMinorsGameAsCleanWin,
        new CardPlayed(FLO, BROWN_TEN),
        new WonSting(THOMAS, new Sting(TEAM_OLD, List.of(YELLOW_TEN, GREEN_TEN, GREEN_NINE, BROWN_TEN))),
        new BetFinished(Optional.of(TEAM_OLD), Optional.of(120), 7),
        new GameFinished(TEAM_OLD, 35, TEAM_YOUNG, 28)
      );
    }

    @Test
    void shouldCompleteGameWithCleanMajorWins() {
      shouldScoreCompleteGame(
        MINORS_OR_MAJORS_CLEAN_WIN_DECK,
        8,
        CleanMajors,
        this::playMajorsGameAsCleanWin,
        new CardPlayed(THOMAS, YELLOW_TEN),
        new WonSting(LEA, new Sting(TEAM_YOUNG, List.of(GREEN_TEN, BROWN_NINE, BROWN_TEN, YELLOW_TEN))),
        new BetFinished(Optional.of(TEAM_YOUNG), Optional.of(120), 7),
        new GameFinished(TEAM_YOUNG, 35, TEAM_OLD, 28)
      );
    }

    @Test
    void shouldCompleteGameWithCleanRedWins() {
      shouldScoreCompleteGame(
        REDS_CLEAN_WIN_DECK,
        10,
        CleanReds,
        this::playRedsGameAsCleanWin,
        new CardPlayed(LEA, BROWN_NINE),
        new WonSting(MARCEL, new Sting(TEAM_OLD, List.of(GREEN_MINOR, RED_NINE, BROWN_TEN, BROWN_NINE))),
        new BetFinished(Optional.of(TEAM_OLD), Optional.of(120), 6),
        new GameFinished(TEAM_OLD, 36, TEAM_YOUNG, 30)
      );
    }

    @Test
    void shouldCompleteGameWithHighMinorWins() {
      shouldScoreCompleteGame(
        MINORS_OR_MAJORS_HIGH_WIN_DECK,
        12,
        HighMinors,
        this::playMinorsGameAsHighWin,
        new CardPlayed(LEA, YELLOW_KING),
        new WonSting(FLO, new Sting(TEAM_YOUNG, List.of(BROWN_NINE, BROWN_TEN, YELLOW_NINE, YELLOW_KING))),
        new BetFinished(Optional.of(TEAM_OLD), Optional.of(106), 5),
        new GameFinished(TEAM_OLD, 35, TEAM_YOUNG, 30)
      );
    }

    @Test
    void shouldCompleteGameWithHighMajorWins() {
      shouldScoreCompleteGame(
        MINORS_OR_MAJORS_HIGH_WIN_DECK,
        12,
        HighMajors,
        this::playMajorsGameAsHighWin,
        new CardPlayed(THOMAS, YELLOW_MINOR),
        new WonSting(FLO, new Sting(TEAM_YOUNG, List.of(YELLOW_KING, RED_ACE, YELLOW_ACE, YELLOW_MINOR))),
        new BetFinished(Optional.of(TEAM_YOUNG), Optional.of(105), 5),
        new GameFinished(TEAM_YOUNG, 35, TEAM_OLD, 30)
      );
    }

    @Test
    void shouldCompleteGameWithHighRedWins() {
      shouldScoreCompleteGame(
        REDS_HIGH_WIN_DECK,
        14,
        HighReds,
        this::playRedsGameAsHighWin,
        new CardPlayed(MARCEL, BROWN_MINOR),
        new WonSting(FLO, new Sting(TEAM_YOUNG, List.of(GREEN_MINOR, YELLOW_KING, GREEN_NINE, BROWN_MINOR))),
        new BetFinished(Optional.of(TEAM_OLD), Optional.of(97), 4),
        new GameFinished(TEAM_OLD, 32, TEAM_YOUNG, 28)
      );
    }

    @Test
    void shouldCompleteGameWithNormalMinorWins() {
      shouldScoreCompleteGame(
        MINORS_OR_MAJORS_HIGH_WIN_DECK,
        20,
        NormalMinors,
        this::playMinorsGameAsNormalWin,
        new CardPlayed(MARCEL, BROWN_NINE),
        new WonSting(FLO, new Sting(TEAM_YOUNG, List.of(BROWN_TEN, GREEN_NINE, GREEN_KING, BROWN_NINE))),
        new BetFinished(Optional.of(TEAM_OLD), Optional.of(80), 3),
        new GameFinished(TEAM_OLD, 33, TEAM_YOUNG, 30)
      );
    }

    @Test
    void shouldCompleteGameWithNormalMajorWins() {
      shouldScoreCompleteGame(
        MINORS_OR_MAJORS_HIGH_WIN_DECK,
        20,
        NormalMajors,
        this::playMajorsGameAsNormalWin,
        new CardPlayed(THOMAS, YELLOW_NINE),
        new WonSting(LEA, new Sting(TEAM_YOUNG, List.of(GREEN_MAJOR, BROWN_NINE, YELLOW_ACE, YELLOW_NINE))),
        new BetFinished(Optional.of(TEAM_YOUNG), Optional.of(72), 3),
        new GameFinished(TEAM_YOUNG, 33, TEAM_OLD, 30)
      );
    }

    @Test
    void shouldCompleteGameWithNormalRedWins() {
      shouldScoreCompleteGame(
        REDS_HIGH_WIN_DECK,
        30,
        NormalReds,
        this::playRedsGameAsNormalWin,
        new CardPlayed(MARCEL, BROWN_MINOR),
        new WonSting(FLO, new Sting(TEAM_YOUNG, List.of(GREEN_MINOR, YELLOW_KING, GREEN_TEN, BROWN_MINOR))),
        new BetFinished(Optional.of(TEAM_OLD), Optional.of(87), 2),
        new GameFinished(TEAM_OLD, 32, TEAM_YOUNG, 30)
      );
    }

    @Test
    void shouldCompleteGameWithTrashWins() {
      shouldScoreCompleteGame(
        REDS_HIGH_WIN_DECK,
        30,
        None,
        this::playRedsGameAsNormalWin,
        new CardPlayed(MARCEL, BROWN_MINOR),
        new WonSting(FLO, new Sting(TEAM_YOUNG, List.of(GREEN_MINOR, YELLOW_KING, GREEN_TEN, BROWN_MINOR))),
        new BetFinished(Optional.of(TEAM_YOUNG), Optional.of(33), 2),
        new GameFinished(TEAM_YOUNG, 32, TEAM_OLD, 30)
      );
    }
  }
}