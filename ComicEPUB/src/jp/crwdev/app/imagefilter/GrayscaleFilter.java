/**
 * グレースケールフィルタ
 */
package jp.crwdev.app.imagefilter;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Date;

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
		
//		GrayscaleOp grayOp = new GrayscaleOp();
//		return grayOp.filter(image, null);

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
    	
    	Date startDate = new Date();
    	long starttime = startDate.getTime();
    	
    	if(false){
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
    	}
    	else{
    		// スレッドで並列処理させる
    		
	    	final int threadNum = 2;	// ３つ以上作ってもあまり変わらなかった(CPU次第か)
	    	
	    	final BufferedImage destCopy = dest;
	    	final BufferedImage srcCopy = image;
	    	Thread[] threads = new Thread[threadNum-1];
	    	for(int i=1; i<threadNum; i++){
	    		final int start = i;
	    		threads[i-1] = new Thread( new Runnable(){
	    			public void run(){
	    				convertThreadProcess(destCopy, srcCopy, start, threadNum);
	    			}
	    		});
	    		threads[i-1].start();
	    	}
			convertThreadProcess(destCopy, srcCopy, 0, threadNum);
			waitForAllThreads(threads);
    	}
    	
    	Date end = new Date();
    	long endtime = end.getTime();
    	System.out.println(String.format("convertTime = %d", endtime - starttime));
 
    	return dest;

	}
	
	private void convertThreadProcess(BufferedImage dest, BufferedImage src, int startLine, int offset){
	   	final double Rparam = 0.298912 * 1024;
    	final double Gparam = 0.586611 * 1024;
    	final double Bparam = 0.114478 * 1024;
    	
    	int height = src.getHeight();
    	int width = src.getWidth();
    	
    	int[] rgbArray = new int[width];

		for(int y=startLine; y<height; y+=offset){
			src.getRGB(0, y, width, 1, rgbArray, 0, width);
			for(int x=0; x<width; x++){
				int rgb = rgbArray[x];
	        	int r = (rgb >> 16) & 0xff;
	        	int g = (rgb >> 8) & 0xff;
	        	int b = rgb & 0xff;
	        	int n = (((int)( r*Rparam + g*Gparam + b*Bparam )) >> 10);
	        	int gray = ((n << 16) | (n << 8) | n);
	        	rgbArray[x] = gray;
			}
			dest.setRGB(0, y, width, 1, rgbArray, 0, width);
		}
	}

    private void waitForAllThreads(Thread[] threads) {
        try {
            for (Thread t:threads){
                t.join(Long.MAX_VALUE);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
