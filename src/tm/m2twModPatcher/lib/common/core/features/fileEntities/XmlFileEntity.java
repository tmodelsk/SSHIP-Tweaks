package tm.m2twModPatcher.lib.common.core.features.fileEntities;

import lombok.Getter;
import lombok.val;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import tm.common.Ctm;
import tm.m2twModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.m2twModPatcher.lib.fileEntities.data.common.Format;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;

/** FileEntity - XML File */
public abstract class XmlFileEntity extends FileEntity {

	@Getter
	protected Document xDoc;

	public void setValue(String nodeXpath, double valueDouble) throws XPathExpressionException, PatcherLibBaseEx {

		setValue(nodeXpath, Format.toString(valueDouble));
	}

	public void setValue(String nodeXpath, String valueStr) throws XPathExpressionException, PatcherLibBaseEx {

		XPath xPath =  XPathFactory.newInstance().newXPath();

		Node node = (Node) xPath.compile(nodeXpath).evaluate(xDoc, XPathConstants.NODE);

		if(node == null) throw new PatcherLibBaseEx("Xml XPath '"+ nodeXpath +"' not found");

		node.setTextContent(valueStr);
	}

	// Assumes that double is "float" attribute
	public void updateAttributeByMultiplier(String nodeXpath, double multiplier) throws XPathExpressionException {

		String attribute = "float";

		double miningFactor =  getAttributeValueAsDouble(nodeXpath,attribute);
		setAttribute(nodeXpath, attribute, miningFactor*multiplier);
	}

	public void updateAttributeByMultiplier(String nodeXpath, String attribute, double multiplier) throws XPathExpressionException {
		double miningFactor =  getAttributeValueAsDouble(nodeXpath,attribute);
		setAttribute(nodeXpath, attribute, miningFactor*multiplier);
	}

	public void setAttribute(String nodeXpath, String attribute, boolean boolValue) throws XPathExpressionException {
		XPath xPath =  XPathFactory.newInstance().newXPath();

		Node node = (Node) xPath.compile(nodeXpath).evaluate(xDoc, XPathConstants.NODE);

		NamedNodeMap nodeMap = node.getAttributes();
		Node nodeAttr = nodeMap.getNamedItem(attribute);

		String boolValueStr;
		if(boolValue) boolValueStr = "true";
		else boolValueStr = "false";

		nodeAttr.setTextContent(boolValueStr);
	}

	public void setAttribute(String nodeXpath, String attribute, int value) throws XPathExpressionException, PatcherLibBaseEx {
		XPath xPath =  XPathFactory.newInstance().newXPath();

		Node node = (Node) xPath.compile(nodeXpath).evaluate(xDoc, XPathConstants.NODE);
		if(node == null) throw new PatcherLibBaseEx("xPath '"+nodeXpath+"' not found");

		NamedNodeMap nodeMap = node.getAttributes();
		Node nodeAttr = nodeMap.getNamedItem(attribute);

		if(nodeAttr == null) throw new PatcherLibBaseEx("xPath '"+nodeXpath+"' atttribute '"+attribute+"' not found!");
		nodeAttr.setTextContent(Integer.toString(value));
	}


	public void setAttribute(String nodeXpath, String attribute, String valueStr) throws XPathExpressionException {
		XPath xPath =  XPathFactory.newInstance().newXPath();

		Node node = (Node) xPath.compile(nodeXpath).evaluate(xDoc, XPathConstants.NODE);

		NamedNodeMap nodeMap = node.getAttributes();
		Node nodeAttr = nodeMap.getNamedItem(attribute);
		nodeAttr.setTextContent(valueStr);
	}

	// Assigns that attribute name is "float" for double value
	public void setAttribute(String nodeXpath, double valueDouble) throws XPathExpressionException {

		setAttribute(nodeXpath, "float", Format.toString(valueDouble));
	}

	public void setAttribute(String nodeXpath, String attribute, double valueDouble) throws XPathExpressionException {

		setAttribute(nodeXpath, attribute, Format.toString(valueDouble));
	}

	public String getAttributeValueAsString(String nodeXpath, String attribute) throws XPathExpressionException {
		XPath xPath =  XPathFactory.newInstance().newXPath();

		Node node = (Node) xPath.compile(nodeXpath).evaluate(xDoc, XPathConstants.NODE);
		if(node == null) {
			throw new PatcherLibBaseEx(Ctm.msgFormat("Node {0} not found!", nodeXpath));
		}

		NamedNodeMap nodeMap = node.getAttributes();

		Node nodeAttr = nodeMap.getNamedItem(attribute);
		return nodeAttr.getTextContent();
	}

	public double getAttributeValueAsDouble(String nodeXpath, String attribute) throws XPathExpressionException {

		String valueStr = getAttributeValueAsString(nodeXpath, attribute);

		return Double.parseDouble(valueStr);
	}

	@Override
	public void load() throws ParserConfigurationException, IOException, SAXException {

		if(getInputStreamProvider() == null) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();

			String fullPath = rootPath + "\\" + filePath;

			xDoc = builder.parse(fullPath);
		} else {
			val is = getInputStreamProvider().get(getFullPath());

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();

			xDoc = builder.parse(is);
		}
	}

	@Override
	public void saveChanges() throws TransformerException {

		String filePath = rootPath + "\\" + this.filePath;

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(xDoc);
		StreamResult result = new StreamResult(new File(filePath));
		transformer.transform(source, result);
	}


	public XmlFileEntity(String filePath) {
		super(filePath);
	}
}
