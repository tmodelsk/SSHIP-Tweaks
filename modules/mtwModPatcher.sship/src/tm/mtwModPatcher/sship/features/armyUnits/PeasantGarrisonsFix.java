package tm.mtwModPatcher.sship.features.armyUnits;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.data.exportDescrUnit.ExportDescrUnitTyped;
import tm.mtwModPatcher.lib.data.exportDescrUnit.UnitDef;

import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by tomek on 16.08.2017.
 */
public class PeasantGarrisonsFix extends Feature {


	@Override
	public void executeUpdates() throws Exception {
		edu = getFileRegisterForUpdated(ExportDescrUnitTyped.class);

		@SuppressWarnings("unused")
		val peasantAttribUnitsOrg = edu.getUnits().stream()
				.filter( u -> u.hasAttribute(UnitDef.ATTRIB_IS_PEASANT) )
				.map( u -> u.Name ).collect(Collectors.toList());

		edu.removeAttributeForAllUnits(UnitDef.ATTRIB_IS_PEASANT);

		val peasants = new ArrayList<String>();

		val peasantArrowArchers = edu.getUnits().stream()
				.filter(u -> u.StatPri.IsMissleUnit() && u.StatPri.Ammunition.equals("peasant_arrow"))
				.collect(Collectors.toList());

		peasantArrowArchers.forEach( paa -> peasants.add(paa.Name) );

		peasants.add("EE Peasants");
		peasants.add("Peasants");
		peasants.add("Southern Peasants");
		peasants.add("Slav Levies");
		peasants.add("Transilvanian Peasants");
		peasants.add("Hunters");
		peasants.add("Woodsmen");

		peasants.add("Religious Fanatics");
		peasants.add("Pisan and Geonese sailors");

		//peasants.add("");

		peasants.forEach( p -> edu.loadUnit(p).addAttribute(UnitDef.ATTRIB_IS_PEASANT) );
	}

	private ExportDescrUnitTyped edu;

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.randomUUID();

	public PeasantGarrisonsFix() {
		setName("Peasant Garrisons Attribute Fix");

		addCategory("Units");

		setDescriptionShort("Only peasant & wild non-town units got is_peasant attribute");
		setDescriptionUrl("http://tmsship.wikidot.com/peasant-garrisons-attribute-fix");
	}
}
