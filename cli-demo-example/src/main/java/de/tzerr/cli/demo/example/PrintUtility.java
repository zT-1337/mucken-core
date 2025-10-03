package de.tzerr.cli.demo.example;

import de.tzerr.mucken.core.game.Hand;
import de.tzerr.mucken.core.game.Player;
import de.tzerr.mucken.core.game.Sting;
import de.tzerr.mucken.core.game.Team;
import de.tzerr.mucken.core.game.bet.Bet;
import de.tzerr.mucken.core.game.card.Card;
import de.tzerr.mucken.core.game.command.Command;
import de.tzerr.mucken.core.game.event.BetShouted;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class PrintUtility {

  private static final String os = System.getProperty("os.name");

  public static void clear() {
    try {
      if (os.startsWith("Windows")) {
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
      } else {
        new ProcessBuilder("clear").inheritIO().start().waitFor();
      }
    } catch (InterruptedException | IOException e) {
      System.out.println("Clear console failed");
      System.out.println(e.getMessage());
      throw new RuntimeException(e);
    }
  }

  public static void printCurrentTotalScore(Map<Team, Integer> scores) {
    printSection("Total Score", () -> scores.forEach(
      (team, score) -> System.out.printf("  - %s: %d%n", team, score)
    ));
  }

  public static void printCurrentBet(Bet currentBet, Player bettingPlayer) {
    printSection("Current Highest Bet", () -> System.out.printf("  %s by %s%n", currentBet, bettingPlayer));
  }

  public static void printCurrentPlayerBettingTurn(List<Command> allowedCommands) {
    printSection("Betting Turn", () -> {
      System.out.println("  Choose bet from: ");
      printCommands(allowedCommands);
    });
  }

  public static void printCurrentPlayerPlayCardTurn(List<Command> allowedCommands) {
    printSection("Play Card", () -> {
      System.out.println("  Choose card from: ");
      printCommands(allowedCommands);
    });
  }

  private static void printCommands(List<Command> commands) {
    for (int i = 0; i < commands.size(); i++) {
      System.out.printf("    - %s (%d)%n", commands.get(i), i);
    }
  }

  private static void printSection(String sectionName, Runnable runnable) {
    var section = String.format("--- %s ---", sectionName);
    System.out.println(section);
    runnable.run();
    System.out.println(section);
    System.out.println();
  }

  public static void printLastBets(List<BetShouted> lastBets) {
    printSection("Last Bets", () -> lastBets.forEach(
      betShouted -> System.out.printf(" - %s%n", betShouted)
    ));
  }

  public static void printHand(Player player, Hand hand) {
    printSection(String.format("Hand of %s", player), () -> System.out.printf("  %s%n", hand));
  }

  public static void printCommandPrompt(List<Command> allowedCommands) {
    System.out.printf("Enter Command (Between 0 - %d): ", allowedCommands.size() - 1);
  }

  public static void printCurrentSting(List<Card> playedCards, List<Player> cardsPlayedBy) {
    printSection("Current Sting", () -> {
      for (int i = 0; i < playedCards.size(); i++) {
        System.out.printf("%s (%s) / ", playedCards.get(i), cardsPlayedBy.get(i));
      }
      System.out.println();
    });
  }

  public static void printLastSting(Sting lastSting) {
    printSection("Last Sting", () -> System.out.printf(
      "Won by %s: %s%n", lastSting.owner(), lastSting.playedCards())
    );
  }

  public static void printFinalResult(Team winningTeam, int winningTeamScore, Team losingTeam, int losingTeamScore) {
    printSection("Final Result", () -> {
      System.out.printf("%s won with %d points%n", winningTeam, winningTeamScore);
      System.out.printf("%s lost with %d points%n", losingTeam, losingTeamScore);
    });
  }
}
