/**
 * 画像ファイルWriter for Zip
 */
package jp.crwdev.app.container.zip;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


import jp.crwdev.app.BufferedImageIO;
import jp.crwdev.app.interfaces.IImageFileInfo;
import jp.crwdev.app.interfaces.IImageFileInfoList;
import jp.crwdev.app.interfaces.IImageFileWriter;
import jp.crwdev.app.interfaces.IImageFilter;

public class ZipImageFileWriter implements IImageFileWriter {

	/** 出力先Zipファイル */
	private File mOutputZipFile = null;
	/** 画像フィルタ */
	private IImageFilter mBaseFilter = null;
	/** 圧縮率(0-10) */
	private int mCompressLevel = 0;

	/**
	 * コンストラクタ
	 */
	public ZipImageFileWriter(){
	}
	
	/**
	 * コンストラクタ
	 * @param filter 画像フィルタ
	 */
	public ZipImageFileWriter(IImageFilter filter){
		setImageFilter(filter);
	}

	/**
	 * 圧縮レベル設定
	 * @param level - the compression level (0-9) 
	 */
	public void setCompressLevel(int level){
		if(level < 0){ level = 0; }
		if(level > 9){ level = 9; }
		mCompressLevel = level;
	}
	
	@Override
	public void setImageFilter(IImageFilter filter){
		mBaseFilter = filter;
	}
	
	@Override
	public boolean open(String filepath) {

		File file = new File(filepath);
		if(file.exists()){
			if(!file.delete()){
				return false;
			}
		}
		
		mOutputZipFile = file;
		
		return true;
	}

	@Override
	public boolean write(IImageFileInfoList list) {
		if(list == null && mOutputZipFile != null){
			return false;
		}

		ZipOutputStream zipOut = null;;
		try {
			zipOut = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(mOutputZipFile)));
			zipOut.setLevel(mCompressLevel);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return false;
		}

		for(int i=0; i<list.size(); i++){
			IImageFileInfo info = list.get(i);
			BufferedImage image = BufferedImageIO.read(info.getInputStream(), info.isJpeg());
			if(mBaseFilter != null){
				image = mBaseFilter.filter(image, info.getFilterParam());
			}
			
			try {
				String filename = String.format("P%04d.jpg", i);
				
				zipOut.putNextEntry(new ZipEntry(filename));
				
				BufferedImageIO.write(image, "jpeg", 0.8f, zipOut);
				
				zipOut.closeEntry();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			zipOut.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	@Override
	public void close() {
		if(mOutputZipFile != null){
			mOutputZipFile = null;
		}
	}

}
