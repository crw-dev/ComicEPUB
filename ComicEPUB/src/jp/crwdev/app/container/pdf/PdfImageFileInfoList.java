package jp.crwdev.app.container.pdf;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.jpedal.PdfDecoder;
import org.jpedal.objects.PdfPageData;

import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.PdfImageObject;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.RenderListener;
import com.itextpdf.text.pdf.parser.TextRenderInfo;

import jp.crwdev.app.container.ImageFileInfoList;
import jp.crwdev.app.interfaces.IImageFileInfoList;

public class PdfImageFileInfoList extends ImageFileInfoList {

	/** PdfReader */
	protected PdfReader mPdfReader = null;
	
	protected PdfDecoder mPdfDecoder = null;
	
	
	/**
	 * コンストラクタ
	 * @param pdfReader 入力PdfファイルReader
	 */
	public PdfImageFileInfoList(PdfReader pdfReader) {
		super();
		setList(pdfReader);
	}
	
	public PdfImageFileInfoList(PdfDecoder pdfDecoder){
		super();
		setList(pdfDecoder);
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void setList(PdfDecoder pdfDecoder){
		mPdfDecoder = pdfDecoder;
		
		if(mPdfDecoder != null){
			PdfPageData pageData = mPdfDecoder.getPdfPageData();
			int pageCount = pageData.getPageCount();
			
			for(int page=1; page<=pageCount; page++){
				int width = pageData.getCropBoxWidth(page);
				int height = pageData.getCropBoxHeight(page);
				add(new PdfImageFileInfo(mPdfDecoder, page, width, height));
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
//				// TODO Auto-generated catch block
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

}
