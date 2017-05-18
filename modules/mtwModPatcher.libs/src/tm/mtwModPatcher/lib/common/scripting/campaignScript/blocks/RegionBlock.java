package tm.mtwModPatcher.lib.common.scripting.campaignScript.blocks;

import tm.mtwModPatcher.lib.common.scripting.campaignScript.core.ScriptElement;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.core.ScriptLines;

/**
 * Created by Tomek on 2016-11-17.
 */
public class RegionBlock extends ScriptBlock {

	private String _Name;
	private boolean _IsCollapbsible = false;
	private ScriptLines _Body = new ScriptLines();

	@Override
	public ScriptLines getScriptBlock() {

		ScriptLines lines = new ScriptLines();

		String collapsiblePrefix = "", collapsibleSuffix="";
		if(_IsCollapbsible) {
			collapsiblePrefix = "if TrueCondition ";
			collapsibleSuffix = "end_if           ";
		}

		lines.line(collapsiblePrefix + ";#region  START: "+_Name);
		lines.add(_Body, 0);
		lines.line(collapsibleSuffix + ";#endregion END: "+_Name);

		return lines;
	}

	public void line(String line) {
		_Body.line(line);
	}

	public void add(ScriptBlock block) {
		_Body.add(block.getScriptBlock(), 0);
	}

	public void add(ScriptElement element) {
		_Body.line(element.getString());
	}

	public RegionBlock(String name) {
		this._Name = name;
	}
	public RegionBlock(String name, boolean isCollapbsible) {
		this._Name = name;
		_IsCollapbsible = isCollapbsible;
	}

}
