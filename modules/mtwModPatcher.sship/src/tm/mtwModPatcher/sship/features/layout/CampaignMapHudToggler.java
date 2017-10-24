package tm.mtwModPatcher.sship.features.layout;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.InputStreamProvider;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.LinesProcessor;
import tm.mtwModPatcher.lib.data.world.maps.campaign.CampaignScript;
import tm.mtwModPatcher.lib.engines.ConfigurationSettings;

import java.util.UUID;

/**
 * Created by tomek on 22.05.2017.
 */
public class CampaignMapHudToggler extends Feature {
	@Override
	public void setParamsCustomValues() {

	}

	@Override
	public void executeUpdates() throws Exception {
		campaignScript = getFileRegisterForUpdated(CampaignScript.class);

		val rootPath = getResourcesPath();

		val scriptLines = LinesProcessor.load(rootPath+"\\CampaignScript-snippet.txt", inputStreamProvider);
		campaignScript.insertAtEndOfFile(scriptLines.getLines());

	}

	private String getResourcesPath() {
		return ConfigurationSettings.VariousDataPath() + "\\CampaignMapHudToggler";
	}

	private CampaignScript campaignScript;

	private InputStreamProvider inputStreamProvider;

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.fromString("1924b3df-1253-492d-95c1-78c314e02731");

	public CampaignMapHudToggler(InputStreamProvider inputStreamProvider) {
		super("Campaign Map Hud Toggler [Userpro]");

		addCategory("Layout");

		setDescriptionShort("Campaign Map Hud Toggler - press Scroll Lock to hide/show campaign map hud");
		setDescriptionUrl("http://tmsship.wikidot.com/campaign-map-hud-toggler-userpro");

		// http://www.twcenter.net/forums/showthread.php?391735-Toggle-Campaign-Map-Hud

		this.inputStreamProvider = inputStreamProvider;
	}
}
