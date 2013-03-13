package jp.crwdev.app.imagefilter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;


import jp.crwdev.app.constant.Constant;
import jp.crwdev.app.interfaces.IImageFilter;

public class AutoCropFilter implements IImageFilter {

	private static final int mCheckOffset = 3;
	private static final int mMargin = 20;
	
	private static float mAspectX = 0;
	private static float mAspectY = 0;
	
	public static void setAspectRatio(float width, float height){
		mAspectX = width;
		mAspectY = height;
	}
	
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
			left = mMargin;
			top = mMargin;
			right = mMargin;
			bottom = mMargin;
		}
		Rectangle rect = getAutoCropRect(image, left, top, right, bottom);
		
		if(!rect.isEmpty()){
			
			if(rect.width < rect.height){
				float scale = 1.0f;
				if(mAspectX == 0 || mAspectY == 0){
					scale = (float)image.getWidth() / (float)image.getHeight();
				}else{
					scale = mAspectX / mAspectY;
				}
				int width = (int)(rect.height * scale);
				Rectangle newRect = new Rectangle(rect.x-(width-rect.width)/2, rect.y, width, rect.height);
				if(newRect.x < 0){
					newRect.x = 0;
				}
				else if(newRect.x+newRect.width >= image.getWidth()){
					newRect.x = image.getWidth()-newRect.width;
				}
				if(rect.width < newRect.width){
					rect = newRect;
				}
			}else{
				float scale = 1.0f;
				if(mAspectX == 0 || mAspectY == 0){
					scale = (float)image.getHeight() / (float)image.getWidth();
				}else{
					scale = mAspectY / mAspectX;
				}
				int height = (int)(rect.width * scale);
				Rectangle newRect = new Rectangle(rect.x, rect.y-(height-rect.height)/2, rect.width, height);
				if(newRect.y < 0){
					newRect.y = 0;
				}
				else if(newRect.y+newRect.height >= image.getHeight()){
					newRect.y = image.getHeight()-newRect.height;
				}
				if(rect.height < newRect.height){
					rect = newRect;
				}
			}
			
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
	
		int width = image.getWidth();
		int height = image.getHeight();
		
		int left = width-1;
		int right = 0;
		int top = height-1;
		int bottom = 0;
		
		for(int y=0; y<height; y+= mCheckOffset){
			for(int x=0; x<left; x+= mCheckOffset){
				if(!isWhiteColor(image.getRGB(x, y))){
					if(x < left){
						left = x;
					}
					break;
				}
			}
		}
		left -= leftMargin;
		left = Math.max(0, left);

		
		for(int y=0; y<height; y+= mCheckOffset){
			for(int x=width-1; x>=right; x-= mCheckOffset){
				if(!isWhiteColor(image.getRGB(x, y))){
					if(x > right){
						right = x;
					}
					break;
				}
			}
		}
		right += rightMargin;
		right = Math.min(width-1, right);
		
		for(int x=0; x<width; x+= mCheckOffset){
			for(int y=0; y<top; y+= mCheckOffset){
				if(!isWhiteColor(image.getRGB(x, y))){
					if(y < top){
						top = y;
					}
					break;
				}
			}
		}
		top -= topMargin;
		top = Math.max(0, top);
		
		for(int x=0; x<width; x+= mCheckOffset){
			for(int y=height-1; y>=bottom; y-= mCheckOffset){
				if(!isWhiteColor(image.getRGB(x, y))){
					if(y > bottom){
						bottom = y;
					}
					break;
				}
			}
		}
		bottom += bottomMargin;
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
