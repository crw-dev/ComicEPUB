/**
 * 余白除去フィルタ
 */
package jp.crwdev.app.imagefilter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import jp.crwdev.app.constant.Constant;
import jp.crwdev.app.interfaces.IImageFilter;

public class CropFilter implements IImageFilter {

	/** 全ページモードフラグ */
	private boolean mIsFullPageMode;
	/** 除去領域表示フラグ */
	//private boolean mIsDisplayCropArea = true;
	
	/**
	 * コンストラクタ
	 * @param fullPage　true:FullPageCropパラメータを使用してCropが実行される
	 *                 false:TextPageCrop or PictPageCropパラメータを使用してCropが実行される
	 */
	public CropFilter(boolean fullPage){
		mIsFullPageMode = fullPage;
	}
	
	@Override
	public BufferedImage filter(BufferedImage image, ImageFilterParam param) {
		if(param == null){
			return image;
		}
		if(!mIsFullPageMode && !param.isTextPageCrop() && !param.isPictPageCrop() && !param.isColorPageCrop()){
			return image;
		}
		else if(mIsFullPageMode && !param.isFullPageCrop()){
			return image;
		}
		
		int width = image.getWidth();
		int height = image.getHeight();
		
		int topLine = 0;
		int leftLine = 0;
		int subWidth = 0;
		int subHeight = 0;
		
		if(!mIsFullPageMode){
			if(param.getConvertPageType() == Constant.PAGETYPE_PICT){
				if(param.isPictPageCrop()){
					topLine = param.getPictPageCropTop();
					leftLine = param.getPictPageCropLeft();
					subWidth = width - param.getPictPageCropLeft() - param.getPictPageCropRight();
					subHeight = height - param.getPictPageCropTop() - param.getPictPageCropBottom();
					
					if(param.isPreview() && param.isDrawCropAreaInPreview() && param.isFullPageCrop()){
						topLine += param.getFullPageCropTop();
						leftLine += param.getFullPageCropLeft();
						subWidth -= param.getFullPageCropLeft() + param.getFullPageCropRight();
						subHeight -= param.getFullPageCropTop() + param.getFullPageCropBottom();
					}
				}
				else{
					return image;
				}
			}
			else if(param.getConvertPageType() == Constant.PAGETYPE_COLOR){
				if(param.isColorPageCrop()){
					topLine = param.getColorPageCropTop();
					leftLine = param.getColorPageCropLeft();
					subWidth = width - param.getColorPageCropLeft() - param.getColorPageCropRight();
					subHeight = height - param.getColorPageCropTop() - param.getColorPageCropBottom();
					
					if(param.isPreview() && param.isDrawCropAreaInPreview() && param.isFullPageCrop()){
						topLine += param.getFullPageCropTop();
						leftLine += param.getFullPageCropLeft();
						subWidth -= param.getFullPageCropLeft() + param.getFullPageCropRight();
						subHeight -= param.getFullPageCropTop() + param.getFullPageCropBottom();
					}
				}
				else{
					return image;
				}
			}
			else{
				if(param.isTextPageCrop()){
					topLine = param.getTextPageCropTop();
					leftLine = param.getTextPageCropLeft();
					subWidth = width - param.getTextPageCropLeft() - param.getTextPageCropRight();
					subHeight = height - param.getTextPageCropTop() - param.getTextPageCropBottom();
					
					if(param.isPreview() && param.isDrawCropAreaInPreview() && param.isFullPageCrop()){
						topLine += param.getFullPageCropTop();
						leftLine += param.getFullPageCropLeft();
						subWidth -= param.getFullPageCropLeft() + param.getFullPageCropRight();
						subHeight -= param.getFullPageCropTop() + param.getFullPageCropBottom();
					}
				}
				else{
					return image;
				}
			}
		}
		else{
			if(param.isFullPageCrop()){
				topLine = param.getFullPageCropTop();
				leftLine = param.getFullPageCropLeft();
				subWidth = width - param.getFullPageCropLeft() - param.getFullPageCropRight();
				subHeight = height - param.getFullPageCropTop() - param.getFullPageCropBottom();
			}
		}

		if(topLine < 0 || subHeight < 0 || leftLine < 0 || subWidth < 0){
			return image;
		}

		if(param.isPreview()){
			if(param.isDrawCropAreaInPreview()){
				//元データを保護するため新しいImageを作成する
				BufferedImage dest = new BufferedImage(width, height, image.getType());
				Graphics2D g2 = dest.createGraphics();
				g2.drawImage((Image)image,0,0,null);  // BufferedImageに描画させる
				g2.setColor(Color.BLACK);
				g2.drawRect(leftLine, topLine, subWidth, subHeight);
				g2.dispose();
				return dest;
				
				//Graphics2D g2 = image.createGraphics();
				//g2.setColor(Color.BLACK);
				//g2.drawRect(leftLine, topLine, subWidth, subHeight);
				//g2.dispose();
				//return image;
			}
			//else{
			//	return image;
			//}
		}
		
		return image.getSubimage(leftLine, topLine, subWidth, subHeight);
	}

}
