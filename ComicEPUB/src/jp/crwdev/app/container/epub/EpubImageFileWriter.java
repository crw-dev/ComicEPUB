package jp.crwdev.app.container.epub;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import jp.crwdev.app.BufferedImageIO;
import jp.crwdev.app.constant.Constant;
import jp.crwdev.app.interfaces.IImageFileInfo;
import jp.crwdev.app.interfaces.IImageFileInfoList;
import jp.crwdev.app.interfaces.IImageFileWriter;
import jp.crwdev.app.interfaces.IImageFilter;

public class EpubImageFileWriter implements IImageFileWriter {

	private final static String META_INF_DIR = "META-INF";
	private final static String CONTAINER_FILENAME = "container.xml";
	private final static String STYLE_FILENAME = "fixed-layout-jp.css";
	private final static String OPF_FILENAME = "standard.opf";
	private final static String NCX_FILENAME = "toc.ncx";
	private final static String NAVIGATION_FILENAME = "navigation-documents.xhtml";
	private final static String ITEM_DIR = "OEBPS";
	private final static String STYLE_DIR = ITEM_DIR + "/" + "style";
	private final static String IMAGE_DIR = ITEM_DIR + "/" + "image";
	private final static String XHTML_DIR = ITEM_DIR + "/" + "xhtml";

	/** 出力先ファイル */
	private File mOutputFile = null;
	/** 画像フィルタ */
	private IImageFilter mBaseFilter = null;
	/** 処理中断フラグ */
	private boolean mIsCancel = false;
	
	private String mTitle = "";
	private String mTitleKana = "";
	private String mAuthor = "";
	private String mAuthorKana = "";
	private String mSeriesTitle = "";
	private String mSeriesTitleKana = "";
	private int mSeriesNumber = 0;
	private String mBookType = "book";
	
	/**
	 * コンストラクタ
	 * @param title
	 * @param titleKana
	 * @param author
	 * @param authorKana
	 * @param bookType "book", "magazine", "comic"
	 */
	public EpubImageFileWriter(String title, String titleKana, String author, String authorKana, String bookType,
			String seriesTitle, String seriesTitleKana, int seriesNumber){
		mTitle = title;
		mTitleKana = titleKana;
		mAuthor = author;
		mAuthorKana = authorKana;
		mBookType = bookType;
		mSeriesTitle = seriesTitle;
		mSeriesTitleKana = seriesTitleKana;
		mSeriesNumber = seriesNumber;
	}
	
	@Override
	public boolean open(String filepath) {
		
		File file = new File(filepath);
		if(file.exists()){
			if(!file.delete()){
				return false;
			}
		}
		
		mOutputFile = file;

		return true;
	}

	@Override
	public void setImageFilter(IImageFilter filter) {
		mBaseFilter = filter;
	}

	@Override
	public boolean write(IImageFileInfoList list, OnProgressListener listener) {

		if(mOutputFile == null){
			return false;
		}
		
		mIsCancel = false;
		
		if(listener != null){
			listener.onProgress(0, null);
		}

		ZipOutputStream zipOut = null;
		
		String uuid = getUUID();
		
		try {
		
			zipOut = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(mOutputFile)));
			zipOut.setLevel(0);

			writeMimeTypeFile(zipOut);
			
		    mkdir(META_INF_DIR, zipOut);
		    writeContainerFile(zipOut);
		    
		    mkdir(STYLE_DIR, zipOut);
		    writeCssFile(zipOut);
		    
		    if(listener != null){
		    	listener.onProgress(5, null);
		    }
		    
		    if(mIsCancel){
		    	return false;
		    }
		    
			mkdir(IMAGE_DIR, zipOut);
			List<Dimension> sizeList = writeImageFiles(IMAGE_DIR, list, zipOut, listener);

		    mkdir(XHTML_DIR, zipOut);
		    writeXHtmlFiles(sizeList, zipOut);
		    
		    writeNcxFile(uuid, mTitle, zipOut);

		    mkdir(ITEM_DIR, zipOut);
		    writeStandardOpf(zipOut, list, uuid, mBookType, mTitle, mTitleKana, mSeriesTitle, mSeriesTitleKana, mSeriesNumber, mAuthor, mAuthorKana, -1, -1);
		    
		    writeNavigationFile(zipOut);

		}catch(Exception e){
			if(!e.getMessage().equals("user cancel")){
				e.printStackTrace();
			}
		}finally{
			if(zipOut != null){
				try {
					zipOut.close();
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
				zipOut = null;
			}
		}

		return true;
	}

	@Override
	public void close() {
		if(mIsCancel){
			if(mOutputFile != null){
				if(mOutputFile.exists()){
					mOutputFile.delete();
				}
			}
		}
		if(mOutputFile != null){
			mOutputFile = null;
		}
	}
	
	@Override
	public void cancel() {
		mIsCancel = true;
	}
	
	@Override
	public String getSuffix() {
		return ".epub";
	}
	
	public String getUUID(){
		UUID uuid = UUID.randomUUID();
        return uuid.toString();
	}

	
	private void mkdir(String name, ZipOutputStream zipOut) throws IOException{
		name = name.endsWith("/") ? name : name + "/";
		zipOut.putNextEntry(new ZipEntry(name));
	}
	
	private void writeMimeTypeFile(ZipOutputStream zipOut) throws IOException{
		ZipEntry entry = new ZipEntry("mimetype");
		entry.setMethod(ZipEntry.STORED);
		byte[] content = "application/epub+zip".getBytes();
		entry.setSize(content.length);
		
		CRC32 crc = new CRC32();
		crc.update(content);
		entry.setCrc(crc.getValue());
		
		zipOut.putNextEntry(entry);
		zipOut.write(content);
	}

	private void writeContainerFile(ZipOutputStream zipOut) throws IOException {
		zipOut.putNextEntry(new ZipEntry(META_INF_DIR + "/" + CONTAINER_FILENAME));
		
		PrintWriter out = new PrintWriter(new OutputStreamWriter(zipOut, "UTF-8"));
		
		out.println("<?xml version=\"1.0\"?>");
		out.println("<container version=\"1.0\" xmlns=\"urn:oasis:names:tc:opendocument:xmlns:container\">");
		out.println("<rootfiles>");
		out.println("<rootfile full-path=\"" + ITEM_DIR + "/standard.opf\" media-type=\"application/oebps-package+xml\" />");
		out.println("</rootfiles>");
		out.println("</container>");
		
		out.flush();
		
		zipOut.closeEntry();
	}

	private void writeCssFile(ZipOutputStream zipOut) throws IOException {
		zipOut.putNextEntry(new ZipEntry(STYLE_DIR + "/" + STYLE_FILENAME));
		
		PrintWriter out = new PrintWriter(new OutputStreamWriter(zipOut, "UTF-8"));
		
		if(true){
			out.println("@charset \"utf-8\";");
			out.println("svg{");
			out.println("	margin:0;");
			out.println("	padding:0;");
			out.println("}");

			out.println("body, html{");
			out.println("	margin:0;");
			out.println("	font-size:0;");
			out.println("	padding:0;");
			out.println("	background-color:white;");
			out.println("}");
	
		}
		else{
		out.println("html {");
		out.println("  writing-mode: tb-rl;");
		//out.println("  direction: rtl;");
		out.println("}");
		out.println("body {");
		out.println("  top: 0px;");
		out.println("  left: 0px;");
		out.println("  margin: 0px;");
		out.println("  padding: 0px;");
		out.println("  text-align: center;");
		out.println("  margin: 0 auto;");
		out.println("  width:100%;");
		out.println("  height:100%;");
		out.println("  background-color:white;");
		out.println("}");
		}
		
		out.flush();
		
		zipOut.closeEntry();
	}
	
	private List<Dimension> writeImageFiles(String imageDir, IImageFileInfoList list, ZipOutputStream zipOut, OnProgressListener listener) throws Exception {

		if(!imageDir.isEmpty()){
			imageDir += "/";
		}
		
		
		int size = list.size();
		float progressOffset = 95 / (float)size;
		
		List<Dimension> sizeList = new ArrayList<Dimension>(size);
		
		for(int i=0; i<size; i++){
			IImageFileInfo info = list.get(i);
			if(!info.isEnable()){
				continue;
			}

			if(mIsCancel){
				throw new Exception("user cancel");
			}
			
			String filename = getImageFileName(sizeList.size(), ".jpg");
			
			zipOut.putNextEntry(new ZipEntry(imageDir + filename));
			
			try {
				//File file = new File(info.getFullPath());
				synchronized(info){
					InputStream in = info.getInputStream();
					
					BufferedImage image = BufferedImageIO.read(in, info.isJpeg());
					if(mBaseFilter != null){
						image = mBaseFilter.filter(image, info.getFilterParam());
					}
					BufferedImageIO.write(image, "jpeg", Constant.jpegQuality, zipOut);
					
					in.close();

					sizeList.add(new Dimension(image.getWidth(), image.getHeight()));
				}

			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			zipOut.flush();
			zipOut.closeEntry();
			
			if(listener != null){
				listener.onProgress((int)((i*1)*progressOffset)+5, null);
			}
		}
		
		return sizeList;
	}
	
	private void writeXHtmlFiles(List<Dimension> list, ZipOutputStream zipOut) throws Exception {
		
		int size = list.size();
		for(int i=0; i<size; i++){
			if(mIsCancel){
				throw new Exception("user cancel");
			}
			
			Dimension imageSize = list.get(i);

			String filename = getXhtmlFileName(i);
			
			zipOut.putNextEntry(new ZipEntry(XHTML_DIR + "/" + filename));
			
			PrintWriter out = new PrintWriter(new OutputStreamWriter(zipOut, "UTF-8"));
			
			out.write(getXhtml(imageSize, getImageFileName(i, ".jpg")));
			
			out.flush();
			
			zipOut.closeEntry();
		}
	}
	
	private void writeNcxFile(String uuid, String title, ZipOutputStream zipOut) throws IOException{
		zipOut.putNextEntry(new ZipEntry(ITEM_DIR + "/" + NCX_FILENAME));
		
		title = convEscape(title);
		
		String filename = getXhtmlFileName(0);

		PrintWriter out = new PrintWriter(new OutputStreamWriter(zipOut, "UTF-8"));

		StringBuffer sb = new StringBuffer();

		sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		//sb.append("<!DOCTYPE ncx PUBLIC \"-//NISO//DTD ncx 2005-1//EN\" \"http://www.daisy.org/z3986/2005/ncx-2005-1.dtd\">\n");
		sb.append("<ncx xmlns=\"http://www.daisy.org/z3986/2005/ncx/\" version=\"2005-1\">\n");
		sb.append("<head>\n");
		sb.append("  <meta name=\"dtb:uid\" content=\"" + uuid + "\" />\n");
		sb.append("  <meta name=\"dtb:depth\" content=\"1\" />\n");
		sb.append("  <meta name=\"dtb:totalPageCount\" content=\"0\" />\n");
		sb.append("  <meta name=\"dtb:maxPageNumber\" content=\"0\" />\n");
		sb.append("</head>\n");
		sb.append("<docTitle>\n");
		sb.append("  <text>" + title + "</text>\n");
		sb.append("</docTitle>\n");
		sb.append("<navMap>\n");
		sb.append("  <navPoint id=\"navPoint-1\" playOrder=\"1\">\n");
		sb.append("    <navLabel>\n");
		sb.append("      <text>表紙</text>\n");
		sb.append("    </navLabel>\n");
		sb.append("    <content src=\"xhtml/" + filename + "\" />\n");
		sb.append("  </navPoint>\n");
		sb.append("</navMap>\n");
		sb.append("</ncx>\n");

		out.write(new String(sb));
		
		out.flush();
		
		zipOut.closeEntry();
	}
	
	private void writeNavigationFile(ZipOutputStream zipOut) throws IOException{
		zipOut.putNextEntry(new ZipEntry(ITEM_DIR + "/" + NAVIGATION_FILENAME));

		String filename = getXhtmlFileName(0);

		PrintWriter out = new PrintWriter(new OutputStreamWriter(zipOut, "UTF-8"));

		StringBuffer sb = new StringBuffer();
		
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<!DOCTYPE html>\n");
		sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:epub=\"http://www.idpf.org/2007/ops\" xml:lang=\"ja\">\n");
		sb.append("<head>\n");
		sb.append("<meta charset=\"UTF-8\"/>\n");
		sb.append("<title>Navigation</title>\n");
		sb.append("</head>\n");
		sb.append("<body>\n");
		sb.append("<nav epub:type=\"toc\" id=\"toc\">\n");
		sb.append("<h1>Navigation</h1>\n");
		sb.append("<ol>\n");
		sb.append("<li><a href=\"xhtml/" + filename + "\">表紙</a></li>\n");
		sb.append("</ol>\n");
		sb.append("</nav>\n");
		sb.append("</body>\n");
		sb.append("</html>\n");


		out.write(new String(sb));
		
		out.flush();
		
		zipOut.closeEntry();

	}
	
	private void writeStandardOpf(ZipOutputStream zipOut, IImageFileInfoList list, String uuid, String booktype, String title, String title_kana, String seriesTitle, String seriesTitleKana, int serieseNum, String creator, String creator_kana, int baseWidth, int baseHeight) throws IOException {
		zipOut.putNextEntry(new ZipEntry(ITEM_DIR + "/" + OPF_FILENAME));
		
		title = convEscape(title);
		title_kana = convEscape(title_kana);
		creator = convEscape(creator);
		creator_kana = convEscape(creator_kana);
		
		PrintWriter out = new PrintWriter(new OutputStreamWriter(zipOut, "UTF-8"));
		
		String head = getStandardOpfHead(booktype, title, title_kana, seriesTitle, seriesTitleKana, serieseNum, creator, creator_kana,
				"", "", uuid, new Date(), baseWidth, baseHeight);
		String body = getStandardOpfBody(list);
		String foot = getStandardOpfFoot();

		out.print(head);
		out.print(body);
		out.print(foot);
		
		out.flush();
		
		zipOut.closeEntry();
	}
	
	@SuppressWarnings("deprecation")
	public String getStandardOpfHead(String booktype, String title, String title_kana, String serieseTitle, String serieseTitleKana, int serieseNum, String creator, String creator_kana,
			String publisher, String publisher_kana, String uuid, Date modifiedDate, int baseWidth, int baseHeight){
		String datatype = booktype; //"comic";// "magazine", "book"
		String date = String.format("%04d-%02d-%02dT00:00:00Z", modifiedDate.getYear()+1900, modifiedDate.getMonth()+1, modifiedDate.getDate());
		String head = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<package xmlns=\"http://www.idpf.org/2007/opf\" version=\"3.0\" xml:lang=\"ja\" unique-identifier=\"BookID\"\n" //=\"" + uuid + "\"\n"
				+ "prefix=\"rendition: http://www.idpf.org/vocab/rendition/#\n"
				+ "         prs: http://xmlns.sony.net/e-book/prs/\n"
				+ "         prism: http://prism.dummy.jp/\n"
				+ "         ebpaj: http://www.ebpaj.jp/\n"
				+ "         fixed-layout-jp: http://www.digital-comic.jp/\">\n"
				+ "<metadata xmlns:dc=\"http://purl.org/dc/elements/1.1/\">\n"
				+ "<!--  作品名 -->\n";
			head += titleTag(title, title_kana, serieseTitle, serieseTitleKana, serieseNum);
			head += "<!--  著者名 -->\n"
				+ authorTag(creator, creator_kana)
				+ "<!--  出版社名 -->\n"
				+ publisherTag(publisher, publisher_kana)
				+ "<!--  言語 -->\n"
				+ "<dc:language>ja</dc:language>\n"
				+ "<!--  ファイルid -->\n"
				+ "<dc:identifier id=\"BookID\">urn:uuid:" + uuid + "</dc:identifier>\n"
				+ "<!--  更新日 -->\n"
				+ "<meta property=\"dcterms:modified\">" + date + "</meta>\n"
				+ "<meta property=\"prs:datatype\">" + datatype + "</meta>\n"
				+ "<!-- Fixed-Layout Documents指定 -->\n"
				+ "<meta property=\"rendition:layout\">pre-paginated</meta>\n";
				//+ "<meta property=\"rendition:spread\">landscape</meta>\n";
			
			if(baseWidth > 0 && baseHeight > 0){
				head += "<!--  基準サイズ -->\n"
				+ "<meta property=\"fixed-layout-jp:viewport\">width=" + baseWidth + ", height=" + baseHeight + "</meta>\n";
			}
				head += "<!-- etc. -->\n"
				+ "<meta name=\"book-type\" content=\"comic\"/>\n"
				+ "<meta property=\"ebpaj:guide-version\">1.1</meta>\n"
				+ "</metadata>\n"
				+ "<manifest>\n"
				+ "<!-- navigation -->\n"
				+ "<item media-type=\"application/xhtml+xml\" id=\"toc\" href=\"navigation-documents.xhtml\" properties=\"nav\" />\n"
				+ "<item href=\"toc.ncx\" id=\"ncx\" media-type=\"application/x-dtbncx+xml\" />\n"
				+ "<!-- style -->\n"
				+ "<item media-type=\"text/css\" id=\"fixed-layout-jp\" href=\"style/fixed-layout-jp.css\"/>\n";
		
		return head;
	}
	private String getStandardOpfFoot(){
		return "</package>";
	}
	
	private String titleTag(String displayTitle, String displayTitleKana, String seriesTitle, String seriesTitleKana, int seriesPosition){
		StringBuilder sb = new StringBuilder();
		
		//if(seriesTitle == null || seriesTitle.length() == 0){
			sb.append("<dc:title id=\"title\">" + displayTitle + "</dc:title>\n");
			if(displayTitleKana != null && !displayTitleKana.isEmpty()){
				sb.append("<meta refines=\"#title\" property=\"file-as\">" + displayTitleKana + "</meta>\n");
			}
			
			if(seriesTitle != null && seriesTitle.length() > 0){
				sb.append("<meta property=\"prism:publicationName\" id=\"publicationName\">" + seriesTitle + "</meta>\n");
				if(seriesTitleKana != null && seriesTitleKana.length() > 0){
					sb.append("<meta refines=\"#publicationName\" property=\"file-as\">" + seriesTitleKana + "</meta>\n");
				}
				if(seriesPosition == 0){
					seriesPosition = 1;
				}
				sb.append("<meta property=\"prism:volume\" id=\"volume\">" + seriesPosition + "</meta>\n");
				//sb.append("<meta property=\"prism:number\" id=\"number\"></meta>\n");
			}
//		}
//		else{
//			sb.append("<dc:title id=\"t1\">" + displayTitle + "</dc:title>\n");
//			if(displayTitleKana != null && !displayTitleKana.isEmpty()){
//				sb.append("<meta refines=\"#t1\" property=\"title-type\">main</meta>\n");
//				sb.append("<meta refines=\"#t1\" property=\"file-as\">" + displayTitleKana + "</meta>\n");
//			}
//		}
	    
		return new String(sb);
	}
	private String authorTag(String creator, String creator_kana){
		
		if(creator == null || creator.isEmpty()){
			return "";
		}
		StringBuilder sb = new StringBuilder();
		sb.append("<dc:creator id=\"creator01\">" + creator + "</dc:creator>\n");
		sb.append("<meta refines=\"#creator01\" property=\"role\" scheme=\"marc:relators\">aut</meta>\n");
		if(creator_kana != null && !creator_kana.isEmpty()){
			sb.append("<meta refines=\"#creator01\" property=\"file-as\">" + creator_kana + "</meta>\n");
		}
		return new String(sb);
	}
	private String publisherTag(String publisher, String publisher_kana){
		if(publisher == null || publisher.isEmpty()){
			return "";
		}
		StringBuilder sb = new StringBuilder();
		sb.append("<dc:creator id=\"publisher\">" + publisher + "</dc:creator>\n");
		if(publisher_kana != null && !publisher_kana.isEmpty()){
			sb.append("<meta refines=\"#publisher\" property=\"file-as\">" + publisher_kana + "</meta>\n");
		}
		return new String(sb);
	}
	
	public String getStandardOpfBody(IImageFileInfoList list){
		StringBuilder sb = new StringBuilder();
		
		sb.append("<!-- image -->\n");
		int size = list.size();
		int index = 0;
		for(int i=0; i<size; i++){
			if(!list.get(i).isEnable()){
				continue;
			}
			String properties = "";
			if(!getImageProperties(index).equals("")){
				properties = " properties=\"" + getImageProperties(index) + "\"";
			}
			sb.append("<item media-type=\"" + getMimeType("jpg") + "\" id=\"" + getImageId(index) + "\" href=\"image/" + getImageFileName(index, ".jpg") + "\"" + properties + "/>\n");
			index++;
		}

		sb.append("<!-- xhtml -->\n");
		index = 0;
		for(int i=0; i<list.size(); i++){
			if(!list.get(i).isEnable()){
				continue;
			}
			sb.append("<item media-type=\"application/xhtml+xml\" id=\"" + getXhtmlId(index)+ "\" href=\"xhtml/" + getXhtmlFileName(index) + "\" properties=\"svg\" fallback=\"" + getImageId(index) + "\"/>\n");
			index++;
		}

		sb.append("</manifest>\n");
		sb.append("<spine page-progression-direction=\"rtl\" toc=\"ncx\">\n");
		
		sb.append("<!-- itemref -->\n");
		index = 0;
		String prevSpread = Constant.PAGESPREAD_AUTO;
		for(int i=0; i<size; i++){
			IImageFileInfo info = list.get(i);
			if(!info.isEnable()){
				continue;
			}
			String properties = "";
			String curSpread = info.getFilterParam().getPageSpread();
			
			//if(!getItemRefProperties(index).equals("")){
			//	properties = " properties=\"" + getItemRefProperties(index) + "\"";
			//}
			String spreadProp = getItemRefProperties(prevSpread, curSpread);
			if(!spreadProp.isEmpty()){
				properties = " properties=\"" + spreadProp + "\"";
			}
			prevSpread = propToConstant(spreadProp);
			
			sb.append("<itemref linear=\"yes\" idref=\"" + getXhtmlId(index) + "\"" + properties + "/>\n");
			index++;
		}
		
		sb.append("</spine>\n");

		return new String(sb);
	}
	
	private String getImageProperties(int index){
		if(index == 0){
			return "cover-image";
		}
		else{
			return "";
		}
	}
	private String getItemRefProperties(int index){
		if(index == 0){
			return "rendition:page-spread-center";
		}
		else{
			if(index%2 == 1){
				return "page-spread-right";
			}
			else{
				return "page-spread-left";
			}
		}
	}
	private String getItemRefProperties(String prev, String current){
		if(true){
			String next = "";
			if(current.equals(Constant.PAGESPREAD_CENTER)){
				next = "rendition:page-spread-center";
			}
			else if(current.equals(Constant.PAGESPREAD_RIGHT)){
				next = "page-spread-right";
			}
			else if(current.equals(Constant.PAGESPREAD_LEFT)){
				next = "page-spread-left";
			}
			else{
				next = "";
			}
			return next;
		}
		else{
			String next = "rendition:page-spread-center";
			if(current.equals(Constant.PAGESPREAD_AUTO)){
				if(prev.equals(Constant.PAGESPREAD_CENTER)){
					next = "page-spread-right";
				}
				else if(prev.equals(Constant.PAGESPREAD_RIGHT)){
					next = "page-spread-left";
				}
				else if(prev.equals(Constant.PAGESPREAD_LEFT)){
					next = "page-spread-right";
				}
			}
			else{
				if(current.equals(Constant.PAGESPREAD_CENTER)){
					next = "rendition:page-spread-center";
				}
				else if(current.equals(Constant.PAGESPREAD_RIGHT)){
					next = "page-spread-right";
				}
				else if(current.equals(Constant.PAGESPREAD_LEFT)){
					next = "page-spread-left";
				}
			}
			
			return next;
		}
	}
	
	private String propToConstant(String spread){
		if(spread.equals("page-spread-right")){
			return Constant.PAGESPREAD_RIGHT;
		}
		if(spread.equals("page-spread-left")){
			return Constant.PAGESPREAD_LEFT;
		}
		return Constant.PAGESPREAD_CENTER;
	}

	
	
	private String getXhtml(Dimension size, String imageFilename){
		StringBuilder sb = new StringBuilder();
				
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<!DOCTYPE html>\n");
		sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:epub=\"http://www.idpf.org/2007/ops\" xml:lang=\"ja\">\n");
		sb.append("<head>\n");
		sb.append("<meta charset=\"UTF-8\"/>\n");
		sb.append("<title></title>\n");
		sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"../style/fixed-layout-jp.css\"/>\n");
		
		sb.append("<meta name=\"viewport\" content=\"width=" + size.width + ", height=" + size.height + "\"/>\n");
		sb.append("</head>\n");
		sb.append("<body>\n");
		sb.append("<div class=\"main\">\n");
		sb.append("<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" width=\"100%\" height=\"100%\" viewBox=\"0 0 " + size.width + " " + size.height + "\">\n");
		sb.append("<image width=\"" + size.width + "\" height=\"" + size.height + "\" xlink:href=\"../image/" + imageFilename + "\"/>\n");
		sb.append("</svg>\n");
		sb.append("</div>\n");
		sb.append("</body>\n");

		sb.append("</html>");
		
		return new String(sb);
	}

	private String convEscape(String text){
		text = text.replaceAll("&", "&amp;");
		text = text.replaceAll("<", "&lt;");
		text = text.replaceAll(">", "&gt;");
		return text;
	}

	private String getMimeType(String suffix){
		if(suffix.equals("jpg")){
			return "image/jpeg";
		}
		else if(suffix.equals("png")){
			return "image/png";
		}
		else if(suffix.equals("gif")){
			return "image/gif";
		}
		else{
			return "image/jpeg";
		}
	}
	
	private String getImageId(int index){
		return String.format("I%04d", index);
	}
	private String getXhtmlId(int index){
		return String.format("P%04d", index);
	}
	private String getImageFileName(int index, String suffix){
		return getImageId(index) + suffix;
	}
	private String getXhtmlFileName(int index){
		return getXhtmlId(index) + ".xhtml";
	}
}
