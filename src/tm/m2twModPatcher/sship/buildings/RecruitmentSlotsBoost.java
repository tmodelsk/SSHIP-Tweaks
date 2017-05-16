package tm.m2twModPatcher.sship.buildings;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import tm.common.collections.ArrayUniqueList;
import tm.common.collections.ListUnique;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.params.ParamId;
import tm.mtwModPatcher.lib.common.core.features.params.ParamIdInteger;
import tm.mtwModPatcher.lib.fileEntities.data.exportDescrBuilding.ExportDescrBuilding;

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
	public static UUID Id = UUID.randomUUID();

	public RecruitmentSlotsBoost() {
		setName("Recruitment Slots Boost");
		addCategory("Buildings");
		setDescriptionShort("Recruitment Slots Boost : +1 slot for every settlement level of city & castle");
		setDescriptionUrl("http://tmsship.wikidot.com/recruitment-slots-boost");

	}
}
