/**
 * Rar画像ファイルスキャナ
 */
package jp.crwdev.app.container.rar;

import java.io.File;
import java.io.IOException;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;


import jp.crwdev.app.interfaces.IImageFileInfoList;
import jp.crwdev.app.interfaces.IImageFileScanner;

public class RarFileScanner implements IImageFileScanner {

	/** 入力Rarファイル */
	private Archive mRarArchive;
	/** ファイルパス */
	private String mFilePath;
	
	@Override
	public boolean open(String path) {
		if(path.contains(".rar")){
			try {
				mRarArchive = new Archive(new File(path));
				mFilePath = path;
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (RarException e) {
				e.printStackTrace();
			}
		}
		return false;
	}


	@Override
	public IImageFileInfoList getImageFileInfoList() {
		return new RarImageFileInfoList(mRarArchive);
	}

	@Override
	public String getOpenFilePath() {
		return mFilePath;
	}
	
	@Override
	public void close() {
		if(mRarArchive != null){
			try {
				mRarArchive.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			mRarArchive = null;
		}
	}


}
