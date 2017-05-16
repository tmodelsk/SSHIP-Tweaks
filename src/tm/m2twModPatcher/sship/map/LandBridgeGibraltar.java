package tm.m2twModPatcher.sship.map;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.OverrideCopyTask;
import tm.mtwModPatcher.lib.common.core.features.OverrideDeleteFilesTask;
import tm.mtwModPatcher.lib.engines.ConfigurationSettings;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Tomek on 2016-11-11.
 */
public class LandBridgeGibraltar extends Feature {

	@Override
	public UUID getId() {
		return Id;
	}

	@Override
	public void executeUpdates() throws Exception {	}

	public static UUID Id = UUID.randomUUID();

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
		isDevMachine = ConfigurationSettings.isDevMachine();

		if(!isDevMachine)
			addOverrideTask(new OverrideDeleteFilesTask("data\\world\\maps\\base\\map.rwm"));
	}
}
