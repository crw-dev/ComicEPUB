/**
 * Contrast/Brightnessフィルタ
 */
package jp.crwdev.app.imagefilter;

import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

import jp.crwdev.app.interfaces.IImageFilter;

public class ContrastFilter implements IImageFilter {

	@Override
	public BufferedImage filter(BufferedImage image, ImageFilterParam param) {
		if(param == null || !param.isContrast()){
			return image;
		}
		/*
		 * Contrast/Brightness
		 */
		float contrast = param.getContrast();
		float brightness = param.getBrightness();
		
		RescaleOp rop = new RescaleOp(contrast, brightness,
			new RenderingHints(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY));
		return rop.filter(image, null);
	}

}
