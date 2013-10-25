package jp.crwdev.app.container.pdf;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.PdfImageObject;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.jmupdf.enums.ImageType;
import com.jmupdf.page.Page;
import com.jmupdf.page.PageRect;
import com.jmupdf.page.PageRenderer;
import com.jmupdf.pdf.PdfDocument;

import jp.crwdev.app.container.ImageFileInfoBase;
import jp.crwdev.app.container.pdf.GhostscriptUtil.GhostscriptUtilEventListener;
import jp.crwdev.app.gui.DebugWindow;
import jp.crwdev.app.util.DPIRenderListener;
import jp.crwdev.app.util.DPIRenderListener.DPI;

public class PdfImageFileInfo extends ImageFileInfoBase implements GhostscriptUtilEventListener {

	/** PdfImageObject */
	private PdfImageObject mPdfImage = null;
	/** Name */
	private int mNumber = 0;
	
	private GhostscriptUtil mGSUtil = null;
	private PdfDocument mPdfDocument = null;
	private PdfReaderContentParser mParser = null;
	
	public PdfImageFileInfo(ImageRenderInfo info){
		super();
		try {
			PdfImageObject image = info.getImage();
			mPdfImage = image;
			String filetype = image.getFileType();
			mFormat = filetype;
			mWidth = 0;
			mHeight = 0;
			mNumber = info.getRef().getNumber();
			
		}catch(Exception e){
			
		}
	}
	
	public PdfImageFileInfo(GhostscriptUtil gs, int page){
		super();
		mGSUtil = gs;
		mFormat = "jpeg";
		mWidth = 0;
		mHeight = 0;
		mNumber = page;
	}
	
	public PdfImageFileInfo(PdfDocument doc, PdfReaderContentParser parser, int page){
		super();
		mPdfDocument = doc;
		mParser = parser;
		mFormat = "jpeg";
		mWidth = 0;
		mHeight = 0;
		mNumber = page;
	}
	
	public PdfImageFileInfo(int number, PdfImageObject image, int width, int height, String format){
		super();
		mNumber = number;
		mPdfImage = image;
		mWidth = width;
		mHeight = height;
		mFormat = format;
	}
	
	@Override
	public void update() {
		if(mWidth != 0 && mHeight != 0){
			return;
		}
		if(mPdfImage != null){
			try {
				BufferedImage buffered = mPdfImage.getBufferedImage();
				mWidth = buffered.getWidth();
				mHeight = buffered.getHeight();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	

	@Override
	public void onComplete(int page, BufferedImage image) {
		if(image != null){
			mWidth = image.getWidth();
			mHeight = image.getHeight();
		}
	}

	@Override
	public String getFileName() {
		return Integer.toString(mNumber);
	}
	
	@Override
	public String getSortString() {
		return String.format("%05d", mNumber);
	}

	@Override
	public String getFullPath() {
		return Integer.toString(mNumber);
	}

	@Override
	public InputStream getInputStream() {
		try {
			if(mPdfDocument != null){
				return null;
			}
			if(mGSUtil != null){
				File file = mGSUtil.getPageAsFile(mNumber);
				if(file != null){
					return new FileInputStream(file);
				}
			}
			if(mPdfImage != null){
				return new ByteArrayInputStream(mPdfImage.getImageAsBytes());
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public BufferedImage getImage() {
		if(mPdfDocument != null){
			Page page = mPdfDocument.getPage(mNumber);
			PageRect boundBox = page.getBoundBox();
			
			DPIRenderListener listener = new DPIRenderListener();
			try {
				mParser.processContent(mNumber, listener);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			DPI dpi = listener.getDPI();
			float originalResolution = dpi.getResolution();
			float scale = originalResolution / 72.0f;
			
			PageRenderer render = new PageRenderer(page, scale, Page.PAGE_ROTATE_AUTO, ImageType.IMAGE_TYPE_RGB);
			
			render.render(true);
			
			DebugWindow.log("getImage", "page=" + mNumber);
			DebugWindow.log("getImage", "resolution=" + originalResolution);
			DebugWindow.log("getImage", "size=(" + boundBox.getWidth() + "," + boundBox.getHeight() + ")");
			DebugWindow.log("getImage", "dpiSize=(" + dpi.width + "," + dpi.height + ")");

			BufferedImage image = render.getImage();
			
			DebugWindow.log("getImage", "imageSize=(" + image.getWidth() + "," + image.getHeight() + ")");

			render.dispose();
			
			return image;
		}
		
//		if(mPdfDecoder != null){
//			try {
//				mPdfDecoder.setPageParameters(1.0f, mNumber);
//				//mPdfDecoder.decodePage(mNumber);
//				//mPdfDecoder.waitForDecodingToFinish();
//				return BufferedImageIO.prepareBufferedImage(mPdfDecoder.getPageAsTransparentImage(mNumber));
//				//return mPdfDecoder.getPageAsImage(mNumber);
//			} catch (PdfException e) {
//				e.printStackTrace();
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		return null;
	}

	@Override
	public void release() {
		// TODO Auto-generated method stub

	}


}
