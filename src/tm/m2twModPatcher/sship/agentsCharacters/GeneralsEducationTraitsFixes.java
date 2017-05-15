package tm.m2twModPatcher.sship.agentsCharacters;

import tm.m2twModPatcher.lib.common.core.features.Feature;
import tm.m2twModPatcher.lib.common.core.features.OverrideCopyTask;
import tm.m2twModPatcher.lib.common.core.features.OverrideDeleteFilesTask;

import java.util.UUID;

/**
 * Created by Tomek on 2016-11-16.
 */
public class GeneralsEducationTraitsFixes extends Feature {

	@Override
	public UUID getId() {
		return Id;
	}

	@Override
	public void executeUpdates() throws Exception {	}

	public static UUID Id = UUID.randomUUID();

	public GeneralsEducationTraitsFixes() {
		super("Generals' Traits' Common Sense fix [JoC][XXZit]");

		setDescriptionShort("Generals' Traits' Common Sense fix & Education of Generals v. 11.19 + XXZit hotfix - by Jurand of Cracov");
		setDescriptionUrl("http://tmsship.wikidot.com/generals-education-traits-fixes-joc");

		addOverrideTask(new OverrideCopyTask("JoC-GTCS-EOG"));
		addOverrideTask(new OverrideDeleteFilesTask("data\\text\\export_VnVs.txt.strings.bin"));
	}
}
