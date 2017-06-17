package tm.mtwModPatcher.sship.armyUnits;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.LineNotFoundEx;
import tm.mtwModPatcher.lib.managers.FactionsDefs;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.ExportDescrBuilding;
import tm.mtwModPatcher.lib.data.exportDescrUnit.ExportDescrUnitTyped;
import tm.mtwModPatcher.lib.data.unitModels.BattleModels;

import java.util.UUID;

/**
 * Created by tomek on 25.05.2017.
 */
public class TrebuchetEarlyAdoption extends Feature {
	@Override
	public void executeUpdates() throws Exception {
		exportDescrBuilding = getFileRegisterForUpdated(ExportDescrBuilding.class);
		exportDescrUnit = getFileRegisterForUpdated(ExportDescrUnitTyped.class);
		battleModels = getFileRegisterForUpdated(BattleModels.class);

		val trebuchetName = "ME Trebuchet";
		exportDescrUnit.loadUnit(trebuchetName).Ownership = "all";
		exportDescrUnit.loadUnit(trebuchetName).OwnershipEra1 = "all";
		battleModels.copyModelBlocksData("me_trebuchet_crew", "aragon", "moors");


		int index;
		String orgLine;
		String requiresRaw = "factions { northern_european, eastern_european, teutonic_order, timurids, southern_european, } and hidden_resource ";
		String requiresPart = "";

		for (val islamFacionName : FactionsDefs.islamFactionsList()) {
			if(!requiresPart.isEmpty())
				requiresPart +=" or ";

			requiresPart += requiresRaw + islamFacionName;
		}

		String unitRequires = " " + requiresPart;
		String buildingRequires = " or " + requiresPart;

		val lines = exportDescrBuilding.getLines();

		// ## Castle Siege ##
		index = exportDescrBuilding.findBuidlingRequiresLine("castle_siege", "c_siege_works" ,"castle");
		if(index <=0) throw new LineNotFoundEx("City siege building not found");

		orgLine = lines.getLine(index);
		lines.replaceLine(index, orgLine + buildingRequires);

		exportDescrBuilding.insertRecruitmentBuildingCapabilities("castle_siege", "c_siege_works" ,"castle",
				trebuchetName,1,0.067, 1, 0 , unitRequires);
	}

	private ExportDescrBuilding exportDescrBuilding;
	private ExportDescrUnitTyped exportDescrUnit;
	private BattleModels battleModels;

	public TrebuchetEarlyAdoption() {
		super("Trebuchet Early Adoption");
	}

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.randomUUID();
}
