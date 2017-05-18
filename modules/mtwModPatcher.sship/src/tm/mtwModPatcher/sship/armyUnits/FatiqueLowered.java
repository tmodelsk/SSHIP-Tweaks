package tm.mtwModPatcher.sship.armyUnits;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.data.exportDescrUnit.ExportDescrUnitTyped;

import java.util.UUID;

/**
 * Created by tomek on 22.04.2017.
 */
public class FatiqueLowered extends Feature {

	@Override
	public void executeUpdates() throws Exception {
		exportDescrUnit=getFileRegisterForUpdated(ExportDescrUnitTyped.class);

		for (val unit : exportDescrUnit.getUnits()) {
			// Lower Stat Heat
			val shOrg = unit.StatHeat;
			if(shOrg > 0){
				if(shOrg <= 5) unit.StatHeat -= 1;
				else unit.StatHeat -= 2;
			}

			// Attribute hardy , very_hardy
			if(unit.isCategoryInfantry() || unit.isCategoryCavalry()) {
				if( !unit.Attributes.contains("very_hardy") ) {

					if(unit.Attributes.contains("hardy")) {
						// Jest hardy, robimy very_hardy
						unit.Attributes = unit.Attributes.replace("hardy","very_hardy");
					}else {
						// nie ma very_hardy ani hardy, robimy hardy
						unit.addAttribute("hardy");
					}
				}
			}
		}
	}


	private ExportDescrUnitTyped exportDescrUnit;

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.randomUUID();

	public FatiqueLowered() {
		setName("Fatique Lowered");

		addCategory("Battle");
		addCategory("Units");
		setDescriptionShort("On Battlefield, fatigue effect is lowered, it's easier to deal with it. Units little slower got tired & recovers much faster.");
		setDescriptionUrl("http://tmsship.wikidot.com/fatique-lowered");
	}
}
