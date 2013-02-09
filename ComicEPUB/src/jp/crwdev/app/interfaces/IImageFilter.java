/**
 * BufferedImageを編集するためのインターフェース
 */

package jp.crwdev.app.interfaces;

import java.awt.image.BufferedImage;

import jp.crwdev.app.imagefilter.ImageFilterParam;

public interface IImageFilter {

	/**
	 * フィルタ処理を行う
	 * @param image 入力画像
	 * @param param フィルタパラメータ
	 * @return 編集後の画像。編集が行われなかった場合は入力画像がそのまま返される
	 */
	BufferedImage filter(BufferedImage image, ImageFilterParam param);
}
