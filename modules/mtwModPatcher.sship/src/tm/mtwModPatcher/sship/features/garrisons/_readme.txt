### Version 1.03 Control Flow ###
monitor_event SettlementTurnStart SettlementName Venice
	set_counter Venice_IsRisingGarrison 0
end_monitor

monitor_event SettlementTurnEnd SettlementName Venice
		and GovernorInResidence
		and SettlementLoyaltyLevel >= loyalty_happy
		and not FactionExcommunicated
		and not SettlementHasPlague
	set_counter Venice_IsRisingGarrison 1
end_monitor

 a w nastepnej turze AI zdarzenie na assault :

 monitor_event GeneralAssaultsResidence IsTargetRegionOneOf Venice_Province
 		and I_SettlementUnderSiege Venice
 		and I_CompareCounter Venice_IsRisingGarrison = 1
 		and I_CompareCounter Venice_LastSiegeMobilizationTurnsCount > 15