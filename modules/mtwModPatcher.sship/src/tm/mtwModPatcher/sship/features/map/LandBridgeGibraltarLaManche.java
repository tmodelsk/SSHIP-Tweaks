package tm.mtwModPatcher.sship.features.map;

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
public class LandBridgeGibraltarLaManche extends Feature {

	@Override
	public void setParamsCustomValues() {

	}

	@Override
	public void executeUpdates() {	}


	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.fromString("6747cd88-4593-434d-afe2-866632b05336");

	@Override
	public Set<UUID> getConflictingFeatures() {
		val conflicts = new HashSet<UUID>();

		conflicts.add(LandBridgeGibraltar.Id);

		return conflicts;
	}

	public LandBridgeGibraltarLaManche() {
		super("Land Bridges: Gibraltar & La Manche");

		setDescriptionShort("Land Bridges: Gibraltar & La Manche");
		setDescriptionUrl("http://tmsship.wikidot.com/land-bridges");

		addOverrideTask(new OverrideCopyTask("GiblartarLaMancheLandBridge"));

		boolean isDevMachine = ConfigurationSettings.isDevEnvironment();
		if(!isDevMachine)
			addOverrideTask(new OverrideDeleteFilesTask("data\\world\\maps\\base\\map.rwm"));
	}
}
