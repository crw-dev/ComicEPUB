package jp.crwdev.app.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SpringLayout;

import jp.crwdev.app.BufferedImageIO;
import jp.crwdev.app.EventObserver;
import jp.crwdev.app.EventObserver.OnEventListener;
import jp.crwdev.app.container.ImageFileInfoSplitWrapper;
import jp.crwdev.app.imagefilter.AddSpaceFilter;
import jp.crwdev.app.imagefilter.ImageFilterParam;
import jp.crwdev.app.imagefilter.PreviewImageFilter;
import jp.crwdev.app.imagefilter.SplitFilter;
import jp.crwdev.app.interfaces.IImageFileInfo;
import jp.crwdev.app.setting.ImageFilterParamSet;
import jp.crwdev.app.util.QueueingThread;
import jp.crwdev.app.util.QueueingThread.IQueueProcess;

public class ImagePanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener, OnEventListener {
	
	private PreviewImageFilter mImageFilter = new PreviewImageFilter();
	
	/** Zoom用の画像を作るかどうか */
	private boolean mCreateZoomImage = true;
	private PreviewImageFilter mPreviewZoomFilter = new PreviewImageFilter();
	private BufferedImage mPreviewZoomImage = null;
	private Object mLockZoomImage = new Object();
	
	private IImageFileInfo mFileInfo = null;
	private int mInfoIndex = 0;
	private BufferedImage mOriginalImage = null;
	private BufferedImage mDisplayImage = null;
	
	private boolean mIsOutputSizePreview = false;
	private Dimension mPreviewSize = new Dimension(600,800);
	
	private Point ptLeftTop = null;
	private Point ptRightBottom = null;
	
	private Point ptRotateA = null;
	private Point ptRotateB = null;
	private boolean mIsRotateVertical = true;
	
	private LineHandleSet guideLineHandle = new LineHandleSet();
	
	private boolean mIsPreviewMode = false;
	private boolean mIsZoomDrag = false;
	private Point mZoomPoint = new Point();
	private Rectangle mImageArea = new Rectangle();
	
	/** カスタム分割モード */
	private boolean mIsCustomSplitMode = false;
	private SplitLineSet splitLineHandle = new SplitLineSet();
	
	
	public ImagePanel(){
		SpringLayout layout = new SpringLayout();
		setLayout(layout);

		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		addHierarchyBoundsListener(new HierarchyBoundsListener(){
			@Override
			public void ancestorMoved(HierarchyEvent arg0) {
			}
			@Override
			public void ancestorResized(HierarchyEvent arg0) {
				if(!mIsOutputSizePreview){
					mImageFilter.setPreviewSize(getWidth(), getHeight());
					if(mCreateZoomImage){
						mPreviewZoomFilter.setPreviewSize((int)(getWidth()*1.5), (int)(getHeight()*1.5));
					}
					System.out.println("width=" + getWidth() + " height=" + getHeight());
				}
			}           
        });
	}
	
	public void finalize(){
		finalizeThread();
	}
	
	private EventObserver mEventSender = null;
	public void setEventObserver(EventObserver observer){
		mEventSender = observer;
	}
	
	public void setImageFilterParam(ImageFilterParamSet params){
		mImageFilter.setImageFilterParam(params);
		if(mCreateZoomImage){
			mPreviewZoomFilter.setImageFilterParam(params);
		}
		mIsPreviewMode = params.get(ImageFilterParamSet.FILTER_INDEX_BASIC).isPreview();
		//updateDisplayImage();
	}
	
	//@Override
	public void paint(Graphics g){
		super.paint(g); //JPanelのクリア
		int w = getWidth();
		int h = getHeight();
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, w, h);
		if (mDisplayImage != null){
			int imageW = mDisplayImage.getWidth();
			int imageH = mDisplayImage.getHeight();
			int x = (w - imageW)/2;
			int y = (h - imageH)/2;
			mImageArea.setBounds(x, y, imageW, imageH);
			
			if(mIsCustomSplitMode){
				
				g.drawImage(mDisplayImage, x, y, this);
				
				splitLineHandle.paint(g, w, h, imageW, imageH);
			}
			else{
				
				if(!mIsPreviewMode && mIsZoomDrag){
					if(mCreateZoomImage){
						createZoomImage();
						imageW = mPreviewZoomImage.getWidth();
						imageH = mPreviewZoomImage.getHeight();
					}
					//TODO
					float imageScale = 1.5f;
					if(imageW > w || imageH > h){
						imageScale = 1.0f;
					}
					int dw = (int)(imageW * imageScale);
					int dh = (int)(imageH * imageScale);
					int dx = (w - dw)/2;
					int dy = (h - dh)/2;
					float scale = dw / (float)w;
					int offsetx = mZoomPoint.x - w/2;
					int offsety = mZoomPoint.y - h/2;
					dx -= offsetx * scale;
					dy -= offsety * scale;
					
					if(mCreateZoomImage){
						g.drawImage(mPreviewZoomImage, dx, dy, dx+imageW, dy+imageH, 0, 0, imageW, imageH, null);
					}
					else{
						g.drawImage(mDisplayImage, dx, dy, dx+dw, dy+dh, 0, 0, imageW, imageH, null);
					}
				}
				else{
					g.drawImage(mDisplayImage, x, y, this);
				}
				
				if(guideLineHandle.isDragHandle()){
					
				}
				else if(ptLeftTop != null && ptRightBottom != null){
					Rectangle cropRect = new Rectangle(ptLeftTop.x, ptLeftTop.y, (ptRightBottom.x-ptLeftTop.x), (ptRightBottom.y-ptLeftTop.y));
					g.setColor(Color.BLACK);
					g.drawRect(cropRect.x, cropRect.y, cropRect.width, cropRect.height);
				}
				if(ptRotateA != null && ptRotateB != null){
					Graphics2D g2 = (Graphics2D)g;
					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g2.setColor(Color.RED);
					g2.drawLine(ptRotateA.x, ptRotateA.y, ptRotateB.x, ptRotateB.y);
					//g.setColor(Color.RED);
					//g.drawLine(ptRotateA.x, ptRotateA.y, ptRotateB.x, ptRotateB.y);
				
					double angle = radian((double)(ptRotateB.x-ptRotateA.x), (double)(ptRotateB.y-ptRotateA.y));
					System.out.println("rad=" + angle);
				}
	
				if(mIsPreviewMode){
					//guideLineHandle.setScale((float)mImageFilter.getResizedScaleW(), (float)mImageFilter.getResizedScaleH());
					guideLineHandle.paint(g, w, h, imageW, imageH);
				}
			
			}
		}
		else{
			mImageArea.setBounds(0,0,0,0);
		}
	}
	
	private void createZoomImage(){
		if(mCreateZoomImage){
			synchronized(mLockZoomImage){
				if(mPreviewZoomImage == null){
					BufferedImage filtered = mPreviewZoomFilter.filter(BufferedImageIO.copyBufferedImage(mOriginalImage), mFileInfo.getFilterParam());
					mPreviewZoomImage = filtered;
				}
			}
		}
	}
	private Rectangle getCropRect(){
		Rectangle rect = null;
		if (mDisplayImage != null && ptLeftTop != null && ptRightBottom != null){
			int w = getWidth();
			int h = getHeight();
			int imageW = mDisplayImage.getWidth();
			int imageH = mDisplayImage.getHeight();
			int x = (w - imageW)/2;
			int y = (h - imageH)/2;
			
			
			
			Rectangle cropRect = new Rectangle(ptLeftTop.x, ptLeftTop.y, (ptRightBottom.x-ptLeftTop.x), (ptRightBottom.y-ptLeftTop.y));
			Rectangle imageRect = new Rectangle(x, y, imageW, imageH);
			Rectangle intersect = imageRect.intersection(cropRect);
			if(intersect.width > 0 && intersect.height > 0){
				rect = intersect;
				ptLeftTop.x = rect.x;
				ptLeftTop.y = rect.y;
				ptRightBottom.x = rect.x + rect.width;
				ptRightBottom.y = rect.y + rect.height;
			}
		}
		return rect;
	}
	
	private float getScale(){
		if(mOriginalImage != null && mDisplayImage != null){
			//SplitModeの場合は表示イメージの幅がオリジナルの半分になってしまうため、ここでは高さを元にスケールを計算する
			//      今後SplitModeに縦分割等が入る場合は修正が必要
			float oh = (float)mOriginalImage.getHeight();
			float dh = (float)mDisplayImage.getHeight();
			return dh/oh;
		}
		return 1.0f;
	}
	private float getScaleX(float ow){
		if(mDisplayImage != null){
			float dw = (float)mDisplayImage.getWidth();
			return dw/ow;
		}
		return 1.0f;
	}
	private float getScaleY(float oh){
		if(mDisplayImage != null){
			float dh = (float)mDisplayImage.getHeight();
			return dh/oh;
		}
		return 1.0f;
	}
	
	public void setImage(BufferedImage image, IImageFileInfo info, int rowIndex){
		AddSpaceFilter filter = new AddSpaceFilter();
		mOriginalImage = filter.filter(image, info.getFilterParam());
		mFileInfo = info;
		mInfoIndex = rowIndex;
		ptLeftTop = null;
		ptRightBottom = null;
		if(mOriginalImage != null){
			updateDisplayImage(false);
		}
	}

	public void updateDisplayImage(boolean async){
		ptLeftTop = null;
		ptRightBottom = null;
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
	
	private QueueingThread mZoomThread = null;
	
	public void updateDisplayImageInternal(){
		if(mImageFilter != null){
			try {
				BufferedImage filtered = mImageFilter.filter(BufferedImageIO.copyBufferedImage(mOriginalImage), mFileInfo.getFilterParam());
				mDisplayImage = filtered;
				mPreviewZoomImage = null;
				
				if(mCreateZoomImage && !mIsPreviewMode){
					
					/*
					if(mZoomThread == null){
						mZoomThread = new QueueingThread();
						mZoomThread.start();
					}
					mZoomThread.add(new IQueueProcess(){

						@Override
						public void doProcess() {
							createZoomImage();
						}
						
					}, true);
					*/
					
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
		repaint();
		//System.out.println("updateDisplayInternal()");
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
									// TODO Auto-generated catch block
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
	
	private void finalizeThread(){
		if(mThread != null){
			mThreadFinish = true;
			synchronized(mThreadLock){
				mThreadLock.notify();
			}
			mThread = null;
		}
	}
	
	public void updateTableInfo(){
		mEventSender.sendEvent(EventObserver.EventTarget_Table, EventObserver.EventType_UpdateFileInfo, mInfoIndex);
		mEventSender.setModified();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(mFileInfo == null){
			return;
		}
		int x = e.getX();
		int y = e.getY();
		
		if(mIsCustomSplitMode){
			splitLineHandle.mouseClicked(e);
		}

		if(javax.swing.SwingUtilities.isRightMouseButton(e)){
			if(mImageArea.contains(x, y)){
				if(mIsCustomSplitMode){
					return;
				}
				mFileInfo.getFilterParam().setRotate(false);
				mFileInfo.getFilterParam().setRotateAngle(0.0f);
				updateDisplayImage(true);
				updateTableInfo();
			}
			else{
				showSettingPopupMenu(x, y);
			}
		}
		else{
			if(mIsCustomSplitMode){
				return;
			}
			if(mImageArea.contains(x, y)){
				clearCropRect();
			}
			else{
				mEventSender.sendEvent(EventObserver.EventTarget_Table, EventObserver.EventType_MoveInfo, 1);
			}
		}

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if(mIsCustomSplitMode){
			splitLineHandle.mouseEntered(e);
		}
		else{
			if(mIsPreviewMode){
				guideLineHandle.mouseEntered(e);
			}
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if(mIsCustomSplitMode){
			splitLineHandle.mouseExited(e);
		}
		else{
			if(mIsPreviewMode){
				guideLineHandle.mouseExited(e);
			}
		}
	}

	private Point mBasePoint = new Point();
	
	@Override
	public void mousePressed(MouseEvent e) {
		if(mIsCustomSplitMode){
			splitLineHandle.mousePressed(e);
		}
		else{
			int x = e.getX();
			int y = e.getY();
			if(javax.swing.SwingUtilities.isRightMouseButton(e)){
				if(mIsPreviewMode){
					guideLineHandle.mousePressed(e);
					if(guideLineHandle.isDragHandle()){
						return;
					}
				}
				if(ptRotateA == null){
					ptRotateA = new Point(x, y);
					ptRotateB = new Point(x, y);
				}
			}
			else{
				if(mIsPreviewMode){
					guideLineHandle.mousePressed(e);
					if(guideLineHandle.isDragHandle()){
						
					}
					else{
						ptLeftTop = new Point();
						ptRightBottom = new Point();
						mBasePoint.setLocation(x, y);
						ptLeftTop.setLocation(x, y);
						ptRightBottom.setLocation(x, y);
					}
				}
				else{
					// Zoom start
					if(mImageArea.contains(x, y)){
						mIsZoomDrag = true;
						mZoomPoint.setLocation(x, y);
						repaint();
					}
				}
			}
		}
	}

	public double radian(double x,double y) { /*ベクトルの角度を計算*/

		if(mIsRotateVertical){
			double s =Math.acos(x/Math.sqrt(x*x+y*y));

			s=(s/Math.PI)*180.0;
		
			return -(s-90.0);
		}
		else{
			double s =Math.acos(y/Math.sqrt(x*x+y*y));

			s=(s/Math.PI)*180.0;

			return (s-90);
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if(mFileInfo == null){
			return;
		}
		
		if(mIsCustomSplitMode){
			splitLineHandle.mouseReleased(e);
		}
		else{
			if(javax.swing.SwingUtilities.isRightMouseButton(e)){
				if(mIsPreviewMode){
					boolean handle = guideLineHandle.isDragHandle();
					guideLineHandle.mouseReleased(e);
					if(handle){
						return;
					}
				}
				if(ptRotateA != null && ptRotateB != null){
					if(!ptRotateA.equals(ptRotateB)){
						ImageFilterParam param = mFileInfo.getFilterParam();
						param.setRotate(true);
						double angle = radian((double)(ptRotateB.x-ptRotateA.x), (double)(ptRotateB.y-ptRotateA.y));
						param.setRotateAngle(angle + param.getRotateAngle());
						updateDisplayImage(true);
						updateTableInfo();
					}
					ptRotateA = null;
					ptRotateB = null;
				}
			}
			else {
				if(mIsPreviewMode){
					boolean isDragged = guideLineHandle.isDragHandle();
					if(!isDragged){
						setCropRect();
					}
					guideLineHandle.mouseReleased(e);
					if(isDragged){
						Dimension offset = new Dimension(0, 0);
						guideLineHandle.getHandleOffset(offset);
						System.out.println("offset=" + offset.toString());
						if(!(offset.width == 0 && offset.height == 0)){
							/* 回転→移動の場合 */
							ImageFilterParam param = mFileInfo.getFilterParam();
							param.setTranslate(true);
							double scaleW = mImageFilter.getResizedScaleW(); // 最後のResize時のScale値
							double scaleH = mImageFilter.getResizedScaleH();
							double angle = param.isRotate() ? param.getRotateAngle() : 0.0f;
							double radian = Math.toRadians(angle);
							
							AffineTransform af = new AffineTransform();
							af.setToRotation(-radian);
							double[] srcPoints = new double[]{ (double)offset.width, (double)offset.height };
							double[] dstPoints = new double[2];
							af.transform(srcPoints, 0, dstPoints, 0, 1);

							System.out.println("src=" + srcPoints[0] + "," + srcPoints[1] + " dst=" +dstPoints[0] + "," + dstPoints[1]);


							param.setTranslateX(param.getTranslateX() - (int)(dstPoints[0] / scaleW));
							param.setTranslateY(param.getTranslateY() - (int)(dstPoints[1] / scaleH));
							//param.setTranslateX(param.getTranslateX() - (int)(offset.width / scaleW));
							//param.setTranslateY(param.getTranslateY() - (int)(offset.height / scaleH));
							updateDisplayImage(true);
							updateTableInfo();
							return;
							
							/* 移動→回転の場合
							ImageFilterParam param = mFileInfo.getFilterParam();
							param.setTranslate(true);
							double scaleW = mImageFilter.getResizedScaleW(); // 最後のResize時のScale値
							double scaleH = mImageFilter.getResizedScaleH();
							param.setTranslateX(param.getTranslateX() - (int)(offset.width / scaleW));
							param.setTranslateY(param.getTranslateY() - (int)(offset.height / scaleH));
							updateDisplayImage();
							updateTableInfo();
							return;
							 */
						}
					}
				}
				else{
					// Zoom end
					mIsZoomDrag = false;
				}
	
			}
		}
		repaint();
	}
	
	private void setCropRect(){
		Rectangle cropRect = getCropRect();
		if(cropRect != null && mFileInfo != null){
			//float scale = getScale();
			Dimension size = SplitFilter.getSplitSize(mOriginalImage, mFileInfo.getFilterParam());
			float scaleX = getScaleX(size.width);
			float scaleY = getScaleY(size.height);
			Rectangle dispImageRect = getDisplayImageRect();
			
			int left = cropRect.x - dispImageRect.x;
			int top = cropRect.y - dispImageRect.y;
			
			int right = (dispImageRect.x + dispImageRect.width) - (cropRect.x + cropRect.width);
			int bottom = (dispImageRect.y + dispImageRect.height) - (cropRect.y + cropRect.height);
			
			float orgLeft = (float)left / scaleX;
			float orgRight = (float)right / scaleX;
			float orgTop = (float)top / scaleY;
			float orgBottom = (float)bottom / scaleY;
			
			ImageFilterParam param = mFileInfo.getFilterParam();
			param.setFullPageCrop(true);
			param.setFullPageCrop((int)orgLeft, (int)orgTop, (int)orgRight, (int)orgBottom);
			param.setTextPageCrop(true);
			param.setTextPageCrop(0, 0, 0, 0);
			param.setPictPageCrop(true);
			param.setPictPageCrop(0, 0, 0, 0);
			
			
			//if(param.getPageType() == FileInfoTable.TYPE_SPLIT_CROP){
			updateDisplayImage(true);
			//}
	
		}
	}
	private void clearCropRect(){
		ImageFilterParam param = mFileInfo.getFilterParam();
		param.setFullPageCrop(false);
		param.setFullPageCrop(0, 0, 0, 0);
		param.setTextPageCrop(false);
		param.setTextPageCrop(0, 0, 0, 0);
		param.setPictPageCrop(false);
		param.setPictPageCrop(0, 0, 0, 0);
		
		//if(param.getPageType() == FileInfoTable.TYPE_SPLIT_CROP){
		updateDisplayImage(true);
	}
	
	private Rectangle getDisplayImageRect(){
		int w = getWidth();
		int h = getHeight();
		int imageW = mDisplayImage.getWidth();
		int imageH = mDisplayImage.getHeight();
		int x = (w - imageW)/2;
		int y = (h - imageH)/2;
		return new Rectangle(x, y, imageW, imageH);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if(mIsCustomSplitMode){
			splitLineHandle.mouseDragged(e);
			repaint();
		}
		else{
			int x = e.getX();
			int y = e.getY();
			if(javax.swing.SwingUtilities.isRightMouseButton(e)){
				if(mIsPreviewMode){
					guideLineHandle.mouseDragged(e);
					if(guideLineHandle.isDragHandle()){
						repaint();
						return;
					}
				}
				if(ptRotateA != null && ptRotateB != null){
					ptRotateB.setLocation(x, y);
					repaint();
				}
			}
			else{
				if(mIsPreviewMode){
					guideLineHandle.mouseDragged(e);
					if(guideLineHandle.isDragHandle()){
						
					}
					else{
						if(x < mBasePoint.x){
							ptLeftTop.x = x;
							ptRightBottom.x = mBasePoint.x;
						}
						if(mBasePoint.x < x){
							ptRightBottom.x = x;
							ptLeftTop.x = mBasePoint.x;
						}
						if(y < mBasePoint.y){
							ptLeftTop.y = y;
							ptRightBottom.y = mBasePoint.y;
						}
						if(mBasePoint.y < y){
							ptRightBottom.y = y;
							ptLeftTop.y = mBasePoint.y;
						}
					}
					repaint();
				}
				else{
					// Zoom
					mZoomPoint.setLocation(x, y);
	
					repaint();
				}
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if(mIsCustomSplitMode){
			splitLineHandle.mouseMoved(e);
		}
		else{
			if(mIsPreviewMode){
				guideLineHandle.mouseMoved(e);
			}
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if(mFileInfo == null){
			return;
		}
		
		if(mIsCustomSplitMode){
			
		}
		else{
			int rotation = e.getWheelRotation();		
			long when = e.getWhen();
			System.out.println("rotation=" + rotation + " when=" + when);
			
			//TODO
			ImageFilterParam param = mFileInfo.getFilterParam();
			param.setRotate(true);
			double angle = 0.1 * rotation;
			param.setRotateAngle(angle + param.getRotateAngle());
			updateDisplayImage(true);
			updateTableInfo();

			
//			ImageFilterParam param = mFileInfo.getFilterParam();
//			double angle = 0.5 * rotation;
//			
//			int x = e.getX();
//			int y = e.getY();
//			int w = getWidth();
//			int h = getHeight();
//			AffineTransform af = new AffineTransform();
//			af.rotate(-Math.toRadians(angle), w/2, h/2);
//			
//			double[] srcPoints = new double[]{ (double)x, (double)y };
//			double[] dstPoints = new double[2];
//			af.transform(srcPoints, 0, dstPoints, 0, 1);
//
//			double offsetx = dstPoints[0] - srcPoints[0];
//			double offsety = dstPoints[1] - srcPoints[1];
//			
//			param.setRotate(true);
//			param.setRotateAngle(angle + param.getRotateAngle());
//			param.setTranslate(true);
//			param.setTranslateX(param.getTranslateX() + (int)offsetx);
//			param.setTranslateY(param.getTranslateY() + (int)offsety);
//			updateDisplayImage();
//			updateTableInfo();
		}
	}
	
	private void showSettingPopupMenu(int x, int y){
		JPopupMenu popup = new JPopupMenu();
		JMenuItem item1 = new JMenuItem("ガイド位置初期化");
		item1.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				guideLineHandle.resetPosition();
				repaint();
			}
		});

		String check0 = guideLineHandle.isVisible() ? "●" : "　";
		JMenuItem item2 = new JMenuItem(check0 + "ガイド表示");
		item2.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				guideLineHandle.setVisible(!guideLineHandle.isVisible());
				repaint();
			}
		});

		String check1 = guideLineHandle.isSyncLineHorizontal() ? "●" : "　";
		JMenuItem item3 = new JMenuItem(check1 + "上下ガイド同期");
		item3.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				guideLineHandle.setSyncLineHorizontal(!guideLineHandle.isSyncLineHorizontal());
				repaint();
			}
		});
		
		String check2 = guideLineHandle.isSyncLineVertical() ? "●" : "　";
		JMenuItem item4 = new JMenuItem(check2 + "左右ガイド同期");
		item4.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				guideLineHandle.setSyncLineVertical(!guideLineHandle.isSyncLineVertical());
				repaint();
			}
		});

		popup.add(item1);
		popup.add(item2);
		popup.add(item3);
		popup.add(item4);
		
		if(mFileInfo != null){
			final ImageFilterParam param = mFileInfo.getFilterParam();
			if(param.isTranslate() && (param.getTranslateX() != 0 || param.getTranslateY() != 0)){
				// 座標移動されていた場合
				JMenuItem item5 = new JMenuItem("位置リセット");
				item5.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent arg0) {
						param.setTranslate(false);
						param.setTranslateX(0);
						param.setTranslateY(0);
						updateDisplayImage(true);
						updateTableInfo();
					}
				});
				
				popup.add(item5);
			}
		}
		
		JMenuItem item5 = new JMenuItem("傾き補正切り替え");
		item5.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				mIsRotateVertical = !mIsRotateVertical;
			}
		});
		
		popup.add(item5);

		
		String check4 = guideLineHandle.isFixed() ? "●" : "　";
		JMenuItem fixMenu1 = new JMenuItem(check4 + "ガイド枠固定");
		fixMenu1.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				guideLineHandle.fixPosition(!guideLineHandle.isFixed());
			}
		});
		
		popup.add(fixMenu1);

		
		JMenu areaMenu = new JMenu("ガイド枠");
		JMenuItem areaMenuItem1 = new JMenuItem("本文ページ切り出し領域に設定");
		areaMenuItem1.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				float top = guideLineHandle.getTopOffset() + 0.5f;
				float bottom = guideLineHandle.getBottomOffset() + 0.5f;
				float left = guideLineHandle.getLeftOffset() + 0.5f;
				float right = guideLineHandle.getRightOffset() + 0.5f;
				if(top < 0){ top = 0; }
				if(top > 1.0f){ top = 1.0f; }
				if(bottom < 0){ bottom = 0; }
				if(bottom > 1.0f){ bottom = 1.0f; }
				if(left < 0){ left = 0; }
				if(left > 1.0f){ left = 1.0f; }
				if(right < 0){ right = 0; }
				if(right > 1.0f){ right = 1.0f; }
				if(mFileInfo != null && mOriginalImage != null){

					Dimension size = SplitFilter.getSplitSize(mOriginalImage, mFileInfo.getFilterParam());
					int imageWidth = size.width;
					int imageHeight = size.height;

					ImageFilterParam param = new ImageFilterParam();
					param.setTextPageCrop(true);
					param.setTextPageCrop((int)(imageWidth * left), (int)(imageHeight * top), imageWidth - (int)(imageWidth * right), imageHeight - (int)(imageHeight * bottom));
					mEventSender.sendEvent(EventObserver.EventTarget_Setting, EventObserver.EventType_UpdateFilterParamOnlyEnable, ImageFilterParamSet.FILTER_INDEX_TEXT, param);
				}
			}
		});
		JMenuItem areaMenuItem2 = new JMenuItem("個別ページ切り出し領域に設定");
		areaMenuItem2.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				float top = guideLineHandle.getTopOffset() + 0.5f;
				float bottom = guideLineHandle.getBottomOffset() + 0.5f;
				float left = guideLineHandle.getLeftOffset() + 0.5f;
				float right = guideLineHandle.getRightOffset() + 0.5f;
				if(top < 0){ top = 0; }
				if(top > 1.0f){ top = 1.0f; }
				if(bottom < 0){ bottom = 0; }
				if(bottom > 1.0f){ bottom = 1.0f; }
				if(left < 0){ left = 0; }
				if(left > 1.0f){ left = 1.0f; }
				if(right < 0){ right = 0; }
				if(right > 1.0f){ right = 1.0f; }
				System.out.println("t=" + top + " l=" + left + " b=" + bottom + " r=" + right);
				if(mFileInfo != null && mOriginalImage != null){
					
					Dimension size = SplitFilter.getSplitSize(mOriginalImage, mFileInfo.getFilterParam());
					int imageWidth = size.width;
					int imageHeight = size.height;
					//float scaleX = getScaleX(size.width);
					//float scaleY = getScaleY(size.height);

					int l = (int)(imageWidth * left);
					int t = (int)(imageHeight * top);
					int r = (int)(imageWidth - (int)(imageWidth * right));
					int b = (int)(imageHeight - (int)(imageHeight * bottom));
					
					ImageFilterParam param = mFileInfo.getFilterParam();
					param.setFullPageCrop(true);
					param.setFullPageCrop(l, t, r, b);
					updateDisplayImage(true);
				}
			}
		});
		JMenuItem areaMenuItem3 = new JMenuItem("縦横比変更（上下固定３：４）");
		areaMenuItem3.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				guideLineHandle.setAspect(600, 800, mImageArea.width, mImageArea.height, true);
				repaint();
			}
		});
		
		areaMenu.add(areaMenuItem1);
		areaMenu.add(areaMenuItem2);
		areaMenu.add(areaMenuItem3);

		popup.add(areaMenu);
		

		boolean isSplited = false;
		if(mFileInfo instanceof ImageFileInfoSplitWrapper){
			JMenuItem splitMenu = new JMenuItem("分割解除");
			splitMenu.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					ImageFileInfoSplitWrapper wrapInfo = (ImageFileInfoSplitWrapper)mFileInfo;
					wrapInfo = wrapInfo.getFirstSplitInfo();
					wrapInfo.getFilterParam().setSplitType(SplitFilter.TYPE_NONE);
					mEventSender.sendEvent(EventObserver.EventTarget_Table, EventObserver.EventType_RenewalList, 0);
					//updateDisplayImage();
				}
			});
			
			popup.add(splitMenu);
		}
//		else{
//			JMenuItem splitMenu1 = new JMenuItem("カスタム分割モード(3x3)");
//			splitMenu1.addActionListener(new ActionListener(){
//				@Override
//				public void actionPerformed(ActionEvent arg0) {
//					if(mIsCustomSplitMode){
//						float[] v = splitLineHandle.getV();
//						float[] h = splitLineHandle.getH();
//						mFileInfo.getFilterParam().setSplitType(SplitFilter.TYPE_CUSTOM, v, h);
//						mEventSender.sendEvent(EventObserver.EventTarget_Table, EventObserver.EventType_RenewalList, 0);
//					}
//					mIsCustomSplitMode = !mIsCustomSplitMode;
//					splitLineHandle.setSplitColRow(3, 3);
//					repaint();
//				}
//			});
//			JMenuItem splitMenu2 = new JMenuItem("カスタム分割モード(3x4)");
//			splitMenu2.addActionListener(new ActionListener(){
//				@Override
//				public void actionPerformed(ActionEvent arg0) {
//					if(mIsCustomSplitMode){
//						float[] v = splitLineHandle.getV();
//						float[] h = splitLineHandle.getH();
//						mFileInfo.getFilterParam().setSplitType(SplitFilter.TYPE_CUSTOM, v, h);
//						mEventSender.sendEvent(EventObserver.EventTarget_Table, EventObserver.EventType_RenewalList, 0);
//					}
//					mIsCustomSplitMode = !mIsCustomSplitMode;
//					splitLineHandle.setSplitColRow(3, 4);
//					repaint();
//				}
//			});
//			
//			popup.add(splitMenu1);
//			popup.add(splitMenu2);
//		}

		
		popup.show(this, x, y);
	}

	public void setOutputSizePreview(boolean enable, int width, int height){
		mIsOutputSizePreview = enable;
		if(enable){
			mPreviewSize.setSize(width, height);
		}else{
			mPreviewSize.setSize(getWidth(), getHeight());
		}
		mImageFilter.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
	}
	
	public void setSimpleZoom(boolean simpleZoom){
		mCreateZoomImage = !simpleZoom;
		if(mCreateZoomImage){
			mPreviewZoomImage = null;
		}
	}
	
	@Override
	public void onEventReceived(int type, int arg1, int arg2, Object obj) {
		switch(type){
		case EventObserver.EventType_PreviewSize:
			Dimension size = (Dimension)obj;
			setOutputSizePreview(arg1 == 1, size.width, size.height);
			break;
		default:
			break;
		}
		
	}
}
