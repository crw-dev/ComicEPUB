/**
 * フォルダ内の画像ファイル情報クラス
 */
package jp.crwdev.app.container.folder;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import jp.crwdev.app.container.ImageFileInfoBase;

public class FolderImageFileInfo extends ImageFileInfoBase {

	/** 親フォルダパス */
	private String mParent;
	/** ファイル名 */
	private String mFilename;

	/**
	 * コンストラクタ
	 * @param filepath フルパス
	 * @throws Exception
	 */
	public FolderImageFileInfo(String filepath) throws Exception{
		super();
		File file = new File(filepath);
		initialize(file.getParent(), file.getName());
	}

	/**
	 * コンストラクタ
	 * @param parent 親フォルダパス
	 * @param filename ファイル名
	 * @throws Exception
	 */
	public FolderImageFileInfo(String parent, String filename) throws Exception{
		super();
		initialize(parent, filename);
	}
	
	/**
	 * 初期化
	 * @param parent 親フォルダパス
	 * @param filename ファイル名
	 * @throws Exception
	 */
	private void initialize(String parent, String filename) throws Exception{
		
		mParent = parent;
		mFilename = filename;
		
		loadBasicParams();
	}
	
	@Override
	public String getFileName() {
		return mFilename;
	}

	@Override
	public String getFullPath(){
		return mParent + File.separatorChar + mFilename;
	}
	
	@Override
	public InputStream getInputStream() {
		try {
			return new FileInputStream(getFullPath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void release() {
		// NOP
	}

}
