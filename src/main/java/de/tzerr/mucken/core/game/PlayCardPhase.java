package de.tzerr.mucken.core.game;

import de.tzerr.mucken.core.game.bet.Bet;
import de.tzerr.mucken.core.game.card.Card;
import de.tzerr.mucken.core.game.card.Color;
import de.tzerr.mucken.core.game.command.Command;
import de.tzerr.mucken.core.game.command.PlayCard;
import de.tzerr.mucken.core.game.event.BetFinished;
import de.tzerr.mucken.core.game.event.CardPlayed;
import de.tzerr.mucken.core.game.event.Event;
import de.tzerr.mucken.core.game.event.GameFinished;
import de.tzerr.mucken.core.game.event.InvalidCommandReceived;
import de.tzerr.mucken.core.game.event.WonSting;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SuppressWarnings("ClassCanBeRecord")
final class PlayCardPhase implements GamePhase {
  private static final int TOTAL_POINTS_PER_BET = 120;

  private final GameState gameState;

  public PlayCardPhase(GameState gameState) {
    this.gameState = gameState;
  }

  @Override
  public @NonNull List<Command> getAcceptableCommands() {
    return gameState.getPlayerHand(gameState.getCurrentPlayer())
      .orElseThrow(() -> new IllegalStateException("Current player has no hand"))
      .getCards()
      .stream()
      .map(card -> new PlayCard(gameState.getCurrentPlayer(), card))
      .filter(playCard -> checkInvalidCommand(playCard) == null)
      .collect(Collectors.toUnmodifiableList());
  }

  @Override
  public @NonNull PhaseResult execute(@NonNull Command command) {
    if (command instanceof PlayCard playCard) {
      return handlePlayedCard(playCard);
    }

    return new PhaseResult(
      Optional.empty(),
      List.of(
        new InvalidCommandReceived(String.format("Only %s command is currently accepted", PlayCard.class.getSimpleName()))
      )
    );
  }

  private PhaseResult handlePlayedCard(PlayCard playedCard) {
    var result = checkInvalidCommand(playedCard);
    if (result != null) {
      return new PhaseResult(Optional.empty(), List.of(result));
    }

    Class<? extends GamePhase> nextPhase = null;
    var resultedEvents = new ArrayList<Event>();

    var cardPlayed = new CardPlayed(playedCard.player(), playedCard.card());
    resultedEvents.add(cardPlayed);
    gameState.apply(cardPlayed);

    if (isStingOver()) {
      var winner = getStingWinningPlayer();
      var sting = new Sting(
        gameState.getTeamOf(winner).orElseThrow(() -> new IllegalStateException("Player is not participating")),
        List.copyOf(gameState.getCurrentlyPlayedCards())
      );
      var wonSting = new WonSting(winner, sting);

      resultedEvents.add(wonSting);
      gameState.apply(wonSting);
    }

    if (isBetFinished()) {
      nextPhase = DealingPhase.class;
      var betFinished = calcBetWinner();
      resultedEvents.add(betFinished);
      gameState.apply(betFinished);
    }

    if (isGameFinished()) {
      nextPhase = FinishedPhase.class;
      var winningTeam = gameState.getWinningTeam().orElseThrow(() -> new IllegalStateException("Winner should already be not null"));
      var loosingTeam = gameState.getOtherTeam(winningTeam);
      resultedEvents.add(
        new GameFinished(
          winningTeam, gameState.getTeamScore(winningTeam),
          loosingTeam, gameState.getTeamScore(loosingTeam))
      );
    }

    return new PhaseResult(Optional.ofNullable(nextPhase), List.copyOf(resultedEvents));
  }

  private Event checkInvalidCommand(PlayCard playedCard) {
    if (isWrongPlayerTurn(playedCard)) {
      return new InvalidCommandReceived(
        String.format("It's the turn of %s and not %s", gameState.getCurrentPlayer().name(), playedCard.player().name())
      );
    }

    if (isCardNotInHand(playedCard)) {
      return new InvalidCommandReceived(
        String.format("%s does not have this card %s", gameState.getCurrentPlayer().name(), playedCard.card())
      );
    }

    if (isColorDenial(playedCard)) {
      return new InvalidCommandReceived(
        String.format("%s has to admit color %s", gameState.getCurrentPlayer().name(), getCurrentlyPlayedColor().name())
      );
    }

    if (isTrumpDenial(playedCard)) {
      return new InvalidCommandReceived(
        String.format("%s has to admit trump", gameState.getCurrentPlayer().name())
      );
    }

    return null;
  }

  private boolean isWrongPlayerTurn(PlayCard playedCard) {
    return !playedCard.player().equals(gameState.getCurrentPlayer());
  }

  private boolean isCardNotInHand(PlayCard playedCard) {
    var playerHand = gameState.getPlayerHand(playedCard.player());
    return !playerHand
      .orElseThrow(() -> new IllegalStateException("Current player has no hand"))
      .hasCard(playedCard.card());
  }

  private boolean isColorDenial(PlayCard playedCard) {
    if (gameState.getCurrentlyPlayedCards().isEmpty()) {
      return false;
    }

    return !isCurrentlyTrumpPlayed() &&
      playerHasColor(playedCard) &&
      isCardOfDifferentColor(playedCard);
  }

  private boolean isCurrentlyTrumpPlayed() {
    return isTrumpCard(gameState.getCurrentlyPlayedBet(), gameState.getCurrentlyPlayedCards().get(0));
  }

  private boolean playerHasColor(PlayCard playedCard) {
    return gameState
      .getPlayerHand(playedCard.player())
      .orElseThrow(() -> new IllegalStateException("Current player has no hand"))
      .hasColor(getCurrentlyPlayedColor(), gameState.getCurrentlyPlayedBet().getMode());
  }

  private boolean isCardOfDifferentColor(PlayCard playedCard) {
    return isTrumpCard(gameState.getCurrentlyPlayedBet(), playedCard.card()) ||
      playedCard.card().getColor() != getCurrentlyPlayedColor();
  }

  private boolean isTrumpCard(Bet bet, Card card) {
    return bet.getMode().isTrumpCard(card);
  }

  private Color getCurrentlyPlayedColor() {
    return gameState.getCurrentlyPlayedCards().get(0).getColor();
  }

  private boolean isTrumpDenial(PlayCard playedCard) {
    if (gameState.getCurrentlyPlayedCards().isEmpty()) {
      return false;
    }

    return isCurrentlyTrumpPlayed() &&
      playerHasTrump(playedCard) &&
      (!isTrumpCard(gameState.getCurrentlyPlayedBet(), playedCard.card()));
  }

  private boolean playerHasTrump(PlayCard playedCard) {
    return gameState
      .getPlayerHand(playedCard.player())
      .orElseThrow(() -> new IllegalStateException("Current player has no hand"))
      .hasTrump(gameState.getCurrentlyPlayedBet().getMode());
  }

  private boolean isStingOver() {
    return gameState.getCurrentlyPlayedCards().size() == gameState.getPlayerCount();
  }

  private Player getStingWinningPlayer() {
    return gameState.getCurrentPlayerOrder().get(indexOfWinningCard());
  }

  private int indexOfWinningCard() {
    var currentlyPlayedCards = gameState.getCurrentlyPlayedCards();
    if (currentlyPlayedCards.size() != gameState.getPlayerCount()) {
      throw new IllegalStateException("Currently played cards should not be empty here");
    }

    var winningCardIndex = 0;

    for (int i = 1; i < currentlyPlayedCards.size(); i++) {
      var winningCard = currentlyPlayedCards.get(winningCardIndex);
      var currentCard = currentlyPlayedCards.get(i);

      if (isHigherCard(winningCard, currentCard)) {
        winningCardIndex = i;
      }
    }

    return winningCardIndex;
  }

  private boolean isHigherCard(Card prevCard, Card nextCard) {
    return isTrumping(prevCard, nextCard) ||
      isOvertrumping(prevCard, nextCard) ||
      isHigherRankFromPlayedColor(prevCard, nextCard);
  }

  private boolean isTrumping(Card prevCard, Card nextCard) {
    var currentBet = gameState.getCurrentlyPlayedBet();
    return !currentBet.getMode().isTrumpCard(prevCard) && currentBet.getMode().isTrumpCard(nextCard);
  }

  private boolean isOvertrumping(Card prevCard, Card nextCard) {
    var currentBet = gameState.getCurrentlyPlayedBet();
    return currentBet.getMode().isTrumpCard(prevCard) &&
      currentBet.getMode().isTrumpCard(nextCard) &&
      nextCard.isHigherTrump(prevCard);
  }

  private boolean isHigherRankFromPlayedColor(Card prevCard, Card nextCard) {
    var currentBet = gameState.getCurrentlyPlayedBet();
    var currentColor = getCurrentlyPlayedColor();

    return !currentBet.getMode().isTrumpCard(prevCard) &&
      !currentBet.getMode().isTrumpCard(nextCard) &&
      nextCard.getColor().equals(currentColor) &&
      nextCard.isHigherRank(prevCard);
  }

  private boolean isBetFinished() {
    return gameState.getCurrentStings().size() == gameState.getHandSize();
  }

  private BetFinished calcBetWinner() {
    var bettingTeam = gameState.getBettingTeam();
    var bettingTeamScore = gameState.getCurrentStings()
      .stream()
      .filter(sting -> sting.owner().equals(bettingTeam))
      .map(Sting::playedCards)
      .flatMap(List::stream)
      .mapToInt(card -> card.getRank().getPoints())
      .sum();

    var otherTeam = gameState.getNonBettingTeam();
    var otherTeamScore = TOTAL_POINTS_PER_BET - bettingTeamScore;

    return getBetWinningTeam(bettingTeam, bettingTeamScore, otherTeam, otherTeamScore)
      .map(
        winningTeam -> {
          var winningTeamScore = winningTeam == bettingTeam ? bettingTeamScore : otherTeamScore;
          var betPoints = gameState.getCurrentlyPlayedBet().calcBetPoints(winningTeamScore);
          return new BetFinished(Optional.of(winningTeam), Optional.of(winningTeamScore), betPoints);
        }
      ).orElseGet(() -> new BetFinished(Optional.empty(), Optional.empty(), 0));
  }

  private Optional<Team> getBetWinningTeam(Team bettingTeam, int bettingTeamScore, Team otherTeam, int otherTeamScore) {
    if (!gameState.getCurrentlyPlayedBet().isNoneBet()) {
      var minWinScore = gameState.getCurrentlyPlayedBet().getModifier().getMinWinScore();
      return bettingTeamScore >= minWinScore ? Optional.of(bettingTeam) : Optional.of(otherTeam);
    }

    if (bettingTeamScore == otherTeamScore) {
      return Optional.empty();
    }

    return bettingTeamScore < otherTeamScore ? Optional.of(bettingTeam) : Optional.of(otherTeam);
  }

  private boolean isGameFinished() {
    return gameState.getWinningTeam().isPresent();
  }
}
