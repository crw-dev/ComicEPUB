/**
 * 画像ファイル情報リスト
 */
package jp.crwdev.app.interfaces;

public interface IImageFileInfoList {

	/**
	 * サイズ取得
	 * @return
	 */
	int size();
	
	/**
	 * 画像ファイル情報取得
	 * @param index
	 * @return
	 */
	IImageFileInfo get(int index);
	
	/**
	 * 画像ファイル情報削除
	 * @param index
	 * @return
	 */
	IImageFileInfo remove(int index);
	
	/**
	 * リスト初期化
	 */
	void clear();
	
	/**
	 * 画像ファイル情報追加
	 * @param info
	 * @return
	 */
	boolean add(IImageFileInfo info);
	
	/**
	 * 再構築
	 * @return
	 */
	public IImageFileInfoList renew();
}
