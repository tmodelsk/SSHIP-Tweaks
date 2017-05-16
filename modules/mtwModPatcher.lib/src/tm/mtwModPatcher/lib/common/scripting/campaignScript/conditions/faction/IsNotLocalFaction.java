package tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.faction;

/**
 * Created by Tomek on 2016-11-24.
 */
public class IsNotLocalFaction extends IsLocalFaction {

	@Override
	public String getString() {
		return "not " + super.getString();
	}

	public IsNotLocalFaction(String factionName) {
		super(factionName);
	}
}
