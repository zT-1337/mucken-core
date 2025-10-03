package de.tzerr.cli.demo.example;

import de.tzerr.mucken.core.game.command.Command;

import java.util.List;
import java.util.Scanner;

public class PromptUtility {

  private static final Scanner scanner = new Scanner(System.in);

  public static Command prompt(Runnable promptPrinter, List<Command> allowedCommands) {
    var commandIndex = -1;

    do {
      promptPrinter.run();
      try {
        commandIndex = Integer.parseInt(scanner.nextLine());
      } catch (Exception ignored) {}
    } while (commandIndex < 0 || commandIndex >= allowedCommands.size());

    return allowedCommands.get(commandIndex);
  }
}
