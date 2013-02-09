/**
 * 画像ファイルWriter
 */
package jp.crwdev.app.interfaces;

public interface IImageFileWriter {

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
	boolean write(IImageFileInfoList list);
	
	/**
	 * 出力ファイルクローズ
	 */
	void close();
}
