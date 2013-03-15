package jp.crwdev.app.imagefilter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;


import jp.crwdev.app.constant.Constant;
import jp.crwdev.app.interfaces.IImageFilter;

public class AutoCropFilter implements IImageFilter {

	private static final int mCheckOffset = 3;
	private static final int mDefaultCropMargin = 30;
	private static final int mLooseMargin = 5;
	
	@Override
	public BufferedImage filter(BufferedImage image, ImageFilterParam param) {
	
		if(param == null){
			return image;
		}
		if(param.isColorPageCrop() || param.isPictPageCrop() || param.isTextPageCrop() || param.isFullPageCrop()){
			return image;
		}
		if((param.getPageType() == Constant.PAGETYPE_COLOR && !param.isColorPageAutoCrop()) ||
			(param.getPageType() == Constant.PAGETYPE_PICT && !param.isPictPageAutoCrop()) ||
			(param.getPageType() == Constant.PAGETYPE_TEXT && !param.isTextPageAutoCrop()) ||
			(param.getPageType() == Constant.PAGETYPE_AUTO && !param.isFullPageAutoCrop())){
			
			return image;
		}
		
		int left = 0;
		int top = 0;
		int right = 0;
		int bottom = 0;
		switch(param.getPageType()){
		case Constant.PAGETYPE_COLOR:
			left = param.getColorPageCropLeft();
			top = param.getColorPageCropTop();
			right = param.getColorPageCropRight();
			bottom = param.getColorPageCropBottom();
			break;
		case Constant.PAGETYPE_PICT:
			left = param.getPictPageCropLeft();
			top = param.getPictPageCropTop();
			right = param.getPictPageCropRight();
			bottom = param.getPictPageCropBottom();
			break;
		case Constant.PAGETYPE_TEXT:
			left = param.getTextPageCropLeft();
			top = param.getTextPageCropTop();
			right = param.getTextPageCropRight();
			bottom = param.getTextPageCropBottom();
			break;
		case Constant.PAGETYPE_AUTO:
		default:
			left = param.getFullPageCropLeft();
			top = param.getFullPageCropTop();
			right = param.getFullPageCropRight();
			bottom = param.getFullPageCropBottom();
			break;
		}
		if(left == 0 && right == 0 && top == 0 && bottom == 0){
			left = mDefaultCropMargin;
			top = mDefaultCropMargin;
			right = mDefaultCropMargin;
			bottom = mDefaultCropMargin;
		}
		Rectangle rect = getAutoCropRect(image, left, top, right, bottom);
		
		if(!rect.isEmpty()){
			
			if(param.isPreview()){
				Graphics2D g = image.createGraphics();
				g.setColor(Color.BLUE);
				g.drawRect(rect.x, rect.y, rect.width, rect.height);
				g.dispose();
			}
			else{
				return image.getSubimage(rect.x, rect.y, rect.width, rect.height);
			}
		}
		
		return image;
	}

	
	private Rectangle getAutoCropRect(BufferedImage image, int leftMargin, int topMargin, int rightMargin, int bottomMargin){
		Rectangle rect = new Rectangle();
	
//		EdgeDetectionFilter edge = new EdgeDetectionFilter();
//		image = edge.filter(image, null);

		int width = image.getWidth();
		int height = image.getHeight();
		
		int left = leftMargin == 0 ? width-1 : leftMargin;
		int right = rightMargin == 0 ? 0 : width - rightMargin - 1;
		int top = topMargin == 0 ? height-1 : topMargin;
		int bottom = bottomMargin == 0 ? 0 : height - bottomMargin - 1;
		
		boolean found = false;
		for(int y=0; y<height; y+= mCheckOffset){
			for(int x=0; x<leftMargin && x<left; x+= 1){
				if(!isWhiteColor(image.getRGB(x, y))){
					if(x < left){
						left = x;
						found = true;
					}
					break;
				}
			}
		}
		if(found){
			if(mLooseMargin > 0){
				left -= Math.min(10,(left / mLooseMargin)) + 1;
			}else{
				left -= 1;
			}
		}
		left = Math.max(0, left);

		
		found = false;
		for(int y=0; y<height; y+= mCheckOffset){
			for(int x=width-1; x>=width-rightMargin-1 && x>=right; x-= 1){
				if(!isWhiteColor(image.getRGB(x, y))){
					if(x > right){
						right = x;
						found = true;
					}
					break;
				}
			}
		}
		if(found){
			if(mLooseMargin > 0){
				int offset = width - right;
				right += Math.min(10,(offset / mLooseMargin)) + 1;
			}else{
				right += 1;
			}
		}
		right = Math.min(width-1, right);
		
		found = false;
		for(int x=0; x<width; x+= mCheckOffset){
			for(int y=0; y<topMargin && y<top; y+= 1){
				if(!isWhiteColor(image.getRGB(x, y))){
					if(y < top){
						top = y;
						found = true;
					}
					break;
				}
			}
		}
		if(found){
			if(mLooseMargin > 0){
				top -= Math.min(10,(top / mLooseMargin)) + 1;
			}else{
				top -= 1;
			}
		}
		top = Math.max(0, top);
		
		found = false;
		for(int x=0; x<width; x+= mCheckOffset){
			for(int y=height-1; y>=height-bottomMargin-1  && y>=bottom; y-= 1){
				if(!isWhiteColor(image.getRGB(x, y))){
					if(y > bottom){
						bottom = y;
						found = true;
					}
					break;
				}
			}
		}
		if(found){
			if(mLooseMargin > 0){
				int offset = height - bottom;
				bottom += Math.min(10,(offset / mLooseMargin)) + 1;
			}else{
				bottom += 1;
			}
		}
		bottom = Math.min(height-1, bottom);

		if(left >= right){
			left = 0;
			right = width-1;
		}
		if(top >= bottom){
			top = 0;
			bottom = height-1;
		}
		
		rect.setBounds(left, top, right-left, bottom-top);
		
		return rect;
	}

	public boolean isWhiteColor(int color){
		int r = (color >> 16) & 0xff;
		int g = (color >> 8) & 0xff;
		int b = color & 0xff;
		if(r >= 0x80 && g >= 0x80 && b >= 0x80){
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
