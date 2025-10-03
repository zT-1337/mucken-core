package de.tzerr.cli.demo.example;

import de.tzerr.mucken.core.game.Game;
import de.tzerr.mucken.core.game.GameState;
import de.tzerr.mucken.core.game.event.BetAccepted;
import de.tzerr.mucken.core.game.event.BetShouted;

import java.util.ArrayList;
import java.util.List;

import static de.tzerr.cli.demo.example.PrintUtility.clear;
import static de.tzerr.cli.demo.example.PrintUtility.printCommandPrompt;
import static de.tzerr.cli.demo.example.PrintUtility.printCurrentBet;
import static de.tzerr.cli.demo.example.PrintUtility.printCurrentPlayerBettingTurn;
import static de.tzerr.cli.demo.example.PrintUtility.printCurrentTotalScore;
import static de.tzerr.cli.demo.example.PrintUtility.printHand;
import static de.tzerr.cli.demo.example.PrintUtility.printLastBets;
import static de.tzerr.cli.demo.example.PromptUtility.prompt;

public class BettingView implements View {

  private final Game game;
  private final GameState gameState;
  private final List<BetShouted> lastBets = new ArrayList<>();

  public BettingView(Game game, GameState gameState) {
    this.game = game;
    this.gameState = gameState;
  }

  @Override
  public Class<? extends View> render() {
    var allowedCommands = game.getAcceptableCommands();

    clear();
    printCurrentTotalScore(gameState.getTeamScores());
    printCurrentBet(gameState.getCurrentlyPlayedBet(), gameState.getCurrentlyBettingPlayer());
    printLastBets(lastBets);

    gameState.getCurrentPlayerHand().ifPresent(hand -> printHand(gameState.getCurrentPlayer(), hand));

    printCurrentPlayerBettingTurn(allowedCommands);

    var command = prompt(() -> printCommandPrompt(allowedCommands), allowedCommands);
    var events = game.execute(command);

    for (var event : events) {
      if (event instanceof BetShouted betShouted) {
        gameState.apply(betShouted);
        lastBets.add(betShouted);
        continue;
      }

      if (event instanceof BetAccepted betAccepted) {
        gameState.apply(betAccepted);
        lastBets.clear();
        return PlayCardView.class;
      }

      throw new IllegalStateException("Invalid Event received: " + event);
    }

    return this.getClass();
  }
}
