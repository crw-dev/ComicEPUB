package jp.crwdev.app.container;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import jp.crwdev.app.BufferedImageIO;
import jp.crwdev.app.interfaces.IImageFileInfo;
import jp.crwdev.app.interfaces.IImageFileInfoList;
import jp.crwdev.app.interfaces.IImageFileWriter;
import jp.crwdev.app.interfaces.IImageFilter;

public class ImageFilePreconverter implements IImageFileWriter {

	/** 画像フィルタ */
	private IImageFilter mBaseFilter = null;
	/** 処理中断フラグ */
	private boolean mIsCancel = false;
	
	/** 画像サイズ */
	private Dimension mUnionSize = new Dimension();

	/**
	 * コンストラクタ
	 */
	public ImageFilePreconverter(){
	}
	
	/**
	 * コンストラクタ
	 * @param filter 画像フィルタ
	 */
	public ImageFilePreconverter(IImageFilter filter){
		setImageFilter(filter);
	}
	
	public Dimension getUnionSize(){
		return mUnionSize;
	}

	@Override
	public boolean open(String filepath) {
		return false;
	}

	@Override
	public void setImageFilter(IImageFilter filter) {
		mBaseFilter = filter;
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
					image = info.getImage();
				}
				if(mBaseFilter != null){
					image = mBaseFilter.filter(image, info.getFilterParam());
				}
				
				try {
					if(in != null){
						in.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(image != null){
				int width = image.getWidth();
				int height = image.getHeight();
				if(mUnionSize.width < width){
					mUnionSize.width = width;
				}
				if(mUnionSize.height < height){
					mUnionSize.height = height;
				}
			}
			

			if(listener != null){
				listener.onProgress((int)((i+1)*progressOffset), null);
			}
		}
		
		return true;
	}

	@Override
	public void cancel() {
		mIsCancel = true;
	}

	@Override
	public String getSuffix() {
		return "";
	}

	@Override
	public void close() {
	}

}
