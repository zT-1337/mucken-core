package de.tzerr.mucken.core.game;

import de.tzerr.mucken.core.game.command.Command;
import de.tzerr.mucken.core.game.event.InvalidCommandReceived;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;

final class FinishedPhase implements GamePhase {

  private static final List<Command> acceptableCommands = List.of();

  @Override
  public @NonNull PhaseResult execute(@NonNull Command command) {
    return new PhaseResult(
      Optional.empty(),
      List.of(new InvalidCommandReceived("Game is already finished. No more commands are accepted"))
    );
  }

  @Override
  public @NonNull List<Command> getAcceptableCommands() {
    return acceptableCommands;
  }
}
