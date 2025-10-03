package de.tzerr.mucken.core.game.event;

public sealed interface Event permits DealtHand, BetShouted, BetAccepted, CardPlayed, WonSting, BetFinished, GameFinished, InvalidCommandReceived  {
}
