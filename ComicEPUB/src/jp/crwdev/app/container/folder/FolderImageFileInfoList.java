/**
 * フォルダ内画像ファイル情報リスト
 */
package jp.crwdev.app.container.folder;

import java.io.File;
import java.io.FilenameFilter;

import jp.crwdev.app.BufferedImageIO;
import jp.crwdev.app.ImageFileInfoList;
import jp.crwdev.app.interfaces.IImageFileInfo;
import jp.crwdev.app.interfaces.IImageFileInfoList;

public class FolderImageFileInfoList extends ImageFileInfoList {
	
	/**
	 * コンストラクタ
	 * @param dir フォルダ
	 */
	public FolderImageFileInfoList(File dir){
		super();
		setList(dir);
	}
	
	/**
	 * コンストラクタ
	 */
	protected FolderImageFileInfoList(){
		super();
	}
	
	/**
	 * フォルダ内の画像ファイルをリストに登録
	 * @param dir フォルダ
	 */
	private void setList(File dir){
		clear();
		if(dir != null){
			
			String[] files = dir.list(new FilenameFilter(){
				@Override
				public boolean accept(File arg0, String arg1) {
					return BufferedImageIO.isSupport(getSuffix(arg1));
				}
			});

			for(String filename : files){
				try {
					IImageFileInfo info = new FolderImageFileInfo(dir.getAbsolutePath(), filename);
					add(info);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	}

	@Override
	public IImageFileInfoList renew() {
		FolderImageFileInfoList list = new FolderImageFileInfoList();
		
		return renewInternal(list);
	}
}
