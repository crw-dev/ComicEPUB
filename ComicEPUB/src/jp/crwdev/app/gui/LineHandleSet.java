/**
 * ガイド線６本セット
 */
package jp.crwdev.app.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import jp.crwdev.app.interfaces.ILineHandle;

public class LineHandleSet implements ILineHandle {
	
	/** ガイド線配列 */
	private LineHandle[] mHandles = new LineHandle[6];
	/** 表示フラグ */
	private boolean mIsVisible = false;
	/** 左右線同期フラグ */
	private boolean mSyncVerticalLines = true;
	/** 上下線同期フラグ */
	private boolean mSyncHorizontalLines = true;
	
	/** ガイド線配列インデックス */
	private static int CENTER_VERTICAL = 0;
	private static int CENTER_HORIZONTAL = 1;
	private static int LEFT_VERTICAL = 2;
	private static int RIGHT_VERTICAL = 3;
	private static int UPPER_HORIZONTAL = 4;
	private static int LOWER_HORIZONTAL = 5;
	
	
	/**
	 * コンストラクタ
	 */
	public LineHandleSet(){
		mHandles[CENTER_VERTICAL] = new LineHandle(true, 0.0f, Color.CYAN, false);
		mHandles[CENTER_HORIZONTAL] = new LineHandle(false, 0.0f, Color.CYAN, false);
		mHandles[LEFT_VERTICAL] = new LineHandle(true, -0.45f, Color.CYAN, false);
		mHandles[RIGHT_VERTICAL] = new LineHandle(true, 0.45f, Color.CYAN, false);
		mHandles[UPPER_HORIZONTAL] = new LineHandle(false, -0.45f, Color.CYAN, false);
		mHandles[LOWER_HORIZONTAL] = new LineHandle(false, 0.45f, Color.CYAN, false);
		
		setSyncLineVertical(true);
		setSyncLineHorizontal(true);
	}

	public void setAspect(float width, float height, int imageWidth, int imageHeight, boolean fixHeight){
		if(fixHeight){
			float ratio = width / height;
			float targetWidth = imageHeight * ratio;
			float offset = targetWidth / imageWidth;
			float baseHeight = Math.abs(mHandles[UPPER_HORIZONTAL].getBaseLineOffset()) +
								Math.abs(mHandles[LOWER_HORIZONTAL].getBaseLineOffset());
			float base = baseHeight * offset / 2.0f;
			mHandles[LEFT_VERTICAL].setSyncLine(null); // 一時的に同期解除
			mHandles[RIGHT_VERTICAL].setSyncLine(null);
			mHandles[LEFT_VERTICAL].setBaseLineOffset(-base);
			mHandles[RIGHT_VERTICAL].setBaseLineOffset(base);
			if(isSyncLineVertical()){
				setSyncLineVertical(true); // 同期
			}
		}
		else{
			float ratio = height / width;
			float targetHeight = imageWidth * ratio;
			float offset = targetHeight / imageHeight;
			float baseWidth = Math.abs(mHandles[LEFT_VERTICAL].getBaseLineOffset()) +
								Math.abs(mHandles[RIGHT_VERTICAL].getBaseLineOffset());
			float base = baseWidth * offset / 2.0f;
			mHandles[UPPER_HORIZONTAL].setSyncLine(null); // 一時的に同期解除
			mHandles[LOWER_HORIZONTAL].setSyncLine(null);
			mHandles[UPPER_HORIZONTAL].setBaseLineOffset(-base);
			mHandles[LOWER_HORIZONTAL].setBaseLineOffset(base);
			if(isSyncLineHorizontal()){
				setSyncLineHorizontal(true); // 同期
			}
		}
	}
	
	/**
	 * ガイドの上線位置の取得
	 * @return
	 */
	public float getTopOffset(){
		return Math.min(mHandles[UPPER_HORIZONTAL].getBaseLineOffset(), mHandles[LOWER_HORIZONTAL].getBaseLineOffset());
	}
	/**
	 * ガイドの下線位置の取得
	 * @return
	 */
	public float getBottomOffset(){
		return Math.max(mHandles[UPPER_HORIZONTAL].getBaseLineOffset(), mHandles[LOWER_HORIZONTAL].getBaseLineOffset());
	}
	/**
	 * ガイドの左線位置の取得
	 * @return
	 */
	public float getLeftOffset(){
		return Math.min(mHandles[LEFT_VERTICAL].getBaseLineOffset(), mHandles[RIGHT_VERTICAL].getBaseLineOffset());
	}
	/**
	 * ガイドの右線位置の取得
	 * @return
	 */
	public float getRightOffset(){
		return Math.max(mHandles[LEFT_VERTICAL].getBaseLineOffset(), mHandles[RIGHT_VERTICAL].getBaseLineOffset());
	}
	
	/**
	 * 左右線同期状態取得
	 * @return
	 */
	public boolean isSyncLineVertical(){
		return mSyncVerticalLines;
	}
	
	/**
	 * 上下線同期状態取得
	 * @return
	 */
	public boolean isSyncLineHorizontal(){
		return mSyncHorizontalLines;
	}

	/**
	 * 左右線同期設定
	 * @param enable
	 */
	public void setSyncLineVertical(boolean enable){
		mSyncVerticalLines = enable;
		if(enable){
			mHandles[LEFT_VERTICAL].setSyncLine(mHandles[RIGHT_VERTICAL]);
			mHandles[RIGHT_VERTICAL].setSyncLine(mHandles[LEFT_VERTICAL]);
		}
		else{
			mHandles[LEFT_VERTICAL].setSyncLine(null);
			mHandles[RIGHT_VERTICAL].setSyncLine(null);
		}
	}

	/**
	 * 上下線同期設定
	 * @param enable
	 */
	public void setSyncLineHorizontal(boolean enable){
		mSyncHorizontalLines = enable;
		if(enable){
			mHandles[UPPER_HORIZONTAL].setSyncLine(mHandles[LOWER_HORIZONTAL]);
			mHandles[LOWER_HORIZONTAL].setSyncLine(mHandles[UPPER_HORIZONTAL]);
		}
		else{
			mHandles[UPPER_HORIZONTAL].setSyncLine(null);
			mHandles[LOWER_HORIZONTAL].setSyncLine(null);
		}
	}
	
	/**
	 * 表示/非表示設定
	 * @param visible
	 */
	public void setVisible(boolean visible) {
		mIsVisible = visible;
	}
	
	/**
	 * 表示状態取得
	 * @return
	 */
	public boolean isVisible() {
		return mIsVisible;
	}
	
	@Override
	public void resetPosition() {
		for(int i=0; i<6; i++){
			mHandles[i].resetPosition();
		}
	}
	
	@Override
	public void getHandleOffset(Dimension offset){
		for(int i=0; i<6; i++){
			mHandles[i].getHandleOffset(offset);
		}
	}
	
	@Override
	public boolean isDragHandle() {
		if(!mIsVisible){
			return false;
		}
		for(int i=0; i<6; i++){
			if(mHandles[i].isDragHandle()){
				return true;
			}
		}
		return false;
	}

	@Override
	public void paint(Graphics g, int width, int height, int imageWidth, int imageHeight) {
		if(!mIsVisible){
			return;
		}
		for(int i=0; i<6; i++){
			mHandles[i].paint(g, width, height, imageWidth, imageHeight);
		}
	}

	@Override
	public void setBaseLineOffset(float offset) {
		for(int i=0; i<6; i++){
			mHandles[i].setBaseLineOffset(offset);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(!mIsVisible){
			return;
		}
		for(int i=0; i<6; i++){
			mHandles[i].mouseClicked(e);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if(!mIsVisible){
			return;
		}
		for(int i=0; i<6; i++){
			mHandles[i].mouseEntered(e);
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if(!mIsVisible){
			return;
		}
		for(int i=0; i<6; i++){
			mHandles[i].mouseExited(e);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(!mIsVisible){
			return;
		}
		for(int i=0; i<6; i++){
			mHandles[i].mousePressed(e);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(!mIsVisible){
			return;
		}
		for(int i=0; i<6; i++){
			mHandles[i].mouseReleased(e);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if(!mIsVisible){
			return;
		}
		for(int i=0; i<6; i++){
			mHandles[i].mouseDragged(e);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if(!mIsVisible){
			return;
		}
		for(int i=0; i<6; i++){
			mHandles[i].mouseMoved(e);
		}
	}

	@Override
	public boolean isFixed() {
		return mHandles[0].isFixed();
	}

	@Override
	public void fixPosition(boolean fixed) {
		for(int i=0; i<6; i++){
			mHandles[i].fixPosition(fixed);
		}
	}

}
