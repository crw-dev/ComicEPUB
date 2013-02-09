/**
 * 画像ファイル情報
 */
package jp.crwdev.app.interfaces;

import java.io.InputStream;

import jp.crwdev.app.imagefilter.ImageFilterParam;

public interface IImageFileInfo {

	/**
	 * ファイル名取得
	 * @return
	 */
	String getFileName();

	/**
	 * フルパス取得
	 * @return
	 */
	String getFullPath();
	
	/**
	 * 画像フォーマット取得
	 * @return "jpeg" or "png" or "gif"
	 */
	String getFormat();

	/**
	 * 画像幅取得
	 * @return
	 */
	int getWidth();
	
	/**
	 * 画像高さ取得
	 * @return
	 */
	int getHeight();
	
	/**
	 * 画像サイズ取得
	 * @return
	 */
	long getSize();
	
	/**
	 * 画像InputStream取得
	 * @return
	 */
	InputStream getInputStream();
	
	/**
	 * Jpeg判定
	 * @return same as "jpeg".equals(getFormat())
	 */
	boolean isJpeg();
	
	/**
	 * 画像フィルタパラメータ取得
	 * @return
	 */
	ImageFilterParam getFilterParam();
	
	/**
	 * 画像フィルタパラメータ設定
	 * @param param
	 */
	void setFilterParam(ImageFilterParam param);

	/**
	 * リソース解放
	 */
	void release();
}
