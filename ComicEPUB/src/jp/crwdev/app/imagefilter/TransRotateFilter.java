/**
 * 移動・回転フィルタ
 */
package jp.crwdev.app.imagefilter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import jp.crwdev.app.interfaces.IImageFilter;

public class TransRotateFilter implements IImageFilter {

	@Override
	public BufferedImage filter(BufferedImage image, ImageFilterParam param) {
		if(param == null || (!param.isTranslate() && !param.isRotate())){
			return image;
		}
		
		double angle = param.getRotateAngle();
		int tx = param.getTranslateX();
		int ty = param.getTranslateY();

		if(angle == 0.0f && tx == 0 && ty == 0){
			return image;
		}
		
		int width = image.getWidth();
		int height = image.getHeight();
		
		int cx = width / 2;
		int cy = height / 2;
		
		
		BufferedImage bimg = new BufferedImage(width,height,image.getType());
		Graphics2D g2 = bimg.createGraphics();
		g2.setBackground(Color.WHITE);
		g2.clearRect(0, 0, width, height);
		AffineTransform beforeAffin = g2.getTransform(); 
		AffineTransform affin = new AffineTransform(); 
		
		// 回転
		if(param.isRotate() && !(angle == 0.0f)){
			affin.rotate(Math.toRadians( angle ), cx, cy);
		}
		// 移動
		if(param.isTranslate() && !(tx == 0 && ty == 0)){
			affin.translate(tx, ty);
		}
//		if(!param.isRotate()){
//			angle = 0.0f;
//		}
//		if(!param.isTranslate()){
//			tx = 0;
//			ty = 0;
//		}
//		affin.setToTranslation(tx, ty);
//		affin.rotate(Math.toRadians(angle), cx-tx, cy-ty);
		

		g2.setTransform(affin);
		
		if(!param.isPreview()){
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		    g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
		    		RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		    		RenderingHints.VALUE_ANTIALIAS_ON);
		    g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
		    		RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		    g2.setRenderingHint(RenderingHints.KEY_RENDERING,
		    		RenderingHints.VALUE_RENDER_QUALITY);
		}

		g2.drawImage(image, 0, 0, null); 
		g2.setTransform(beforeAffin);
		image = null;

		return bimg;
	}

}
