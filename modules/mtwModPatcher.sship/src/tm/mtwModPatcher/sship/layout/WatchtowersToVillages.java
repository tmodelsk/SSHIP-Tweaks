package tm.mtwModPatcher.sship.layout;

import lombok.val;
import tm.common.Tuple2;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.OverrideCopyTask;
import tm.mtwModPatcher.lib.data.DescrCultures;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by tomek on 25.04.2017.
 */
public class WatchtowersToVillages extends Feature {

	@Override
	// Empty
	public void executeUpdates() throws Exception {
		descrCultures = getFileRegisterForUpdated(DescrCultures.class);

		val lines =  descrCultures.getLines();

		val replaceList = new ArrayList<Tuple2<String, String>>();
		replaceList.add(new Tuple2<>("SE_watchtower.CAS", "southern_european_village.CAS"));
		replaceList.add(new Tuple2<>("NE_watchtower.CAS", "northern_european_village.CAS"));
		replaceList.add(new Tuple2<>("ME_watchtower.CAS", "middle_eastern_village.CAS"));

		for (val replacer : replaceList)
			lines.replaceInAllLines(replacer.getItem1(), replacer.getItem2());
	}

	private DescrCultures descrCultures;

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.randomUUID();

	public WatchtowersToVillages() {
		setName("Watchtowers To Villages [+Marius+]");

		addCategory("Various");

		setDescriptionShort("Watchtowers To Villages by [+Marius+]");
		setDescriptionUrl("http://tmsship.wikidot.com/watchtowers-to-villages");

		addOverrideTask(new OverrideCopyTask("WatchtowersToVillagesVanilaForts"));
	}
}
