package jp.crwdev.app.setting;

import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

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
import jp.crwdev.app.container.ImageFileInfoSplitWrapper;
import jp.crwdev.app.imagefilter.ImageFilterParam;
import jp.crwdev.app.imagefilter.SplitFilter;
import jp.crwdev.app.interfaces.IImageFileInfo;
import jp.crwdev.app.interfaces.IImageFileInfoList;

public class XmlWriter {

	private Document mDocument = null;
	private Element mRootElement = null;
	
	private String mFilePath = null;
	
	private HashMap<IImageFileInfo, ImageFileInfoSplitWrapper> mSpInfoMap = new HashMap<IImageFileInfo, ImageFileInfoSplitWrapper>();
	
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
	public void writeSetting(Element parent, OutputSettingParam output, ImageFilterParamSet bases, IImageFileInfoList list){
		if(output == null || bases == null || list == null){
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
		//writeParam(baseElem, base);
		writeFilterBase(baseElem, bases.get(0), "basic");
		writeFilterBase(baseElem, bases.get(1), "color");
		writeFilterBase(baseElem, bases.get(2), "pict");
		writeFilterBase(baseElem, bases.get(3), "text");
		parent.appendChild(baseElem);

		// <infos>
		Element infosElem = mDocument.createElement("infos");
		for(int i=0; i<list.size(); i++){
			writeInfo(infosElem, list.get(i));
		}
		parent.appendChild(infosElem);
		
		closeDocument();
	}
	
	private void writeFilterBase(Element parent, ImageFilterParam param, String pageType){
		if(param == null){
			return;
		}
		Element filterElem = mDocument.createElement("filter");
		filterElem.setAttribute("pageType", pageType);
		writeParam(filterElem, param);
		parent.appendChild(filterElem);
	}
	
	private void writeOutput(Element parent, OutputSettingParam output){
		if(parent == null){
			parent = mRootElement;
		}

		//TODO:
		
		parent.setAttribute("fileType", output.getFileType());
		parent.setAttribute("bookType", output.getEpubType());
		
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
		
		boolean isEnable = info.isEnable();
		if(isEnable && info instanceof ImageFileInfoSplitWrapper){
			ImageFileInfoSplitWrapper wrap = (ImageFileInfoSplitWrapper)info;
			for(int i=0; i<wrap.getRelativeSplitInfoSize(); i++){
				if(!wrap.getRelativeSplitInfo(i).isEnable()){
					isEnable = false;
					break;
				}
			}
		}
		
		ImageFilterParam param = info.getFilterParam();
		if(param.isEdit() || param.getPageType() != Constant.PAGETYPE_AUTO || param.getSplitType() != SplitFilter.TYPE_NONE || !isEnable){
		
			if(param.getSplitType() == SplitFilter.TYPE_NONE || (param.getSplitType() != SplitFilter.TYPE_NONE && param.getSplitIndex() == 0)){
				
				//<info>
				Element infoElem = mDocument.createElement("info");
		
				//<filename>
				Element filenameElem = mDocument.createElement("filename");
				filenameElem.appendChild(mDocument.createTextNode(info.getFileName()));
				
				infoElem.appendChild(filenameElem);
	
				//<split>
				writeSplit(infoElem, info);
				//writeParam(infoElem, param);
				
				parent.appendChild(infoElem);
	
			}

		}
		
	}
	
	private void writeSplit(Element parent, IImageFileInfo info){

		ImageFilterParam param = info.getFilterParam();
		int splitType = param.getSplitType();
		

		//<split>
		Element splitElem = mDocument.createElement("split");
		//if(!info.isEnable()){
		//	splitElem.setAttribute("enable", Boolean.toString(info.isEnable()));
		//}

		
		splitElem.setAttribute("type", Integer.toString(splitType));
		
		//TODO: attribute of vline, hline
		float[] v = param.getSplitOffsetV();
		float[] h = param.getSplitOffsetH();
		if(v != null && h != null && v.length >= 2 && h.length >= 2){
			String vline = createFloatArrayString(v);
			String hline = createFloatArrayString(h);
			
			splitElem.setAttribute("vline", vline);
			splitElem.setAttribute("hline", hline);
		}

		if(info instanceof ImageFileInfoSplitWrapper){
			ImageFileInfoSplitWrapper wrapInfo = (ImageFileInfoSplitWrapper)info;
			int size = wrapInfo.getRelativeSplitInfoSize();
			String disable = "";
			for(int i=0; i<size; i++){
				IImageFileInfo winfo = wrapInfo.getRelativeSplitInfo(i);
				if(!winfo.isEnable()){
					disable += "," + i;
				}
				ImageFilterParam sparam = wrapInfo.getRelativeSplitInfoFilterParam(i);
				writeParam(splitElem, sparam);
			}
			if(!disable.isEmpty()){
				splitElem.setAttribute("disable", disable.substring(1));
			}
		}
		else{
			if(!info.isEnable()){
				splitElem.setAttribute("disable", "0");
			}
			writeParam(splitElem, param);
		}
		
		parent.appendChild(splitElem);
	}
	
	private String createFloatArrayString(float[] v){
		StringBuilder sb = new StringBuilder();
		sb.append(Float.toString(v[0]));
		for(int i=1; i<v.length; i++){
			sb.append("," + Float.toString(v[i]));
		}
		return new String(sb);
	}
	
	private void writeParam(Element parent, ImageFilterParam param){
		
		if(parent == null){
			parent = mRootElement;
		}

		Element paramElem = mDocument.createElement("param");
		
		paramElem.setAttribute("index", Integer.toString(param.getSplitIndex()));
		
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
		if(param.isColorPageCrop()){
			Element elem = mDocument.createElement("colorCrop");
			elem.appendChild(mDocument.createTextNode("true"));
			elem.setAttribute("l", Integer.toString(param.getColorPageCropLeft()));
			elem.setAttribute("t", Integer.toString(param.getColorPageCropTop()));
			elem.setAttribute("r", Integer.toString(param.getColorPageCropRight()));
			elem.setAttribute("b", Integer.toString(param.getColorPageCropBottom()));
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
		if(param.isBlur()){
			Element elem = mDocument.createElement("blur");
			elem.appendChild(mDocument.createTextNode("true"));
			elem.setAttribute("value", Float.toString(param.getBlurPixels()));
			paramElem.appendChild(elem);
		}
		if(param.isSharpness()){
			Element elem = mDocument.createElement("sharpness");
			elem.appendChild(mDocument.createTextNode("true"));
			elem.setAttribute("value", Float.toString(param.getSharpnessPixels()));
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
		//if(param.getSplitType() != SplitFilter.TYPE_NONE){
		//	Element elem = mDocument.createElement("split");
		//	elem.appendChild(mDocument.createTextNode("true"));
		//	elem.setAttribute("type", Integer.toString(param.getSplitType()));
		//	paramElem.appendChild(elem);
		//}
		if(param.getPageType() != Constant.PAGETYPE_AUTO){
			Element elem = mDocument.createElement("pageType");
			elem.appendChild(mDocument.createTextNode("true"));
			elem.setAttribute("type", Integer.toString(param.getPageType()));
			paramElem.appendChild(elem);
		}
					
		parent.appendChild(paramElem);

	}
	
	
	public boolean openLoadSettingFile(String filepath){
		try {
			File file = new File(filepath);
			if(!file.exists()){
				return false;
			}
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
				builder = factory.newDocumentBuilder();
			factory.setIgnoringElementContentWhitespace(true);
			factory.setIgnoringComments(true);
			factory.setValidating(false);
			Document document = builder.parse("file:///" + filepath);
			mDocument = document;
			return true;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	public void loadSetting(OutputSettingParam output, ImageFilterParamSet baseParams, IImageFileInfoList list){
		if(mDocument == null){
			return;
		}
		
		HashMap<String, IImageFileInfo> map = null;
		if(list != null){
			map = new HashMap<String, IImageFileInfo>();
			for(int i=0; i<list.size(); i++){
				IImageFileInfo info = list.get(i);
				map.put(info.getFileName(), info);
			}
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
					if(output != null){
						loadOutput(node, output);
					}
				}
				else if(name.equalsIgnoreCase("base")){
					if(baseParams != null){
						NodeList baseNodes = node.getChildNodes();
						for(int n=0; n<baseNodes.getLength(); n++){
							loadFilterBase(baseNodes.item(n), baseParams);
							//loadParam(baseNodes.item(n), baseParam);
						}
					}
				}
				else if(name.equalsIgnoreCase("infos")){
					if(list != null){
						NodeList infosNodes = node.getChildNodes();
						for(int n=0; n<infosNodes.getLength(); n++){
							loadInfo(infosNodes.item(n), map);
						}
					}
				}
			}
		}
		
		if(mSpInfoMap.size() > 0){
			List<IImageFileInfo> tmpList = new ArrayList<IImageFileInfo>();
			for(int i=0; i<list.size(); i++){
				tmpList.add(list.get(i));
			}
			
			list.clear();
		
			for(int i=0; i<tmpList.size(); i++){
				IImageFileInfo info = tmpList.get(i);
				if(mSpInfoMap.containsKey(info)){
					ImageFileInfoSplitWrapper wrapInfo = mSpInfoMap.get(info);
					int size = wrapInfo.getRelativeSplitInfoSize();
					for(int n=0; n<size; n++){
						ImageFileInfoSplitWrapper winfo = wrapInfo.getRelativeSplitInfo(n);
						list.add(winfo);
					}
				}
				else{
					list.add(info);
				}
			}
		}
	}
	
	private void loadFilterBase(Node filterNode, ImageFilterParamSet params){
		if(filterNode.getNodeName().equalsIgnoreCase("filter")){
			NamedNodeMap attrs = filterNode.getAttributes();
			String pageType = getAttributeValue(attrs, "pageType");
			if(!pageType.isEmpty()){
				int index = 0;
				if(pageType.equals("basic")){
					index = 0;
				}
				else if(pageType.equals("color")){
					index = 1;
				}
				else if(pageType.equals("pict")){
					index = 2;
				}
				else if(pageType.equals("text")){
					index = 3;
				}
				ImageFilterParam param = params.get(index);
				if(param == null){
					params.set(index, new ImageFilterParam());
				}
				NodeList nodes = filterNode.getChildNodes();
				if(nodes.getLength() > 0){
					loadParam(nodes.item(0), params.get(index));
				}
			}
		}
	}
	
	private void loadOutput(Node outputNode, OutputSettingParam output){
		if(outputNode.getNodeName().equalsIgnoreCase("output")){
			//TODO:
			NamedNodeMap outputAttrs = outputNode.getAttributes();
			String fileType = getAttributeValue(outputAttrs, "fileType");
			if(fileType.isEmpty()){
				fileType = "zip";
			}
			String bookType = getAttributeValue(outputAttrs, "bookType");
			if(bookType.isEmpty()){
				bookType = "book";
			}
			
			output.setFileType(fileType);
			output.setEpubType(bookType);

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
				else if(node.getNodeName().equalsIgnoreCase("split")){
					paramNode = node;
				}
			}
			if(filenameNode != null && paramNode != null){
				String filename = filenameNode.getFirstChild().getNodeValue();
				IImageFileInfo info = map.get(filename);
				if(info != null){
					loadSplit(paramNode, info);
					//loadParam(paramNode, info.getFilterParam());
				}
			}
		}
	}
	
	private void loadSplit(Node splitNode, IImageFileInfo info){
		if(splitNode.getNodeName().equalsIgnoreCase("split")){
			// type="0" vline="-0.5,0,0.5" hline="-0.5,0,0.5"
			NamedNodeMap attrs = splitNode.getAttributes();
			String type = getAttributeValue(attrs, "type");
			
			int splitType = Integer.parseInt(type);
			if(splitType == SplitFilter.TYPE_NONE){
				//String enable = getAttributeValue(attrs, "enable");
				//boolean value = true;
				//if(!enable.isEmpty()){
				//	value = Boolean.parseBoolean(enable);
				//}
				//info.setEnable(value);
				String disable = getAttributeValue(attrs, "disable");
				if(!disable.isEmpty()){
					info.setEnable(false);
				}
				
				// 分割無し
				NodeList nodes = splitNode.getChildNodes();
				for(int i=0; i<nodes.getLength(); i++){
					Node node = nodes.item(i);
					loadParam(node, info.getFilterParam());
				}
			}
			else{
				String vline = getAttributeValue(attrs, "vline");
				String hline = getAttributeValue(attrs, "hline");
				
				NodeList nodes = splitNode.getChildNodes();
				
				//TODO: vline hline
				float[] v = null;
				float[] h = null;
				if(!vline.isEmpty() && !hline.isEmpty()){
					v = parseFloatArray(vline);
					h = parseFloatArray(hline);
				}
				
				info.getFilterParam().setSplitType(splitType, v, h);
				
				ImageFileInfoSplitWrapper first = null;// = new ImageFileInfoSplitWrapper(info, 0);
				
				
				String disable = getAttributeValue(attrs, "disable");
				String[] svals = disable.split(",");
				boolean[] vals = null;
				if(!disable.isEmpty() && svals.length > 0){
					vals = new boolean[nodes.getLength()];
					for(int i=0; i<vals.length; i++){
						vals[i] = true;
					}
					for(int i=0; i<svals.length; i++){
						try {
							int index = Integer.parseInt(svals[i]);
							vals[index] = false;
						}catch(NumberFormatException e){
						}
					}
				}

				for(int i=0; i<nodes.getLength(); i++){
					Node node = nodes.item(i);
					ImageFileInfoSplitWrapper relWrapInfo = new ImageFileInfoSplitWrapper(info, i /*dummy*/);
					loadParam(node, relWrapInfo.getFilterParam());

					if(first == null){
						first = relWrapInfo;
					}
					first.addRelativeSplitInfo(relWrapInfo);
					relWrapInfo.setFirstSplitInfo(first);
					if(vals != null){
						relWrapInfo.setEnable(vals[i]);
					}
				}
				
				mSpInfoMap.put(info, first);
			}
		}
	}
	
	private float[] parseFloatArray(String v){
		String[] va = v.split(",");
		float[] ar = new float[va.length];
		for(int i=0; i<va.length; i++){
			ar[i] = Float.parseFloat(va[i]);
		}
		return ar;
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
				else if(name.equalsIgnoreCase("colorCrop")){
					String l = getAttributeValue(attrs, "l");
					String r = getAttributeValue(attrs, "r");
					String t = getAttributeValue(attrs, "t");
					String b = getAttributeValue(attrs, "b");
					String enable = node.getFirstChild().getNodeValue();
					param.setColorPageCrop(Boolean.parseBoolean(enable));
					param.setColorPageCrop(Integer.parseInt(l), Integer.parseInt(t), Integer.parseInt(r), Integer.parseInt(b));					
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
				else if(name.equalsIgnoreCase("blur")){
					String enable = node.getFirstChild().getNodeValue();
					String value = getAttributeValue(attrs, "value");
					param.setBlur(Boolean.parseBoolean(enable));
					param.setBlurPixels(Float.parseFloat(value));
				}
				else if(name.equalsIgnoreCase("sharpness")){
					String enable = node.getFirstChild().getNodeValue();
					String value = getAttributeValue(attrs, "value");
					param.setSharpness(Boolean.parseBoolean(enable));
					param.setSharpnessPixels(Float.parseFloat(value));
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
				//else if(name.equalsIgnoreCase("split")){
				//	String type = getAttributeValue(attrs, "type");
				//	String enable = node.getFirstChild().getNodeValue();
				//	if(Boolean.parseBoolean(enable)){
				//		param.setSplitType(Integer.parseInt(type));
				//	}
				//}
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
		if(node != null){
			return node.getNodeValue();
		} else {
			return "";
		}
	}
	
}
