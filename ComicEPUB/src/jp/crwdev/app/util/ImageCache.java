package jp.crwdev.app.util;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;

import jp.crwdev.app.BufferedImageIO;
import jp.crwdev.app.gui.DebugWindow;
import jp.crwdev.app.imagefilter.PreviewImageFilter;
import jp.crwdev.app.interfaces.IImageFileInfo;
import jp.crwdev.app.interfaces.IImageFileInfoList;
import jp.crwdev.app.interfaces.IImageFilter;
import jp.crwdev.app.setting.ImageFilterParamSet;

public class ImageCache {

	public static boolean enable = false;
	private static ImageCache mInstance;
	
	public static ImageCache getInstance(){
		if(mInstance == null){
			mInstance = new ImageCache();
		}
		return mInstance;
	}

	public static void clear(){
		if(mInstance != null){
			mInstance.clearCache();
		}
	}
	
	public static void dispose(){
		if(mInstance != null){
			mInstance.release();
			mInstance = null;
		}
	}
	
	public void setImageFileInfoList(IImageFileInfoList list){
		release();
		mFileInfoList = list;
	}
	
	protected ImageCache(){
		mFileInfoList = null;
	}
	
	protected void release(){
		finalizeThread();
		mImageMap.clear();
	}
	
	protected void clearCache(){
		if(mTempMap != null){
			mTempMap.clear();
		}
		mImageMap.clear();
	}
	
	private HashMap<Integer, ImageData> mImageMap = new HashMap<Integer, ImageData>();
	private IImageFileInfoList mFileInfoList = null;
	private PreviewImageFilter mImageFilter = new PreviewImageFilter();

	public void setImageFilterParam(ImageFilterParamSet params){
		mImageFilter.setImageFilterParam(params);
		mImageMap.clear();
	}
	
	
	public ImageData getImageData(int targetPage){
		if(!mImageMap.containsKey(targetPage)){
			return loadOriginalImage(targetPage);
		}
		return mImageMap.get(targetPage);
	}
	
	
	private LinkedList<Integer> mQueue = new LinkedList<Integer>();
	private Object mThreadLock = new Object();
	private Thread mThread = null;
	private boolean mThreadFinish = false;
	public void startRenderImage(int startPage){
		if(mThread == null){
			mThread = new Thread(){
				@Override
				public void run(){
					int targetPage = 0;
					while(!mThreadFinish){
						synchronized(mThreadLock){
							if(mQueue.isEmpty()){
								try {
									mThreadLock.wait();
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
							if(!mQueue.isEmpty()){
								targetPage = mQueue.pop();
							}
						}
						try {
							boolean loop = true;
							storeImage();
							while(loop && !mThreadFinish){
								loadOriginalImage(targetPage);
								synchronized(mQueue){
									loop = !mQueue.isEmpty();
									if(loop){
										targetPage = mQueue.pop();
									}
								}
							}
							removeImage();
						}catch(Exception e){
						}catch(OutOfMemoryError e){
						}
					}
					mThread = null;
				}
			};
			mThread.setPriority(Thread.NORM_PRIORITY);
			mThread.start();
		}
		synchronized(mThreadLock){
			//while(mQueue.size() > 0){
			//	mQueue.remove();
			//}
			for(int i=0; i<3; i++){
				mQueue.addLast(startPage+i);
			}
			mThreadLock.notify();
		}
	}
	
	private void finalizeThread(){
		if(mThread != null){
			mThreadFinish = true;
			synchronized(mThreadLock){
				mThreadLock.notify();
			}
			mThread = null;
		}
	}
	
	private HashMap<Integer, ImageData> mTempMap;
	private void storeImage(){
		mTempMap = mImageMap;
		mImageMap = new HashMap<Integer, ImageData>();
	}
	private void removeImage(){
		mTempMap.clear();
		outputCacheLog();
	}
	
	private void outputCacheLog(){
		StringBuffer sb = new StringBuffer("ImageCache ");
		for(ImageData image : mImageMap.values()){
			sb.append("[page=" + image.getPage() + " ");
			if(image.hasOriginal()){
				sb.append("o");
			}
			if(image.hasDisplay()){
				sb.append("d");
			}
			if(image.hasZoom()){
				sb.append("z");
			}
			sb.append("]");
		}
		DebugWindow.log(sb.toString());
	}
	
	private ImageData loadOriginalImage(int targetPage){
		int size = mFileInfoList.size();
		if(targetPage < 0 || size <= targetPage){
			return null;
		}
		
		if(mTempMap != null && mTempMap.containsKey(targetPage)){
			ImageData data = mTempMap.remove(targetPage);
			mImageMap.put(targetPage, data);
			return data;
		}
		if(!mImageMap.containsKey(targetPage)){
			DebugWindow.log("loadOriginalImage(" + targetPage + ") start");
			ImageData data = new ImageData(targetPage, mFileInfoList.get(targetPage));
			data.getOriginalImage();
			mImageMap.put(targetPage, data);
			DebugWindow.log("loadOriginalImage(" + targetPage + ") end");
			outputCacheLog();
			return data;
		}
		return mImageMap.get(targetPage);
	}
	
	public class ImageData {
		
		private BufferedImage mOriginalImage;
		private BufferedImage mDisplayImage;
		private BufferedImage mZoomImage;
		private boolean mIsLoadingZoomImage = false;
		private boolean mIsLoadingDisplayImage = false;
		
		private int mPage;
		private IImageFileInfo mInfo;
		//private PreviewImageFilter mImageFilter = new PreviewImageFilter();
		//private PreviewImageFilter mPreviewZoomFilter = new PreviewImageFilter();

		public ImageData(int page, IImageFileInfo info){
			mPage = page;
			mInfo = info;
		}
		
		public int getPage(){
			return mPage;
		}
		
		public boolean hasOriginal(){
			return mOriginalImage != null;
		}
		public boolean hasDisplay(){
			return mDisplayImage != null;
		}
		public boolean hasZoom(){
			return mZoomImage != null;
		}
		
		public BufferedImage getOriginalImage(){
			if(mOriginalImage == null){
				mIsLoadingDisplayImage = false;
				mIsLoadingZoomImage = false;
				
				InputStream stream = mInfo.getInputStream();
				try {
					BufferedImage image = null;
					if(stream != null){
						image = BufferedImageIO.read(stream, mInfo.isJpeg());
					}
					else{
						//boolean preview = true;
						image = mInfo.getImage(true);
					}
					
					mOriginalImage = image;
					
				}catch(OutOfMemoryError e){
					
				}
			}
			return mOriginalImage;
		}
		
		public BufferedImage getDisplayImage(IImageFilter filter, boolean needUpdate){
			if(needUpdate){
				mDisplayImage = null;
				mIsLoadingDisplayImage = false;
				mZoomImage = null;
				mIsLoadingZoomImage = false;
			}
			if(mDisplayImage == null && !mIsLoadingDisplayImage){
				mIsLoadingDisplayImage = true;
				DebugWindow.log("getDisplayImage(" + mPage + ") start");
				BufferedImage originalImage = getOriginalImage();
				if(originalImage != null){
					BufferedImage filtered = filter.filter(BufferedImageIO.copyBufferedImage(mOriginalImage), mInfo.getFilterParam());
					mDisplayImage = filtered;
				}
				DebugWindow.log("getDisplayImage(" + mPage + ") end");
				outputCacheLog();
			}
			return mDisplayImage;
		}
		
		public BufferedImage getZoomImage(IImageFilter filter){
			if(mZoomImage == null && !mIsLoadingZoomImage){
				mIsLoadingDisplayImage = true;
				DebugWindow.log("getZoomImage(" + mPage + ") start");
				BufferedImage originalImage = getOriginalImage();
				if(originalImage != null){
					BufferedImage filtered = filter.filter(BufferedImageIO.copyBufferedImage(mOriginalImage), mInfo.getFilterParam());
					mZoomImage = filtered;
				}
				DebugWindow.log("getZoomImage(" + mPage + ") end");
				outputCacheLog();
			}
			return mZoomImage;
		}
	}
}
