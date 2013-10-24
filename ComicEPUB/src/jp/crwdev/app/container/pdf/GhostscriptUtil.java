package jp.crwdev.app.container.pdf;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.Matrix;
import com.itextpdf.text.pdf.parser.PdfImageObject;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.RenderListener;
import com.itextpdf.text.pdf.parser.TextRenderInfo;

import jp.crwdev.app.BufferedImageIO;
import jp.crwdev.app.util.InifileProperty;

public class GhostscriptUtil {

	private static GhostscriptUtil mInstance;
	
	private static String mGSC = "C:\\Program Files\\gs\\gs9.10\\bin\\gswin32c.exe";
	private static String mTmpImageHead = "";
	private static String mTmpImageName = "_%05d.jpg";
	private static String mTmpFolder = "tmp\\";

	private static PdfReader mPdfReader;
	private static PdfReaderContentParser mParser;
	
	private static String mPdfFilePath;
	
	
	private static int mPageCount = 0;
	
	public interface GhostscriptUtilEventListener {
		void onComplete(int page, BufferedImage image);
	}
	
	static public GhostscriptUtil getInstance(){
		if(mInstance == null){
			mInstance = new GhostscriptUtil();
			mInstance.setGSC(InifileProperty.getInstance().getGhostScriptPath());
		}
		return mInstance;
	}
	
	protected GhostscriptUtil(){
		
	}
	public void setGSC(String gsc){
		mGSC = gsc;
	}
	
	public boolean isEnable(){
		File file = new File(mGSC);
		return file.exists();
	}
	
	public int getNumOfPages(){
		return mPageCount;
	}
	
	public boolean open(String path){
		mPdfFilePath = path;
		try {
			if(mPdfReader != null){
				mPdfReader.close();
			}
			mPdfReader = new PdfReader(path);
			mParser = new PdfReaderContentParser(mPdfReader);
			
			File file = new File(path);
			String name = file.getName();
			mTmpImageHead = name.substring(0, name.length() - 4);
			
			File dir = new File(mTmpFolder);
			if(!dir.exists()){
				dir.mkdirs();
			}
			mPageCount =  mPdfReader.getNumberOfPages();
			
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private class DPI {
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
		
		public String getString(){
			int x = (int)Math.round(dpiX);
			int y = (int)Math.round(dpiY);
			return x + "x" + y;
		}
	}
	
	private DPI getDPI(int page){
		
		DPIRenderListener listener = new DPIRenderListener();
		try {
			mParser.processContent(page, listener);
			return listener.getDPI();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new DPI(72f, 72f);
	}
	
	private class DPIRenderListener implements RenderListener {

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
	
	public void close(){
		if(mPdfReader != null){
			mPdfReader.close();
			mPdfReader = null;
		}
		if(mPdfFilePath != null){
			mPdfFilePath = null;
			File dir = new File(mTmpFolder);
			if(dir.exists()){
				if(!dir.delete()){
					dir.deleteOnExit();
				}
			}
		}
	}
	
	
	public void getPageAsImage(int page, GhostscriptUtilEventListener event){
		final int fPage = page;
		final GhostscriptUtilEventListener fEvent = event;
		new Thread(){
			public void run(){
				BufferedImage image = getPageAsImage(fPage);
				if(fEvent != null){
					fEvent.onComplete(fPage, image);
				}
			}
		}.start();
	}
	public BufferedImage getPageAsImage(int page){
		File file = getPageAsFile(page);
		if(file != null){
			FileInputStream in = null;
			try {
				in = new FileInputStream(file);
				return BufferedImageIO.read(in, true);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				if(in != null){
					try {
						in.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}
	
	public File getPageAsFile(int page){
		if(page < 1 || mPageCount < page){
			return null;
		}
		
		
		String imageName = String.format(mTmpImageHead + mTmpImageName, page);
		File file = new File(mTmpFolder + imageName);
		if(file.exists()){
			return file;
		}
		else{
			Runtime r = Runtime.getRuntime();
			DPI dpi = getDPI(page);
			String command = mGSC + " -dSAFER -dBATCH -dFirstPage=" + page + " -dLastPage=" + page + " -dNOPAUSE -sDEVICE=jpeg -dDisplayFormat=16#30804 -dJPEGQ=100 -dQFactor=1.0";
			command += " -r" + dpi.getString();
			command += " -sOutputFile=\"" + mTmpFolder + imageName + "\" ";
			command += "\"" + mPdfFilePath + "\"";
			int result = 0;
			try {
				Process p = r.exec(command);
				result = p.waitFor();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(result == 0){
				if(file.exists()){
					return file;
				}
			}
			return null;
		}
	}
}
