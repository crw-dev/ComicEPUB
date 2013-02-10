/**
 * Zipファイル内の画像ファイル情報クラス
 */
package jp.crwdev.app.container.zip;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipException;

import org.apache.commons.compress.archivers.zip.ZipFile;

import jp.crwdev.app.container.ImageFileInfoBase;


public class ZipImageFileInfo extends ImageFileInfoBase {
	
	/** 入力Zipファイル */
	private ZipFile mZipFile;
	/** Zipファイル内の画像ファイル名 */
	private String mEntryName;

	/**
	 * コンストラクタ
	 * @param entryName Zipファイル内の画像ファイル名
	 * @param zipFile 入力Zipファイル
	 * @throws Exception
	 */
	public ZipImageFileInfo(String entryName, ZipFile zipFile) throws Exception{
		super();
		initialize(entryName, zipFile);
	}
	
	/**
	 * 初期化
	 * @param entryName Zipファイル内の画像ファイル名
	 * @param zipFile 入力Zipファイル
	 * @throws Exception
	 */
	private void initialize(String entryName, ZipFile zipFile) throws Exception{
		mEntryName = entryName;
		mZipFile = zipFile;
		
		loadBasicParams();
	}

	@Override
	public String getFileName() {
		return mEntryName;
	}
	
	@Override
	public String getFullPath() {
		return mEntryName;
	}

	@Override
	public InputStream getInputStream() {
		if(mZipFile != null){
			try {
				return mZipFile.getInputStream(mZipFile.getEntry(mEntryName));
			} catch (ZipException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public void release() {
		mZipFile = null;
		mEntryName = null;
	}
	
}
