package tm.mtwModPatcher.lib.data.world.maps.campaign;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.LineNotFoundEx;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.LinesProcessorFileEntity;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.blocks.ScriptBlock;

import java.util.ArrayList;
import java.util.List;

/** data\world\maps\campaign\imperial_campaign\campaign_script.txt */
public class CampaignScript extends LinesProcessorFileEntity {

	public int getLastInsertLineForMonitors() {
		int index;

		try {
			index = getLines().findExpFirstRegexLine("^wait_monitors");
		}
		catch (LineNotFoundEx lineNotFoundEx) {
			throw new PatcherLibBaseEx("Unable to determine wait_monitors tag (end of file) in campaign_script.txt",lineNotFoundEx);
		}

		return index;
	}

	public void insertAtEndOfFile(ScriptBlock scriptBlock) throws PatcherLibBaseEx {

		val lines = scriptBlock.getScriptBlock().getLines();

		insertAtEndOfFile(lines);
	}

	public void insertAtEndOfFile(List<String> lines) throws PatcherLibBaseEx {
		int index = getLastInsertLineForMonitors();

		lines.add("");	// add empty line

		getLines().insertAt(index, lines );
	}

	public CampaignScript()  {
		super("data\\world\\maps\\campaign\\imperial_campaign\\campaign_script.txt");

		FactionsAiEcIds = new ArrayList<>();


		FactionsAiEcIds.add(new FactionAiEcId(2,"venice"));
		FactionsAiEcIds.add(new FactionAiEcId(3,"sicily"));
		FactionsAiEcIds.add(new FactionAiEcId(4,"milan"));
		FactionsAiEcIds.add(new FactionAiEcId(5,"papal_states"));
		FactionsAiEcIds.add(new FactionAiEcId(6,"denmark"));
		FactionsAiEcIds.add(new FactionAiEcId(7,"egypt"));
		FactionsAiEcIds.add(new FactionAiEcId(8,"scotland"));
		FactionsAiEcIds.add(new FactionAiEcId(9,"cumans"));
		FactionsAiEcIds.add(new FactionAiEcId(10,"mongols"));
		FactionsAiEcIds.add(new FactionAiEcId(11,"turks"));
		FactionsAiEcIds.add(new FactionAiEcId(12,"france"));
		FactionsAiEcIds.add(new FactionAiEcId(13,"hre"));
		FactionsAiEcIds.add(new FactionAiEcId(14,"england"));
		FactionsAiEcIds.add(new FactionAiEcId(15,"portugal"));
		FactionsAiEcIds.add(new FactionAiEcId(16,"poland"));
		FactionsAiEcIds.add(new FactionAiEcId(17,"byzantium"));
		FactionsAiEcIds.add(new FactionAiEcId(18,"moors"));
		FactionsAiEcIds.add(new FactionAiEcId(19,"russia"));
		FactionsAiEcIds.add(new FactionAiEcId(20,"spain"));
		FactionsAiEcIds.add(new FactionAiEcId(21,"hungary"));
		FactionsAiEcIds.add(new FactionAiEcId(22,"aragon"));
		FactionsAiEcIds.add(new FactionAiEcId(23,"lithuania"));
		FactionsAiEcIds.add(new FactionAiEcId(24,"kievan_rus"));
		FactionsAiEcIds.add(new FactionAiEcId(25,"teutonic_order"));
		FactionsAiEcIds.add(new FactionAiEcId(26,"timurids"));
		FactionsAiEcIds.add(new FactionAiEcId(27,"norway"));
		FactionsAiEcIds.add(new FactionAiEcId(28,"jerusalem"));
		FactionsAiEcIds.add(new FactionAiEcId(29,"kwarezm"));
		FactionsAiEcIds.add(new FactionAiEcId(30,"pisa"));
		FactionsAiEcIds.add(new FactionAiEcId(31,"rum"));

	}

	public List<FactionAiEcId> FactionsAiEcIds;

	private static String nl = System.lineSeparator();
}
