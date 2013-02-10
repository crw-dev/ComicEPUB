package jp.crwdev.app.container.rar;

import java.io.IOException;
import java.io.InputStream;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;


import jp.crwdev.app.container.ImageFileInfoBase;

public class RarImageFileInfo extends ImageFileInfoBase {

	/** 入力Rarファイル */
	private Archive mRarArchive;
	/** ファイルヘッダ */
	private FileHeader mFileHeader;
	/** Rarファイル内の画像ファイル名 */
	private String mEntryName;

	/**
	 * コンストラクタ
	 * @param entryName Rarファイル内の画像ファイル名
	 * @param rarArchive 入力Rarファイル
	 * @throws Exception
	 */
	public RarImageFileInfo(FileHeader fileHeader, Archive rarArchive) throws Exception{
		super();
		initialize(fileHeader, rarArchive);
	}
	
	private void initialize(FileHeader fileHeader, Archive rarArchive) throws Exception{
		mFileHeader = fileHeader;
		mEntryName = fileHeader.getFileNameString();
		mRarArchive = rarArchive;
		
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
		try {
			return mRarArchive.getInputStream(mFileHeader);
		} catch (RarException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void release() {
		// NOP
	}

}
