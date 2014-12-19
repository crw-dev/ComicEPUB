/**
 * 画像ファイルWriter for Folder
 */
package jp.crwdev.app.container.folder;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import jp.crwdev.app.BufferedImageIO;
import jp.crwdev.app.constant.Constant;
import jp.crwdev.app.interfaces.IImageFileInfo;
import jp.crwdev.app.interfaces.IImageFileInfoList;
import jp.crwdev.app.interfaces.IImageFileWriter;
import jp.crwdev.app.interfaces.IImageFilter;

public class FolderImageFileWriter implements IImageFileWriter {

	/** 出力先フォルダ */
	private File mOutputFolder = null;
	/** 画像フィルタ */
	private IImageFilter mBaseFilter = null;
	/** 処理中断フラグ */
	private boolean mIsCancel = false;
	

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
	public boolean write(IImageFileInfoList list, OnProgressListener listener) {
		
		mIsCancel = false;
		
		if(listener != null){
			listener.onProgress(0, null);
		}
		
		int size = list.size();
		float progressOffset = 100 / (float)size;
		
		for(int i=0; i<size; i++){
			
			if(mIsCancel){
				return false;
			}
			
			IImageFileInfo info = list.get(i);
			BufferedImage image = null;
			
			synchronized(info){
				if(!info.isEnable()){
					continue;
				}
				
				InputStream in = info.getInputStream();
				
				if(in != null){
					image = BufferedImageIO.read(in, info.isJpeg());
				}
				else{
					image = info.getImage(false);
				}
				if(mBaseFilter != null){
					image = mBaseFilter.filter(image, info.getFilterParam());
				}
				
				try {
					if(in != null){
						in.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			File file = new File(mOutputFolder, String.format("P%04d.jpg", i));
			try {
				FileOutputStream outStream = new FileOutputStream(file);
				
				BufferedImageIO.write(image, "jpeg", Constant.jpegQuality, outStream);
				
				outStream.flush();
				outStream.close();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			if(listener != null){
				listener.onProgress((int)((i+1)*progressOffset), null);
			}
		}
		
		return true;
	}

	public boolean write(IImageFileInfo info) {
		
		mIsCancel = false;
		
		BufferedImage image = null;
		
		synchronized(info){
			//if(!info.isEnable()){
			//	continue;
			//}
			
			InputStream in = info.getInputStream();
			
			if(in != null){
				image = BufferedImageIO.read(in, info.isJpeg());
			}
			else{
				image = info.getImage(false);
			}
			if(mBaseFilter != null){
				image = mBaseFilter.filter(image, info.getFilterParam());
			}
			
			try {
				if(in != null){
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		String filename = getUniqueFileName(mOutputFolder);
		
		if(filename == null){
			return false;
		}
		
		File file = new File(filename);
		try {
			FileOutputStream outStream = new FileOutputStream(file);
			
			BufferedImageIO.write(image, "jpeg", Constant.jpegQuality, outStream);
			
			outStream.flush();
			outStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	private String getUniqueFileName(File outputFolder){
		try {
			File tmpFile = File.createTempFile("image", ".jpg", outputFolder);
			return tmpFile.getAbsolutePath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void close() {
		if(mIsCancel){
			// NOP
		}
	}
	
	@Override
	public void cancel() {
		mIsCancel = true;
	}

	@Override
	public String getSuffix() {
		return "";
	}
}
