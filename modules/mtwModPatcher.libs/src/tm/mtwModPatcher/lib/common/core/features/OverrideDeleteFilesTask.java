package tm.mtwModPatcher.lib.common.core.features;

import lombok.Getter;
import tm.common.collections.ArrayUniqueList;
import tm.common.collections.ListUnique;

import java.util.List;

/**  */
public class OverrideDeleteFilesTask extends OverrideTask {
	@Override
	public List<String> getAffectedFilesRelativePaths() {
		return Patchs;
	}


	@Getter
	private ListUnique<String> Patchs;

	public OverrideDeleteFilesTask(String singlePath) {
		Patchs = new ArrayUniqueList<>();
		Patchs.add(singlePath);
	}

	public OverrideDeleteFilesTask(List<String> patchs) {
		Patchs = new ArrayUniqueList<>(patchs);
	}

	public OverrideDeleteFilesTask() {
		Patchs = new ArrayUniqueList<>();
	}

	public static final OverrideDeleteFilesTask DELETE_MAP_RWM=new OverrideDeleteFilesTask("data\\world\\maps\\base\\map.rwm");
}
