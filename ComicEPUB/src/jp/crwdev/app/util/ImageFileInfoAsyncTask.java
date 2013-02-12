package jp.crwdev.app.util;

import jp.crwdev.app.interfaces.IImageFileInfo;
import jp.crwdev.app.interfaces.IImageFileInfoList;

public class ImageFileInfoAsyncTask {

	public interface OnTaskObserver {
		void onStart();
		void onProcess(int index, int total, IImageFileInfo info);
		void onFinish();
	};
	
	private boolean mCancelTask = false;
	private OnTaskObserver mTask = null;
	private IImageFileInfoList mList = null;
	private Thread mThread = null;
	
	public ImageFileInfoAsyncTask(IImageFileInfoList list, OnTaskObserver task){
		mList = list;
		mTask = task;
		mThread = null;
	}
	
	public void start(){
		if(mTask == null || mList == null){
			return;
		}
	
		if(mThread == null){
			mThread = new Thread(){
				@Override
				public void run(){
					threadTask();
				}
			};
			mThread.start();
		}
	}
	
	public void stop(){
		if(mThread != null){
			mCancelTask = true;
			mThread = null;
		}
	}
	
	private void threadTask(){
		mTask.onStart();
		int size = mList.size();
		for(int i=0; i<size; i++){
			IImageFileInfo info = mList.get(i);
			
			mTask.onProcess(i, size, info);
			if(mCancelTask){
				break;
			}
		}
		mTask.onFinish();
	}
}
