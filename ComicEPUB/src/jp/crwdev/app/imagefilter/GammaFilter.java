/**
 * ガンマ補正フィルタ
 */
package jp.crwdev.app.imagefilter;

import java.awt.image.BufferedImage;
import java.awt.image.ByteLookupTable;
import java.awt.image.LookupOp;

import jp.crwdev.app.interfaces.IImageFilter;

public class GammaFilter implements IImageFilter {

	/** ガンマ値 */
	private double mCurrentGamma = 0.0f;
	/** ガンマテーブル */
	private byte[] mGammaTable = null;
	
	/**
	 * コンストラクタ
	 */
	public GammaFilter(){
		
	}

	/**
	 * ガンマ値からガンマテーブルを作成する
	 * @param gamma
	 * @return
	 */
	private byte[] getGammaTable(double gamma){
		if(gamma <= 0){
			gamma = 1.0;
		}
		if(mGammaTable == null){
			mGammaTable = new byte[256];
		}
		if(mCurrentGamma != gamma || mCurrentGamma == 0){
			mCurrentGamma = gamma;
			for(int i=0; i<256; i++){
				int val = (int)Math.round(255.0 * Math.pow(((double)i / 255.0),(gamma)));
	            if (val > 255) val = 255;
	            if (val < 0 ) val = 0;
	            mGammaTable[i] = (byte)val;
			}
		}
		return mGammaTable;
	}
	
	@Override
	public BufferedImage filter(BufferedImage image, ImageFilterParam param) {
		if(param == null || !param.isGamma()){
			return image;
		}
		
		byte[] gammaTable = getGammaTable(param.getGamma());
		
		BufferedImage dest = new BufferedImage(image.getWidth(),image.getHeight(),image.getType());
		
		ByteLookupTable blt = new ByteLookupTable(0,gammaTable);
		LookupOp lookupOp = new LookupOp(blt, null);
		lookupOp.filter(image, dest);
		
		return dest;
	}

}
