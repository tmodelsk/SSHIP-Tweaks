package tm.mtwModPatcher.sship.features.global;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import tm.common.collections.ArrayUniqueList;
import tm.common.collections.ListUnique;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.params.ParamId;
import tm.mtwModPatcher.lib.common.core.features.params.ParamIdBoolean;
import tm.mtwModPatcher.lib.common.core.features.params.ParamIdDouble;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.ExportDescrBuilding;

import java.util.UUID;
import java.util.regex.Pattern;

/**  */
public class PopulationResourcesLimited extends Feature {

	@Override
	public void setParamsCustomValues() {
		replenishRateMult = 0.3334;    // 0.4
		maxStackMult = 1.5;    // 2.0
		maxStackMinimumTwo = true;
	}

	@Override
	public void executeUpdates() throws Exception {
		exportDescrBuilding = getFileRegisterForUpdated(ExportDescrBuilding.class);

		val lines = exportDescrBuilding.getLines();

		// Loop throught whole file & "        recruit_pool  "Polish Knights"  0.5   0.035   1  0  requires " string
		val patt = Pattern.compile("^\\s+recruit_pool\\s+");
		int index = 1;
		while (index > 0) {
			index = lines.findFirstByRegexLine(patt, index);
			if (index > 0) {
				// found recuit_pool line !
				val orgLine = lines.getLine(index);
				val unitRecruitInfo = exportDescrBuilding.parseUnitRecruitmentInfo(orgLine);

				// unitRecruitInfo.MaxStack > 0 &&
				if (unitRecruitInfo.ReplenishRate > 0.01) {    // less than 100 turns ? leave as it is

					double replMultiTemp = replenishRateMult;
					if (unitRecruitInfo.ReplenishRate <= 0.034 && unitRecruitInfo.MaxStack > 0)    // more than 30 turns for true entries - bonus *2
						replMultiTemp *= 1.5;

					unitRecruitInfo.ReplenishRate *= replMultiTemp;

					unitRecruitInfo.MaxStack *= maxStackMult;
					if (maxStackMinimumTwo && unitRecruitInfo.MaxStack < 2.0 && unitRecruitInfo.MaxStack > 1.0)
						unitRecruitInfo.MaxStack = 2.0;

					double newInitial;
					if (unitRecruitInfo.InitialReplenishCounter < 1) {
						newInitial = 1.0;
					} else {
						newInitial = unitRecruitInfo.InitialReplenishCounter * (1.0 + replMultiTemp);
					}

					newInitial = Math.min(unitRecruitInfo.MaxStack, newInitial);    // no more that MaxStack

					unitRecruitInfo.InitialReplenishCounter = newInitial;

					val newRecruitLine = unitRecruitInfo.toRecruitmentPoolLine();
					lines.replaceLine(index, newRecruitLine);
				}
				index++;    // go to next line
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

		params.add(new ParamIdBoolean("MaxStackMinimumTwo", "Max Stack Minimum Two",
				feature -> ((PopulationResourcesLimited) feature).isMaxStackMinimumTwo(),
				(feature, value) -> ((PopulationResourcesLimited) feature).setMaxStackMinimumTwo(value)));

		return params;
	}

	@Getter @Setter private double replenishRateMult;
	@Getter @Setter private double maxStackMult;
	@Getter @Setter private boolean maxStackMinimumTwo;

	private ExportDescrBuilding exportDescrBuilding;

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.fromString("f8423399-3683-4a7e-8bad-dfe5dba9bd5b");

	public PopulationResourcesLimited() {
		setName("Population Resources Limited");

		addCategory("Campaign");

		setDescriptionShort("The aim of this feature is to introduce bigger effect of limited population resources in terms of limited recruitment availabilities");
		setDescriptionUrl("http://tmsship.wikidot.com/population-resources-limited");
	}
}
