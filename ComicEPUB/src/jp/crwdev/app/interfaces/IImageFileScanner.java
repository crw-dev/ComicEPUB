/**
 * 画像ファイルスキャナ
 */
package jp.crwdev.app.interfaces;

public interface IImageFileScanner {

	/**
	 * スキャン対象ファイルオープン
	 * @param path ファイルパス
	 * @return true=成功、false=失敗
	 */
	boolean open(String path);
	
	/**
	 * 画像ファイル情報リスト取得
	 * @return
	 */
	IImageFileInfoList getImageFileInfoList();
	
	/**
	 * オープンしたファイルorフォルダのパス取得
	 * @return
	 */
	String getOpenFilePath();
	
	/**
	 * スキャン対象ファイルクローズ
	 */
	void close();
	
}
