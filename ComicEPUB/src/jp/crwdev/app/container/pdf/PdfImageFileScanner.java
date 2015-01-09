package jp.crwdev.app.container.pdf;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import jp.crwdev.app.gui.DebugWindow;
import jp.crwdev.app.interfaces.IImageFileInfoList;
import jp.crwdev.app.interfaces.IImageFileScanner;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.jmupdf.exceptions.DocException;
import com.jmupdf.exceptions.DocSecurityException;
import com.jmupdf.pdf.PdfDocument;


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

				mSupportJMuPDF = false;
				mSupportGS = false;

				DebugWindow.log("open pdf " + path);
				try {
					mPdfDocument = new PdfDocument(path, 20);
					mPdfReader = new PdfReader(path);
					mSupportJMuPDF = true;
					DebugWindow.log("use JMuPDF");
					return true;
				} catch (DocSecurityException e) {
					err = e.getMessage();
					e.printStackTrace();
				} catch (UnsatisfiedLinkError e){
					err = e.getMessage();
					e.printStackTrace();
				} catch (NoClassDefFoundError e){
					err = e.getMessage();
					e.printStackTrace();
				}

//				GhostscriptUtil gs = GhostscriptUtil.getInstance();
//				if(gs.isEnable()){
//					gs.open(path);
//					mSupportGS = true;
//					DebugWindow.log("use GhostScript");
//					return true;
//				}
//				else{
//					JOptionPane.showMessageDialog(null, "default.iniにGhostScriptのコマンドライン実行ファイルパスを設定して下さい。\n(例: ghostScriptPath=C:/gs/gs9.10/bin/gswin64c.exe)");
//				}

			} catch (OutOfMemoryError e) {
				err = e.getMessage();
				e.printStackTrace();
			} catch (DocException e) {
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
