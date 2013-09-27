/**
 * 余白を追加し全ページ同じサイズにするためのフィルタ
 * 画像サイズが異なるとAndroid版Readerアプリでページめくりがスライドになってしまうため用意。
 * 一度全ページチェックして全ページが収まる最小の画像サイズを求めた方が良いが手間がかかるので設定の出力サイズで固定にしている
 */
package jp.crwdev.app.imagefilter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import jp.crwdev.app.constant.Constant;
import jp.crwdev.app.interfaces.IImageFilter;

public class FixedSizeFilter implements IImageFilter {

	private boolean mIsEnable = true;
	
	/**
	 * コンストラクタ
	 */
	public FixedSizeFilter(boolean enable){
		mIsEnable = enable;
	}
	
	public void setEnable(boolean enable){
		mIsEnable = enable;
	}
	
	@Override
	public BufferedImage filter(BufferedImage image, ImageFilterParam param) {
		
		if(!mIsEnable || param == null){
			return image;
		}
		
		Dimension targetSize = param.getResizeDimension();
		if(targetSize.width == 0 || targetSize.height == 0){
			return image;
		}
		
		int width = image.getWidth();
		int height = image.getHeight();
		if(width >= targetSize.width && height >= targetSize.height){
			// 同じサイズなら何もしない
			return image;
		}
		
		BufferedImage dest = new BufferedImage(targetSize.width, targetSize.height, image.getType());
		
		int dx = (targetSize.width - width) / 2;
		int dy = (targetSize.height - height) / 2;
		
		Graphics2D g = dest.createGraphics();
		g.setBackground(Color.WHITE);
		g.clearRect(0, 0, targetSize.width, targetSize.height);
		g.drawImage(image, dx, dy, image.getWidth(), image.getHeight(), null);
		g.dispose();
		
		return dest;
	}

}
