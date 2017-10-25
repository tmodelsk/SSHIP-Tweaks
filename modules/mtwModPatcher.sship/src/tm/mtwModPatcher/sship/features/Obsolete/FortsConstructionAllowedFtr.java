package tm.mtwModPatcher.sship.features.Obsolete;

import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.data._root.DescrCampaignDb;
import tm.mtwModPatcher.lib.data._root.DescrCultures;

import java.util.UUID;

/**
 * Created by Tomek on 2016-04-16.
 */
public class FortsConstructionAllowedFtr extends Feature {

	@Override
	public void setParamsCustomValues() {

	}

	@Override
	public void executeUpdates() throws Exception {

		// ## Enable forts constructions ##
		_DescrCampaignDb = fileEntityFactory.getFile(DescrCampaignDb.class);

		//<destroy_empty_forts bool="false"/>
		_DescrCampaignDb.setAttribute("/root/settlement/can_build_forts", "bool" ,true);
		_DescrCampaignDb.setAttribute("/root/settlement/destroy_empty_forts", "bool" ,true);
		_DescrCampaignDb.setAttribute("/root/misc/allow_resource_forts", "bool" ,true);
		_DescrCampaignDb.setAttribute("/root/misc/allow_enemy_forts", "bool" ,true);

		_DescrCampaignDb.setAttribute("/root/misc/fort_devastation_distance", "uint", 0);
		_DescrCampaignDb.setAttribute("/root/misc/fort_devastation_modifier", "float", 0.0);

		registerForUpdate(_DescrCampaignDb);


		// ## Boost fort cost ##
		_DescrCultures = fileEntityFactory.getFile(DescrCultures.class);
		_DescrCultures.getLines().updateAllRegexLines("^\\s*fort_cost\\s+\\d+", "fort_cost\t\t\t6000");
		registerForUpdate(_DescrCultures);
	}

	protected DescrCampaignDb _DescrCampaignDb;
	protected DescrCultures _DescrCultures;

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.fromString("6b76d04c-4e6f-4c33-9977-94854146debe");

	public FortsConstructionAllowedFtr() {

		super("Forts Construction allowed");

	}
}
