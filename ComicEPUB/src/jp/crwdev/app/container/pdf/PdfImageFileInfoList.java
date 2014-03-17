package jp.crwdev.app.container.pdf;

import java.io.IOException;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.RenderListener;
import com.itextpdf.text.pdf.parser.TextRenderInfo;
import com.jmupdf.pdf.PdfDocument;

import jp.crwdev.app.container.ImageFileInfoList;
import jp.crwdev.app.interfaces.IImageFileInfoList;

public class PdfImageFileInfoList extends ImageFileInfoList {

	/** PdfReader */
	protected PdfReader mPdfReader = null;
	
	protected GhostscriptUtil mGSUtil;
	protected PdfDocument mPdfDocument;
	protected PdfReaderContentParser mParser;
	
	/**
	 * コンストラクタ
	 * @param pdfReader 入力PdfファイルReader
	 */
	public PdfImageFileInfoList(PdfReader pdfReader) {
		super();
		setList(pdfReader);
	}
	
	public PdfImageFileInfoList(GhostscriptUtil gs){
		super();
		setList(gs);
	}
	
	public PdfImageFileInfoList(PdfDocument doc, PdfReaderContentParser parser){
		super();
		setList(doc, parser);
	}
	
	/**
	 * コンストラクタ
	 */
	protected PdfImageFileInfoList() {
		super();
	}
	
	private void setList(PdfReader pdfReader){
		mPdfReader = pdfReader;
		
		
		if(mPdfReader != null){
			PdfReaderContentParser parser = new PdfReaderContentParser(mPdfReader);
			int pageCount = mPdfReader.getNumberOfPages();
			
			MyRenderImageListener listener = new MyRenderImageListener();
			
			for(int page=1; page<=pageCount; page++){
				try {
					parser.processContent(page, listener);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void setList(GhostscriptUtil gs){
		mGSUtil = gs;
		
		if(mGSUtil != null){
			int pageCount = mGSUtil.getNumOfPages();
			
			for(int page=1; page<=pageCount; page++){
				add(new PdfImageFileInfo(mGSUtil, page));
			}
		}
	}

	private void setList(PdfDocument doc, PdfReaderContentParser parser){
		mPdfDocument = doc;
		mParser = parser;
		
		if(mPdfDocument != null){
			int pageCount = mPdfDocument.getPageCount();
			
			for(int page=1; page<=pageCount; page++){
				add(new PdfImageFileInfo(mPdfDocument, mParser, page));
			}
		}
	}

	private class MyRenderImageListener implements RenderListener {

		public MyRenderImageListener(){
		}
		
		@Override
		public void renderImage(ImageRenderInfo info) {
//			try {
//				PdfImageObject image = info.getImage();
//				String filetype = image.getFileType();
//				PdfName filter = (PdfName)image.get(PdfName.FILTER);
//				int number = info.getRef().getNumber();
////				if(PdfName.DCTDECODE.equals(filter)){
////					number = info.getRef().getNumber();
////				}
////				else{
////					number = info.getRef().getNumber();
////				}
//				BufferedImage buffered = image.getBufferedImage();
//				int width = buffered.getWidth();
//				int height = buffered.getHeight();
				
				add(new PdfImageFileInfo(info));
				
//				System.out.println("fileType=" + filetype + " number=" + number + " w=" + width + " h=" + height);
				
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
			
			
		}

		@Override
		public void beginTextBlock() {
		}

		@Override
		public void endTextBlock() {
		}

		@Override
		public void renderText(TextRenderInfo arg0) {
		}
		
	}
	
	@Override
	public IImageFileInfoList renew() {
		PdfImageFileInfoList list = new PdfImageFileInfoList();
		
		list.mPdfReader = mPdfReader;
		
		return renewInternal(list);
	}

	@Override
	public void release(){
		if(mPdfDocument != null){
//			mPdfDocument.dispose();
//			mPdfDocument = null;
		}
	}
}
