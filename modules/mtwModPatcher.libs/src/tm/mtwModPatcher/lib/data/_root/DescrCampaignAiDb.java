package tm.mtwModPatcher.lib.data._root;

import lombok.val;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import tm.common.Ctm;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.XmlFileEntity;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class DescrCampaignAiDb extends XmlFileEntity {

	public void replaceGlobalParameter(Document replaceWithDoc) throws XPathExpressionException {
		replaceGlobalParameter("trusted_ally_fs_threshold", replaceWithDoc);
		replaceGlobalParameter("trusted_ally_target_fs_threshold", replaceWithDoc);
		replaceGlobalParameter("trusted_ally_target_human_fs_threshold", replaceWithDoc);
		replaceGlobalParameter("trusted_ally_gs_threshold", replaceWithDoc);
		replaceGlobalParameter("trusted_ally_target_gs_threshold", replaceWithDoc);
		replaceGlobalParameter("trusted_ally_enemy_auto_war", replaceWithDoc);

		replaceGlobalParameter("use_cheat_overrides", replaceWithDoc);
		replaceGlobalParameter("invade_priority_fs_modifier", replaceWithDoc);
		replaceGlobalParameter("invade_priority_gs_modifier", replaceWithDoc);
		replaceGlobalParameter("invade_priority_assistance_offset", replaceWithDoc);
		replaceGlobalParameter("invade_priority_min", replaceWithDoc);
		replaceGlobalParameter("invade_priority_max", replaceWithDoc);
	}

	public void replaceGlobalParameter(String paramName, Document replaceWithDoc) throws XPathExpressionException {
		val nodeXpath = Ctm.format("/root/{0}",paramName);

		XPath xPath =  XPathFactory.newInstance().newXPath();
		val xPathCpl = xPath.compile(nodeXpath);

		val thisNode = (Node) xPathCpl.evaluate(xDoc, XPathConstants.NODE);
		val copyNode = (Node) xPathCpl.evaluate(replaceWithDoc, XPathConstants.NODE);

		val root = getNode("/root");

		if(thisNode != null) root.removeChild(thisNode);

		val importedNode = xDoc.importNode(copyNode.cloneNode(true), true);
		root.appendChild(importedNode);
	}

	public void replaceOrAddFactionAiSet(String aiLabelName, Node factionAiNewXmlNode) throws XPathExpressionException {
		assertThat(factionAiNewXmlNode).isNotNull();

		// ## Update aiLabel in new Node
		factionAiNewXmlNode.getAttributes().getNamedItem("name").setTextContent(aiLabelName);

		// ### FIND NODE ###
		val node = getFactionAiNode(aiLabelName, xDoc);
		val root = getNode("/root");

		if(node == null) {

			try {
				val importedNode = xDoc.importNode(factionAiNewXmlNode.cloneNode(true), true);
				root.appendChild(importedNode);
			}
			catch (Exception ex) {
				throw new PatcherLibBaseEx("Error appending DescrCampaignAiDb faction_ai_label name="+aiLabelName , ex);
			}
		}
		else {

			try {
				root.removeChild(node);
				val importedNode = xDoc.importNode(factionAiNewXmlNode.cloneNode(true), true);
				root.appendChild(importedNode);

			}
			catch (Exception ex) {
				throw new PatcherLibBaseEx("Error replacing DescrCampaignAiDb node faction_ai_label name="+aiLabelName, ex);
			}
		}

	}

	public void removeFactionAiSet(String aiLabelName) throws XPathExpressionException {
		// ### FIND NODE ###
		val node = getFactionAiNode(aiLabelName, xDoc);
		if(node == null) throw new PatcherLibBaseEx(Ctm.format("Faction AI Label {0} not found",aiLabelName));

		val root = loadNode("/root");

		root.removeChild(node);
	}

	public static Node getFactionAiNode(String aiLabelName, Document descrCampaignAiDbDoc) throws XPathExpressionException {
		// "/root/factor_modifiers/factor[@name='SIF_MINING']/pip_modifier"
		val nodeXpath = Ctm.format("/root/faction_ai_label[@name='{0}']",aiLabelName);

		XPath xPath =  XPathFactory.newInstance().newXPath();

		Node node = (Node) xPath.compile(nodeXpath).evaluate(descrCampaignAiDbDoc, XPathConstants.NODE);

		return node;
	}

	public void purgeRootContent() throws XPathExpressionException {
		val root = getNode("/root");

		Node childNode;
		do {
			childNode = root.getFirstChild();
			root.removeChild(childNode);
			childNode = root.getFirstChild();
		} while (childNode != null);

	}

	public DescrCampaignAiDb() {
		super("data\\descr_campaign_ai_db.xml");
	}
}
