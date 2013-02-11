package jp.crwdev.app.container.pdf;

import java.io.File;
import java.io.IOException;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.itextpdf.text.pdf.PdfReader;

import jp.crwdev.app.interfaces.IImageFileInfoList;
import jp.crwdev.app.interfaces.IImageFileScanner;

public class PdfImageFileScanner implements IImageFileScanner {

	/** PDFドキュメント */
	private PdfReader mPdfReader = null;
	/** ファイルパス */
	private String mFilePath;
	
	@Override
	public boolean open(String path) {
		if(path.contains(".pdf")){
			try {
				File file = new File(path);
				if(!file.exists()){
					return false;
				}
				
				mPdfReader = new PdfReader(path);
				mFilePath = path;
				
				return true;
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
	}

	@Override
	public IImageFileInfoList getImageFileInfoList() {
		return new PdfImageFileInfoList(mPdfReader);
	}

	@Override
	public String getOpenFilePath() {
		return mFilePath;
	}

}
