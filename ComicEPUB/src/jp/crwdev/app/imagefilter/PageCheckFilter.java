/**
 * 挿絵・本文ページのチェックを行うフィルタ
 */
package jp.crwdev.app.imagefilter;

import java.awt.image.BufferedImage;

import jp.crwdev.app.constant.Constant;
import jp.crwdev.app.interfaces.IImageFilter;

public class PageCheckFilter implements IImageFilter {

	private boolean mIsTextPageMode = false;
	
	public PageCheckFilter(boolean textPage){
		mIsTextPageMode = textPage;
	}
	
	/**
	 * 挿絵・本文ページのチェックを行った結果は ImageFilterParam.getConvertPageType() で取得可能
	 */
	@Override
	public BufferedImage filter(BufferedImage image, ImageFilterParam param) {
		if(image == null || param == null){
			return image;
		}
		
		boolean isWhitePage = isWhiteImage(image, param, false);
		param.setConvertPageType(isWhitePage ? Constant.PAGETYPE_TEXT : param.getPageType());

		return image;
	}

	public boolean isWhiteImage(BufferedImage image, ImageFilterParam param, boolean forceCheckWithoutPicture) {
		int width = image.getWidth();
		int height = image.getHeight();
		
		int topLine = 0;
		int leftLine = 0;
		int bottomLine = 0;
		int rightLine = 0;
		
		//TODO: 要見直し
		if(mIsTextPageMode){
			if(forceCheckWithoutPicture){
				if(param.getPageType() == Constant.PAGETYPE_PICT || param.getPageType() == Constant.PAGETYPE_COLOR){
					return false;
				}
			}else{
				if(param.getPageType() == Constant.PAGETYPE_TEXT){
					return true;
				}
				else if(param.getPageType() == Constant.PAGETYPE_PICT || param.getPageType() == Constant.PAGETYPE_COLOR){
					return false;
				}
			}
			
			topLine = param.getTextPageCropTop();
			leftLine = param.getTextPageCropLeft();
			rightLine = width - param.getTextPageCropRight() - 1;
			bottomLine = height - param.getTextPageCropBottom() - 1;
			
			if(param.isPreview() && param.isDrawCropAreaInPreview() && param.isFullPageCrop()){
				topLine += param.getFullPageCropTop();
				leftLine += param.getFullPageCropLeft();
				rightLine -= param.getFullPageCropRight();
				bottomLine -= param.getFullPageCropBottom();
			}
		}
		else{
			if(param.isFullPageCrop()){
				topLine = param.getFullPageCropTop();
				leftLine = param.getFullPageCropLeft();
				rightLine = width - param.getFullPageCropRight() - 1;
				bottomLine = height - param.getFullPageCropBottom() - 1;
			}
		}
		
		// 入力値チェック：不正な値なら外周
		if(topLine > bottomLine){
			int tmp = topLine;
			topLine = bottomLine;
			bottomLine = tmp;
		}
		if(leftLine > rightLine){
			int tmp = leftLine;
			leftLine = rightLine;
			rightLine = tmp;
		}
		if(leftLine < 0 || rightLine >= width || topLine < 0 || bottomLine >= height){
			leftLine = 0;
			rightLine = width - 1;
			topLine = 0;
			bottomLine = height - 1;
		}
		
		int total = 0;
		int white = 0;
		for(int x=0; x<width; x++){
			if(isWhiteColor(image.getRGB(x, topLine))){
				white++;
			}
			total++;
			if(isWhiteColor(image.getRGB(x, bottomLine))){
				white++;
			}
			total++;
		}
		for(int y=0; y<height; y++){
			if(isWhiteColor(image.getRGB(leftLine, y))){
				white++;
			}
			total++;
			if(isWhiteColor(image.getRGB(rightLine, y))){
				white++;
			}
			total++;
		}
		
		if((white*100/total) >= 95){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean isWhiteColor(int color){
		int r = (color >> 16) & 0xff;
		int g = (color >> 8) & 0xff;
		int b = color & 0xff;
		if(r >= 0xd8 && g >= 0xd8 && b >= 0xd8){
			return true;
		}
		return false;
	}
	public boolean isBlackColor(int color){
		int r = (color >> 16) & 0xff;
		int g = (color >> 8) & 0xff;
		int b = color & 0xff;
		if(r <= 0x50 && g <= 0x50 && b <= 0x50){
			return true;
		}
		return false;
	}
	
}
