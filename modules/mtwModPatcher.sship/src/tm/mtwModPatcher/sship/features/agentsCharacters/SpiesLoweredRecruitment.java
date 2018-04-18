package tm.mtwModPatcher.sship.features.agentsCharacters;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.ExportDescrBuilding;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings.BuildingLevel;
import tm.mtwModPatcher.sship.lib.Buildings;

import java.util.UUID;
import java.util.regex.Pattern;

public class SpiesLoweredRecruitment extends Feature {
	@Override
	public void setParamsCustomValues() {

	}

	@Override
	public void executeUpdates() throws Exception {
		edb = getFileRegisterForUpdated(ExportDescrBuilding.class);

		/*	agent spy  0  requires factions { northern_european, }
			agent spy  0  requires factions { middle_eastern, }
			agent spy  0  requires factions { eastern_european, }
			agent spy  0  requires factions { greek, }
			agent spy  0  requires factions { southern_european, }
			agent_limit spy 1 */

		removeSpyRecuitment();

		addSpyToBuilding(Buildings.TavernCity, 1, 1, 1, 2, 2);
		addSpyToBuilding(Buildings.TavernCastle, 1);

		val capitalCondition = "and hidden_resource capital";
		addSpyToBuilding(Buildings.WallsCity, capitalCondition, 1, 1, 1, 1, 1);
		addSpyToBuilding(Buildings.WallsCastle, capitalCondition, 1, 1, 1, 1 ,1);

	}
	private void addSpyToBuilding(BuildingLevel building, int ... agentLimit) {
		addSpyToBuilding(building, null,  agentLimit);
	}
	private void addSpyToBuilding(BuildingLevel building, String addCondition, int ... agentLimit) {
		boolean isNext = false;

		BuildingLevel tmpBuilding = building;

		for(int limit : agentLimit) {
			if(isNext) tmpBuilding = tmpBuilding.createNextLevel();

			addSpyToBuilding(tmpBuilding, limit, addCondition);
			isNext = true;
		}
	}
	private void addSpyToBuilding(BuildingLevel building, int agentLimit) {
		addSpyToBuilding(building, agentLimit, null);
	}
	private void addSpyToBuilding(BuildingLevel building, int agentLimit, String addCondition) {
		String requires = " requires factions { northern_european, middle_eastern, eastern_european, greek, southern_european, }";

		if(addCondition != null && !addCondition.isEmpty())
			requires += " " + addCondition;

		edb.addCapabilities(building, "	agent spy 0"+requires);
		edb.addCapabilities(building, "	agent_limit spy "+agentLimit+requires);
	}

	private void removeSpyRecuitment() {
		val recruitRegex = Pattern.compile("^\\s*agent\\s+spy\\s+");
		val limitRegex = Pattern.compile("^\\s*agent_limit\\s+spy\\s+");

		edb.getLines().removeAllRegexLines(recruitRegex);
		edb.getLines().removeAllRegexLines(limitRegex);
	}

	private ExportDescrBuilding edb;

	public SpiesLoweredRecruitment() {
		super("Spies Lowered Recruitment");

		addCategory(CATEGORY_AGENTS);

		setDescriptionShort("Spies recruitment is lowered");
		setDescriptionUrl("http://tmsship.wikidot.com/...");
	}

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.fromString("dc0e8afb-f6ba-4087-a685-c28952334df2");
}
