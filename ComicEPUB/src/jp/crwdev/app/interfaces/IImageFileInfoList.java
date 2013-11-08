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
	 * 画像ファイル情報挿入
	 * @param index
	 * @param info
	 */
	void insert(int index, IImageFileInfo info);
	
	/**
	 * 再構築
	 * @return
	 */
	public IImageFileInfoList renew();
	
	
	/**
	 * ソート
	 */
	void sort();
	
	/**
	 * ソート有効設定
	 */
	void setEnableSort(boolean enable);
	
	/**
	 * ソート有効フラグ取得
	 * @return
	 */
	boolean isEnableSort();
	
	/**
	 * リリース
	 */
	void release();
}
