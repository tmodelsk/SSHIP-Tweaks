package tm.mtwModPatcher.lib.data._root;

import javafx.util.Pair;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.LinesProcessor;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.LinesProcessorFileEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tomek on 2016-07-17.
 */
public class DescrProjectiles extends LinesProcessorFileEntity {

	public void setAttributeValue(String projectilneName, String attributeName, String valueStr) throws PatcherLibBaseEx {
		LinesProcessor lines = _Lines;
		Pair<Integer,Integer> projLines = loadProjectileLines(projectilneName);

		int index = lines.findFirstRegexLine("^" + attributeName + "\\s+" , projLines.getKey() , projLines.getValue());

		if(index < 0) throw new PatcherLibBaseEx("Attribute '" + attributeName + "' not found in projectile '" + projectilneName + "' , insert not implemented!");

		lines.replaceLine(index , attributeName + "\t\t" + valueStr);
	}

	public List<String> getAllProjectileNames() throws PatcherLibBaseEx {
		List<String> result = new ArrayList<>();

		Pattern p = Pattern.compile("^projectile\\s+(\\w+)\\s*$");

		int index = 0;

		while (index >= 0) {

			index = _Lines.findFirstRegexLine(p, index+1);

			if(index > 0) {
				// ## Parse name ##
				String line = _Lines.getLine(index);
				Matcher m = p.matcher(line);
				if(m.find())	result.add(m.group(1));
				else throw new PatcherLibBaseEx("Unable to parse projectile name in add : "+line);
			}
		}

		return result;
	}

	protected Pair<Integer,Integer> loadProjectileLines(String projectileName) throws PatcherLibBaseEx {

		LinesProcessor lines = _Lines;

		int start = lines.findExpFirstRegexLine("^projectile\\s+" + projectileName + "\\s*$");

		int end = lines.findFirstRegexLine("^projectile\\s+\\w+", start+1);

		if(end < 0) end = lines.count();

		return new Pair<>(start, end);
	}

	public DescrProjectiles() {
		super("data\\descr_projectile.txt");
	}
}
