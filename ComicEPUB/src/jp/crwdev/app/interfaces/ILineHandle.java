/**
 * マウス操作可能なガイド線とガイド線からのOffsetを取得するインターフェース
 */
package jp.crwdev.app.interfaces;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public interface ILineHandle extends MouseListener, MouseMotionListener {

	/**
	 * ドラッグ中判定
	 * @return ドラッグ中はtrue、それ以外はfalse
	 */
	boolean isDragHandle();
	
	/**
	 * 位置リセット
	 * ガイド線を初期位置に戻す
	 */
	void resetPosition();
	
	/**
	 * ガイド線の初期位置設定
	 * @param offset 画像中央からのoffset。中央が0、左上端が-0.5、右下端が0.5と
	 */
	void setBaseLineOffset(float offset);
	
	/**
	 * 描画
	 * @param g Graphicsオブジェクト
	 * @param width 描画領域の幅
	 * @param height 描画領域の高さ
	 * @param imageWidth 画像の幅
	 * @param imageHeight 画像の高さ
	 */
	void paint(Graphics g, int width, int height, int imageWidth, int imageHeight);
	
	/**
	 * ガイド線からのOffsetを取得する
	 * @param offset
	 */
	void getHandleOffset(Dimension offset);

	/**
	 * ガイド線の位置固定の設定
	 * @param fixed 
	 */
	void fixPosition(boolean fixed);
	
	/**
	 * ガイド線の固定判定
	 * @return
	 */
	boolean isFixed();

}
