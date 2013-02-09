/**
 * リサイズフィルタ
 */
package jp.crwdev.app.imagefilter;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.AreaAveragingScaleFilter;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;

import jp.crwdev.app.interfaces.IImageFilter;

public class ResizeFilter implements IImageFilter {

	/** リサイズ時のスケーリング係数W */
	private double mResizedScaleW = 1.0f;
	/** リサイズ時のスケーリング係数H */
	private double mResizedScaleH = 1.0f;
	
	/**
	 * リサイズ時のスケーリング係数W を取得
	 * @return
	 */
	public double getResizedScaleW(){
		return mResizedScaleW;
	}
	/**
	 * リサイズ時のスケーリング係数H を取得
	 * @return
	 */
	public double getResizedScaleH(){
		return mResizedScaleH;
	}
	
	@Override
	public BufferedImage filter(BufferedImage image, ImageFilterParam param) {
		if(param == null || !param.isResize()){
			return image;
		}
		
		/*
		 * Scale
		 */
		int width = image.getWidth();
		int height = image.getHeight();
		Dimension maxSize = param.getResizeDimension();
		Dimension resize = getResizeDimension(width, height, maxSize.width, maxSize.height);
		int rwidth = resize.width;//rect.width;
		int rheight = resize.height;//rect.height;
		
		double scalew = (double)rwidth/(double)width;
		double scaleh = (double)rheight/(double)height;
		
		mResizedScaleW = scalew;
		mResizedScaleH = scaleh;
		
		if(!param.isPreview() || true){
			// 低速・高品質
			ImageFilter filter = new AreaAveragingScaleFilter(rwidth, rheight);
			ImageProducer im = new FilteredImageSource(image.getSource(), filter);
			Image newImage = Toolkit.getDefaultToolkit().createImage(im);
			BufferedImage writeImage= new BufferedImage(rwidth, rheight, image.getType());
			
			RenderingHints hints = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			hints.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
			
			Graphics2D g2Dimage = writeImage.createGraphics();
			g2Dimage.setRenderingHints(hints);
			g2Dimage.drawImage(newImage, null, null);
			g2Dimage.dispose();
			
			return writeImage;
		}
		else{
			// 高速・低品質
			AffineTransformOp atOp = null;
			atOp = new AffineTransformOp(AffineTransform.getScaleInstance(scalew, scaleh),
					AffineTransformOp.TYPE_BILINEAR);
			
			BufferedImage dest2 = new BufferedImage(rwidth, rheight, image.getType()); 
			atOp.filter(image, dest2);
			
			return dest2;
		}
	}

	/**
	 * リサイズ後の領域サイズを取得する
	 * @param imgWidth 画像幅
	 * @param imgHeight　画像高さ
	 * @param maxWidth 最大幅
	 * @param maxHeight 最大高さ
	 * @return maxWidth/maxHeightに内接するサイズを返す。どちらもmaxに満たない場合は入力をそのまま返す
	 */
	public static Dimension getResizeDimension(int imgWidth, int imgHeight, int maxWidth, int maxHeight){
		
		if(maxWidth >= imgWidth && maxHeight >= imgHeight){
			return new Dimension(imgWidth, imgHeight);
		}
		
		if(imgHeight >= maxHeight){
			int height = maxHeight;
			float aspect = (float)maxHeight/(float)imgHeight;
			int width = (int)((float)imgWidth * aspect);
			if(width > maxWidth){
				width = maxWidth;
				aspect = (float)maxWidth/(float)imgWidth;
				height = (int)((float)imgHeight * aspect);
				return new Dimension(width,height);
			}
			return new Dimension(width,height);
		}
		else if(imgWidth >= maxWidth){
			int width = maxWidth;
			float aspect = (float)maxWidth/(float)imgWidth;
			int height = (int)((float)imgHeight * aspect);
			if(height > maxHeight){
				height = maxHeight;
				aspect = (float)maxHeight/(float)imgHeight;
				width = (int)((float)maxWidth * aspect);
				return new Dimension(width,height);
			}
			return new Dimension(width,height);
		}
		return new Dimension(imgWidth, imgHeight);
	}


}
