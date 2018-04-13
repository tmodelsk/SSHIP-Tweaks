package tm.mtwModPatcher.sship.features.map;

import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.data.world.maps.base.DescrRegions;
import tm.mtwModPatcher.sship.lib.Provinces;

import java.util.UUID;

/** Chapter Houses added to Aragon, more Crusadadable & Jihadable cities */
public class AragonChapterHousesBoost extends Feature {

	@Override
	public void setParamsCustomValues() {

	}

	@Override
	public void executeUpdates() throws Exception {
		descrRegions = getFileRegisterForUpdated(DescrRegions.class);

		descrRegions.addResource(Provinces.Zaragoza, DescrRegions.KnightsOfSantiago, DescrRegions.StJohnKnights);

		descrRegions.addResource(Provinces.Pamplona, DescrRegions.KnightsOfSantiago );
// Barcelona did not have any Militant Orders of note on it, so remove the Hospitaller chapter houses from Barcelona altogether.
//		descrRegions.addResources("Barcelona_Province", Arrays.asList(
//				DescrRegions.KnightsOfSantiago,
//				DescrRegions.TemplarKnights) );
	}

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.fromString("2f0ff2db-1982-45d4-a891-0d44fc20ec31");

	private DescrRegions descrRegions;

	public AragonChapterHousesBoost() {
		super("Aragon Chapter Houses Boost ");

		addCategory("Campaign");

		setDescriptionShort("Aragon faction has more Chapter Houses available to build it homeland settlements");
		setDescriptionUrl("http://tmsship.wikidot.com/aragon-chapter-houses-boost");

		requestForMapRemoval();
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
