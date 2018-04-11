package tm.mtwModPatcher.lib.data.exportDescrBuilding;

import tm.common.collections.ListUnique;

/** ExportDescBuidilng unit Requires single condition line */
public class UnitRequire {

	public ListUnique<String> Factions;

	public String RestConditions;


	public void setFactionSingle(String factionSymbol) {
		Factions.clear();
		Factions.add(factionSymbol);
	}

}
