package jp.crwdev.app.container.pdf;

import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import com.itextpdf.text.pdf.PdfReader;

import jp.crwdev.app.interfaces.IImageFileInfoList;
import jp.crwdev.app.interfaces.IImageFileScanner;


public class PdfImageFileScanner implements IImageFileScanner {

	/** PDFドキュメント */
	private PdfReader mPdfReader = null;
	/** ファイルパス */
	private String mFilePath;
	
	private boolean mSupportGS = true;
	
	@Override
	public boolean open(String path) {
		if(path.contains(".pdf")){
			try {
				File file = new File(path);
				if(!file.exists()){
					return false;
				}
				
				mFilePath = path;
				
				GhostscriptUtil gs = GhostscriptUtil.getInstance();
				if(mSupportGS && gs.isEnable()){
					mPdfReader = new PdfReader(path);
					gs.open(path, mPdfReader.getNumberOfPages());
					return true;
				}
				else{
					JOptionPane.showMessageDialog(null, "default.iniにGhostScriptのコマンドライン実行ファイルパスを設定して下さい。\n(例: ghostScriptPath=C:/gs/gs9.10/bin/gswin64c.exe)");
				}
				
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
	}

	@Override
	public IImageFileInfoList getImageFileInfoList() {
		if(mSupportGS){
			return new PdfImageFileInfoList(GhostscriptUtil.getInstance());
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
