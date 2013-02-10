/**
 * グレースケールフィルタ
 */
package jp.crwdev.app.imagefilter;

import java.awt.image.BufferedImage;

import com.mortennobel.imagescaling.ImageUtils;

import jp.crwdev.app.interfaces.IImageFilter;

public class GrayscaleFilter implements IImageFilter {

	@Override
	public BufferedImage filter(BufferedImage image, ImageFilterParam param) {
		if(param == null || !param.isGrayscale()){
			return image;
		}
		
		/*
		 * Grayscale
		 */
	   	final double Rparam = 0.298912 * 1024;
    	final double Gparam = 0.586611 * 1024;
    	final double Bparam = 0.114478 * 1024;

    	// 元画像に影響が出ないようにコピーを作成
    	BufferedImage dest = image;
    	try {
    		dest = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
    	}
    	catch(OutOfMemoryError e){
    		e.printStackTrace();
    		// メモリ不足の場合は仕方ないので直接編集する
    	}
    	
    	int width = image.getWidth();
		int height = image.getHeight();
	    for( int y = 0; y < height; y ++ ) {
	        for( int x = 0; x < width; x ++ ) {
	        	int rgb = image.getRGB(x, y);
	        	int r = (rgb >> 16) & 0xff;
	        	int g = (rgb >> 8) & 0xff;
	        	int b = rgb & 0xff;
	        	int n = (((int)( r*Rparam + g*Gparam + b*Bparam )) >> 10);
	        	int gray = ((n << 16) | (n << 8) | n);
	        	dest.setRGB(x, y, gray);
	        }
	    }
    	
    	return dest;
	}
	
}
