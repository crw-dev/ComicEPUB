/**
 * 分割フィルタ
 */
package jp.crwdev.app.imagefilter;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import jp.crwdev.app.constant.Constant;
import jp.crwdev.app.interfaces.IImageFilter;

public class SplitFilter implements IImageFilter {
	
	public static final int TYPE_NONE = 0;
	public static final int TYPE_R2L_2 = 1;
	public static final int TYPE_R2L_2x2 = 2;
	public static final int TYPE_R2L_3x3 = 3;
	public static final int TYPE_L2R_2 = 4;
	public static final int TYPE_L2R_2x2 = 5;
	public static final int TYPE_L2R_3x3 = 6;
	
	public static final float duplicateMargin = 0.02f;	// 重複領域
	
	
	/**
	 * コンストラクタ
	 */
	public SplitFilter(){
	}
	
	public static Dimension getSplitSize(BufferedImage image, ImageFilterParam param){
		int splitType = param.getSplitType();
		if(splitType == SplitFilter.TYPE_NONE){
			return new Dimension(image.getWidth(), image.getHeight());
		}
		
		int width = image.getWidth();
		int height = image.getHeight();
		
		int widthMargin = (int)(width * duplicateMargin);
		int heightMargin = (int)(height * duplicateMargin);
		
		if(splitType == SplitFilter.TYPE_R2L_2){
			width /= 2;
			width += widthMargin;
		}
		else if(splitType == SplitFilter.TYPE_R2L_2x2){
			width /= 2;
			height /= 2;
			width += widthMargin;
			height += heightMargin;
		}
		else if(splitType == SplitFilter.TYPE_R2L_3x3){
			width /= 3;
			height /= 3;
			width += widthMargin;
			height += heightMargin;
		}
		else if(splitType == SplitFilter.TYPE_L2R_2){
			width /= 2;
			width += widthMargin;
		}
		else if(splitType == SplitFilter.TYPE_L2R_2x2){
			width /= 2;
			height /= 2;
			width += widthMargin;
			height += heightMargin;
		}
		else if(splitType == SplitFilter.TYPE_L2R_3x3){
			width /= 3;
			height /= 3;
			width += widthMargin;
			height += heightMargin;
		}
		
		
		return new Dimension(width, height);
	}

	@Override
	public BufferedImage filter(BufferedImage image, ImageFilterParam param) {
		int splitType = param.getSplitType();
		if(splitType == SplitFilter.TYPE_NONE){
			return image;
		}
		
		int splitIndex = param.getSplitIndex();
		
		int x = 0;
		int y = 0;
		int width = image.getWidth();
		int height = image.getHeight();
		
		int widthMargin = (int)(width * duplicateMargin);
		int heightMargin = (int)(height * duplicateMargin);

		if(splitType == SplitFilter.TYPE_L2R_2){
			width /= 2;
			x = width * splitIndex;
			if(splitIndex % 2 == 1){
				x -= widthMargin;
			}
			width += widthMargin;
		}
		else if(splitType == SplitFilter.TYPE_L2R_2x2){
			width /= 2;
			height /= 2;
			x = width * (splitIndex % 2);
			y = height * (splitIndex / 2);
			if(splitIndex % 2 == 1){
				x -= widthMargin;
			}
			if(splitIndex / 2 == 1){
				y -= heightMargin;
			}
			width += widthMargin;
			height += heightMargin;
		}
		else if(splitType == SplitFilter.TYPE_L2R_3x3){
			width /= 3;
			height /= 3;
			x = width * (splitIndex % 3);
			y = height * (splitIndex / 3);
			if(splitIndex % 3 == 1){
				x -= widthMargin / 2;
			}else if(splitIndex % 3 == 2){
				x -= widthMargin;
			}
			if(splitIndex / 3 == 1){
				y -= heightMargin / 2;
			}else if(splitIndex / 3 == 2){
				y -= heightMargin;
			}
			width += widthMargin;
			height += heightMargin;
		}
		else if(splitType == SplitFilter.TYPE_R2L_2){
			width /= 2;
			x = width * (1-splitIndex);
			if(splitIndex % 2 == 0){
				x -= widthMargin;
			}
			width += widthMargin;
		}
		else if(splitType == SplitFilter.TYPE_R2L_2x2){
			width /= 2;
			height /= 2;
			x = width * (1-(splitIndex % 2));
			y = height * (splitIndex / 2);
			if(splitIndex % 2 == 0){
				x -= widthMargin;
			}
			if(splitIndex / 2 == 1){
				y -= heightMargin;
			}
			width += widthMargin;
			height += heightMargin;
		}
		else if(splitType == SplitFilter.TYPE_R2L_3x3){
			width /= 3;
			height /= 3;
			x = width * (2-(splitIndex % 3));
			y = height * (splitIndex / 3);
			if(splitIndex % 3 == 1){
				x -= widthMargin / 2;
			}else if(splitIndex % 3 == 0){
				x -= widthMargin;
			}
			if(splitIndex / 3 == 1){
				y -= heightMargin / 2;
			}else if(splitIndex / 3 == 2){
				y -= heightMargin;
			}
			width += widthMargin;
			height += heightMargin;
		}
		

		BufferedImage dest = new BufferedImage(width, height, image.getType());
		Graphics2D g = dest.createGraphics();
		g.drawImage(image, 0, 0, width, height, x, y, x+width, y+height, null);
		g.dispose();
		
		return dest;
	}
	
//	@Override
//	public BufferedImage filter(BufferedImage image, ImageFilterParam param) {
//		if(param.getSplitType() == Constant.SPLITTYPE_NONE){
//			return image;
//		}
//		
//		int width = image.getWidth() / 2;
//		int height = image.getHeight();
//		int x = 0;
//		int y = 0;
//		if(param.getSplitType() == Constant.SPLITTYPE_RIGHT_TO_LEFT){
//			if(param.getSplitIndex() == 0){
//				x = width;
//			}
//		}
//		else if(param.getSplitType() == Constant.SPLITTYPE_LEFT_TO_RIGHT){
//			if(param.getSplitIndex() > 0){
//				x = width;
//			}
//		}
//		
//		BufferedImage dest = new BufferedImage(width, height, image.getType());
//		Graphics2D g = dest.createGraphics();
//		g.drawImage(image, 0, 0, width, height, x, y, x+width, y+height, null);
//		g.dispose();
//		
//		return dest;
//	}

}
