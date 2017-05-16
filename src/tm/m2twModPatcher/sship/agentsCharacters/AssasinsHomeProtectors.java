package tm.m2twModPatcher.sship.agentsCharacters;

import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.fileEntities.data.DescrCampaignDb;

import java.util.UUID;

/**
 * Created by Tomek on 2016-04-22.
 */
public class AssasinsHomeProtectors extends Feature {

	protected DescrCampaignDb _DescrCampaignDb;

	@Override
	public void executeUpdates() throws Exception {

		_DescrCampaignDb = fileEntityFactory.getFile(DescrCampaignDb.class);
		registerUpdatedFile(_DescrCampaignDb);

		// para assassinate_attack_modifier = 2.0 i  assassinate_own_region_modifier = 0.15 dziala OK

		_DescrCampaignDb.setAttribute("/root/agents/assassinate_attack_modifier", 1.2);			// Org : 0.8
		_DescrCampaignDb.setAttribute("/root/agents/assassinate_defence_modifier", 0.8);		// Org : 0.8
		_DescrCampaignDb.setAttribute("/root/agents/assassinate_own_region_modifier", 0.5);	// Org : 0.8

	}

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.randomUUID();

	public AssasinsHomeProtectors() {
		super("Assasins Home Protectors");

		addCategory("Agents");

		setDescriptionShort("Assasins kill easier in homeland territory and more difficult in enemy territory");
		setDescriptionUrl("http://tmsship.wikidot.com/assasins-home-protectors");
	}
}
