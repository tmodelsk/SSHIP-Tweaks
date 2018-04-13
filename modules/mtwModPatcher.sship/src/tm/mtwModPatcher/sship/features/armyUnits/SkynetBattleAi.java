package tm.mtwModPatcher.sship.features.armyUnits;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.OverrideCopyTask;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.InputStreamProvider;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.LineNotFoundEx;
import tm.mtwModPatcher.lib.data.world.maps.campaign.CampaignScript;
import tm.mtwModPatcher.lib.engines.ConfigurationSettings;

import java.util.ArrayList;
import java.util.UUID;

/**  */
public class SkynetBattleAi extends Feature {
	@Override
	public void setParamsCustomValues() {

	}

	@Override
	public void executeUpdates() throws Exception {
		campaignScript = getFileRegisterForUpdated(CampaignScript.class);
		val lines = campaignScript.getLines();

		// Find & Delete old Battle AI
		int aiStartIndex = 0, aiEndIndex=0;

		val g5AiStartRegexes = new ArrayList<String>();
		g5AiStartRegexes.add("^\\s*;========================================================");
		g5AiStartRegexes.add("^\\s*;===================== G5 BAI STUFF =====================");
		g5AiStartRegexes.add("^\\s*;========================================================");

		aiStartIndex = lines.findFirstLineByLinePath(g5AiStartRegexes) - 2 ;	// incuding first line
		if(aiStartIndex < 0) throw new LineNotFoundEx("G5 BAI STUFF starting tag not found!");

		val g5AiEndRegexes=new ArrayList<String>();

		g5AiEndRegexes.add("^\\s*;=======================================================;");
		g5AiEndRegexes.add("^\\s*;================= ADDITIONAL SCRIPTS ==================;");
		g5AiEndRegexes.add("^\\s*;=======================================================;");

		aiEndIndex = lines.findFirstLineByLinePath(g5AiEndRegexes, aiStartIndex) - 3;	// excluding first line

		// ### DELETE G5 AI ###
		lines.removeRange(aiStartIndex, aiEndIndex);

		// ### INSERT Skynet AI ###
		val rootPath = getResourcesPath();
		//val scriptLines = LinesProcessor.load(rootPath+"\\general-campaign-script-snippet.txt", inputStreamProvider);
		//val scriptLines = LinesProcessor.load(rootPath+"\\campaign-campaign-script-snippet.txt", inputStreamProvider);
		//lines.insertAt(aiStartIndex, scriptLines.lines());

	}

	private String getResourcesPath() {
		return ConfigurationSettings.OverrideRootPath() + "\\SkynetBattleAI-Various";
	}

	public SkynetBattleAi(InputStreamProvider inputStreamProvider) {
		super("Skynet Battle AI [z3n]");

		addOverrideTask(new OverrideCopyTask("SkynetBattleAI"));
		//addOverrideTask(new OverrideDeleteFilesTask("data\\scripts\\show_me\\"));

		this.inputStreamProvider = inputStreamProvider;
	}

	private CampaignScript campaignScript;
	private InputStreamProvider inputStreamProvider;

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.fromString("9b04c959-19ea-4690-9bf5-79368afb6e71");
}
