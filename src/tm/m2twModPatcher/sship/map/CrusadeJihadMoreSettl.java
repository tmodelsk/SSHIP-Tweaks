package tm.m2twModPatcher.sship.map;

import tm.m2twModPatcher.lib.common.core.features.Feature;
import tm.m2twModPatcher.lib.common.core.features.OverrideDeleteFilesTask;
import tm.m2twModPatcher.lib.engines.ConfigurationSettings;
import tm.m2twModPatcher.lib.fileEntities.data.world.maps.base.DescrRegions;

import java.util.Arrays;
import java.util.UUID;

/** More settlements available for Crusade & Jihad */
public class CrusadeJihadMoreSettl extends Feature {

	@Override
	public void executeUpdates() throws Exception {
		descrRegions = getFileRegisterForUpdated(DescrRegions.class);

		descrRegions.addResources("Rome_Province", Arrays.asList(
				DescrRegions.Jihad,
				DescrRegions.Crusade ) );

		descrRegions.addResources("Zaragoza_Province", Arrays.asList(
				DescrRegions.Jihad,
				DescrRegions.Crusade ) );

		descrRegions.addResources("Barcelona_Province", Arrays.asList(
				DescrRegions.Jihad ) );

		descrRegions.addResources("Milan_Province", Arrays.asList(DescrRegions.Crusade, DescrRegions.Jihad ) );
		descrRegions.addResources("Cordoba_Province", Arrays.asList(DescrRegions.Crusade, DescrRegions.Jihad ) );

		descrRegions.addResource("Paris_Province", DescrRegions.Jihad );
		descrRegions.addResource("Constantinople_Province", DescrRegions.Jihad );
		descrRegions.addResource("Esztergom_Province", DescrRegions.Jihad );
		descrRegions.addResource("Lisbon_Province", DescrRegions.Crusade );
		descrRegions.addResource("Damascus_Province", DescrRegions.Jihad );
		descrRegions.addResource("Granada_Province", DescrRegions.Jihad );
		descrRegions.addResource("Sevilla_Province", DescrRegions.Jihad );
		descrRegions.addResource("Leon_Province", DescrRegions.Crusade );
	}

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.randomUUID();

	private DescrRegions descrRegions;

	public CrusadeJihadMoreSettl() {

		setName("Crusade & Jihad Additional Settlements");
		addCategory("Capmaign");

		setDescriptionShort("More settlements available for Crusade and/or Jihad");
		setDescriptionUrl("http://tmsship.wikidot.com/crusade-jihad-more-settlements");

		boolean isDevMachine = ConfigurationSettings.isDevMachine();

		if(!isDevMachine)
			addOverrideTask(new OverrideDeleteFilesTask("data\\world\\maps\\base\\map.rwm"));
	}
}
