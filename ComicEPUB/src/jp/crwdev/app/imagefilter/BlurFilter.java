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
		if(!param.isBlur()){
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
			ConvolveOp blurOp = new ConvolveOp(blurKernel); 

			blurOp.filter(image, dest); // ぼかし 
		}
		else{
//		      float f0 = - 0.1f;
//		      float f1 = -0.3f;
//		      float f2 = 3.0f;
//		      float[] matrix = {
//		          f0,f0,f0,f0,f0,
//		          f0,f1,f1,f1,f0,
//		          f0,f1,f2,f1,f0,
//		          f0,f1,f1,f1,f0,
//		          f0,f0,f0,f0,f0};
//		     Kernel sharpCarnel2 = new Kernel(5,5,matrix);
//				ConvolveOp sharpOp = new ConvolveOp(sharpCarnel2, ConvolveOp.EDGE_NO_OP, null); 
//				sharpOp.filter(image, dest); // シャープ 
		      
			float[] sharp =	{-0.06f, -0.11f, -0.06f,  //operator[1] 鮮鋭化
				       -0.11f,  1.68f, -0.11f,
				       -0.06f, -0.11f, -0.06f};
//			float[] sharp = {0.0f,-1.0f,0.0f,-1.0f,5.0f,-1.0f,0.f,-1.0f,0.0f}; 
			Kernel sharpKernel = new Kernel(3, 3, sharp); 
			ConvolveOp sharpOp = new ConvolveOp(sharpKernel, ConvolveOp.EDGE_NO_OP, null); 
			sharpOp.filter(image, dest); // シャープ 
		}

		return dest;
	}

}
