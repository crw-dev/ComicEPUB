package jp.crwdev.app.util;

import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;

public class QueueingThread extends Thread {

	public interface IQueueProcess {
		void doProcess();
	}
	
	/**
	 * Constructor
	 */
	public QueueingThread(){
		
	}
	
	private LinkedList<IQueueProcess> mQueue = new LinkedList<IQueueProcess>();
	private boolean mIsFinish = false;
	private CountDownLatch mLatch = new CountDownLatch(1);
	
	public void run(){
		
		IQueueProcess process = null;
		
		while(!mIsFinish){
			try {
				mLatch.await();
				mLatch = new CountDownLatch(1);
				
			} catch (InterruptedException e) {
			}
			synchronized(mQueue){
				if(mQueue.size() == 0){
					continue;
				}
				process = mQueue.pollLast();
				//process = mQueue.pop();
				mQueue.clear();
			}
			if(process != null){
				process.doProcess();
				process = null;
			}
		}
	}
	
	public void add(IQueueProcess process, boolean clearQueue){
		synchronized(mQueue){
			if(clearQueue){
				mQueue.clear();
			}
			mQueue.push(process);
		}
		mLatch.countDown();
	}
	
	@Override
	public void start(){
		mIsFinish = false;
		super.start();
	}
	
	public void release(){
		mIsFinish = false;
		synchronized(mQueue){
			mQueue.clear();
		}
		mLatch.countDown();
		super.interrupt();
	}
	
}
