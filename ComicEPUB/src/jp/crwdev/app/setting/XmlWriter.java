package jp.crwdev.app.setting;

import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import jp.crwdev.app.OutputSettingParam;
import jp.crwdev.app.constant.Constant;
import jp.crwdev.app.imagefilter.ImageFilterParam;
import jp.crwdev.app.imagefilter.SplitFilter;
import jp.crwdev.app.interfaces.IImageFileInfo;
import jp.crwdev.app.interfaces.IImageFileInfoList;

public class XmlWriter {

	private Document mDocument = null;
	private Element mRootElement = null;
	
	private String mFilePath = null;
	
	
	public XmlWriter(){
	}
	
	public boolean openSaveSettingFile(String filepath){
		mFilePath = filepath;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			DOMImplementation domImpl=builder.getDOMImplementation();
			mDocument = domImpl.createDocument("","Setting",null);
			mRootElement = mDocument.getDocumentElement();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
	
	private void closeDocument(){
		//出力
		try {
			TransformerFactory transFactory = TransformerFactory.newInstance();
			Transformer transformer = transFactory.newTransformer();
	
			DOMSource source = new DOMSource(mDocument);
			File newXML = new File(mFilePath);
			FileOutputStream os = new FileOutputStream(newXML);
			StreamResult result = new StreamResult(os);
			transformer.transform(source, result);
			
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void writeSetting(Element parent, OutputSettingParam output, ImageFilterParam base, IImageFileInfoList list){
		if(output == null || base == null || list == null){
			return;
		}
		if(parent == null){
			parent = mRootElement;
		}

		// <output>
		Element outputElem = mDocument.createElement("output");
		writeOutput(outputElem, output);
		parent.appendChild(outputElem);
		
		// <base>
		Element baseElem = mDocument.createElement("base");
		writeParam(baseElem, base);
		parent.appendChild(baseElem);

		// <infos>
		Element infosElem = mDocument.createElement("infos");
		for(int i=0; i<list.size(); i++){
			writeInfo(infosElem, list.get(i));
		}
		parent.appendChild(infosElem);
		
		closeDocument();
	}
	
	private void writeOutput(Element parent, OutputSettingParam output){
		if(parent == null){
			parent = mRootElement;
		}

		//TODO:
		// <folder>
		String outputFolder = output.getOutputPath();
		if(!outputFolder.isEmpty()){
			Element folderElem = mDocument.createElement("folder");
			folderElem.appendChild(mDocument.createTextNode(outputFolder));
			parent.appendChild(folderElem);
		}

		// <size>
		Dimension size = output.getImageSize();
		if(size != null && size.width != 0 && size.height != 0){
			Element sizeElem = mDocument.createElement("size");
			sizeElem.setAttribute("width", Integer.toString(size.width));
			sizeElem.setAttribute("height", Integer.toString(size.height));
			parent.appendChild(sizeElem);
		}
		
		// <title>
		String title = output.getTitle();
		if(!title.isEmpty()){
			Element titleElem = mDocument.createElement("title");
			titleElem.appendChild(mDocument.createTextNode(title));
			parent.appendChild(titleElem);
		}

		// <title_kana>
		String titleKana = output.getTitleKana();
		if(!titleKana.isEmpty()){
			Element titleKanaElem = mDocument.createElement("title_kana");
			titleKanaElem.appendChild(mDocument.createTextNode(titleKana));
			parent.appendChild(titleKanaElem);
		}
		
		// <author>
		String author = output.getAuthor();
		if(!author.isEmpty()){
			Element authorElem = mDocument.createElement("author");
			authorElem.appendChild(mDocument.createTextNode(author));
			parent.appendChild(authorElem);
		}

		// <title_kana>
		String authorKana = output.getAuthorKana();
		if(!authorKana.isEmpty()){
			Element authorKanaElem = mDocument.createElement("author_kana");
			authorKanaElem.appendChild(mDocument.createTextNode(authorKana));
			parent.appendChild(authorKanaElem);
		}

	}
	
	private void writeInfo(Element parent, IImageFileInfo info){
		
		if(parent == null){
			parent = mRootElement;
		}
		
		ImageFilterParam param = info.getFilterParam();
		if(param.isEdit() || param.getPageType() != Constant.PAGETYPE_AUTO){
		
			Element infoElem = mDocument.createElement("info");
	
			Element filenameElem = mDocument.createElement("filename");
			filenameElem.appendChild(mDocument.createTextNode(info.getFileName()));
			
			infoElem.appendChild(filenameElem);
			
			writeParam(infoElem, param);
			
			parent.appendChild(infoElem);
		
		}
		
	}
	
	private void writeParam(Element parent, ImageFilterParam param){
		
		if(parent == null){
			parent = mRootElement;
		}

		Element paramElem = mDocument.createElement("param");
		
		if(param.isPreview()){
			Element elem = mDocument.createElement("preview");
			elem.appendChild(mDocument.createTextNode("true"));
			paramElem.appendChild(elem);
		}
		if(param.isResize()){
			Element elem = mDocument.createElement("resize");
			elem.appendChild(mDocument.createTextNode("true"));
			paramElem.appendChild(elem);
		}
		if(param.isUnificationTextPage()){
			Element elem = mDocument.createElement("unification_text");
			elem.appendChild(mDocument.createTextNode("true"));
			paramElem.appendChild(elem);
		}
		if(param.isRotate()){
			Element elem = mDocument.createElement("rotate");
			elem.appendChild(mDocument.createTextNode("true"));
			elem.setAttribute("angle", Double.toString(param.getRotateAngle()));
			paramElem.appendChild(elem);
		}
		if(param.isTranslate()){
			Element elem = mDocument.createElement("translate");
			elem.appendChild(mDocument.createTextNode("true"));
			elem.setAttribute("x", Integer.toString(param.getTranslateX()));
			elem.setAttribute("y", Integer.toString(param.getTranslateY()));
			paramElem.appendChild(elem);
		}
		if(param.isFullPageCrop()){
			Element elem = mDocument.createElement("fullCrop");
			elem.appendChild(mDocument.createTextNode("true"));
			elem.setAttribute("l", Integer.toString(param.getFullPageCropLeft()));
			elem.setAttribute("t", Integer.toString(param.getFullPageCropTop()));
			elem.setAttribute("r", Integer.toString(param.getFullPageCropRight()));
			elem.setAttribute("b", Integer.toString(param.getFullPageCropBottom()));
			paramElem.appendChild(elem);
		}
		if(param.isTextPageCrop()){
			Element elem = mDocument.createElement("textCrop");
			elem.appendChild(mDocument.createTextNode("true"));
			elem.setAttribute("l", Integer.toString(param.getTextPageCropLeft()));
			elem.setAttribute("t", Integer.toString(param.getTextPageCropTop()));
			elem.setAttribute("r", Integer.toString(param.getTextPageCropRight()));
			elem.setAttribute("b", Integer.toString(param.getTextPageCropBottom()));
			paramElem.appendChild(elem);
		}
		if(param.isPictPageCrop()){
			Element elem = mDocument.createElement("pictCrop");
			elem.appendChild(mDocument.createTextNode("true"));
			elem.setAttribute("l", Integer.toString(param.getPictPageCropLeft()));
			elem.setAttribute("t", Integer.toString(param.getPictPageCropTop()));
			elem.setAttribute("r", Integer.toString(param.getPictPageCropRight()));
			elem.setAttribute("b", Integer.toString(param.getPictPageCropBottom()));
			paramElem.appendChild(elem);
		}
		if(param.isGrayscale()){
			Element elem = mDocument.createElement("grayscale");
			elem.appendChild(mDocument.createTextNode("true"));
			paramElem.appendChild(elem);
		}
		if(param.isGamma()){
			Element elem = mDocument.createElement("gamma");
			elem.appendChild(mDocument.createTextNode("true"));
			elem.setAttribute("value", Double.toString(param.getGamma()));
			paramElem.appendChild(elem);
		}
		if(param.isContrast()){
			Element elem = mDocument.createElement("contrast");
			elem.appendChild(mDocument.createTextNode("true"));
			elem.setAttribute("scale", Float.toString(param.getContrast()));
			elem.setAttribute("offset", Float.toString(param.getBrightness()));
			paramElem.appendChild(elem);
		}
		if(param.getSplitType() != SplitFilter.TYPE_NONE){
			Element elem = mDocument.createElement("split");
			elem.appendChild(mDocument.createTextNode("true"));
			elem.setAttribute("type", Integer.toString(param.getSplitType()));
			paramElem.appendChild(elem);
		}
		if(param.getPageType() != Constant.PAGETYPE_AUTO){
			Element elem = mDocument.createElement("pageType");
			elem.appendChild(mDocument.createTextNode("true"));
			elem.setAttribute("type", Integer.toString(param.getPageType()));
			paramElem.appendChild(elem);
		}
					
		parent.appendChild(paramElem);

	}
	
	
	public void openLoadSettingFile(String filepath){
		try {
			File file = new File(filepath);
			if(!file.exists()){
				return;
			}
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
				builder = factory.newDocumentBuilder();
			factory.setIgnoringElementContentWhitespace(true);
			factory.setIgnoringComments(true);
			factory.setValidating(false);
			Document document = builder.parse("file:///" + filepath);
			mDocument = document;
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void loadSetting(OutputSettingParam output, ImageFilterParam baseParam, IImageFileInfoList list){
		if(mDocument == null){
			return;
		}
		
		HashMap<String, IImageFileInfo> map = new HashMap<String, IImageFileInfo>();
		for(int i=0; i<list.size(); i++){
			IImageFileInfo info = list.get(i);
			map.put(info.getFileName(), info);
		}
		
		NodeList topNodes = mDocument.getChildNodes();
		if(topNodes.getLength() != 1){
			return;
		}
		Node settingNode = topNodes.item(0);
		if(settingNode.getNodeName().equalsIgnoreCase("setting")){
			NodeList settingNodes = settingNode.getChildNodes();
			for(int i=0; i<settingNodes.getLength(); i++){
				Node node = settingNodes.item(i);
				String name = node.getNodeName();
				if(name.equalsIgnoreCase("output")){
					loadOutput(node, output);
				}
				else if(name.equalsIgnoreCase("base")){
					NodeList baseNodes = node.getChildNodes();
					if(baseNodes.getLength() == 1){
						loadParam(baseNodes.item(0), baseParam);
					}
				}
				else if(name.equalsIgnoreCase("infos")){
					NodeList infosNodes = node.getChildNodes();
					for(int n=0; n<infosNodes.getLength(); n++){
						loadInfo(infosNodes.item(n), map);
					}
				}
			}
		}
	}
	
	private void loadOutput(Node outputNode, OutputSettingParam output){
		if(outputNode.getNodeName().equalsIgnoreCase("output")){
			//TODO:

			NodeList nodes = outputNode.getChildNodes();
			for(int i=0; i<nodes.getLength(); i++){
				Node node = nodes.item(i);
				NamedNodeMap attrs = node.getAttributes();
				String name = node.getNodeName();
				if(name.equalsIgnoreCase("folder")){
					if(node.hasChildNodes()){
						String path = node.getFirstChild().getNodeValue();
						output.setOutputPath(path);
					}
				}
				else if(name.equalsIgnoreCase("size")){
					String width = getAttributeValue(attrs, "width");
					String height = getAttributeValue(attrs, "height");
					output.setImageSize(Integer.parseInt(width), Integer.parseInt(height));
				}
				else if(name.equalsIgnoreCase("title")){
					if(node.hasChildNodes()){
						String value = node.getFirstChild().getNodeValue();
						output.setTitle(value);
					}
				}
				else if(name.equalsIgnoreCase("title_kana")){
					if(node.hasChildNodes()){
						String value = node.getFirstChild().getNodeValue();
						output.setTitleKana(value);
					}
				}
				else if(name.equalsIgnoreCase("author")){
					if(node.hasChildNodes()){
						String value = node.getFirstChild().getNodeValue();
						output.setAuthor(value);
					}
				}
				else if(name.equalsIgnoreCase("author_kana")){
					if(node.hasChildNodes()){
						String value = node.getFirstChild().getNodeValue();
						output.setAuthorKana(value);
					}
				}
			}

		}
	}
	
	private void loadInfo(Node infoNode, HashMap<String, IImageFileInfo> map){
		if(infoNode.getNodeName().equalsIgnoreCase("info")){

			NodeList nodes = infoNode.getChildNodes();
			Node filenameNode = null;
			Node paramNode = null;
			for(int i=0; i<nodes.getLength(); i++){
				Node node = nodes.item(i);
				if(node.getNodeName().equalsIgnoreCase("filename")){
					filenameNode = node;
				}
				else if(node.getNodeName().equalsIgnoreCase("param")){
					paramNode = node;
					//loadParam(node, info.getFilterParam());
				}
			}
			if(filenameNode != null && paramNode != null){
				String filename = filenameNode.getFirstChild().getNodeValue();
				IImageFileInfo info = map.get(filename);
				if(info != null){
					loadParam(paramNode, info.getFilterParam());
				}
			}
		}
	}
	
	private void loadParam(Node paramNode, ImageFilterParam param){
		if(paramNode.getNodeName().equalsIgnoreCase("param")){
			NodeList nodes = paramNode.getChildNodes();
			for(int i=0; i<nodes.getLength(); i++){
				Node node = nodes.item(i);
				NamedNodeMap attrs = node.getAttributes();
				String name = node.getNodeName();
				if(name.equalsIgnoreCase("preview")){
					String enable = node.getFirstChild().getNodeValue();
					param.setPreview(Boolean.parseBoolean(enable));
				}
				if(name.equalsIgnoreCase("resize")){
					String enable = node.getFirstChild().getNodeValue();
					param.setResize(Boolean.parseBoolean(enable));
				}
				if(name.equalsIgnoreCase("unification_text")){
					String enable = node.getFirstChild().getNodeValue();
					param.setUnificationTextPage(Boolean.parseBoolean(enable));
				}
				if(name.equalsIgnoreCase("rotate")){
					String angle = getAttributeValue(attrs, "angle");
					String enable = node.getFirstChild().getNodeValue();
					param.setRotate(Boolean.parseBoolean(enable));
					param.setRotateAngle(Double.parseDouble(angle));
				}
				else if(name.equalsIgnoreCase("translate")){
					String x = getAttributeValue(attrs, "x");
					String y = getAttributeValue(attrs, "y");
					String enable = node.getFirstChild().getNodeValue();
					param.setTranslate(Boolean.parseBoolean(enable));
					param.setTranslateX(Integer.parseInt(x));
					param.setTranslateY(Integer.parseInt(y));
				}
				else if(name.equalsIgnoreCase("fullCrop")){
					String l = getAttributeValue(attrs, "l");
					String r = getAttributeValue(attrs, "r");
					String t = getAttributeValue(attrs, "t");
					String b = getAttributeValue(attrs, "b");
					String enable = node.getFirstChild().getNodeValue();
					param.setFullPageCrop(Boolean.parseBoolean(enable));
					param.setFullPageCrop(Integer.parseInt(l), Integer.parseInt(t), Integer.parseInt(r), Integer.parseInt(b));					
				}
				else if(name.equalsIgnoreCase("textCrop")){
					String l = getAttributeValue(attrs, "l");
					String r = getAttributeValue(attrs, "r");
					String t = getAttributeValue(attrs, "t");
					String b = getAttributeValue(attrs, "b");
					String enable = node.getFirstChild().getNodeValue();
					param.setTextPageCrop(Boolean.parseBoolean(enable));
					param.setTextPageCrop(Integer.parseInt(l), Integer.parseInt(t), Integer.parseInt(r), Integer.parseInt(b));					
				}
				else if(name.equalsIgnoreCase("pictCrop")){
					String l = getAttributeValue(attrs, "l");
					String r = getAttributeValue(attrs, "r");
					String t = getAttributeValue(attrs, "t");
					String b = getAttributeValue(attrs, "b");
					String enable = node.getFirstChild().getNodeValue();
					param.setPictPageCrop(Boolean.parseBoolean(enable));
					param.setPictPageCrop(Integer.parseInt(l), Integer.parseInt(t), Integer.parseInt(r), Integer.parseInt(b));					
				}
				else if(name.equalsIgnoreCase("grayscale")){
					String enable = node.getFirstChild().getNodeValue();
					param.setGrayscale(Boolean.parseBoolean(enable));
				}
				else if(name.equalsIgnoreCase("gamma")){
					String gamma = getAttributeValue(attrs, "value");
					String enable = node.getFirstChild().getNodeValue();
					param.setGamma(Boolean.parseBoolean(enable));
					param.setGamma(Double.parseDouble(gamma));
				}
				else if(name.equalsIgnoreCase("contrast")){
					String scale = getAttributeValue(attrs, "scale");
					String offset = getAttributeValue(attrs, "offset");
					String enable = node.getFirstChild().getNodeValue();
					param.setContrast(Boolean.parseBoolean(enable));
					param.setContrast(Float.parseFloat(scale));
					param.setBrightness(Float.parseFloat(offset));
				}
				else if(name.equalsIgnoreCase("split")){
					String type = getAttributeValue(attrs, "type");
					String enable = node.getFirstChild().getNodeValue();
					if(Boolean.parseBoolean(enable)){
						param.setSplitType(Integer.parseInt(type));
					}
				}
				else if(name.equalsIgnoreCase("pageType")){
					String type = getAttributeValue(attrs, "type");
					String enable = node.getFirstChild().getNodeValue();
					if(Boolean.parseBoolean(enable)){
						param.setPageType(Integer.parseInt(type));
					}
				}
			}
		}
	}
	
	private String getAttributeValue(NamedNodeMap attrs, String name){
		Node node = attrs.getNamedItem(name);
		return node.getNodeValue();
	}
	
}
