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
	/** 処理中断フラグ */
	private boolean mIsCancel = false;

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
	public boolean write(IImageFileInfoList list, OnProgressListener listener) {
		if(list == null && mOutputZipFile != null){
			return false;
		}
		
		mIsCancel = false;

		if(listener != null){
			listener.onProgress(0, null);
		}
		
		ZipOutputStream zipOut = null;;
		try {
			zipOut = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(mOutputZipFile)));
			zipOut.setLevel(mCompressLevel);

			int size = list.size();
			float progressOffset = 100 / (float)size;
			
			for(int i=0; i<size; i++){
				IImageFileInfo info = list.get(i);
				BufferedImage image = BufferedImageIO.read(info.getInputStream(), info.isJpeg());
				if(mBaseFilter != null){
					image = mBaseFilter.filter(image, info.getFilterParam());
				}
				
				if(mIsCancel){
					return false;
				}
				
				try {
					String filename = String.format("P%04d.jpg", i);
					
					zipOut.putNextEntry(new ZipEntry(filename));
					
					BufferedImageIO.write(image, "jpeg", 0.8f, zipOut);
					
					zipOut.closeEntry();
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				if(listener != null){
					listener.onProgress((int)((i+1)*progressOffset), null);
				}
			}
			
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return false;
		}
		finally{
			try {
				if(zipOut != null){
					zipOut.close();
					zipOut = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		
		return true;
	}

	@Override
	public void close() {
		if(mIsCancel){
			if(mOutputZipFile != null){
				if(mOutputZipFile.exists()){
					mOutputZipFile.delete();
				}
			}
		}
		if(mOutputZipFile != null){
			mOutputZipFile = null;
		}
	}

	@Override
	public void cancel() {
		mIsCancel = true;
	}

}
