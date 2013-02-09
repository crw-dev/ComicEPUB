/**
 * 分割フィルタ
 */
package jp.crwdev.app.imagefilter;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import jp.crwdev.app.constant.Constant;
import jp.crwdev.app.interfaces.IImageFilter;

public class SplitFilter implements IImageFilter {
	
	/**
	 * コンストラクタ
	 */
	public SplitFilter(){
	}

	@Override
	public BufferedImage filter(BufferedImage image, ImageFilterParam param) {
		if(param.getSplitType() == Constant.SPLITTYPE_NONE){
			return image;
		}
		
		int width = image.getWidth() / 2;
		int height = image.getHeight();
		int x = 0;
		int y = 0;
		if(param.getSplitType() == Constant.SPLITTYPE_RIGHT_TO_LEFT){
			if(param.getSplitIndex() == 0){
				x = width;
			}
		}
		else if(param.getSplitType() == Constant.SPLITTYPE_LEFT_TO_RIGHT){
			if(param.getSplitIndex() > 0){
				x = width;
			}
		}
		
		BufferedImage dest = new BufferedImage(width, height, image.getType());
		Graphics2D g = dest.createGraphics();
		g.drawImage(image, 0, 0, width, height, x, y, x+width, y+height, null);
		g.dispose();
		
		return dest;
	}

}
