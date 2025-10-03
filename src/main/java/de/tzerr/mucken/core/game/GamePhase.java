package de.tzerr.mucken.core.game;

import de.tzerr.mucken.core.game.command.Command;
import lombok.NonNull;

import java.util.List;

sealed interface GamePhase permits DealingPhase, BettingPhase, PlayCardPhase, FinishedPhase {

  @NonNull
  PhaseResult execute(@NonNull Command command);

  @NonNull
  List<Command> getAcceptableCommands();
}
