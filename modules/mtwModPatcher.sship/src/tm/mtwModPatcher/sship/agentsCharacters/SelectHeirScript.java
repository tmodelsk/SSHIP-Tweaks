package tm.mtwModPatcher.sship.agentsCharacters;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.OverrideCopyTask;
import tm.mtwModPatcher.lib.common.core.features.OverrideDeleteFilesTask;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.InputStreamProvider;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.LinesProcessor;
import tm.mtwModPatcher.lib.engines.ConfigurationSettings;
import tm.mtwModPatcher.lib.data.ExportDescrCharacterTraits;
import tm.mtwModPatcher.lib.data.text.ExportVnvs;
import tm.mtwModPatcher.lib.data.text.HistoricEvents;
import tm.mtwModPatcher.lib.data.world.maps.campaign.CampaignScript;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by tomek on 22.04.2017.
 */
public class SelectHeirScript extends Feature {


	@Override
	public void executeUpdates() throws Exception {
		exportDescrCharacterTraits = getFileRegisterForUpdated(ExportDescrCharacterTraits.class);
		historicEvents = getFileRegisterForUpdated(HistoricEvents.class);
		exportVnvs = getFileRegisterForUpdated(ExportVnvs.class);
		campaignScript = getFileRegisterForUpdated(CampaignScript.class);

		processExportDescrCharacterTraits();
		processHistoricEvents();
		processExportVnVs();
		processCampaignScript();
	}

	private void processCampaignScript() throws IOException {
		val rootPath = getResourcesPath();

		val scriptLines = LinesProcessor.load(rootPath+"\\ADD_IN_campaign_script.txt", inputStreamProvider);
		campaignScript.insertAtEndOfFile(scriptLines.getLines());
	}

	private void processExportVnVs() throws IOException {
		val rootPath = getResourcesPath();
		val expVnVsLines = LinesProcessor.load(rootPath + "\\ADD_IN_export_vnvs.txt", inputStreamProvider);

		exportVnvs.insertAtStartOfFile(expVnVsLines.getLines());
	}

	private void processHistoricEvents() throws IOException {
		val rootPath = getResourcesPath();
		val histEventsLines = LinesProcessor.load(rootPath + "\\ADD_IN_historic_events.txt", inputStreamProvider);

		historicEvents.insertAtStartOfFile(histEventsLines.getLines());
	}

	private void processExportDescrCharacterTraits() throws IOException {
		val rootPath = getResourcesPath();

		val traitLines = LinesProcessor.load(rootPath + "\\ADD_IN_export_descr_character_traits.trait.txt", inputStreamProvider);
		val triggerLines = LinesProcessor.load(rootPath + "\\ADD_IN_export_descr_character_traits.trigger.txt", inputStreamProvider);

		// ### Traits ###
		LinesProcessor lines = exportDescrCharacterTraits.getLines();
		int index = lines.findFirstByRexexLines("^;=+ VNV TRAITS START HERE ","^;=+;");
		if(index < 0) throw new PatcherLibBaseEx("Unable to find start of traits");
		index+=2;

		lines.insertAt(index, traitLines.getLines());

		// ### Triggers ###
		lines.insertAt( lines.count() , triggerLines.getLines() );
	}

	private String getResourcesPath() {
		return ConfigurationSettings.VariousDataPath() + "\\SelectHeirScript";
	}

	private ExportDescrCharacterTraits exportDescrCharacterTraits;
	private HistoricEvents historicEvents;
	private ExportVnvs exportVnvs;
	private CampaignScript campaignScript;


	private InputStreamProvider inputStreamProvider;

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.randomUUID();

	public SelectHeirScript(InputStreamProvider inputStreamProvider) {
		setName("Select Heir Script [Miguel_80]");

		addCategory("Campaign");
		addCategory("Agents");

		setDescriptionShort("Tweak - script for selecting next Heir");
		setDescriptionUrl("http://tmsship.wikidot.com/select-heir-script-miguel-80");

		addOverrideTask(new OverrideCopyTask("SelectHeirScript"));
		addOverrideTask(new OverrideDeleteFilesTask("data\\text\\historic_events.txt.strings.bin"));
		addOverrideTask(new OverrideDeleteFilesTask("data\\text\\export_VnVs.txt.strings.bin"));

		this.inputStreamProvider = inputStreamProvider;
	}
}
