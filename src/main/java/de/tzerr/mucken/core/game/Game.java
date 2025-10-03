package de.tzerr.mucken.core.game;

import de.tzerr.mucken.core.game.command.Command;
import de.tzerr.mucken.core.game.event.Event;
import lombok.NonNull;

import java.util.List;

public sealed interface Game permits DefaultGame {

  @NonNull
  List<Event> execute(@NonNull Command command);

  @NonNull
  List<Command> getAcceptableCommands();
}
