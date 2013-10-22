package jp.crwdev.app.container.pdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.jpedal.PdfDecoder;
import org.jpedal.exception.PdfException;
import com.itextpdf.text.pdf.PdfReader;

import jp.crwdev.app.interfaces.IImageFileInfoList;
import jp.crwdev.app.interfaces.IImageFileScanner;


public class PdfImageFileScanner implements IImageFileScanner {

	/** PDFドキュメント */
	private PdfReader mPdfReader = null;
	/** ファイルパス */
	private String mFilePath;
	
	private boolean mSupportPDFRender = true;
	private boolean mSupportJpedl = true;
	private PdfDecoder mPdfDecoder;
	
	@Override
	public boolean open(String path) {
		if(path.contains(".pdf")){
			mSupportPDFRender = true;
			try {
				File file = new File(path);
				if(!file.exists()){
					return false;
				}
				
				mPdfReader = new PdfReader(path);
				mFilePath = path;
				
				if(mSupportJpedl){
					mPdfDecoder = new PdfDecoder(true);
					
					FileInputStream input = new FileInputStream(file);
					try {
						mPdfDecoder.openPdfFileFromInputStream(input, false);
					} catch (PdfException e) {
						e.printStackTrace();
					}
				}
				
				return true;
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public void close() {
		if(mPdfReader != null){
			mPdfReader.close();
			mPdfReader = null;
		}
		if(mPdfDecoder != null){
			mPdfDecoder.closePdfFile();
			mPdfDecoder = null;
		}
	}

	@Override
	public IImageFileInfoList getImageFileInfoList() {
		if(this.mSupportJpedl){
			return new PdfImageFileInfoList(mPdfDecoder);
		}
		else{
			return new PdfImageFileInfoList(mPdfReader);
		}
	}

	@Override
	public String getOpenFilePath() {
		return mFilePath;
	}

}
