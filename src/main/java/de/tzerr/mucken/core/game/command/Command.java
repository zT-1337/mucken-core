package de.tzerr.mucken.core.game.command;

public sealed interface Command permits DealHands, ShoutBet, PlayCard {
}
