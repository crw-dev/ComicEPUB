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

	private static Dimension mUnificationTextPageSize = new Dimension();
	
	
	/**
	 * コンストラクタ
	 */
	public AddSpaceFilter(){
		
	}
	
	public static void setUnificationTextPageSize(Dimension size){
		AddSpaceFilter.mUnificationTextPageSize = size;
	}
	
	public static void setUnificationTextPageSize(int width, int height){
		AddSpaceFilter.mUnificationTextPageSize = new Dimension(width, height);
	}
	
	
//	/**
//	 * 余白追加後のサイズ指定
//	 * @param size
//	 */
//	public void setTargetSize(Dimension size){
//		mTargetSize = size;
//	}
	
	@Override
	public BufferedImage filter(BufferedImage image, ImageFilterParam param) {
		if(param.getPageType() == Constant.PAGETYPE_PICT){
			return image;
		}
		
		Dimension targetSize = mUnificationTextPageSize;
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
