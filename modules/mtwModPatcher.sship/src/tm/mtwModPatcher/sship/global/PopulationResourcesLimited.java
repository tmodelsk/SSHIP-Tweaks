package tm.mtwModPatcher.sship.global;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import tm.common.collections.ArrayUniqueList;
import tm.common.collections.ListUnique;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.params.ParamId;
import tm.mtwModPatcher.lib.common.core.features.params.ParamIdDouble;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.ExportDescrBuilding;

import java.util.UUID;
import java.util.regex.Pattern;

/**  */
public class PopulationResourcesLimited extends Feature {

	@Getter @Setter
	private double replenishRateMult = 0.3334;	// 0.4
	@Getter @Setter
	private double maxStackMult = 2.0;

	@Override
	public void executeUpdates() throws Exception {
		exportDescrBuilding = getFileRegisterForUpdated(ExportDescrBuilding.class);

		val lines = exportDescrBuilding.getLines();

		// Loop throught whole file & "        recruit_pool  "Polish Knights"  0.5   0.035   1  0  requires " string
		val patt = Pattern.compile("^\\s+recruit_pool\\s+");
		int index = 1;
		while ( index > 0) {
			index = lines.findFirstByRegexLine(patt, index);
			if(index > 0) {
				// found recuit_pool line !
				val orgLine = lines.getLine(index);
				val unitRecruitInfo = exportDescrBuilding.parseUnitRecruitmentInfo(orgLine);

				// unitRecruitInfo.MaxStack > 0 &&
				if(unitRecruitInfo.ReplenishRate > 0.01) {

					double replMultiTemp = replenishRateMult;
					if(unitRecruitInfo.ReplenishRate <= 0.034)	// 30 turns
						replMultiTemp *= 2;

					unitRecruitInfo.ReplenishRate *= replMultiTemp;
					unitRecruitInfo.MaxStack *= maxStackMult;

					double newInitial;
					if(unitRecruitInfo.InitialReplenishCounter < 1) {
						newInitial = 1.0;
					}
					else {
						newInitial = unitRecruitInfo.InitialReplenishCounter * (1.0+replMultiTemp);
					}

					newInitial = Math.min(unitRecruitInfo.MaxStack , newInitial);	// no more that MaxStack

					unitRecruitInfo.InitialReplenishCounter = newInitial;

					val newRecruitLine = unitRecruitInfo.toRecruitmentPoolLine();
					lines.replaceLine(index, newRecruitLine);
				}
				index++;	// go to next line
			}
		}
	}

	@Override
	public ListUnique<ParamId> defineParamsIds() {
		val params = new ArrayUniqueList<ParamId>();

		params.add(new ParamIdDouble("ReplenishRateMult", "Replenish Rate Mult",
				feature -> ((PopulationResourcesLimited) feature).getReplenishRateMult(),
				(feature, value) -> ((PopulationResourcesLimited) feature).setReplenishRateMult(value)));

		params.add(new ParamIdDouble("MaxStackMult", "Max Stack Mult",
				feature -> ((PopulationResourcesLimited) feature).getMaxStackMult(),
				(feature, value) -> ((PopulationResourcesLimited) feature).setMaxStackMult(value)));

		return params;
	}

	private ExportDescrBuilding exportDescrBuilding;

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.randomUUID();

	public PopulationResourcesLimited() {
		setName("Population Resources Limited");

		addCategory("Campaign");

		setDescriptionShort("The aim of this feature is to introduce bigger effect of limited population resources in terms of limited recruitment availabilities");
		setDescriptionUrl("http://tmsship.wikidot.com/population-resources-limited");
	}
}
