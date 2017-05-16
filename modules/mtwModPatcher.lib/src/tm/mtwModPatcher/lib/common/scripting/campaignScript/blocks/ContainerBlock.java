package tm.mtwModPatcher.lib.common.scripting.campaignScript.blocks;

import tm.mtwModPatcher.lib.common.scripting.campaignScript.core.ScriptElement;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.core.ScriptLines;

/** Container block for code, not addin any start/end code  */
public class ContainerBlock extends ScriptBlock {
	private ScriptLines _Body = new ScriptLines();

	@Override
	public ScriptLines getScriptBlock() {
		return _Body;
	}

	public void add(String line) {
		_Body.line(line);
	}

	public void add(ScriptBlock block) {
		_Body.add(block.getScriptBlock(), 0);
	}

	public void add(ScriptElement element) {
		_Body.line(element.getString());
	}
}
