package jp.crwdev.app.container.rar;

import java.util.List;


import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;

import jp.crwdev.app.BufferedImageIO;
import jp.crwdev.app.container.ImageFileInfoList;
import jp.crwdev.app.interfaces.IImageFileInfoList;

public class RarImageFileInfoList extends ImageFileInfoList {

	
	/** 入力Zipファイル */
	protected Archive mRarArchive;

	/**
	 * コンストラクタ
	 * @param zipFile 入力Zipファイル
	 */
	public RarImageFileInfoList(Archive rarArchive) {
		super();
		setList(rarArchive);
	}
	
	/**
	 * コンストラクタ
	 */
	protected RarImageFileInfoList(){
		super();
	}

	
	private void setList(Archive rarArchive){
		mRarArchive = rarArchive;
		clear();

		List<FileHeader> headers = rarArchive.getFileHeaders();
		for(FileHeader header : headers){
			String filename = header.getFileNameString();
			if(BufferedImageIO.isSupport(getSuffix(filename))){
				try {
					add(new RarImageFileInfo(header, mRarArchive));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public IImageFileInfoList renew() {
		
		RarImageFileInfoList list = new RarImageFileInfoList();
		
		list.mRarArchive = mRarArchive;
		
		return renewInternal(list);
	}

}
