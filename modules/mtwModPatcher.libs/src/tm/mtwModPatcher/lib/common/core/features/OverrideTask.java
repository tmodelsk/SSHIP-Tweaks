package tm.mtwModPatcher.lib.common.core.features;

import java.io.IOException;
import java.util.List;

/**
 * Created by Tomek on 2016-04-11.
 */
public abstract class OverrideTask extends TaskBase {

    public abstract List<String> getAffectedFilesRelativePaths() throws IOException;

    public String RootPath;
}
