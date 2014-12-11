package jp.crwdev.app.gui;

import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

import jp.crwdev.app.BufferedImageIO;
import jp.crwdev.app.EventObserver;
import jp.crwdev.app.EventObserver.OnEventListener;
import jp.crwdev.app.imagefilter.PreviewImageFilter;
import jp.crwdev.app.interfaces.IImageFileInfo;
import jp.crwdev.app.setting.ImageFilterParamSet;
import jp.crwdev.app.util.ImageCache;
import jp.crwdev.app.util.InifileProperty;
import jp.crwdev.app.util.ImageCache.ImageData;

@SuppressWarnings("serial")
public class FullscreenWindow extends JFrame implements MouseListener, MouseMotionListener, OnEventListener {

	public static boolean mEnableFullScreen = false;
	public int windowWidth = 640;
	public int windowHeight = 480;
	public float mZoomScale = 1.75f;
	
	private GraphicsDevice mDevice;
	private BufferStrategy mBufferStrategy;
	
	private int mScreenWidth = 640;
	private int mScreenHeight = 480;
	private Rectangle mImageArea = new Rectangle();


	public FullscreenWindow(){
		setTitle("FullscreenView");
		setBounds(0, 0, 640, 480);
		setResizable(false);
		//setIgnoreRepaint(true); // paintイベントを無効化
		
		mEnableFullScreen = InifileProperty.getInstance().isEnableFullScreen();
		
		addMouseListener(this);
		addMouseMotionListener(this);

		// ESCキーで終了
		InputMap imap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW); 
		imap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escape"); 
		getRootPane().getActionMap().put("escape", new AbstractAction(){ 
			@Override 
			public void actionPerformed(ActionEvent e) { 
				closeWindow();
			}
		});
		
		initFullScreen();
		//		setUndecorated(true);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		
		createBufferStrategy(2);
		mBufferStrategy = getBufferStrategy();

		repaintEvent();
		screenUpdate();
	}
	
	private void initFullScreen() {
		
		GraphicsEnvironment ge =
				GraphicsEnvironment.getLocalGraphicsEnvironment();
		mDevice = ge.getDefaultScreenDevice();  // GraphicsDeviceの取得
		
		DisplayMode mode = mDevice.getDisplayMode();
		int width = mode.getWidth();
		int height = mode.getHeight();
		int depth = mode.getBitDepth();
		
		if(mEnableFullScreen){

			setUndecorated(true); // タイトルバー・ボーダー非表示
			setIgnoreRepaint(false);
			
			if (!mDevice.isFullScreenSupported()) {
				System.out.println("フルスクリーンモードはサポートされていません。");
				System.exit(0);
			}
	
			// フルスクリーン化！
			mDevice.setFullScreenWindow(this);
			
			setDisplayMode(width, height, depth);
			
			mScreenWidth = width;
			mScreenHeight = height;
		}
		else{
			setUndecorated(true); // タイトルバー・ボーダー非表示
			setIgnoreRepaint(true);
			
			if(InifileProperty.getInstance().isShowDebugWindow()){
				mScreenWidth = windowWidth;
				mScreenHeight = windowHeight;
			}else{
				mScreenWidth = width;
				mScreenHeight = height;
			}
			
			setBounds(0, 0, mScreenWidth, mScreenHeight);
			
		}
		
	}
	
	/**
	 * ディスプレイモードを設定
	 * 
	 * @param width
	 * @param height
	 * @param bitDepth
	 */
	private void setDisplayMode(int width, int height, int bitDepth) {
		if (!mDevice.isDisplayChangeSupported()) {
			System.out.println("ディスプレイモードの変更はサポートされていません。");
			return;
		}

		DisplayMode dm = new DisplayMode(width, height, bitDepth,
				DisplayMode.REFRESH_RATE_UNKNOWN);
		mDevice.setDisplayMode(dm);
	}
	
	
	/**
	 * Same as ImagePanel
	 */
	private PreviewImageFilter mImageFilter = new PreviewImageFilter();
	private PreviewImageFilter mPreviewZoomFilter = new PreviewImageFilter();
	private BufferedImage mPreviewZoomImage = null;
	private Object mLockZoomImage = new Object();
	private IImageFileInfo mFileInfo = null;
	private int mInfoIndex = 0;
	private BufferedImage mOriginalImage = null;
	private BufferedImage mDisplayImage = null;
	private boolean mIsZoomDrag = false;
	private Point mZoomPoint = new Point();
	private EventObserver mEventSender = null;
	public void setEventObserver(EventObserver observer){
		mEventSender = observer;
	}

	public void setImageFilterParam(ImageFilterParamSet params){
		mImageFilter.setImageFilterParam(params);
		mImageFilter.setPreviewSize(getWidth(), getHeight());
		mPreviewZoomFilter.setImageFilterParam(params);
		mPreviewZoomFilter.setPreviewSize((int)(getWidth()*mZoomScale), (int)(getHeight()*mZoomScale));
	}

	public void setImage(BufferedImage image, IImageFileInfo info, int rowIndex){
		//AddSpaceFilter filter = new AddSpaceFilter();
		//mOriginalImage = filter.filter(image, info.getFilterParam());
		mOriginalImage = image;
		mFileInfo = info;
		mInfoIndex = rowIndex;
		if(mOriginalImage != null){
			updateDisplayImage(true);
		}
	}
	
	public void updateDisplayImage(boolean async){
		if(mOriginalImage == null){
			return;
		}
		
		if(async){
			startRenderImage();
		}
		else{
			updateDisplayImageInternal();
		}
	}
	
	private LinkedList<Integer> mQueue = new LinkedList<Integer>();
	private Object mThreadLock = new Object();
	private Thread mThread = null;
	private boolean mThreadFinish = false;
	public void startRenderImage(){
		if(mThread == null){
			mThread = new Thread(){
				@Override
				public void run(){
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
								mQueue.pop();
							}
						}
						try {
							boolean loop = true;
							while(loop && !mThreadFinish){
								updateDisplayImageInternal();
								synchronized(mQueue){
									loop = !mQueue.isEmpty();
									if(loop){
										mQueue.pop();
									}
								}
							}
						}catch(Exception e){
							mEventSender.setProgressMessage(e.getMessage());
						}catch(OutOfMemoryError e){
							mEventSender.setProgressMessage(e.getMessage());
						}
					}
					mThread = null;
				}
			};
			mThread.setPriority(Thread.MAX_PRIORITY);
			mThread.start();
		}
		synchronized(mThreadLock){
			while(mQueue.size() > 0){
				mQueue.remove();
			}
			mQueue.push(0);
			mThreadLock.notify();
		}
	}
	
//	private void finalizeThread(){
//		if(mThread != null){
//			mThreadFinish = true;
//			synchronized(mThreadLock){
//				mThreadLock.notify();
//			}
//			mThread = null;
//		}
//	}
	
	public void updateDisplayImageInternal(){
		if(mImageFilter != null){
			try {
				if(ImageCache.enable){
					ImageData data = ImageCache.getInstance().getImageData(mInfoIndex);
					mDisplayImage = data.getDisplayImage(mImageFilter, false);
					mPreviewZoomImage = null;
					new Thread(){
						@Override
						public void run(){
							createZoomImage();
						}
					}.start();
				}
				else{
					BufferedImage filtered = mImageFilter.filter(BufferedImageIO.copyBufferedImage(mOriginalImage), mFileInfo.getFilterParam());
					mDisplayImage = filtered;
					mPreviewZoomImage = null;
					
					new Thread(){
						@Override
						public void run(){
							createZoomImage();
						}
					}.start();
				}
				
			}catch(OutOfMemoryError e){
				mEventSender.setProgressMessage(e.getMessage());
				e.printStackTrace();
			}
		}
		repaintEvent();
		screenUpdate();
//		repaint();
	}
	
	private void createZoomImage(){
		synchronized(mLockZoomImage){
			if(mPreviewZoomImage == null){
				if(ImageCache.enable){
					if(mPreviewZoomImage == null){
						ImageData data = ImageCache.getInstance().getImageData(mInfoIndex);
						mPreviewZoomImage = data.getZoomImage(mPreviewZoomFilter);
					}
				}else{
					if(mPreviewZoomImage == null){
						BufferedImage filtered = mPreviewZoomFilter.filter(BufferedImageIO.copyBufferedImage(mOriginalImage), mFileInfo.getFilterParam());
						mPreviewZoomImage = filtered;
					}
				}
				if(mEnableFullScreen){
					repaintEvent();
					screenUpdate();
				}
			}
		}
	}

	@Override
	public void paint(Graphics g){
		//super.paint(g);
		
		repaintEvent(g);
	}
	
	private void repaintEvent(){
		if(mEnableFullScreen){
			repaintEvent(mBufferStrategy.getDrawGraphics());
		}else{
			repaint();
		}
	}
	
	
	
	private void repaintEvent(Graphics g){
		
		// 背景
		g.setColor(Color.BLACK);
		//g.fillRect(0, 0, mScreenWidth, mScreenHeight);
		
		int w = getWidth();
		int h = getHeight();


		if (mDisplayImage != null){
			int imageW = mDisplayImage.getWidth();
			int imageH = mDisplayImage.getHeight();
			int x = (w - imageW)/2;
			int y = (h - imageH)/2;
			mImageArea.setBounds(x, y, imageW, imageH);
			
			
			if(mIsZoomDrag){
				//createZoomImage();
				
				float scale = mZoomScale;
				int mx = mZoomPoint.x;
				int my = mZoomPoint.y;
				int cx = w / 2;
				int cy = h / 2;
				mx += (mx - cx);
				my += (my - cy);
				int dw = (int)(imageW);
				int dh = (int)(imageH);
				int zw = (int)(dw * scale);
				int zh = (int)(dh * scale);
				
				int pw = mPreviewZoomImage.getWidth();
				int ph = mPreviewZoomImage.getHeight();
				
				if(mPreviewZoomImage != null){
					if(zw < pw || zh < ph){
						zw = pw;
						zh = ph;
					}
				}
				
				float panX = (float)(mx - cx) / (float)dw;
				float panY = (float)(my - cy) / (float)dh;
				int zx = mx - (int)(zw * (0.5f + panX));
				int zy = my - (int)(zh * (0.5f + panY));
				
				
				if(zx > 0){
					g.fillRect(0, 0, zx, h);
				}
				if(zx+zw < w){
					g.fillRect(zx+zw, 0, w, h);
				}
				if(zy > 0){
					g.fillRect(zx, 0, zw, zy);
				}
				if(zy+zh < h){
					g.fillRect(zx, zy+zh, zw, h);
				}
				
				if(mPreviewZoomImage != null){
					if(zw != pw || zh != ph){
						Graphics2D g2 = (Graphics2D)g;
						g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);//RenderingHints.VALUE_INTERPOLATION_BICUBIC);
					}
					g.drawImage(mPreviewZoomImage, zx, zy, zx+zw, zy+zh, 0, 0, pw, ph, null);
				}else{
					if(zw != imageW || zh != imageH){
						Graphics2D g2 = (Graphics2D)g;
						g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);//RenderingHints.VALUE_INTERPOLATION_BICUBIC);
					}
					g.drawImage(mDisplayImage, zx, zy, zx+zw, zy+zh, 0, 0, imageW, imageH, null);
				}
			}
			else{
				g.fillRect(0, 0, w, h);
				g.drawImage(mDisplayImage, x, y, this);
			}
			
		}
		else{
			mImageArea.setBounds(0,0,0,0);
		}
		
		if(mEnableFullScreen){
			g.dispose();
		}
	}

	/**
	 * スクリーンの更新（BufferStrategyを使用）
	 * 
	 */
	private void screenUpdate() {
		if(mEnableFullScreen){
			if (!mBufferStrategy.contentsLost()) {
				mBufferStrategy.show();
			} else {
				System.out.println("Contents Lost");
			}
			Toolkit.getDefaultToolkit().sync();
		}
	}

	private void closeWindow(){
		if(mEnableFullScreen){
			mDevice.setFullScreenWindow(null);
		}
		mEventSender.sendEvent(EventObserver.EventTarget_Main, EventObserver.EventType_EndFullscreen, 0);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		
		if(javax.swing.SwingUtilities.isRightMouseButton(e)){
			if(e.getClickCount() >= 2){
				closeWindow();
			}
			else{
				mEventSender.sendEvent(EventObserver.EventTarget_Table, EventObserver.EventType_MoveInfo, -1);
			}
		}
		else{
			if(!mImageArea.contains(e.getX(), e.getY())){
				mEventSender.sendEvent(EventObserver.EventTarget_Table, EventObserver.EventType_MoveInfo, 1);
			}
		}
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		if(javax.swing.SwingUtilities.isRightMouseButton(e)){
		}
		else{
			// Zoom start
			if(mImageArea.contains(x, y)){
				mIsZoomDrag = true;
				mZoomPoint.setLocation(x, y);
				
				repaintEvent();
				screenUpdate();
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(mFileInfo == null){
			return;
		}
		
		if(javax.swing.SwingUtilities.isRightMouseButton(e)){
		}
		else {
			// Zoom end
			if(mIsZoomDrag){
				mIsZoomDrag = false;
				repaintEvent();
				screenUpdate();
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		if(javax.swing.SwingUtilities.isRightMouseButton(e)){
		}
		else{
			// Zoom
			mZoomPoint.setLocation(x, y);
			
			repaintEvent();
			screenUpdate();
		}
	}

	
	@Override
	public void mouseMoved(MouseEvent e) {
		
		
//		repaintEvent();
//		screenUpdate();

	}

	@Override
	public void onEventReceived(int type, int arg1, int arg2, Object obj) {
		switch(type){
		case EventObserver.EventType_PreviewSize:
			//Dimension size = (Dimension)obj;
			//setOutputSizePreview(arg1 == 1, size.width, size.height);
			break;
		default:
			break;
		}
	}

}
