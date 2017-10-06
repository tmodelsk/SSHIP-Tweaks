package tm.mtwModPatcher.lib.common.scripting.campaignScript.core;

/** http://www.twcenter.net/forums/showthread.php?691274-What-is-the-processing-sequence-of-TurnStart  */
public enum EventType {

	PreFactionTurnStart,

	CharacterTurnStart ,

	SettlementTurnStart,

	FactionTurnStart,

	CharacterTurnEnd,

	CharacterTurnEndInSettlement,

	SettlementTurnEnd,

	GeneralAssaultsResidence,

	FactionTurnEnd ,

	ButtonPressed
}
