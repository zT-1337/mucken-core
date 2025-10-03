package de.tzerr.mucken.core.game;

import de.tzerr.mucken.core.game.bet.Bet;
import de.tzerr.mucken.core.game.card.Card;
import de.tzerr.mucken.core.game.event.BetAccepted;
import de.tzerr.mucken.core.game.event.BetFinished;
import de.tzerr.mucken.core.game.event.BetShouted;
import de.tzerr.mucken.core.game.event.CardPlayed;
import de.tzerr.mucken.core.game.event.DealtHand;
import de.tzerr.mucken.core.game.event.WonSting;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GameState {

  private static final int PLAYER_COUNT = 4;
  private static final int HAND_SIZE = 6;

  private final Player[] players;
  private final Team[] teams;
  private final int[] teamScores;
  private final Map<Player, Hand> playerHands = new HashMap<>(PLAYER_COUNT);
  private int activePlayerIndex = 0;

  private final List<Card> currentlyPlayedCards = new ArrayList<>(4);
  private final List<Player> currentPlayerOrder = new ArrayList<>(4);
  private final List<Sting> currentStings = new ArrayList<>(6);

  @Getter
  @NonNull
  private Bet currentlyPlayedBet = Bet.None;
  @Getter
  private int consecutiveNoneBets = 0;
  @Getter
  @NonNull
  private Player currentlyBettingPlayer;

  public GameState(List<Player> players) {
    this(players.toArray(new Player[0]));
  }

  public GameState(Player[] players) {
    if (players.length != PLAYER_COUNT) {
      throw new IllegalStateException("Only accepting exactly " + PLAYER_COUNT + " players");
    }

    this.players = players;
    this.teams = new Team[]{new Team(players[0], players[2]), new Team(players[1], players[3])};
    this.teamScores = new int[]{0, 0};
    this.currentlyBettingPlayer = players[0];
  }

  private void nextPlayer() {
    activePlayerIndex = (activePlayerIndex + 1) % PLAYER_COUNT;
  }

  private void resetCurrentPlayer() {
    activePlayerIndex = 0;
  }

  private void resetBet() {
    currentlyPlayedBet = Bet.None;
    currentlyBettingPlayer = players[0];
    consecutiveNoneBets = 0;
  }

  private void resetHands() {
    playerHands.clear();
  }

  private void nextCycle() {
    Collections.rotate(Arrays.asList(players), -1);

    resetHands();
    resetCurrentPlayer();
    resetBet();
  }

  private void setAsCurrentPlayer(Player player) {
    this.activePlayerIndex = indexOf(player);
  }

  private int indexOf(Player player) {
    for (int i = 0; i < players.length; i++) {
      if (player.equals(players[i])) {
        return i;
      }
    }

    throw new IllegalStateException("Could not find player");
  }

  private int indexOf(Team team) {
    for (int i = 0; i < teams.length; i++) {
      if (team.equals(teams[i])) {
        return i;
      }
    }

    throw new IllegalStateException("Could not find team");
  }

  public void apply(DealtHand dealtHand) {
    playerHands.put(dealtHand.player(), dealtHand.hand());
  }

  public void apply(BetShouted betShouted) {
    if (betShouted.bet().isNoneBet()) {
      consecutiveNoneBets++;
    } else {
      consecutiveNoneBets = 0;
      currentlyPlayedBet = betShouted.bet();
      currentlyBettingPlayer = betShouted.player();
    }

    nextPlayer();
  }

  public void apply(BetAccepted ignoredBetAccepted) {
    resetCurrentPlayer();
    consecutiveNoneBets = 0;
  }

  public void apply(CardPlayed cardPlayed) {
    currentlyPlayedCards.add(cardPlayed.card());
    currentPlayerOrder.add(cardPlayed.player());
    getPlayerHand(cardPlayed.player()).orElseThrow().markCardAsPlayed(cardPlayed.card());
    nextPlayer();
  }

  public void apply(WonSting wonSting) {
    currentStings.add(wonSting.sting());
    setAsCurrentPlayer(wonSting.winner());

    currentlyPlayedCards.clear();
    currentPlayerOrder.clear();
  }

  public void apply(BetFinished betFinished) {
    betFinished.winner().ifPresent(
      winningTeam -> teamScores[indexOf(winningTeam)] += betFinished.betPoints()
    );

    nextCycle();
    currentStings.clear();
  }

  public List<Player> getPlayers() {
    return List.of(players);
  }

  public Player getCurrentPlayer() {
    return players[activePlayerIndex];
  }

  public int getPlayerCount() {
    return players.length;
  }

  public Optional<Hand> getCurrentPlayerHand() {
    return getPlayerHand(getCurrentPlayer());
  }

  public Optional<Hand> getPlayerHand(Player player) {
    if (!playerHands.containsKey(player)) {
      return Optional.empty();
    }

    return Optional.of(playerHands.get(player));
  }

  public int getHandSize() {
    return HAND_SIZE;
  }

  public List<Card> getCurrentlyPlayedCards() {
    return List.copyOf(currentlyPlayedCards);
  }

  public List<Player> getCurrentPlayerOrder() {
    return List.copyOf(currentPlayerOrder);
  }

  public List<Sting> getCurrentStings() {
    return List.copyOf(currentStings);
  }

  public Optional<Sting> getLastSting() {
    if (currentStings.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(currentStings.get(currentStings.size() - 1));
  }

  public Optional<Team> getTeamOf(Player player) {
    return Arrays.stream(teams)
      .filter(team -> team.containsPlayer(player))
      .findFirst();
  }

  public Team getBettingTeam() {
    return getTeamOf(currentlyBettingPlayer)
      .orElseThrow(() -> new IllegalStateException("Betting player is not participating"));
  }

  public Team getNonBettingTeam() {
    return Arrays.stream(teams)
      .filter(team -> !team.containsPlayer(currentlyBettingPlayer))
      .findFirst()
      .orElseThrow(() -> new IllegalStateException("Non betting team could not be found"));
  }

  public Team getOtherTeam(Team team) {
    for (Team otherTeam : teams) {
      if (otherTeam != team) {
        return otherTeam;
      }
    }

    throw new IllegalStateException("No other team found");
  }

  public int getTeamScore(Team team) {
    return teamScores[indexOf(team)];
  }

  public Map<Team, Integer> getTeamScores() {
    return Map.of(teams[0], teamScores[0], teams[1], teamScores[1]);
  }

  public Optional<Team> getWinningTeam() {
    for (int i = 0; i < teams.length; i++) {
      if (teamScores[i] > 30) {
        return Optional.of(teams[i]);
      }
    }

    return Optional.empty();
  }
}
