/**
 * ガイド線クラス
 */
package jp.crwdev.app.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import jp.crwdev.app.interfaces.ILineHandle;

public class LineHandle implements ILineHandle {
	
	/** 描画色 */
	private Color mBaseLineColor = Color.CYAN;
	private Color mHandleLineColor = Color.RED;
	
	private boolean mIsEnableLeftDrag = false;

	/** 縦線フラグ */
	private boolean mIsVertical = true;
	/** 基準点 */
	private int mBaseLine;
	/** 可動点 */
	private int mHandleLine;
	/** 画像中央からの基準点 */
	private float mBaseLineOffset = 0.0f;
	/** 画像中央からの基準点初期値 */
	private float mInitBaseLineOffset = 0.0f;
	
	/** 可動点のドラッグ状態 */
	private boolean mIsDragHandle = false;
	/** 基準点のドラッグ状態 */
	private boolean mIsDragBaseLine = false;
	
	/** 同期して動く線 */
	private LineHandle mSyncLine = null;
	
	/** 描画領域幅 */
	private int mWidth;
	/** 描画領域高さ */
	private int mHeight;
	/** 画像幅 */
	private int mImageWidth;
	/** 画像高さ */
	private int mImageHeight;
	
	/** 基準点からの距離 */
	private int mHandleOffset = 0;
	
	/** 基準点固定フラグ */
	private boolean mIsFixed = false;
	

	/**
	 * コンストラクタ
	 * @param isVertical　true=縦線, false=横線
	 * @param offset 画像中央からの基準点初期値
	 */
	public LineHandle(boolean isVertical, float offset, Color baseColor, boolean leftDrag){
		mIsVertical = isVertical;
		mBaseLineOffset = offset;
		mInitBaseLineOffset = offset;
		mBaseLineColor = baseColor;
		mIsEnableLeftDrag = leftDrag;
	}
	
	/**
	 * 同期する線の設定
	 * @param syncLine
	 */
	public void setSyncLine(LineHandle syncLine){
		mSyncLine = syncLine;
	}

	/**
	 * 基準点の取得
	 * @return
	 */
	public float getBaseLineOffset(){
		return mBaseLineOffset;
	}
	
	/**
	 * 描画色設定
	 * @param color
	 */
	public void setBaseLineColor(Color color){
		mBaseLineColor = color;
	}

	@Override
	public void resetPosition(){
		mBaseLineOffset = mInitBaseLineOffset;
	}
	
	@Override
	public void getHandleOffset(Dimension offset){
		if(mIsVertical){
			offset.width += mHandleOffset;
		}
		else{
			offset.height += mHandleOffset;			
		}
	}
	
	@Override
	public boolean isDragHandle(){
		return mIsDragHandle || mIsDragBaseLine;
	}
	
	@Override
	public void setBaseLineOffset(float offset){
		mBaseLineOffset = offset;
	}
	
	private void setViewSize(int width, int height, int imageWidth, int imageHeight){
		mWidth = width;
		mHeight = height;
		mImageWidth = imageWidth;
		mImageHeight = imageHeight;
		if(mIsVertical){
			mBaseLine = width / 2 + (int)(mBaseLineOffset * imageWidth);
		}
		else{
			mBaseLine = height / 2 + (int)(mBaseLineOffset * imageHeight);
		}
	}
	
	@Override
	public void paint(Graphics g, int width, int height, int imageWidth, int imageHeight){
		setViewSize(width, height, imageWidth, imageHeight);
		if(mIsVertical){
			g.setColor(mBaseLineColor);
			g.drawLine(mBaseLine, 0, mBaseLine, height-1);
			if(mIsDragHandle){
				g.setColor(mHandleLineColor);
				g.drawLine(mHandleLine, 0, mHandleLine, height-1);
			}
		}
		else{
			g.setColor(mBaseLineColor);
			g.drawLine(0, mBaseLine, width-1, mBaseLine);
			if(mIsDragHandle){
				g.setColor(mHandleLineColor);
				g.drawLine(0, mHandleLine, width-1, mHandleLine);
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if(mIsDragHandle){
			mIsDragHandle = false;
			mHandleLine = mBaseLine;
		}
		if(mIsDragBaseLine){
			mIsDragBaseLine = false;
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		final int HandleSize = 2;
		mHandleOffset = 0;
		if(mIsVertical){
			int x = e.getX();
			if(mBaseLine - HandleSize <= x && x <= mBaseLine + HandleSize){
				if(mIsEnableLeftDrag || javax.swing.SwingUtilities.isRightMouseButton(e)){
					if(!mIsFixed){
						mIsDragBaseLine = true;
					}
				}
				else{
					mIsDragHandle = true;
					mHandleLine = x;
				}
			}
		}
		else{
			int y = e.getY();
			if(mBaseLine - HandleSize <= y && y <= mBaseLine + HandleSize){
				if(mIsEnableLeftDrag || javax.swing.SwingUtilities.isRightMouseButton(e)){
					if(!mIsFixed){
						mIsDragBaseLine = true;
					}
				}
				else{
					mIsDragHandle = true;
					mHandleLine = y;
				}
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mHandleOffset = 0;
		if(mIsDragHandle){
			if(mHandleLine != mBaseLine){
				int diff = mHandleLine - mBaseLine;
				mHandleOffset = diff;
				System.out.println("handle offset=" + diff);
			}
			mIsDragHandle = false;
		}
		else if(mIsDragBaseLine){
			mIsDragBaseLine = false;
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if(mIsDragHandle){
			if(mIsVertical){
				mHandleLine = e.getX();
			}
			else{
				mHandleLine = e.getY();
			}
		}
		else if(mIsDragBaseLine){
			if(mIsVertical){
				mBaseLineOffset = (float)((e.getX() - mWidth / 2)/(float)mImageWidth);
				if(mSyncLine != null){
					mSyncLine.setBaseLineOffset(-mBaseLineOffset);
				}
			}
			else{
				mBaseLineOffset = (float)((e.getY() - mHeight / 2)/(float)mImageHeight);
				if(mSyncLine != null){
					mSyncLine.setBaseLineOffset(-mBaseLineOffset);
				}
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		
	}

	@Override
	public boolean isFixed() {
		return mIsFixed;
	}

	@Override
	public void fixPosition(boolean fixed) {
		mIsFixed = fixed;
	}

}
