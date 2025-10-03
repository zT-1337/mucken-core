package de.tzerr.mucken.core.game;

import de.tzerr.mucken.core.game.event.Event;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;

record PhaseResult(@NonNull Optional<Class<? extends GamePhase>> nextPhase, @NonNull List<Event> events) {
}
