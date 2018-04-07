package tm.mtwModPatcher.sship.features.agentsCharacters;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.ExportDescrBuilding;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AssasinsRemoved  extends Feature {
	private ExportDescrBuilding edb;

	@Override
	public void executeUpdates() throws Exception {
		edb = getFileRegisterForUpdated(ExportDescrBuilding.class);

		// Clear all assassins recruitments
		val assasinRecuitRegex = "^\\s*agent\\s+assassin\\s+.*";
		val assasinLimitRegex = "^\\s*agent_limit\\s+assassin\\s+.*";

		edb.getLines().removeAllRegexLines(assasinRecuitRegex);
		edb.getLines().removeAllRegexLines(assasinLimitRegex);
	}

	@Override
	public void setParamsCustomValues() { }

	public AssasinsRemoved() {
		super("Assasins removed");

		addCategory(CATEGORY_AGENTS);

		setDescriptionShort("Assasins are completly removed");
		setDescriptionUrl("http://tmsship.wikidot.com/assasins-removed");
	}

	@Override
	public Set<UUID> getConflictingFeatures() {
		val conflicts = new HashSet<UUID>();

		conflicts.add(AssasinsNumbersLimited.Id);
		conflicts.add(AssasinsHomeProtectors.Id);
		conflicts.add(NoDreadOnAssasinations.Id);

		return conflicts;
	}

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.fromString("fa2bb9dd-67c1-44ba-87cb-484539291eb4");
}
