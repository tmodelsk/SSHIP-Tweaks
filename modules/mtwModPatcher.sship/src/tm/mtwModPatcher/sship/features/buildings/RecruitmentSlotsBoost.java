package tm.mtwModPatcher.sship.features.buildings;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import tm.common.collections.ArrayUniqueList;
import tm.common.collections.ListUnique;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.params.ParamId;
import tm.mtwModPatcher.lib.common.core.features.params.ParamIdInteger;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.ExportDescrBuilding;

import java.util.UUID;

public class RecruitmentSlotsBoost extends Feature {

	@Getter @Setter
	private int recruitmentSlotsBonus=1;

	@Override
	public void executeUpdates() throws Exception {
		exportDescrBuilding = getFileRegisterForUpdated(ExportDescrBuilding.class);

		exportDescrBuilding.addToWallsRecruitmentSlots(recruitmentSlotsBonus);
	}

	@Override
	public ListUnique<ParamId> defineParamsIds() {
		val parIds = new ArrayUniqueList<ParamId>();

		parIds.add(new ParamIdInteger("RecruitmentSlotsBonus" , "Recruitment Slots Bonus",
				feature -> ((RecruitmentSlotsBoost)feature).getRecruitmentSlotsBonus(),
				(feature, value) -> ((RecruitmentSlotsBoost)feature).setRecruitmentSlotsBonus(value)));

		return parIds;
	}

	private ExportDescrBuilding exportDescrBuilding;

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.fromString("218d0b25-f84e-40cb-845e-0a5ac7053017");

	public RecruitmentSlotsBoost() {
		setName("Recruitment Slots Boost");
		addCategory("Buildings");
		setDescriptionShort("Recruitment Slots Boost : +1 slot for every settlement level of city & castle");
		setDescriptionUrl("http://tmsship.wikidot.com/recruitment-slots-boost");

	}
}
