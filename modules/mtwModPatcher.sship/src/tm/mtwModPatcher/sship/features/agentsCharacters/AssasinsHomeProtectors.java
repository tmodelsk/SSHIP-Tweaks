package tm.mtwModPatcher.sship.features.agentsCharacters;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.data._root.DescrCampaignDb;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Tomek on 2016-04-22.
 */
public class AssasinsHomeProtectors extends Feature {

	protected DescrCampaignDb descrCampaignDb;

	@Override
	public void setParamsCustomValues() {

	}

	@Override
	public void executeUpdates() throws Exception {

		descrCampaignDb = fileEntityFactory.getFile(DescrCampaignDb.class);
		registerForUpdate(descrCampaignDb);

		// para assassinate_attack_modifier = 2.0 i  assassinate_own_region_modifier = 0.15 dziala OK

		descrCampaignDb.setAttribute("/root/agents/assassinate_attack_modifier", 1.2);			// Org : 0.8
		descrCampaignDb.setAttribute("/root/agents/assassinate_defence_modifier", 0.8);		// Org : 0.8
		descrCampaignDb.setAttribute("/root/agents/assassinate_own_region_modifier", 0.5);	// Org : 0.8

	}

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.fromString("155e45e9-7c25-471f-9822-6cc06ef3d1fa");

	public AssasinsHomeProtectors() {
		super("Assasins Home Protectors");

		addCategory("Agents");

		setDescriptionShort("Assasins kill easier in homeland territory and more difficult in enemy territory");
		setDescriptionUrl("http://tmsship.wikidot.com/assasins-home-protectors");
	}

	@Override
	public Set<UUID> getConflictingFeatures() {
		val conflicts = new HashSet<UUID>();

		conflicts.add(AssasinsRemoved.Id);

		return conflicts;
	}
}
