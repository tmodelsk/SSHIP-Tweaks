package tm.mtwModPatcher.sship.features.agentsCharacters;

import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.OverrideCopyTask;
import tm.mtwModPatcher.lib.common.core.features.OverrideDeleteFilesTask;

import java.util.UUID;

/**
 * Created by Tomek on 2016-11-16.
 */
public class GeneralsEducationTraitsFixes extends Feature {

	@Override
	public void setParamsCustomValues() {

	}

	@Override
	public void executeUpdates() throws Exception {	}

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.fromString("b1567f5d-5eb3-4699-82c5-58e77998f739");

	public GeneralsEducationTraitsFixes() {
		super("Generals' Traits' Common Sense fix [JoC][XXZit]");

		setDescriptionShort("Generals' Traits' Common Sense fix & Education of Generals v. 11.19 + XXZit hotfix - by Jurand of Cracov");
		setDescriptionUrl("http://tmsship.wikidot.com/generals-education-traits-fixes-joc");

		addOverrideTask(new OverrideCopyTask("JoC-GTCS-EOG"));
		addOverrideTask(new OverrideDeleteFilesTask("data\\text\\export_VnVs.txt.strings.bin"));
	}
}
