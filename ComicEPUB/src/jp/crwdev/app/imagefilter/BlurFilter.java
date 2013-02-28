package jp.crwdev.app.imagefilter;

import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

import jp.crwdev.app.interfaces.IImageFilter;

public class BlurFilter implements IImageFilter {

	private boolean mIsBlur = true;
	
	public BlurFilter(boolean blur){
		mIsBlur = blur;
	}
	
	@Override
	public BufferedImage filter(BufferedImage image, ImageFilterParam param) {
		if((mIsBlur && !param.isBlur()) || (!mIsBlur && !param.isSharpness())){
			return image;
		}
		
		BufferedImage dest = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());


		if(mIsBlur){
			float[] blur5x5 = {
					   1, 4, 6, 4, 1,
					   4, 16, 24, 16, 4,
					   6, 24, 36, 24, 6,
					   4, 16, 24, 16, 4,
					   1, 4, 6, 4, 1,
			};
			float total = 0.0f;
			for(int i=0; i<blur5x5.length; i++){
				total += blur5x5[i];
			}
			for(int i=0; i<blur5x5.length; i++){
				blur5x5[i] = blur5x5[i] / total;
			}

			Kernel blurKernel = new Kernel(5, 5, blur5x5); 
			ConvolveOp blurOp = new ConvolveOp(blurKernel, ConvolveOp.EDGE_NO_OP, null); 

			blurOp.filter(image, dest); // ぼかし 
		}
		else{
			float level = param.getSharpnessPixels();
			
			float slant = level * -0.02f;
			float side = slant * 2.0f;
			float aroundValue = side*4 + slant*4;
			float centerValue = 1.0f  - aroundValue;
			float[] sharp = {slant, side, slant,
					         side, centerValue, side,
					         slant, side, slant};

		      
//			float[] sharp =	{-0.06f, -0.11f, -0.06f,  //operator[1] 鮮鋭化
//				       -0.11f,  1.68f, -0.11f,
//				       -0.06f, -0.11f, -0.06f};
//			float[] sharp = {0.0f,-1.0f,0.0f,-1.0f,5.0f,-1.0f,0.f,-1.0f,0.0f}; 
			Kernel sharpKernel = new Kernel(3, 3, sharp); 
			ConvolveOp sharpOp = new ConvolveOp(sharpKernel, ConvolveOp.EDGE_NO_OP, null); 
			sharpOp.filter(image, dest); // シャープ 
		}

		return dest;
	}

}
