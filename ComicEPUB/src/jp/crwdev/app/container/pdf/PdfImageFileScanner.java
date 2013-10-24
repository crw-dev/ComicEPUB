package jp.crwdev.app.container.pdf;

import java.io.File;

import javax.swing.JOptionPane;


import jp.crwdev.app.interfaces.IImageFileInfoList;
import jp.crwdev.app.interfaces.IImageFileScanner;


public class PdfImageFileScanner implements IImageFileScanner {

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
					gs.open(path);
					return true;
				}
				else{
					JOptionPane.showMessageDialog(null, "default.iniにGhostScriptのコマンドライン実行ファイルパスを設定して下さい。\n(例: ghostScriptPath=C:/gs/gs9.10/bin/gswin64c.exe)");
				}
				
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public void close() {
	}

	@Override
	public IImageFileInfoList getImageFileInfoList() {
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
