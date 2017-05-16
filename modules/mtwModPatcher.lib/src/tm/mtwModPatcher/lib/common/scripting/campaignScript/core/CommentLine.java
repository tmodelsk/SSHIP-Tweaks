package tm.mtwModPatcher.lib.common.scripting.campaignScript.core;

/**
 * Created by Tomek on 2016-12-04.
 */
public class CommentLine extends ScriptElement {

	private String _CommentString;

	@Override
	public String getString() {
		return ";-" + _CommentString ;
	}

	public CommentLine(String commentString) {
		this._CommentString = commentString;
	}
}
