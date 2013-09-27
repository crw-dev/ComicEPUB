package jp.crwdev.app.imagefilter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import jp.crwdev.app.interfaces.IImageFilter;

public class SpecificSizeFilter implements IImageFilter {

	private static Dimension mSpecifiedSize = null;
	
	/**
	 * コンストラクタ
	 */
	public SpecificSizeFilter(){
		
	}
	public SpecificSizeFilter(int width, int height){
		setUnificationTextPageSize(width, height);
	}
	
	public static void setUnificationTextPageSize(Dimension size){
		mSpecifiedSize = size;
	}
	
	public static void setUnificationTextPageSize(int width, int height){
		mSpecifiedSize = new Dimension(width, height);
	}

	@Override
	public BufferedImage filter(BufferedImage image, ImageFilterParam param) {
		Dimension targetSize = mSpecifiedSize;
		if(targetSize == null || targetSize.width == 0 || targetSize.height == 0){
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
