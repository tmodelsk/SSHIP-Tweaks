package tm.mtwModPatcher.sship.features.armyUnits;

import lombok.val;
import tm.common.Ctm;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.data._root.WeatherDb;

import javax.xml.xpath.XPathExpressionException;
import java.util.UUID;

/** Battle Snow Storm is nerfed, less fog  */
public class SnowStormNerfed extends Feature {

	@Override
	public void setParamsCustomValues() {

	}

	@Override
	public void executeUpdates() throws Exception {
		weatherDb = getFileRegisterForUpdated(WeatherDb.class);

		nerfFogInSnowPattern();
		nerfSnowStormProbability();
	}

	private void nerfSnowStormProbability() throws XPathExpressionException {
		val basePath = "/weather/seasons/season[@id='winter']/climates/climate[@id='{0}']/patterns/pattern[@id='snow']";

		weatherDb.updateAttributeByMultiplier(Ctm.format(basePath, "steppe"), "chance" , 0.5);
		weatherDb.updateAttributeByMultiplier(Ctm.format(basePath, "highland"), "chance" , 0.5);
		weatherDb.updateAttributeByMultiplier(Ctm.format(basePath, "alpine"), "chance" , 0.5);
		weatherDb.updateAttributeByMultiplier(Ctm.format(basePath, "temperate_deciduous_forest"), "chance" , 0.5);
		weatherDb.updateAttributeByMultiplier(Ctm.format(basePath, "temperate_coniferous_forest"), "chance" , 0.5);
	}

	private void nerfFogInSnowPattern() throws XPathExpressionException {
		 //val attribStr = weatherDb.getAttributeValueAsString("/weather/patterns/pattern[@id='snow']/periods/period[1]/events/event[2]/fog", "max");

		for (int periodNo = 1; periodNo <= 6; periodNo++) {
			for (int eventNo = 1; eventNo <= 3; eventNo++) {
				val path = Ctm.format("/weather/patterns/pattern[@id='snow']/periods/period[{0}]/events/event[{1}]/fog" ,
						periodNo, eventNo);

				weatherDb.updateAttributeByMultiplier(path,"max", 0.5);
			}
		}
	}

	private WeatherDb weatherDb;

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.fromString("a4406c12-0f41-427d-9d07-6ae4969266c4");

	public SnowStormNerfed() {
		setName("Snow Storm Nerfed");

		addCategory("Various");
		addCategory("Battle");

		setDescriptionShort("Snow Storm has smaller fog & overall probability");
		setDescriptionUrl("http://tmsship.wikidot.com/snow-storm-nerfed");
	}
}
