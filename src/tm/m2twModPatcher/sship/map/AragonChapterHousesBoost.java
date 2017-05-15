package tm.m2twModPatcher.sship.map;

import tm.m2twModPatcher.lib.common.core.features.Feature;
import tm.m2twModPatcher.lib.common.core.features.OverrideDeleteFilesTask;
import tm.m2twModPatcher.lib.engines.ConfigurationSettings;
import tm.m2twModPatcher.lib.fileEntities.data.world.maps.base.DescrRegions;

import java.util.Arrays;
import java.util.UUID;

/** Chapter Houses added to Aragon, more Crusadadable & Jihadable cities */
public class AragonChapterHousesBoost extends Feature {

	@Override
	public void executeUpdates() throws Exception {
		descrRegions = getFileRegisterForUpdated(DescrRegions.class);

		descrRegions.addResources("Zaragoza_Province", Arrays.asList(
				DescrRegions.KnightsOfSantiago,
				DescrRegions.StJohnKnights) );

		descrRegions.addResource("Pamplona_Province",
				DescrRegions.KnightsOfSantiago );

		descrRegions.addResources("Barcelona_Province", Arrays.asList(
				DescrRegions.KnightsOfSantiago,
				DescrRegions.TemplarKnights) );
	}

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.randomUUID();

	private DescrRegions descrRegions;

	public AragonChapterHousesBoost() {
		super("Aragon Chapter Houses Boost ");

		addCategory("Campaign");

		setDescriptionShort("Aragon faction has more Chapter Houses available to build it homeland settlements");
		setDescriptionUrl("http://tmsship.wikidot.com/aragon-chapter-houses-boost");

		boolean isDevMachine=false;
		if (ConfigurationSettings.isDevMachine()) isDevMachine = true;
		else isDevMachine = false;

		if(!isDevMachine)
			addOverrideTask(new OverrideDeleteFilesTask("data\\world\\maps\\base\\map.rwm"));
	}

	//	Zaragoza :
//	templars_chapter_house
//	+ knights_of_santiago_chapter_house
//	+ st_johns_chapter_house
//	+ jihad
//  + crusade
//
//	Pamplona :
//	st_johns_chapter_house
//	+ knights_of_santiago_chapter_house
//
//	Barcelona : nic
//	+knights_of_santiago_chapter_house
//	+templars_chapter_house
//	+ jihad

/*
	Paris:
		+ jihad

	Constantinople:
		+ jihad

	Toulouse, Bordeaux:
		+ 90% heretics

	Esztergom (Hungary capital)
		+ jihad

	Milan:
		+ jihad, crusade

	Lisbona:
		+ crusade

	Damascus:
		+ jihad

	Granada :
		+ jihad

	Sevilla :
		+ jihad

	Leon :
		+ crusade

*/

}
