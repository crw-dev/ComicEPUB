package jp.crwdev.app.util;

import java.io.IOException;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.Matrix;
import com.itextpdf.text.pdf.parser.PdfImageObject;
import com.itextpdf.text.pdf.parser.RenderListener;
import com.itextpdf.text.pdf.parser.TextRenderInfo;

public class DPIRenderListener implements RenderListener {

	public class DPI {
		public float dpiX;
		public float dpiY;
		public int width;
		public int height;
		
		public DPI(float x, float y){
			dpiX = x;
			dpiY = y;
			width = 0;
			height = 0;
		}
		
		public float getResolution(){
			int dpi = Math.round((dpiX + dpiY)/2.0f);
			return dpi;
		}
		
		public String getString(){
			int dpi = (int)Math.round((dpiX + dpiY)/2.0f);
			return Integer.toString(dpi);
		}
	}

	private DPI mDPI = new DPI(300f, 300f);
	
	@Override
	public void beginTextBlock() {
	}

	@Override
	public void endTextBlock() {
	}

	@Override
	public void renderImage(ImageRenderInfo renderInfo) {
		
		try {
			PdfImageObject imgObj = renderInfo.getImage();
			PdfDictionary imgDic = imgObj.getDictionary();
			PdfName filter = imgDic.getAsName(PdfName.FILTER);
			
			int width = imgDic.getAsNumber( PdfName.WIDTH ).intValue();
			int height = imgDic.getAsNumber( PdfName.HEIGHT ).intValue();
			mDPI.width = width;
			mDPI.height = height;
			byte[] bytes = null;
			if ( filter == PdfName.JBIG2DECODE ) {
				//mDPI.width = width;
				//mDPI.height = height;
			} else {
				bytes = imgObj.getImageAsBytes();
				if (bytes == null) {
					//mDPI.width = width;
					//mDPI.height = height;
				} else {
					Image image = Image.getInstance(bytes);
					
					//if ( image.getDpiX() > 0 && image.getDpiY() > 0 ) {
					//	mDPI.dpiX = image.getDpiX();
					//	mDPI.dpiY = image.getDpiY();
					//}
					//else {
						Matrix ctm = renderInfo.getImageCTM();
						float widthScale = ctm.get( Matrix.I11 );
						float heightScale = ctm.get( Matrix.I22 );
						
						mDPI.dpiX = image.getScaledWidth() / widthScale * 72f;
						mDPI.dpiY = image.getScaledHeight() / heightScale * 72f;
					//}
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BadElementException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void renderText(TextRenderInfo arg0) {
	}
	
	public DPI getDPI(){
		return mDPI;
	}
	
}
