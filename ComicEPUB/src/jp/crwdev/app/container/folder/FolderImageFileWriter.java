/**
 * 画像ファイルWriter for Folder
 */
package jp.crwdev.app.container.folder;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import jp.crwdev.app.BufferedImageIO;
import jp.crwdev.app.interfaces.IImageFileInfo;
import jp.crwdev.app.interfaces.IImageFileInfoList;
import jp.crwdev.app.interfaces.IImageFileWriter;
import jp.crwdev.app.interfaces.IImageFilter;

public class FolderImageFileWriter implements IImageFileWriter {

	/** 出力先フォルダ */
	private File mOutputFolder = null;
	/** 画像フィルタ */
	private IImageFilter mBaseFilter = null;
	

	/**
	 * コンストラクタ
	 */
	public FolderImageFileWriter(){
	}
	
	/**
	 * コンストラクタ
	 * @param filter 画像フィルタ
	 */
	public FolderImageFileWriter(IImageFilter filter){
		setImageFilter(filter);
	}
	
	@Override
	public void setImageFilter(IImageFilter filter){
		mBaseFilter = filter;
	}
	
	@Override
	public boolean open(String filepath) {
		
		File file = new File(filepath);
		if(!file.exists()){
			if(!file.mkdirs()){
				return false;
			}
		}
		if(!file.isDirectory()){
			return false;
		}
		
		mOutputFolder = file;
		
		return true;
	}

	@Override
	public boolean write(IImageFileInfoList list) {
		
		for(int i=0; i<list.size(); i++){
			IImageFileInfo info = list.get(i);
			BufferedImage image = BufferedImageIO.read(info.getInputStream(), info.isJpeg());
			if(mBaseFilter != null){
				image = mBaseFilter.filter(image, info.getFilterParam());
			}
			File file = new File(mOutputFolder, String.format("P%04d.jpg", i));
			try {
				FileOutputStream outStream = new FileOutputStream(file);
				
				BufferedImageIO.write(image, "jpeg", 0.8f, outStream);
				
				outStream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return true;
	}

	@Override
	public void close() {

	}

}
