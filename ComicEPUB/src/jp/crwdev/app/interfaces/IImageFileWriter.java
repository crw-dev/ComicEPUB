/**
 * 画像ファイルWriter
 */
package jp.crwdev.app.interfaces;

public interface IImageFileWriter {

	public interface OnProgressListener{
		void onProgress(int progress, String message);
	}
	
	/**
	 * 出力ファイルオープン
	 * @param filepath
	 * @return true=成功、false=失敗
	 */
	boolean open(String filepath);
	
	/**
	 * ImageFilter設定
	 * @param filter
	 */
	void setImageFilter(IImageFilter filter);
	
	/**
	 * ファイル書き出し
	 * @param list IImageFileInfoListオブジェクト
	 * @return true=成功、false=失敗
	 */
	boolean write(IImageFileInfoList list, OnProgressListener listener);
	
	/**
	 * ファイル出力処理キャンセル
	 */
	void cancel();
	
	/**
	 * 拡張子取得
	 * @return
	 */
	String getSuffix();
	
	/**
	 * 出力ファイルクローズ
	 */
	void close();
}
