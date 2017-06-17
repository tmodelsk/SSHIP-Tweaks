package tm.mtwModPatcher.lib.data._root;

import tm.common.Ctm;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.XmlFileEntity;

/** Could be use as base class 'file' for AI Sets */
public class BattleConfig extends XmlFileEntity {

	public BattleConfig() {
		super("data\\battle_config.xml");
	}

	public BattleConfig(int aiSet) {
		super(Ctm.msgFormat("data\\AiSet{0}\\battle_config.xml",aiSet));
	}

	public BattleConfig(String filePath) {
		super(filePath);
	}
}
