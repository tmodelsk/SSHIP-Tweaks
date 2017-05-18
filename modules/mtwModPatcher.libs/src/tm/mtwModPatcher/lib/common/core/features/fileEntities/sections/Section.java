package tm.mtwModPatcher.lib.common.core.features.fileEntities.sections;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Created by Tomek on 2016-04-18.
 */
public abstract class Section {

	public abstract void SaveChanges(BufferedWriter bw) throws IOException;

	public String Name;

	public Section(String name) {
		Name = name;
	}

	public Section() {
	}
}
