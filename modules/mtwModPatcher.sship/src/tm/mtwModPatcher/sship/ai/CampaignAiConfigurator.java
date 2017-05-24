package tm.mtwModPatcher.sship.ai;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import tm.common.Ctm;
import tm.common.collections.ArrayUniqueList;
import tm.common.collections.ListUnique;
import tm.mtwModPatcher.lib.common.core.features.*;
import tm.mtwModPatcher.lib.common.core.features.params.ParamId;
import tm.mtwModPatcher.lib.common.core.features.params.ParamIdString;
import tm.mtwModPatcher.lib.common.entities.FactionInfo;
import tm.mtwModPatcher.lib.common.entities.FactionsDefs;
import tm.mtwModPatcher.lib.data.DescrCampaignAiDb;
import tm.mtwModPatcher.lib.data.DescrSMFactions;
import tm.mtwModPatcher.lib.data.world.maps.campaign.DescrStratSectioned;
import tm.mtwModPatcher.lib.engines.ConsoleLogger;
import tm.mtwModPatcher.lib.engines.FileEntityFactory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/** Base Class for Campaign AI Features, provide core functionalities */
public abstract class CampaignAiConfigurator extends Feature {

	protected Map<String, CampaignAiType> factionAiLabelsMap = new TreeMap<>();
	protected List<FactionInfo> factionInfos;

	@Getter @Setter
	protected CampaignAiType globalParametersAi = CampaignAiType.QUIETER_DEFAULT;

	@Override
	public ListUnique<ParamId> defineParamsIds() {
		val pars = new ArrayUniqueList<ParamId>();

		pars.add(new ParamIdString("GlobalParametersAi", "Global Parameters Ai",
				feature -> ((CampaignAiConfigurator)feature).getGlobalParametersAi().toString(),
				(feature, value) -> ((CampaignAiConfigurator)feature).setGlobalParametersAi(Enum.valueOf(CampaignAiType.class, value))));

		for (val factionSymbol : factionAiLabelsMap.keySet()) {

			pars.add( new ParamIdString(factionSymbol, factionSymbol,
					feature ->
						((CampaignAiConfigurator)feature).factionAiLabelsMap.get(factionSymbol).toString(),
					(feature, value) -> ((CampaignAiConfigurator)feature).updateFactionAiLabelMap(factionSymbol, value)
			));
		}

		return pars;
	}

	public void updateFactionAiLabelMap(String factionSymbol, String value) {
		CampaignAiType aiType = Enum.valueOf(CampaignAiType.class, value);
		factionAiLabelsMap.replace(factionSymbol, aiType);
	}

	@Override
	public void executeUpdates() throws Exception {
		descrStrat = getFileRegisterForUpdated(DescrStratSectioned.class);
		descrCampaignAiDb = getFileRegisterForUpdated(DescrCampaignAiDb.class);
		descrSMFactions = getFileRegisterForUpdated(DescrSMFactions.class);

		// clean up file
		descrCampaignAiDb.purgeRootContent();
		descrCampaignAiDb.replaceGlobalParameter(loadAiDoc(globalParametersAi));

		setFactionAiLabels();

		processCampaignAiFiles();
		//removeOldLabels();
	}

	protected void removeOldLabels() throws XPathExpressionException {
		for (val aiLabel : getFactionLabelsToRemove()) {
			descrCampaignAiDb.removeFactionAiSet(aiLabel);
		}
	}

	protected void processCampaignAiFiles() throws Exception {
		for(val aiLabel : factionAiLabelsMap.keySet()) {
			if(factionAiLabelsMap.containsKey(aiLabel)) {
				val campaignAiType = (CampaignAiType) factionAiLabelsMap.get(aiLabel);

				val factionInfoList = factionInfos.stream().filter(f -> f.Symbol.equals(aiLabel)).collect(Collectors.toList());
				Node aiNode;

				if(factionInfoList.size() > 0) {
					val factionInfo = factionInfoList.get(0);
					aiNode = getFactionAiNode(campaignAiType, factionInfo);
				}
				else {
					aiNode = getFactionAiNode(campaignAiType, aiLabel);
				}

				descrCampaignAiDb.replaceOrAddFactionAiSet(aiLabel, aiNode);
			}
		}
	}

	protected Node getFactionAiNode(CampaignAiType campaignAiType, FactionInfo faction) throws Exception {
		String aiLabel = null;

		switch (campaignAiType) {

			case SKYNET:
				aiLabel = "default";
				break;

			case BEEMUGCARL_DEFAULT:
				aiLabel = "default";
				break;
			case BEEMUGCARL_PAPACY:
				aiLabel = "papal_faction";
				break;

			case QUIETER_DEFAULT:
				aiLabel="default";
				break;
			case QUIETER_PAPAL:
				aiLabel="papal_faction";
				break;
			case QUIETER_CATHOLIC:
				aiLabel="catholic";
				break;
			case QUIETER_MONGOL:
				aiLabel="mongol";
				break;
			case QUIETER_ISLAM:
				aiLabel="islam";
				break;
			case QUIETER_PAGAN:
				aiLabel="pagan";
				break;
			case QUIETER_SLAVE:
				aiLabel="slave_faction";
				break;

			case SSHIP_DEFAULT:
				aiLabel="default";
				break;
			case SSHIP_PAPAL:
				aiLabel="papal_faction";
				break;
			case SSHIP_CATHOLIC:
				aiLabel="catholic";
				break;
			case SSHIP_MONGOL:
				aiLabel="mongols";
				break;
			case SSHIP_ISLAM:
				aiLabel="islam";
				break;
			case SSHIP_ORTHODOX:
				aiLabel="orthodox";
				break;
			case SSHIP_SLAVE:
				aiLabel="slave_faction";
				break;

			default:
				throw new PatcherNotSupportedEx(campaignAiType.toString());
		}

		if(aiLabel == null || aiLabel.isEmpty()) throw new PatcherNotSupportedEx("AI Label not found for "+campaignAiType);

		return getFactionAiNode(campaignAiType, aiLabel);
	}

	private Node getFactionAiNode(CampaignAiType campaignAiType, String aiLabel) throws Exception {
		assertThat(aiLabel).isNotEmpty();

		val xDoc = loadAiDoc(campaignAiType);
		val node = DescrCampaignAiDb.getFactionAiNode(aiLabel, xDoc);

		return node;
	}

	protected Document loadAiDoc(CampaignAiType campaignAiType) throws ParserConfigurationException, IOException, SAXException {
		String path = getResourcesPath() + "\\";

		switch (campaignAiType) {

			case SKYNET:
				path += "Skynet\\descr_campaign_ai_db.xml";
				break;

			case BEEMUGCARL_DEFAULT:
			case BEEMUGCARL_PAPACY:
				path += "BeeMugCarl\\descr_campaign_ai_db.xml";
				break;

			case QUIETER_DEFAULT:
			case QUIETER_PAPAL:
			case QUIETER_CATHOLIC:
			case QUIETER_MONGOL:
			case QUIETER_ISLAM:
			case QUIETER_PAGAN:
			case QUIETER_SLAVE:
				path += "Quieter\\descr_campaign_ai_db.xml";
				break;


			case SSHIP_DEFAULT:
			case SSHIP_PAPAL:
			case SSHIP_CATHOLIC:
			case SSHIP_MONGOL:
			case SSHIP_ISLAM:
			case SSHIP_ORTHODOX:
			case SSHIP_SLAVE:
				path += "SSHIP\\descr_campaign_ai_db.xml";
				break;

				default:
				throw new PatcherNotSupportedEx("descr_campaign_ai_db.xml not suported for " +campaignAiType.toString());
		}

		val xDoc = getAiDoc(path);
		if(xDoc == null) throw new PatcherLibBaseEx(Ctm.msgFormat("descr_campaign_ai_db.xml AI for {0} not found in {1}", campaignAiType, path));

		return xDoc;
	}

	protected Document getAiDoc(String path) throws ParserConfigurationException, IOException, SAXException {
		val is = resourcesProvider.getInputStream(path);

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();

		val xDoc = builder.parse(is);

		return xDoc;
	}

	protected abstract void initializeDefaults();

	protected void setFactionAiLabels() {
		for (val factionInfo : factionInfos) {
			descrStrat.setFactionAiLabel(factionInfo.Symbol, factionInfo.Symbol);
		}
	}

	protected Set<String> getFactionLabelsToRemove() {
		val res = new HashSet<String>();

		res.add("papal_faction");
		res.add("slave_faction");
		res.add("catholic");
		res.add("orthodox");
		res.add("islam");
		res.add("mongols");

		return res;
	}

	void updateNavalInvasionForAll(boolean isNavalInvasion) {
		for (val factionInfo : factionInfos) {
			updateNavalInvasion(factionInfo.Symbol, isNavalInvasion);
		}
	}

	void updateNavalInvasion(String factionSymbol, boolean isNavalInvasion) {
		descrSMFactions.updateNavalInvasion(factionSymbol, isNavalInvasion);
	}

	@NotNull
	private String getResourcesPath() {
		return resourcesProvider.getOverridesPath() + "\\CampaignAIs-Various";
	}

	protected DescrStratSectioned descrStrat;
	protected DescrCampaignAiDb descrCampaignAiDb;
	protected DescrSMFactions descrSMFactions;

	private ResourcesProvider resourcesProvider;

	protected static final String SKYNET_CAI = "Skynet";

	@Override
	public void initialize(FileEntityFactory fileEntityFactory, ConsoleLogger logger) {
		factionInfos = FactionsDefs.getFactionInfos();
		initializeDefaults();

		super.initialize(fileEntityFactory, logger);
	}

	@Override
	public UUID getId() {
		throw new NotImplementedException();
	}

	public CampaignAiConfigurator(String name, ResourcesProvider resourcesProvider) {
		super(name);
		this.resourcesProvider = resourcesProvider;
	}
}
