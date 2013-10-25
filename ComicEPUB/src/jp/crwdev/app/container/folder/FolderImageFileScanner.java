/**
 * フォルダ画像ファイルスキャナ
 */
package jp.crwdev.app.container.folder;

import java.io.File;

import jp.crwdev.app.interfaces.IImageFileInfoList;
import jp.crwdev.app.interfaces.IImageFileScanner;


public class FolderImageFileScanner implements IImageFileScanner {

	/** フォルダ */
	private File mFolder;

	@Override
	public boolean open(String path) {
		File dir = new File(path);
		if(dir.isDirectory()){
			mFolder = dir;
			return true;
		}
		return false;
	}

	@Override
	public IImageFileInfoList getImageFileInfoList() {
		return new FolderImageFileInfoList(mFolder);
	}

	@Override
	public String getOpenFilePath(){
		return mFolder.getAbsolutePath();
	}

	@Override
	public void close() {
		mFolder = null;		
	}


}
