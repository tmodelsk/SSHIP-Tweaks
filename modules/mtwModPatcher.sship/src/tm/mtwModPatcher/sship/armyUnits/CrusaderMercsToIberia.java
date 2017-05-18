package tm.mtwModPatcher.sship.armyUnits;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.data.world.maps.campaign.DescrMercenaries;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Tomek on 2016-07-27.
 */
public class CrusaderMercsToIberia extends Feature {

	@Override
	public void executeUpdates() throws Exception {
		_DescrMercenaries = getFileRegisterForUpdated(DescrMercenaries.class);

		// ## Christian Crusaders ##

		// 0.14  =  7 turns
		// 0.10  = 10 turns
		// 0.084 = 12 turns
		// 0.067 = 15 turns
		// 0.050 = 20 turns

		val sergntsStr = "\tunit Crusader Sergeants\t\t\texp 1 cost 750 replenish 0.067 - 0.10 max 1 initial 1 religions { catholic }";	// replenish 0.07 - 0.17
		//val kngtsFtStr = "\tunit Dismounted Crusader Knights\texp 1 cost 800 replenish 0.05 - 0.084 max 1 initial 1 religions { catholic }";	// 0.05 - 0.12
		val knightsStr = "\tunit Crusader Knights\t\t\texp 1 cost 1200 replenish 0.05 - 0.067 max 1 initial 1 religions { catholic }";	// 0.04 - 0.10

		List<String> crusadersList = new ArrayList<>();
		crusadersList.add(knightsStr);
		crusadersList.add(sergntsStr);
		//crusadersList.add(knightsFootStr);

		_DescrMercenaries.addUnitRecruitmentLine("Pyrenaes" , crusadersList);
		_DescrMercenaries.addUnitRecruitmentLine("Andalusia" , crusadersList);
		_DescrMercenaries.addUnitRecruitmentLine("Spain" , crusadersList);

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

		_DescrMercenaries.addUnitRecruitmentLine("Spain" , jihadsLowList);
		_DescrMercenaries.addUnitRecruitmentLine("Pyrenaes" , jihadsLowList);

		_DescrMercenaries.addUnitRecruitmentLine("Andalusia" , jihadsHightList);
		_DescrMercenaries.addUnitRecruitmentLine("North_East_Africa" , jihadsHightList);
		_DescrMercenaries.addUnitRecruitmentLine("Ghana_Africa" , jihadsHightList);	// northern-west Africa

		_DescrMercenaries.addUnitRecruitmentLine("Berber_Africa" , jihadsLowList);
	}

	protected DescrMercenaries _DescrMercenaries;

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.randomUUID();

	public CrusaderMercsToIberia() {
		super("Crusader Jihad Mercenaries To Iberia ");

		addCategory("Campaign");

		setDescriptionShort("Adds Crusader & Jihad mercenaries recruitable without ongoing Crusade/Jihad - just as normal mercenaries for Iberia & North Africa");
		setDescriptionUrl("http://tmsship.wikidot.com/crusader-jihad-mercenaries-to-iberia");
	}
}
