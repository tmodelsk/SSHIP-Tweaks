package tm.mtwModPatcher.sship.armyUnits;

import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.fileEntities.data.world.maps.campaign.DescrMercenaries;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Tomek on 2016-07-21.
 */
public class CrusaderMercsToLevant extends Feature {

	@Override
	public void executeUpdates() throws Exception {
		_DescrMercenaries = getFileRegisterForUpdated(DescrMercenaries.class);

		String sergentsStr = "\tunit Crusader Sergeants\t\t\texp 1 cost 800 replenish 0.067 - 0.10 max 1 initial 1 religions { catholic }";
		//String knightsFootStr = "\tunit Dismounted Crusader Knights\texp 1 cost 1584 replenish 0.07 - 0.17 max 1 initial 1 religions { catholic }";
		String knightsStr = "\tunit Crusader Knights\t\t\texp 1 cost 1250 replenish 0.05 - 0.067 max 1 initial 1 religions { catholic }";

		List<String> crusadersList = new ArrayList<>();
		crusadersList.add(knightsStr);
		crusadersList.add(sergentsStr);
		//crusadersList.add(knightsFootStr);

		_DescrMercenaries.addUnitRecruitmentLine("Crusaders_Path" , crusadersList);
		_DescrMercenaries.addUnitRecruitmentLine("Levant" , crusadersList);


		// ## Muslim Jihadists ##
		String ghazisHigh = "\tunit Ghazis\t\t\t\texp 3 cost 245 replenish 0.084 - 0.14 max 2 initial 1 religions { islam }";
		String mutatawwiWariorsHigh = "  unit Mutatawwi Warriors\t\t\t\texp 2 cost 984 replenish 0.067 - 0.10 max 1 initial 1 religions { islam }";

		List<String> jihadsHightList = new ArrayList<>();
		jihadsHightList.add(ghazisHigh);
		jihadsHightList.add(mutatawwiWariorsHigh);

		String ghazisLow = "\tunit Ghazis\t\t\t\texp 1 cost 245 replenish 0.067 - 0.10 max 1 initial 1 religions { islam }";
		String mutatawwiWariorsLow = "  unit Mutatawwi Warriors\t\t\t\texp 1 cost 984 replenish 0.05 - 0.067 max 1 initial 1 religions { islam }";

		List<String> jihadsLowList = new ArrayList<>();
		jihadsLowList.add(ghazisLow);
		jihadsLowList.add(mutatawwiWariorsLow);

		_DescrMercenaries.addUnitRecruitmentLine("Crusaders_Path" , jihadsLowList);
		_DescrMercenaries.addUnitRecruitmentLine("Levant" , jihadsLowList);

		_DescrMercenaries.addUnitRecruitmentLine("Armenia" , jihadsLowList);

		_DescrMercenaries.addUnitRecruitmentLine("Egypt" , jihadsHightList);
		_DescrMercenaries.addUnitRecruitmentLine("Arabia" , jihadsHightList);
		_DescrMercenaries.addUnitRecruitmentLine("Mesopotamia" , jihadsHightList);

		_DescrMercenaries.addUnitRecruitmentLine("Central_Anatolia" , jihadsLowList);
		_DescrMercenaries.addUnitRecruitmentLine("Eastern_Anatolia" , jihadsLowList);
		_DescrMercenaries.addUnitRecruitmentLine("Kurdish" , jihadsLowList);



	}

	protected DescrMercenaries _DescrMercenaries;

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.randomUUID();

	public CrusaderMercsToLevant() {
		super("Crusader Jihad Mercenariess To Levant ");

		addCategory("Campaign");

		setDescriptionShort("Adds Crusader & Jihad mercenaries recruitable withour ongoing Crusade/Jihad - just as normal mercenaries");
		setDescriptionUrl("http://tmsship.wikidot.com/crusader-jihad-mercenariess-to-levant");
	}
}
