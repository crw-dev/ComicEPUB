package jp.crwdev.app.container.pdf;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfImage;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStream;
import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.PdfImageObject;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.RenderListener;
import com.itextpdf.text.pdf.parser.TextRenderInfo;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;

import jp.crwdev.app.BufferedImageIO;
import jp.crwdev.app.container.ImageFileInfoBase;
import jp.crwdev.app.container.pdf.GhostscriptUtil.GhostscriptUtilEventListener;

public class PdfImageFileInfo extends ImageFileInfoBase implements GhostscriptUtilEventListener {

	/** PdfImageObject */
	private PdfImageObject mPdfImage = null;
	/** Name */
	private int mNumber = 0;
	
	private GhostscriptUtil mGSUtil = null;
	
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
		if(mGSUtil != null){
//			mGSUtil.getPageAsImage(mNumber, this);
			//BufferedImage buffered = mGSUtil.getPageAsImage(mNumber);
			//if(buffered != null){
			//	mWidth = buffered.getWidth();
			//	mHeight = buffered.getHeight();
			//}
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
