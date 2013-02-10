/**
 * Zipファイル内画像ファイル情報リスト
 */
package jp.crwdev.app.container.zip;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import jp.crwdev.app.BufferedImageIO;
import jp.crwdev.app.container.ImageFileInfoList;
import jp.crwdev.app.interfaces.IImageFileInfo;
import jp.crwdev.app.interfaces.IImageFileInfoList;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;


public class ZipImageFileInfoList extends ImageFileInfoList {

	/** 入力Zipファイル */
	protected ZipFile mZipFile;

	/**
	 * コンストラクタ
	 * @param zipFile 入力Zipファイル
	 */
	public ZipImageFileInfoList(ZipFile zipFile) {
		super();
		setList(zipFile);
	}
	
	/**
	 * コンストラクタ
	 */
	protected ZipImageFileInfoList(){
		super();
	}
	
	/**
	 * Zipファイル内の画像ファイルをリストに登録
	 * @param dir フォルダ
	 */
	private void setList(ZipFile zipFile){
		mZipFile = zipFile;
		clear();
		if(mZipFile != null){
			List<String> files = listZip(mZipFile);
			for(String entryName : files){
				try {
					IImageFileInfo info = new ZipImageFileInfo(entryName, mZipFile);
					add(info);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	/**
	 * Zip内画像ファイルリスト取得
	 * @param zip
	 * @return
	 */
	private List<String> listZip(ZipFile zip){
		List<String> files = new ArrayList<String>();
		try{
			Enumeration<? extends ZipArchiveEntry> enu=zip.getEntries();
			while(enu.hasMoreElements()){
				listFile((ZipArchiveEntry)enu.nextElement(), files);
			}
			return files;
		}catch(IOException e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * サポート画像ファイル判定
	 * @param entry ファイルエントリ
	 * @param files リスト
	 * @throws IOException
	 */
	private void listFile(ZipArchiveEntry entry, List<String> files) throws IOException{
		String name = entry.getName();
		if(!entry.isDirectory()){
			if(BufferedImageIO.isSupport(getSuffix(name))){
				files.add(name);
			}
		}
	}
	
	@Override
	public IImageFileInfoList renew(){
		ZipImageFileInfoList list = new ZipImageFileInfoList();
		
		list.mZipFile = mZipFile;
		
		return renewInternal(list);
	}
}
