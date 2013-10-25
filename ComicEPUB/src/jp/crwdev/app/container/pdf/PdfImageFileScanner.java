package jp.crwdev.app.container.pdf;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;


import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.jmupdf.exceptions.DocException;
import com.jmupdf.exceptions.DocSecurityException;
import com.jmupdf.pdf.PdfDocument;

import jp.crwdev.app.gui.DebugWindow;
import jp.crwdev.app.interfaces.IImageFileInfoList;
import jp.crwdev.app.interfaces.IImageFileScanner;


public class PdfImageFileScanner implements IImageFileScanner {

	/** ファイルパス */
	private String mFilePath;
	
	private boolean mSupportJMuPDF = true;
	private PdfDocument mPdfDocument;
	private PdfReader mPdfReader;
	
	private boolean mSupportGS = true;
	
	@Override
	public boolean open(String path) {
		if(path.contains(".pdf")){
			String err = "";
			try {
				File file = new File(path);
				if(!file.exists()){
					return false;
				}
				
				mFilePath = path;
				
				
				if(mSupportJMuPDF){
					DebugWindow.log("open pdf " + path);
					mPdfDocument = new PdfDocument(path, 20);
					mPdfReader = new PdfReader(path);
					return true;
				}
				else{
					GhostscriptUtil gs = GhostscriptUtil.getInstance();
					if(mSupportGS && gs.isEnable()){
						gs.open(path);
						return true;
					}
					else{
						JOptionPane.showMessageDialog(null, "default.iniにGhostScriptのコマンドライン実行ファイルパスを設定して下さい。\n(例: ghostScriptPath=C:/gs/gs9.10/bin/gswin64c.exe)");
					}
				}
				
			} catch (OutOfMemoryError e) {
				err = e.getMessage();
				e.printStackTrace();
			} catch (DocException e) {
				err = e.getMessage();
				e.printStackTrace();
			} catch (DocSecurityException e) {
				err = e.getMessage();
				e.printStackTrace();
			} catch (IOException e) {
				err = e.getMessage();
				e.printStackTrace();
			}
			if(err.length() > 0){
				JOptionPane.showMessageDialog(null, err);
			}
		}
		return false;
	}

	@Override
	public void close() {
	}

	@Override
	public IImageFileInfoList getImageFileInfoList() {
		if(mSupportJMuPDF){
			return new PdfImageFileInfoList(mPdfDocument, new PdfReaderContentParser(mPdfReader));
		}
		if(mSupportGS){
			return new PdfImageFileInfoList(GhostscriptUtil.getInstance());
		}
		return null;
	}

	@Override
	public String getOpenFilePath() {
		return mFilePath;
	}

}
