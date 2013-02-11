package jp.crwdev.app.container.pdf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfImage;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStream;
import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.PdfImageObject;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.RenderListener;
import com.itextpdf.text.pdf.parser.TextRenderInfo;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;

import jp.crwdev.app.container.ImageFileInfoBase;

public class PdfImageFileInfo extends ImageFileInfoBase {

	/** PdfImageObject */
	private PdfImageObject mPdfImage = null;
	/** Name */
	private int mNumber = 0;
	
	public PdfImageFileInfo(int number, PdfImageObject image, int width, int height, String format){
		super();
		mNumber = number;
		mPdfImage = image;
		mWidth = width;
		mHeight = height;
		mFormat = format;
	}
	
	@Override
	public String getFileName() {
		return Integer.toString(mNumber);
	}

	@Override
	public String getFullPath() {
		return Integer.toString(mNumber);
	}

	@Override
	public InputStream getInputStream() {
		try {
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
	public void release() {
		// TODO Auto-generated method stub

	}

}
