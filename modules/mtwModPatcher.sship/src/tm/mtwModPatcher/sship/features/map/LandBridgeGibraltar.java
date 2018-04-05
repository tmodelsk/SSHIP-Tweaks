package tm.mtwModPatcher.sship.features.map;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.OverrideCopyTask;
import tm.mtwModPatcher.lib.common.core.features.OverrideDeleteFilesTask;
import tm.mtwModPatcher.lib.engines.ConfigurationSettings;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/** Created by Tomek on 2016-11-11. */
public class LandBridgeGibraltar extends Feature {

	@Override
	public void setParamsCustomValues() {

	}

	@Override
	public void executeUpdates() throws Exception {	}

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.fromString("1448773b-9a51-419d-8733-6f4364653580");

	@Override
	public Set<UUID> getConflictingFeatures() {
		val conflicts = new HashSet<UUID>();

		conflicts.add(LandBridgeGibraltarLaManche.Id);

		return conflicts;
	}

	public LandBridgeGibraltar() {
		super("Land Bridge - Gibraltar");


		setDescriptionShort("Land Bridge - Gibraltar");
		setDescriptionUrl("http://tmsship.wikidot.com/land-bridges");

		addOverrideTask(new OverrideCopyTask("GibraltarLandBridge"));

		boolean isDevMachine=false;
		isDevMachine = ConfigurationSettings.isDevEnvironment();

		requestForMapRemoval();
	}
}
