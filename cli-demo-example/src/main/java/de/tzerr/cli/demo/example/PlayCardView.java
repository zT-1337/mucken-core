package de.tzerr.cli.demo.example;

import de.tzerr.mucken.core.game.Game;
import de.tzerr.mucken.core.game.GameState;
import de.tzerr.mucken.core.game.event.BetFinished;
import de.tzerr.mucken.core.game.event.CardPlayed;
import de.tzerr.mucken.core.game.event.GameFinished;
import de.tzerr.mucken.core.game.event.WonSting;

import static de.tzerr.cli.demo.example.PrintUtility.clear;
import static de.tzerr.cli.demo.example.PrintUtility.printCommandPrompt;
import static de.tzerr.cli.demo.example.PrintUtility.printCurrentBet;
import static de.tzerr.cli.demo.example.PrintUtility.printCurrentPlayerPlayCardTurn;
import static de.tzerr.cli.demo.example.PrintUtility.printCurrentSting;
import static de.tzerr.cli.demo.example.PrintUtility.printCurrentTotalScore;
import static de.tzerr.cli.demo.example.PrintUtility.printFinalResult;
import static de.tzerr.cli.demo.example.PrintUtility.printHand;
import static de.tzerr.cli.demo.example.PromptUtility.prompt;

@SuppressWarnings("ClassCanBeRecord")
public class PlayCardView implements View {

  private final Game game;
  private final GameState gameState;

  public PlayCardView(Game game, GameState gameState) {
    this.game = game;
    this.gameState = gameState;
  }

  @Override
  public Class<? extends View> render() {
    var allowedCommands = game.getAcceptableCommands();

    clear();
    printCurrentTotalScore(gameState.getTeamScores());
    printCurrentBet(gameState.getCurrentlyPlayedBet(), gameState.getCurrentlyBettingPlayer());

    gameState.getLastSting().ifPresent(PrintUtility::printLastSting);

    printCurrentSting(gameState.getCurrentlyPlayedCards(), gameState.getCurrentPlayerOrder());

    gameState.getCurrentPlayerHand().ifPresent(hand -> printHand(gameState.getCurrentPlayer(), hand));

    printCurrentPlayerPlayCardTurn(allowedCommands);

    var command = prompt(() -> printCommandPrompt(allowedCommands), allowedCommands);
    var events = game.execute(command);
    Class<? extends View> nextView = PlayCardView.class;

    for (var event : events) {
      if (event instanceof CardPlayed cardPlayed) {
        gameState.apply(cardPlayed);
        continue;
      }

      if (event instanceof WonSting wonSting) {
        gameState.apply(wonSting);
        continue;
      }

      if (event instanceof BetFinished betFinished) {
        gameState.apply(betFinished);
        nextView = DealingView.class;
        continue;
      }

      if (event instanceof GameFinished) {
        nextView = FinishedView.class;
        var winningTeam = gameState.getWinningTeam().orElseThrow(() -> new IllegalStateException("Winning team is null"));
        var winningTeamScore = gameState.getTeamScore(winningTeam);
        var losingTeam = gameState.getOtherTeam(winningTeam);
        var losingTeamScore = gameState.getTeamScore(losingTeam);

        clear();
        printFinalResult(winningTeam, winningTeamScore, losingTeam, losingTeamScore);
        continue;
      }

      throw new IllegalArgumentException("Invalid Event received: " + event);
    }

    return nextView;
  }
}
