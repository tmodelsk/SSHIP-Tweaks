package tm.mtwModPatcher.lib.data._root;

import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.XmlFileEntity;

import javax.xml.xpath.XPathExpressionException;

/**
 * Created by Tomek on 2016-04-15.
 */
public class DescrSettlementMechanics extends XmlFileEntity {

	public void SetCityPopulationCastleData(int level, int min, int base, int upgrade, int max) throws PatcherLibBaseEx, XPathExpressionException {

		String levelStr;

		switch (level) {
			case 1:
				levelStr = "moot_and_bailey";
				break;
			case 2:
				levelStr = "wooden_castle";
				break;
			case 3:
				levelStr = "castle";
				break;
			case 4:
				levelStr = "fortress";
				break;
			case 5:
				levelStr = "citadel";
				break;
			default:
				throw new PatcherLibBaseEx("Castle level "+ level+" not recognized");
		}

		SetCityPopulationData(levelStr, min, base, upgrade, max);
	}

	public void SetCityPopulationData(String levelStr, int min, int base, int upgrade, int max) throws XPathExpressionException, PatcherLibBaseEx {
		String xpath = "/root/population_levels/level[@name='" + levelStr + "']";

		setAttribute(xpath, "min", min);
		setAttribute(xpath, "base", base);
		setAttribute(xpath, "upgrade", upgrade);
		setAttribute(xpath, "max", max);
	}


	public DescrSettlementMechanics() {
		super("data\\descr_settlement_mechanics.xml");
	}

}
