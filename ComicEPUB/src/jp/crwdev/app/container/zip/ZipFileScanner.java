/**
 * Zip画像ファイルスキャナ
 */
package jp.crwdev.app.container.zip;

import java.io.IOException;

import org.apache.commons.compress.archivers.zip.ZipFile;

import jp.crwdev.app.interfaces.IImageFileInfoList;
import jp.crwdev.app.interfaces.IImageFileScanner;

public class ZipFileScanner implements IImageFileScanner {

	/** 入力Zipファイル */
	private ZipFile mZipFile;
	/** ファイルパス */
	private String mFilePath;
	
	@Override
	public boolean open(String path) {
		if(path.contains(".zip")){
			try {
				mZipFile = new ZipFile(path, "Windows-31J");
				mFilePath = path;
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public IImageFileInfoList getImageFileInfoList() {
		return new ZipImageFileInfoList(mZipFile);
	}
	
	@Override
	public String getOpenFilePath(){
		return mFilePath;
	}

	@Override
	public void close() {
		if(mZipFile != null){
			try {
				mZipFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			mZipFile = null;
		}
	}

}
