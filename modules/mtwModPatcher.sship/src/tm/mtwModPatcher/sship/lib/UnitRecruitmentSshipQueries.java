package tm.mtwModPatcher.sship.lib;

import lombok.val;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.ExportDescrBuilding;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.UnitRecuitmentInfo;
import tm.mtwModPatcher.lib.managers.FactionsDefs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tomek on 17.06.2017.
 */
public class UnitRecruitmentSshipQueries extends tm.mtwModPatcher.lib.data.exportDescrBuilding.UnitRecruitmentQueries {

	public List<UnitRecuitmentInfo> findMuslim() {
		val withRegions = findMuslimAndMuslimRegions();
		val withoutRegions = findMuslimNoHiddenResources();

		val muslimRecruitments = new ArrayList<UnitRecuitmentInfo>();

		muslimRecruitments.addAll(withRegions);
		muslimRecruitments.addAll(withoutRegions);

		return  muslimRecruitments;
	}


	public List<UnitRecuitmentInfo> findMuslimAndMuslimRegions() {
		val factions = FactionsDefs.islamFactionsList();
		val hiddenResources = FactionsDefs.islamFactionsList();
		hiddenResources.add("andalusia");
		hiddenResources.add("berber");

		val res = getByFactionsHiddenResourcesPositive(factions, hiddenResources);

		return res;
	}

	public List<UnitRecuitmentInfo> findMuslimNoHiddenResources() {
		val factions = FactionsDefs.islamFactionsList();

		val res = getByFactionsNoHiddenResources(factions);

		return res;
	}


	public UnitRecruitmentSshipQueries(ExportDescrBuilding edb) {
		super(edb);
	}
}
