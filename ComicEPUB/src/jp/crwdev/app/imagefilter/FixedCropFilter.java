/**
 * 指定サイズに切り抜いて同じ画像サイズにするためのフィルタ
 * ※実装検討中
 */
package jp.crwdev.app.imagefilter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import jp.crwdev.app.interfaces.IImageFilter;

public class FixedCropFilter implements IImageFilter {
	
	/**
	 * Constructor
	 */
	public FixedCropFilter(){
		
	}

	@Override
	public BufferedImage filter(BufferedImage image, ImageFilterParam param) {

		int fixedWidth = 600;
		int fixedHeight = 800;
		
		int width = image.getWidth();
		int height = image.getHeight();

		if(width<=fixedWidth && height<=fixedHeight){
			return image;
		}
		
		
		float centerPosition = 0.5f;
		
		int centerH = (int)(width * centerPosition);
		int left = centerH - fixedWidth / 2;
		int right = left + fixedWidth;
		
		int centerV = (int)(height * centerPosition);
		int top = centerV - fixedHeight / 2;
		int bottom = top + fixedHeight;
		
		BufferedImage dest = new BufferedImage(fixedWidth, fixedHeight, image.getType());
		
		Graphics2D g = dest.createGraphics();
		g.setBackground(Color.WHITE);
		g.clearRect(0, 0, fixedWidth, fixedHeight);
		g.drawImage(image, 0, 0, fixedWidth, fixedHeight, left, top, right, bottom, null);
		g.dispose();

		
		return dest;
	}

}
