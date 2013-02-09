/**
 * 余白追加フィルタ - 本文ページのみを対象とする
 */
package jp.crwdev.app.imagefilter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import jp.crwdev.app.constant.Constant;
import jp.crwdev.app.interfaces.IImageFilter;

public class AddSpaceFilter implements IImageFilter {

	private Dimension mTargetSize = new Dimension();
	
	
	/**
	 * コンストラクタ
	 */
	public AddSpaceFilter(){
		
	}
	
	/**
	 * 余白追加後のサイズ指定
	 * @param size
	 */
	public void setTargetSize(Dimension size){
		mTargetSize = size;
	}
	
	@Override
	public BufferedImage filter(BufferedImage image, ImageFilterParam param) {
		if(param.getPageType() == Constant.PAGETYPE_PICT){
			return image;
		}
		if(mTargetSize.width == 0 || mTargetSize.height == 0){
			return image;
		}
		
		BufferedImage dest = new BufferedImage(mTargetSize.width, mTargetSize.height, image.getType());
		
		int dx = (mTargetSize.width - image.getWidth()) / 2;
		int dy = (mTargetSize.height - image.getHeight()) / 2;
		
		Graphics2D g = dest.createGraphics();
		g.setBackground(Color.WHITE);
		g.clearRect(0, 0, mTargetSize.width, mTargetSize.height);
		g.drawImage(image, dx, dy, image.getWidth(), image.getHeight(), null);
		g.dispose();
		
		return dest;
	}

}
